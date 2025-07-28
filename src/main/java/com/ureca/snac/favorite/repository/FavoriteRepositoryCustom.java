package com.ureca.snac.favorite.repository;

import com.ureca.snac.favorite.dto.FavoriteListRequest;
import com.ureca.snac.favorite.entity.Favorite;
import com.ureca.snac.member.Member;
import org.springframework.data.domain.Slice;

public interface FavoriteRepositoryCustom {
    /**
     * @param fromMember 단골 목록을 조회하는 회원
     * @param request    커서와 페이지 크기 정보를 담은 요청 DTO
     * @return 단골 목록 Slice
     */
    Slice<Favorite> findFavoritesByFromMember(Member fromMember,
                                              FavoriteListRequest request);
}
