package com.hana.hana1pick.domain.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Account {

    @Column
    @NotNull
    private String accPw;

    @Column
    private Long balance;

    @Column
    @CreatedDate
    private LocalDate createDate;

    @Column
    private AccountStatus status;
}
