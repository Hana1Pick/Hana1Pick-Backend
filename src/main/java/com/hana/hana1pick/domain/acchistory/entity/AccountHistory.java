package com.hana.hana1pick.domain.acchistory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class AccountHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "acc_his_idx")
    private Long idx;

    @Column
    private String memo;

    @Column
    private LocalDateTime transDate;

    @Enumerated(EnumType.STRING)
    @Column
    private TransType transType; // 거래 유형 (DEPOSIT, WITHDRAW, AUTO_TRANSFER)

    @Column
    private Long transAmount;

    @Column
    private String inAccId;

    @Column
    private String inAccName;

    @Column
    private String outAccId;

    @Column
    private Long beforeInBal;

    @Column
    private Long afterInBal;

    @Column
    private Long beforeOutBal;

    @Column
    private Long afterOutBal;

    @Column
    private String hashtag;

    @Column
    private boolean isFx;

}
