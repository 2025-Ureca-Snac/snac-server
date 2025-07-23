package com.ureca.snac.favorite.service;


import com.ureca.snac.favorite.dto.CursorResult;
import com.ureca.snac.favorite.dto.FavoriteMemberDto;

import java.time.LocalDateTime;

public interface FavoriteService {

    /**
     * 특정 사용자를 단골 목록에 추가합니다.
     *
     * @param fromUserEmail 단골 등록하는 주체 회원 이메일 (로그인한 사용자)
     * @param toMemberId    단골로 등록될 대상 회원 ID
     */
    void createFavorite(String fromUserEmail, Long toMemberId);

    /**
     * 로그인한 사용자의 단골 목록을 커서 기반 페이지네이션 조회
     * 최신순으로 조회 복합 커서 기반임
     *
     * @param fromUserEmail   단골 목록 조회하는 회원 이메일
     * @param cursorCreatedAt 이전 페이지 마지막 항목의 생성 시간 null
     * @param cursorId        이전페이지 마지막 항목 ID 첫페이지는 null
     * @param size            조회할 페이지 크기 null 이면 기본값
     * @return 커서 정보를 포함한 단골 회원 목록
     */

    CursorResult<FavoriteMemberDto> getMyFavorites(
            String fromUserEmail,
            LocalDateTime cursorCreatedAt,
            Long cursorId,
            Integer size
    );

    /**
     * 단골 삭제
     *
     * @param fromUserEmail 삭제하는 회원 이메일
     * @param toMemberId    삭제 당하는 회원 ID
     */
    void deleteFavorite(String fromUserEmail, Long toMemberId);
}
