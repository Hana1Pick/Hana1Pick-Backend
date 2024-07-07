package com.hana.hana1pick.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserTrsfLimit {

    @Id
    @Column
    private UUID userIdx;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_idx")
    private User user;

    @Column
    @ColumnDefault("0")
    @Builder.Default()
    private Long dailyAmount = 0L;

    @Column
    @ColumnDefault("500000")
    @Builder.Default()
    private Long transferLimit = 500000L;

    public void accumulate(Long amount) {
        dailyAmount += amount;
    }

    public void resetDailyAccAmount() {
        dailyAmount = 0L;
    }
}