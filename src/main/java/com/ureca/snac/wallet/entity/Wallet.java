package com.ureca.snac.wallet.entity;

import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.wallet.exception.InsufficientBalanceException;
import com.ureca.snac.wallet.exception.InvalidAMountException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wallet")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(name = "balance_point", nullable = false)
    private Long point;

    @Column(name = "balance_money", nullable = false)
    private Long money;

    @Builder
    private Wallet(Member member, Long point, Long money) {
        this.member = member;
        this.point = point;
        this.money = money;
    }

    public static Wallet create(Member member) {
        return Wallet.builder()
                .member(member)
                .point(0L)
                .money(0L)
                .build();
    }

    public void depositMoney(long amount) {
        if (amount <= 0) {
            throw new InvalidAMountException();
        }
        this.money += amount;
    }

    public void withdrawMoney(long amount) {
        if (amount <= 0) {
            throw new InvalidAMountException();
        }
        if (this.money < amount) {
            throw new InsufficientBalanceException();
        }
        this.money -= amount;
    }

    public void depositPoint(long amount) {
        if (amount <= 0) {
            throw new InvalidAMountException();
        }
        this.point += amount;
    }

    public void withdrawPoint(long amount) {
        if (amount <= 0) {
            throw new InvalidAMountException();
        }
        if (this.point < amount) {
            throw new InsufficientBalanceException();
        }
        this.point -= amount;
    }
}
