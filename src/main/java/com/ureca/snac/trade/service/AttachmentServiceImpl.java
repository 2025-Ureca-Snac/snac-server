package com.ureca.snac.trade.service;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;
import com.ureca.snac.common.s3.S3Path;
import com.ureca.snac.common.s3.S3Uploader;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.trade.entity.Attachment;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.exception.AttachmentAlreadyExistsException;
import com.ureca.snac.trade.exception.AttachmentPermissionDenied;
import com.ureca.snac.trade.repository.AttachmentRepository;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.service.interfaces.AttachmentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional // 모든 메서드에 트랜잭션 적용
public class AttachmentServiceImpl implements AttachmentService {

    private final TradeRepository tradeRepository;
    private final AttachmentRepository attachmentRepository;
    private final MemberRepository memberRepository;
    private final S3Uploader s3Uploader;

    @Override
    public void upload(Long tradeId, String userEmail, MultipartFile image) {

        // 거래, 회원 검증 :
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new BusinessException(BaseCode.TRADE_NOT_FOUND));

        Member uploader = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(BaseCode.MEMBER_NOT_FOUND));

        // 권한 체크 : 판매자만 업로드 ( 판매자가 데이터 보낸 스크린샷 첨부 )
        if (!trade.getSeller().getId().equals(uploader.getId())) {
            throw new AttachmentPermissionDenied();
        }

        // 중복 업로드 방지 ( 거래당 이미지 한 개 )
        if (attachmentRepository.existsByTradeId(tradeId)) {
            throw new AttachmentAlreadyExistsException();
        }

        // S3 업로드, 객체 키 반환받음
        String s3Key = s3Uploader.upload(image, S3Path.TRADE_ATTACHMENT);

        // 엔티티 저장
        Attachment attachment = Attachment.builder()
                .trade(trade) // 연결할 거래
                .uploader(uploader) // 판매자
                .s3Key(s3Key) // 객체키
                .build();

        attachmentRepository.save(attachment);
    }

    @Override
    public String generatePresignedUrl(Long tradeId, String userEmail) {
        // 거래 검증
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new BusinessException(BaseCode.TRADE_NOT_FOUND));

        // 권한 체크 : 현재 거래의 판매자, 구매자만 url 요청 가능
        if (!trade.getSeller().getEmail().equals(userEmail)
                && !trade.getBuyer().getEmail().equals(userEmail)) {
            throw new BusinessException(BaseCode.ATTACHMENT_PERMISSION_DENIED);
        }

        // 첨부 파일 조회
        Attachment attachment = attachmentRepository.findByTradeId(tradeId)
                .orElseThrow(() -> new BusinessException(BaseCode.ATTACHMENT_NOT_FOUND));

        // url 생성 및 반환 s3 객체키로 url 만듦
        return s3Uploader.generatePresignedUrl(attachment.getS3Key());
    }
}
