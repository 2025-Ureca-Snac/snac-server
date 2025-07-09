package com.ureca.snac.board.service;

import com.ureca.snac.board.controller.request.CreateCardRequest;

public interface CardService {

    Long createCard(Long memberId, CreateCardRequest request);
}
