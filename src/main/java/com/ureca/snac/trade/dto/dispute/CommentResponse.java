package com.ureca.snac.trade.dto.dispute;

import com.ureca.snac.trade.entity.AuthorType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private AuthorType author;
    private String content;
    private LocalDateTime createdAt;
}
