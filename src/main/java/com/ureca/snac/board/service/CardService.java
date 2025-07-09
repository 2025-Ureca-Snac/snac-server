package com.ureca.snac.board.service;

import com.ureca.snac.board.controller.request.CreateCardRequest;
import com.ureca.snac.board.controller.request.UpdateCardRequest;

public interface CardService {

    Long createCard(Long memberId, CreateCardRequest request);

    void updateCard(Long memberId, Long cardId, UpdateCardRequest updateCardRequest);
}
