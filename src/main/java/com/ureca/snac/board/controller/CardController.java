package com.ureca.snac.board.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.board.controller.request.CreateCardRequest;
import com.ureca.snac.board.controller.request.SellStatusFilter;
import com.ureca.snac.board.controller.request.UpdateCardRequest;
import com.ureca.snac.board.dto.CardDto;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.entity.constants.PriceRange;
import com.ureca.snac.board.service.CardService;
import com.ureca.snac.board.service.response.CardResponse;
import com.ureca.snac.board.service.response.CreateCardResponse;
import com.ureca.snac.board.service.response.ScrollCardResponse;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.swagger.annotation.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.ureca.snac.common.BaseCode.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController implements CardControllerSwagger {

    private final CardService cardService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<CreateCardResponse>> createCard(@Validated @RequestBody CreateCardRequest request,
                                                                      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long cardId = cardService.createCard(customUserDetails.getUsername(), request);

        return ResponseEntity.status(CARD_CREATE_SUCCESS.getStatus())
                .body(ApiResponse.of(CARD_CREATE_SUCCESS, new CreateCardResponse(cardId)));
    }

    @Override
    @PutMapping("/{cardId}")
    public ResponseEntity<ApiResponse<?>> editCard(@PathVariable("cardId") Long cardId,
                                                   @Validated @RequestBody UpdateCardRequest updateCardRequest,
                                                   @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        cardService.updateCard(customUserDetails.getUsername(), cardId, updateCardRequest);

        return ResponseEntity.status(CARD_UPDATE_SUCCESS.getStatus())
                .body(ApiResponse.ok(CARD_UPDATE_SUCCESS));
    }

    @Override
    @GetMapping("/scroll")
    public ResponseEntity<ApiResponse<ScrollCardResponse>> scrollCards(@RequestParam CardCategory cardCategory,
                                                                       @RequestParam(required = false) Carrier carrier,
                                                                       @RequestParam(value = "priceRanges") PriceRange priceRange,
                                                                       @RequestParam SellStatusFilter sellStatusFilter,
                                                                       @RequestParam(defaultValue = "true") Boolean highRatingFirst,
                                                                       @RequestParam(defaultValue = "54") Integer size,
                                                                       @RequestParam(required = false) Long lastCardId,
                                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastUpdatedAt,
                                                                       @RequestParam(defaultValue = "false") Boolean favoriteOnly,
                                                                       @UserInfo CustomUserDetails userDetails) {

        String username = (userDetails != null) ? userDetails.getUsername() : null;

        ScrollCardResponse response = cardService.scrollCards(cardCategory, carrier, priceRange,
                sellStatusFilter, highRatingFirst, size, lastCardId, lastUpdatedAt,
                favoriteOnly, username);

        return ResponseEntity.ok(ApiResponse.of(CARD_READ_SUCCESS, response));
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<ScrollCardResponse>> getCardByOwner(@RequestParam CardCategory cardCategory,
                                                                          @RequestParam(defaultValue = "54") Integer size,
                                                                          @RequestParam(required = false) Long lastCardId,
                                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastUpdatedAt,
                                                                          @AuthenticationPrincipal UserDetails userDetails) {
        ScrollCardResponse response = cardService.getCardsByOwner(userDetails.getUsername(), cardCategory, size, lastCardId, lastUpdatedAt);

        return ResponseEntity.ok(ApiResponse.of(CARD_READ_SUCCESS, response));
    }

    @Override
    @DeleteMapping("/{cardId}")
    public ResponseEntity<ApiResponse<?>> removeCard(@PathVariable("cardId") Long cardId,
                                                     @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        cardService.deleteCard(customUserDetails.getUsername(), cardId);

        return ResponseEntity.ok(ApiResponse.ok(CARD_DELETE_SUCCESS));
    }

    @Override
    @GetMapping("/dev")
    public ResponseEntity<ApiResponse<List<CardDto>>> getDevCardList() {
        return ResponseEntity.ok(ApiResponse.of(CARD_READ_SUCCESS, cardService.findAllDevCard()));
    }

    @Override
    @GetMapping("/{cardId}")
    public ResponseEntity<ApiResponse<CardResponse>> getCardById(@PathVariable("cardId") Long cardId) {
        return ResponseEntity.ok(ApiResponse.of(CARD_READ_SUCCESS, cardService.findCardById(cardId)));
    }

    @Override
    @GetMapping("/favorite/{email}")
    public ResponseEntity<ApiResponse<List<CardResponse>>> getCardsByFavoriteUser(@PathVariable("email") String email) {
        List<CardResponse> response = cardService.getSellingCardsByEmail(email);
        return ResponseEntity.ok(ApiResponse.of(CARD_READ_SUCCESS, response));
    }
}
