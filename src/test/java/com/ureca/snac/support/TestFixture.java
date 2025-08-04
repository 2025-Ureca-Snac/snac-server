package com.ureca.snac.support;

import com.ureca.snac.asset.entity.AssetType;
import com.ureca.snac.asset.entity.SourceDomain;
import com.ureca.snac.asset.entity.TransactionCategory;
import com.ureca.snac.asset.entity.TransactionType;
import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.infra.dto.response.TossConfirmResponse;
import com.ureca.snac.member.Activated;
import com.ureca.snac.member.Role;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.payment.entity.Payment;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class TestFixture {

    // Member
    public static Member createTestMember(Long id, String email) {
        Member member = Member.builder()
                .email(email)
                .password("1")
                .name("테스터")
                .nickname("테스터입니다")
                .nicknameUpdatedAt(LocalDateTime.now().minusDays(2))
                .phone("01011111111")
                .birthDate(LocalDate.from(LocalDateTime.now().minusYears(20)))
                .role(Role.USER)
                .activated(Activated.NORMAL)
                .ratingScore(1000)
                .build();
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    public static Member createTestMember() {
        return createTestMember(1L, "test@test.com");
    }

    // Payment
    public static Payment createPendingPayment(Member member, Long amount) {
        Payment payment = Payment.prepare(member, amount);
        ReflectionTestUtils.setField(payment, "id", 1L);
        return payment;
    }

    public static Payment createPendingPayment(Member member) {
        return createPendingPayment(member, 10000L);
    }

    public static Payment createSuccessPayment(Member member, Long amount, String method,
                                               OffsetDateTime paidAt) {
        Payment payment = createPendingPayment(member, amount);
        payment.complete("test_payment_key", method, paidAt);
        return payment;
    }

    // Toss event
    public static TossConfirmResponse createTossConfirmResponse() {
        return new TossConfirmResponse("toss_key", "카드", OffsetDateTime.now());
    }

    public static AssetChangedEvent createDummyEvent() {
        return AssetChangedEvent.builder()
                .memberId(1L)
                .assetType(AssetType.MONEY)
                .transactionType(TransactionType.DEPOSIT)
                .category(TransactionCategory.RECHARGE)
                .amount(10000L)
                .balanceAfter(20000L)
                .title("머니 충전")
                .sourceDomain(SourceDomain.PAYMENT)
                .sourceId(1L)
                .build();
    }

    public static AssetChangedEvent createDummyEventWithMemberId(Long memberId) {
        return AssetChangedEvent.builder()
                .memberId(memberId)
                .assetType(AssetType.MONEY)
                .transactionType(TransactionType.DEPOSIT)
                .category(TransactionCategory.RECHARGE)
                .amount(10000L)
                .balanceAfter(20000L)
                .title("머니 충전")
                .sourceDomain(SourceDomain.PAYMENT)
                .sourceId(1L)
                .build();
    }
}
