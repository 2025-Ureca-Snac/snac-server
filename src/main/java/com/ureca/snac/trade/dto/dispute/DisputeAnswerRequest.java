package com.ureca.snac.trade.dto.dispute;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisputeAnswerRequest {
    //관리자 답변
    private boolean needMore;   // 자료 추가 요청
    private String  answer;
}