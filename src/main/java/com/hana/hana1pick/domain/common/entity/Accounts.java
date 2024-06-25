package com.hana.hana1pick.domain.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

@Getter
@Entity
@Immutable
public class Accounts {
    private UUID userIdx;
    private String email;
    @Id
    private String accountId;
    private String accountType;
    private Integer accountStatus;
}
