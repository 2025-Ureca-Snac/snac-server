package com.ureca.snac.trade.support;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.exception.CardNotFoundException;
import com.ureca.snac.board.repository.CardRepository;
import com.ureca.snac.trade.dto.TradeMessageDto;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeStatus;
import com.ureca.snac.trade.exception.TradeNotFoundException;
import com.ureca.snac.trade.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.ureca.snac.board.entity.constants.SellStatus.TRADING;

@Component
@RequiredArgsConstructor
public class TradeMessageBuilder {

    private final TradeRepository tradeRepository;
    private final CardRepository cardRepository;

    public TradeMessageDto buildTradeMessage(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow(TradeNotFoundException::new);
        Card card = cardRepository.findById(trade.getCardId()).orElseThrow(CardNotFoundException::new);

        TradeStatus status = trade.getStatus();

        String statusText;

        List<String> phoneList = new ArrayList<>();

        if (status == TradeStatus.PAYMENT_CONFIRMED && card.getCardCategory() == CardCategory.BUY && card.getSellStatus() == TRADING) {
            statusText = "구매글 거래가 시작되었습니다";
            phoneList.add(trade.getBuyer().getPhone());

        } else {
            switch (status) {
                case PAYMENT_CONFIRMED:
                    statusText = "결제가 완료되었습니다";
                    phoneList.add(trade.getSeller().getPhone());
                    break;

                case DATA_SENT:
                    statusText = "데이터 전송이 완료되었습니다";
                    phoneList.add(trade.getBuyer().getPhone());
                    break;

                case COMPLETED:
                    statusText = "거래가 확정되었습니다";
                    phoneList.add(trade.getSeller().getPhone());
                    break;

                case CANCELED:
                    statusText = "거래가 취소되었습니다";
                    phoneList.add(trade.getSeller().getPhone());
                    phoneList.add(trade.getBuyer().getPhone());
                    break;

                default:
                    statusText = "상태가 변경되었습니다";
            }
        }

        // 데이터량 GB, 가격 원 단위 포맷
        int dataGb = trade.getDataAmount();
        String priceText = String.format("%,d원", trade.getPriceGb());

        return new TradeMessageDto(phoneList, String.format(
                "[SNAC] 거래 ID %d: %s.\n데이터 %dGB, 거래 금액 %s.\n",
                trade.getId(), statusText, dataGb, priceText
        ));
    }
}
