package com.hana.hana1pick.domain.celublog.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hana.hana1pick.domain.celebrity.entity.Celebrity;
import com.hana.hana1pick.domain.common.entity.Account;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
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
public class Celublog extends Account {

    @Id
    @Column
    private String accountId;

    @Column
    private String name;

    @Column
    private String imgSrc;

    @ManyToOne
    @JoinColumn(name = "out_acc_id")
    @JsonBackReference
    private Deposit outAcc;

    @ManyToOne
    @JoinColumn(name = "celebrity_idx")
    @JsonBackReference
    private Celebrity celebrity;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    @JsonBackReference
    private User user;
}
