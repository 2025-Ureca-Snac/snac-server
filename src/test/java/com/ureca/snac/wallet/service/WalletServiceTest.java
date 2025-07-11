//package com.ureca.snac.wallet.service;
//
//import com.ureca.snac.member.Member;
//import com.ureca.snac.member.MemberRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//class WalletServiceTest {
//
//    @Autowired
//    private WalletService walletService;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    private Member tmember;
//
//    @BeforeEach
//    void 셋업() {
//        tmember = Member.builder()
//                .email("test@test.com")
//                .password("pw")
//                .name("테스트")
//                .build();
//        memberRepository.save(tmember);
//        walletService.createWallet(tmember);
//    }
//
//    @Test
//    @DisplayName("머니 입금 후 출금이 정확히 잔액에 반영")
//    void 머니출금_잔액있음() {
//        //given
//        // 10000원 입금
//        walletService.depositMoney(tmember.getId(), 10000L);
//        //when
//        // 3000 출금
//        walletService.withdrawMoney(tmember.getId(), 3000L);
//        //then
//        long finalBalance = walletService.getMoneyBalance(tmember.getId());
//        Assertions.assertThat(finalBalance).isEqualTo(7000L);
//    }
//}