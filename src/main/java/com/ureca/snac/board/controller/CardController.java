package com.ureca.snac.board.controller;

import com.ureca.snac.board.controller.request.CreateCardRequest;
import com.ureca.snac.board.controller.request.UpdateCardRequest;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.entity.constants.PriceRange;
import com.ureca.snac.board.service.CardService;
import com.ureca.snac.board.service.response.ScrollCardResponse;
import com.ureca.snac.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.ureca.snac.common.BaseCode.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController implements CardControllerSwagger{

    private final CardService cardService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createCard(@Validated @RequestBody CreateCardRequest request) {
        cardService.createCard(1L, request);

        return ResponseEntity.status(CARD_CREATE_SUCCESS.getStatus())
                .body(ApiResponse.ok(CARD_CREATE_SUCCESS));
    }

    @Override
    @PutMapping("/{cardId}")
    public ResponseEntity<ApiResponse<?>> editCard(@PathVariable("cardId") Long cardId,
                                                   @Validated @RequestBody UpdateCardRequest updateCardRequest) {
        cardService.updateCard(1L, cardId, updateCardRequest);

        return ResponseEntity.status(CARD_UPDATE_SUCCESS.getStatus())
                .body(ApiResponse.ok(CARD_UPDATE_SUCCESS));
    }

    @Override
    @GetMapping("/scroll")
    public ResponseEntity<ApiResponse<ScrollCardResponse>> scrollCards(@RequestParam CardCategory cardCategory,
                                                                       @RequestParam(required = false) Carrier carrier,
                                                                       @RequestParam(value = "priceRanges") List<PriceRange> priceRanges,
                                                                       @RequestParam(defaultValue = "54") int size,
                                                                       @RequestParam(required = false) Long lastCardId,
                                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastUpdatedAt) {

        ScrollCardResponse response = cardService.scrollCards(cardCategory, carrier, priceRanges, size, lastCardId, lastUpdatedAt);

        return ResponseEntity.ok(ApiResponse.of(CARD_READ_SUCCESS, response));
    }

    @Override
    @DeleteMapping("/{cardId}")
    public ResponseEntity<ApiResponse<?>> removeCard(@PathVariable("cardId") Long cardId) {
        cardService.deleteCard(1L, cardId);

        return ResponseEntity.ok(ApiResponse.ok(CARD_DELETE_SUCCESS));
    }
}
