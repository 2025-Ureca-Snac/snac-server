package com.ureca.snac.common.s3;


import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;
import com.ureca.snac.config.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3Properties props;

    // S3 파일 업로드
    public String upload(MultipartFile multipartFile, String prefix) {
        try {
            String s3Key = buildKey(multipartFile.getOriginalFilename(), prefix);

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(props.getBucket()) // 버킷 명
                    .key(s3Key) // 업로드 경로
                    .contentType(multipartFile.getContentType())
                    .build();

            s3Client.putObject(
                    putRequest,
                    RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize())
            );

            return s3Key;
        } catch (Exception e) {
            throw new BusinessException(BaseCode.S3_UPLOAD_FAILED);
        }
    }
    // 고유 객체키 생성 역할
    // 객체 키 = {prefix}/{yyyy}/{MM}/{uuid}_{원본명}
    public String buildKey(String originalName, String prefix) {
        LocalDate today = LocalDate.now();
        String uuid = UUID.randomUUID().toString();
        return String.format("%s/%d/%02d/%s_%s",
                prefix.replaceAll("/$", ""),
                today.getYear(),
                today.getMonthValue(),
                uuid,
                originalName.replaceAll("\\s+", "_"));
    }

    // Presigned URL 생성 기능
    // 버킷의 접근 권한을 Public 으로 설정하지 않고 사용자에게 임시 접근 권한을 부여
    public String generatePresignedUrl(String s3Key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(props.getBucket())
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5)) // 5분 간 유효한 URL 생성
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    // S3에서 파일 삭제
    public void delete(String s3Key) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteRequest);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}", s3Key, e);
            throw new BusinessException(BaseCode.S3_DELETE_FAILED);
        }
    }

    public String generatePresignedPutUrl(String s3Key) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(props.getBucket())
                .key(s3Key)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }
}
