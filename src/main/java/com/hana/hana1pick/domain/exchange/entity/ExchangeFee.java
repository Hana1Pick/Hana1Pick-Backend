package com.hana.hana1pick.domain.exchange.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ExchangeFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 통화: KRW, JPY, CNY
    @Column
    private String currency;

    // 환율 수수료
    @Column(name = "fee_rate")
    private Double feeRate;

    // 생성일
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Getters and Setters
}
