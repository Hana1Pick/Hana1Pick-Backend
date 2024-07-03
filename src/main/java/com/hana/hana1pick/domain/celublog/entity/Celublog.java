package com.hana.hana1pick.domain.celublog.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hana.hana1pick.domain.celebrity.entity.Celebrity;
import com.hana.hana1pick.domain.common.entity.Account;
import com.hana.hana1pick.domain.common.entity.AccountStatus;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
import com.hana.hana1pick.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
//@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToMany(mappedBy = "celublog")
    @JsonManagedReference
    private List<Rules> ruleList = new ArrayList<>();
    @Builder
    public Celublog(String accountId, String name, String imgSrc, Deposit outAcc, Celebrity celebrity, User user, List<Rules> ruleList, Long balance) {
        super(balance, AccountStatus.ACTIVE);
        this.accountId = accountId;
        this.name = name;
        this.imgSrc = imgSrc;
        this.outAcc = outAcc;
        this.celebrity = celebrity;
        this.user = user;
        this.ruleList = ruleList;
    }
}
