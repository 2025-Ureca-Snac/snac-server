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

        TradeStatus status = trade.getStatus();

        String statusText;

        List<String> phoneList = new ArrayList<>();

        if (status == TradeStatus.PAYMENT_CONFIRMED_ACCEPTED) {
            statusText = "[SNAC] 구매글 거래가 시작되었습니다. 판매자와 연락하여 진행해 주세요.";
            phoneList.add(trade.getBuyer().getPhone());

        } else {
            switch (status) {
                case PAYMENT_CONFIRMED:
                    statusText = "[SNAC] 결제가 완료되었습니다. 판매자는 데이터 전송을 진행해 주세요.";
                    phoneList.add(trade.getSeller() != null ? trade.getSeller().getPhone() : "");
                    break;

                case DATA_SENT:
                    statusText = "[SNAC] 데이터가 전송되었습니다. 구매자는 확인 후 확정해 주세요.";
                    phoneList.add(trade.getBuyer().getPhone());
                    break;

                case COMPLETED:
                    statusText = "[SNAC] 거래가 최종 확정되었습니다. 이용해 주셔서 감사합니다.";
                    phoneList.add(trade.getSeller().getPhone());
                    break;

                case CANCELED:
                    statusText = "[SNAC] 거래가 취소되었습니다. 사유를 확인해 주세요.";
                    phoneList.add(trade.getSeller() != null ? trade.getSeller().getPhone() : "");
                    phoneList.add(trade.getBuyer().getPhone());
                    break;

                case AUTO_REFUND:
                    statusText = "[SNAC] 판매자 지연으로 인해 자동으로 환불 처리되었습니다.";
                    phoneList.add(trade.getSeller().getPhone());
                    phoneList.add(trade.getBuyer().getPhone());
                    break;
                case AUTO_PAYOUT:
                    statusText = "[SNAC] 구매자가 확정하지 않아 자동으로 정산 처리되었습니다.";
                    phoneList.add(trade.getSeller().getPhone());
                    phoneList.add(trade.getBuyer().getPhone());
                    break;

                default:
                    statusText = "[SNAC] 거래 상태가 변경되었습니다. 앱에서 상세 내용을 확인해 주세요.";
            }
        }

        // 데이터량 GB, 가격 원 단위 포맷
        int dataGb = trade.getDataAmount();
//        String priceText = String.format("%,d원", trade.getPriceGb());

//        return new TradeMessageDto(phoneList, String.format(
//                "[SNAC] 거래 ID %d: %s.\n데이터 %dGB, 거래 금액 %s.\n",
//                trade.getId(), statusText, dataGb, priceText
//        ));
        return new TradeMessageDto(phoneList, statusText);
    }
}
