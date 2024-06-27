package com.hana.hana1pick.domain.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Immutable;

import java.util.UUID;

@Getter
@Entity
@Immutable
@ToString
public class Accounts {
    @Id
    private String accountId;
    private UUID userIdx;
    private String email;
    private String name;
    private String accountType;
    private Integer accountStatus;
    private Long accountBalance;
}
