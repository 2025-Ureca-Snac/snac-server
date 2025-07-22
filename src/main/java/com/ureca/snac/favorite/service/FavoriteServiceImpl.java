package com.ureca.snac.favorite.service;

import com.ureca.snac.favorite.dto.CursorResult;
import com.ureca.snac.favorite.dto.FavoriteCursor;
import com.ureca.snac.favorite.dto.FavoriteMemberDto;
import com.ureca.snac.favorite.entity.Favorite;
import com.ureca.snac.favorite.exception.AlreadyFavoriteMember;
import com.ureca.snac.favorite.exception.CannotFavoriteSelfException;
import com.ureca.snac.favorite.exception.FavoriteRelationNotFoundException;
import com.ureca.snac.favorite.repository.FavoriteRepository;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;
    private static final int SIZE = 10;

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
            String fromUserEmail, LocalDateTime cursorCreatedAt,
            Long cursorId, Integer size
    ) {
        log.info("단골 목록 조회] 누가 : {}, 시간 : {}, cursorID : {}, size : {}",
                fromUserEmail, cursorCreatedAt, cursorId, size);
        // size가 null 이거나 0 이하의 예외를 방지
        int pageSize = (size == null || size <= 0) ? SIZE : size;

        Member fromMember = findMemberByEmail(fromUserEmail);

        // 페이지 조회 커서값 초기화
        LocalDateTime currentCursorCreatedAt = (cursorCreatedAt == null) ?
                LocalDateTime.now() : cursorCreatedAt;

        Long currentCursorId = (cursorId == null) ?
                Long.MAX_VALUE : cursorId;

        // 실제 데이터 목록, 다음 페이지 여부 hasNext 포함
        Slice<Favorite> favoriteSlice =
                favoriteRepository.findAllWithToMemberByCursor(
                        fromMember, currentCursorCreatedAt, currentCursorId,
                        PageRequest.of(0, pageSize)
                );

        // 데이터 변환
        List<Favorite> favorites = favoriteSlice.getContent();

        List<FavoriteMemberDto> favoriteDto = new ArrayList<>();

        for (Favorite favorite : favorites) {
            Member toMember = favorite.getToMember();
            FavoriteMemberDto dto = FavoriteMemberDto.from(toMember);
            favoriteDto.add(dto);
        }

        FavoriteCursor nextCursor = null;
        // 커서 계산
        if (!favorites.isEmpty()) {
            Favorite lastFavorite = favorites.get(favorites.size() - 1);

            nextCursor = new FavoriteCursor(lastFavorite.getCreatedAt(), lastFavorite.getId());
        }

        return CursorResult.of(favoriteDto, nextCursor, favoriteSlice.hasNext());
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

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
    }
}
