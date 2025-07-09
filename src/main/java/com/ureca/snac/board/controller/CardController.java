package com.ureca.snac.board.controller;

import com.ureca.snac.board.controller.request.CreateCardRequest;
import com.ureca.snac.board.service.CardService;
import com.ureca.snac.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ureca.snac.common.BaseCode.CARD_CREATE_SUCCESS;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createCard(@Validated @RequestBody CreateCardRequest request) {
        cardService.createCard(1L, request);

        return ResponseEntity.status(CARD_CREATE_SUCCESS.getStatus())
                .body(ApiResponse.ok(CARD_CREATE_SUCCESS));
    }
}
