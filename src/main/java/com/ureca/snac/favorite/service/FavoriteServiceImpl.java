package com.ureca.snac.favorite.service;

import com.ureca.snac.common.CursorResult;
import com.ureca.snac.favorite.dto.FavoriteListRequest;
import com.ureca.snac.favorite.dto.FavoriteMemberDto;
import com.ureca.snac.favorite.entity.Favorite;
import com.ureca.snac.favorite.exception.AlreadyFavoriteMember;
import com.ureca.snac.favorite.exception.CannotFavoriteSelfException;
import com.ureca.snac.favorite.exception.FavoriteRelationNotFoundException;
import com.ureca.snac.favorite.repository.FavoriteRepository;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void createFavorite(String fromUserEmail, Long toMemberId) {

        // 사용자 존재 여부
        Member fromMember = findMemberByEmail(fromUserEmail);

        if (fromMember.getId().equals(toMemberId)) {
            throw new CannotFavoriteSelfException();
        }

        Member toMember = findMemberById(toMemberId);

        if (favoriteRepository.existsByFromMemberAndToMember(fromMember, toMember)) {
            throw new AlreadyFavoriteMember();
        }

        Favorite favorite = Favorite.builder()
                .fromMember(fromMember)
                .toMember(toMember)
                .build();
        favoriteRepository.save(favorite);
        log.info("[단골 등록] 이사람이 쟤를 : {} -> {}", fromMember.getId(), toMemberId);
    }

    @Override
    public CursorResult<FavoriteMemberDto> getMyFavorites(
            String fromUserEmail, FavoriteListRequest request
    ) {
        log.info("[단골 목록 조회] 시작. 누가 : {}", fromUserEmail);

        Member fromMember = findMemberByEmail(fromUserEmail);

        // 실제 데이터 목록, 다음 페이지 여부 hasNext 포함
        Slice<Favorite> favoriteSlice =
                favoriteRepository.findFavoritesByFromMember(
                        fromMember, request);

        // 데이터 변환
        List<FavoriteMemberDto> favoriteDtos = new ArrayList<>();

        for (Favorite favorite : favoriteSlice.getContent()) {
            Member toMember = favorite.getToMember();
            FavoriteMemberDto dto = FavoriteMemberDto.from(toMember);
            favoriteDtos.add(dto);
        }

        String nextCursor = calculateNextCursor(favoriteSlice);

        log.info("[단골 목록 조회] 완료. 조회된 단골 수 : {}, 다음 페이지 존재 여부 : {}",
                favoriteDtos.size(), favoriteSlice.hasNext());
        return new CursorResult<>(favoriteDtos, nextCursor, favoriteSlice.hasNext());
    }

    @Override
    @Transactional
    public void deleteFavorite(String fromUserEmail, Long toMemberId) {
        Member fromMember = findMemberByEmail(fromUserEmail);
        Member toMember = findMemberById(toMemberId);

        Favorite favorite = favoriteRepository.findByFromMemberAndToMember(fromMember, toMember)
                .orElseThrow(FavoriteRelationNotFoundException::new);

        favoriteRepository.delete(favorite);
        log.info("[단골 삭제] 얘가 쟤를 삭제 : {} -> {}", fromMember.getId(), toMemberId);
    }

    @Override
    public Long getFavoriteCount(String fromUserEmail) {
        Member fromMember = findMemberByEmail(fromUserEmail);
        Long count = favoriteRepository.countByFromMember(fromMember);
        log.info("[단골 수 조회] 회원 ID: {}, count: {}", fromMember.getId(), count);
        return count;
    }

    @Override
    public boolean checkFavoriteStatus(String fromUserEmail, Long toMemberId) {
        Member fromMember = findMemberByEmail(fromUserEmail);
        Member toMember = findMemberById(toMemberId);

        boolean isFavorite = favoriteRepository.existsByFromMemberAndToMember(fromMember, toMember);

        log.info("[단골 여부 확인] 요청자 : {}, 상대방 : {}, 여부 : {}",
                fromUserEmail, toMemberId, isFavorite);

        return isFavorite;
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
    }

    private String calculateNextCursor(Slice<Favorite> slice) {
        if (!slice.hasNext() || slice.getContent().isEmpty()) {
            return null;
        }
        Favorite lastFavorite = slice.getContent().get(slice.getContent().size() - 1);

        return lastFavorite.getCreatedAt().toString() + "," + lastFavorite.getId();
    }
}
