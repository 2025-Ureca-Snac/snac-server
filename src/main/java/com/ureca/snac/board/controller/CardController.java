package com.ureca.snac.board.controller;

import com.ureca.snac.board.controller.request.CreateCardRequest;
import com.ureca.snac.board.controller.request.UpdateCardRequest;
import com.ureca.snac.board.service.CardService;
import com.ureca.snac.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.ureca.snac.common.BaseCode.CARD_CREATE_SUCCESS;
import static com.ureca.snac.common.BaseCode.CARD_UPDATE_SUCCESS;

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

    @PutMapping("/{cardId}")
    public ResponseEntity<ApiResponse<?>> editCard(@PathVariable("cardId") Long cardId,
                                                   @Validated @RequestBody UpdateCardRequest updateCardRequest) {
        cardService.updateCard(1L, cardId, updateCardRequest);

        return ResponseEntity.status(CARD_UPDATE_SUCCESS.getStatus())
                .body(ApiResponse.ok(CARD_UPDATE_SUCCESS));
    }
}
