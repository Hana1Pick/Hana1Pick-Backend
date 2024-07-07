package com.hana.hana1pick.domain.deposit.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hana.hana1pick.domain.autotranfer.entity.AutoTransfer;
import com.hana.hana1pick.domain.celublog.entity.Celublog;
import com.hana.hana1pick.domain.common.entity.Account;
import com.hana.hana1pick.domain.common.entity.AccountStatus;
import com.hana.hana1pick.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

    @OneToMany(mappedBy = "outAcc")
    @JsonManagedReference
    private List<AutoTransfer> autoTransferList = new ArrayList<>();

    @OneToMany(mappedBy = "outAcc")
    @JsonManagedReference
    private List<Celublog> celublogList = new ArrayList<>();

    @Builder
    public Deposit(Long balance, AccountStatus status, String accountId, String name, LocalDate createDate, User user) {
        super(balance, status, name, createDate);
        this.accountId = accountId;
        this.user = user;
    }
}
