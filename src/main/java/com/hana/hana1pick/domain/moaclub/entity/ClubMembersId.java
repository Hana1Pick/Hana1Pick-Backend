package com.hana.hana1pick.domain.moaclub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ClubMembersId implements Serializable {

    @Column
    private String accountId;

    @Column
    private UUID userIdx;
}
