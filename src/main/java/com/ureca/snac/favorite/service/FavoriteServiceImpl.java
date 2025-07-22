package com.ureca.snac.favorite.service;

import com.ureca.snac.favorite.dto.CursorResult;
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
import org.springframework.data.domain.Pageable;
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
    private static final int SIZE = 10;

    @Override
    @Transactional
    public void createFavorite(Long fromMemberId, Long toMemberId) {
        if (fromMemberId.equals(toMemberId)) {
            throw new CannotFavoriteSelfException();
        }

        // 사용자 존재 여부
        Member fromMember = findMemberById(fromMemberId);
        Member toMember = findMemberById(toMemberId);

        if (favoriteRepository.existsByFromMemberAndToMember(fromMember, toMember)) {
            throw new AlreadyFavoriteMember();
        }

        Favorite favorite = Favorite.builder()
                .fromMember(fromMember)
                .toMember(toMember)
                .build();
        favoriteRepository.save(favorite);
        log.info("[단골 등록] 이사람이 쟤를 : {} -> {}", fromMemberId, toMemberId);
    }

    @Override
    public CursorResult<FavoriteMemberDto> getMyFavorites(Long fromMemberId, Long cursorId, int size) {

        // size가 0이하의 예외를 방지
        if (size <= 0) {
            size = SIZE;
        }
        Member fromMember = findMemberById(fromMemberId);
        Long currentCursorId = (cursorId == null || cursorId <= 0) ?
                Long.MAX_VALUE : cursorId;

        // 데이터 조회
        Pageable pageable = PageRequest.of(0, size);
        // 실제 데이터 목록, 다음 페이지 여부 hasNext 포함
        Slice<Favorite> favoriteSlice = favoriteRepository.findAllWithToMemberByCursor(
                fromMember, currentCursorId, pageable
        );

        // 데이터 변환
        List<Favorite> favorites = favoriteSlice.getContent();
        
        List<FavoriteMemberDto> favoriteDto = new ArrayList<>();

        for (Favorite favorite : favorites) {
            Member toMember = favorite.getToMember();
            FavoriteMemberDto dto = FavoriteMemberDto.from(toMember);
            favoriteDto.add(dto);
        }

        // 커서 계산
        Long nextCursorId = favorites.isEmpty() ?
                null : favorites.get(favorites.size() - 1).getId();

        return CursorResult.of(favoriteDto, nextCursorId, favoriteSlice.hasNext());
    }

    @Override
    @Transactional
    public void deleteFavorite(Long fromMemberId, Long toMemberId) {
        Member fromMember = findMemberById(fromMemberId);
        Member toMember = findMemberById(toMemberId);


        Favorite favorite = favoriteRepository.findByFromMemberAndToMember(fromMember, toMember)
                .orElseThrow(FavoriteRelationNotFoundException::new);

        favoriteRepository.delete(favorite);
        log.info("[단골 삭제] 얘가 쟤를 삭제 : {} -> {}", fromMemberId, toMemberId);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }
}
