package com.ureca.snac.finance.entity;

import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.finance.service.AccountNumberConverter;
import com.ureca.snac.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseTimeEntity {

    @Id
    @Column(name = "account_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;

    @Column(name = "account_number", nullable = false, columnDefinition = "VARCHAR(512)")
    @Convert(converter = AccountNumberConverter.class)
    private String accountNumber;

    @Builder
    private Account(Member member, Bank bank, String accountNumber) {
        this.member = member;
        this.bank = bank;
        this.accountNumber = accountNumber;
    }

    public void update(Bank bank, String accountNumber) {
        this.bank = bank;
        this.accountNumber = accountNumber;
    }
}
