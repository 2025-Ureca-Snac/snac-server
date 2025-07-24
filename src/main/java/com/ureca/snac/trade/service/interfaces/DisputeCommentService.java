package com.ureca.snac.trade.service.interfaces;

import com.ureca.snac.trade.dto.dispute.CommentResponse;
import com.ureca.snac.trade.entity.AuthorType;

import java.util.List;

public interface DisputeCommentService {
    Long addComment(Long disputeId, String email,
                    String content, AuthorType author, boolean requestExtra);

    List<CommentResponse> listComment(Long disputeId, String requesterEmail);
}