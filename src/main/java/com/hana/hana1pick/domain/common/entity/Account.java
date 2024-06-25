package com.hana.hana1pick.domain.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
public class Account {

    @Column
    private Long balance;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate createDate;

    @Column
    private AccountStatus status;

    public Account(Long balance, AccountStatus status) {
        this.balance = balance;
        this.status = status;
    }
}
