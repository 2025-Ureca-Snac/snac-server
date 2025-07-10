package com.ureca.snac.board.controller.request;

import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateCardRequest {

    @NotNull(message = "판매 유형은 필수입니다.")
    private CardCategory cardCategory;

    @NotNull(message = "통신사는 필수입니다.")
    private Carrier carrier;

    @NotNull(message = "데이터 용량은 필수입니다.")
    @Positive(message = "데이터 용량은 1 이상이어야 합니다.")
    private Integer dataAmount;

    @NotNull(message = "단위 가격은 필수입니다.")
    @PositiveOrZero(message = "단위 가격은 0 이상이어야 합니다.")
    private Integer price;
}
