package com.ureca.snac.finance.entity;

import com.ureca.snac.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "bank")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bank extends BaseTimeEntity {

    @Id
    @Column(name = "bank_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    public Bank(String name) {
        this.name = name;
    }

    public void update(String name) {
        this.name = name;
    }
}
