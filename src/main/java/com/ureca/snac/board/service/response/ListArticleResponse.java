package com.ureca.snac.board.service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ListArticleResponse {
    private List<ArticleResponse> articleResponseList;
    private Boolean hasNext;
}
