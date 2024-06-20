package com.hana.hana1pick.domain.deposit.entity;

import com.hana.hana1pick.domain.common.entity.Account;
import com.hana.hana1pick.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Deposit extends Account {

    @Id
    @Column
    private String accountId;

    @OneToOne
    @JoinColumn(name = "user_idx")
    private User user;
}
