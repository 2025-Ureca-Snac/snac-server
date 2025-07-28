package com.ureca.snac.trade.dto.dispute;

import com.ureca.snac.trade.entity.Dispute;
import com.ureca.snac.trade.entity.DisputeStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisputeAnswerRequest {
    //관리자 답변
    private DisputeStatus result;   // 자료 추가 요청
    private String  answer;
}