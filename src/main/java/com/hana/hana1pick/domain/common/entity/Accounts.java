package com.hana.hana1pick.domain.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
public class Accounts {
    @Id
    private UUID userIdx;
    private String accountId;
    private String accountType;
    private Integer accountStatus;
}
