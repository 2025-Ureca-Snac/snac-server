package com.ureca.snac.trade.entity;

import com.ureca.snac.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wallet")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet {

    @Id
    @Column(name = "member_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "balance_point", nullable = false)
    private Long point;

    @Column(name = "balance_money", nullable = false)
    private Long money;

    public void decrease(Long amount, PaymentType type) {
        if (type == PaymentType.POINT) this.point -= amount;
        else this.money -= amount;
    }

    public void increase(Long amount, PaymentType type) {
        if (type == PaymentType.POINT) this.point += amount;
        else this.money += amount;
    }

}