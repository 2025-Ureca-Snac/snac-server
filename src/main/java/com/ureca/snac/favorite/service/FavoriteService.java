package com.ureca.snac.favorite.service;


import com.ureca.snac.common.CursorResult;
import com.ureca.snac.favorite.dto.FavoriteListRequest;
import com.ureca.snac.favorite.dto.FavoriteMemberDto;

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
     * @param fromUserEmail 단골 목록 조회하는 회원 이메일
     * @param request       커서와 페이지 크기를 정보를 담은 DTO
     * @return 커서 정보를 포함한 단골 회원 목록
     */
    CursorResult<FavoriteMemberDto> getMyFavorites(
            String fromUserEmail,
            FavoriteListRequest request
    );

    /**
     * 단골 삭제
     *
     * @param fromUserEmail 삭제하는 회원 이메일
     * @param toMemberId    삭제 당하는 회원 ID
     */
    void deleteFavorite(String fromUserEmail, Long toMemberId);

    /**
     * 내가 등록한 단골의 총 개수를 조회.
     *
     * @param fromUserEmail 검색할 주체 회원 이메일
     * @return 등록된 단골 수
     */
    Long getFavoriteCount(String fromUserEmail);

    /**
     * 단골 등록했는지에 대한 여부
     *
     * @param fromUserEmail 검색할 주체 회원 이메일
     * @param toMemberId    상대방 ID
     * @return 단골 여부
     */
    boolean checkFavoriteStatus(String fromUserEmail, Long toMemberId);
}
