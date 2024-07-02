package com.hana.hana1pick.domain.moaclub.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hana.hana1pick.domain.common.entity.Account;
import com.hana.hana1pick.domain.common.entity.AccountStatus;
import com.hana.hana1pick.domain.moaclub.dto.request.ClubUpdateReqDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "moaclub")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class MoaClub extends Account {

    @Id
    @Column
    private String accountId;

    @Column
    private String name;

    @Column
    private Long clubFee;

    @Column
    private int atDate;

    @Column
    @NotNull
    private Currency currency;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final List<MoaClubMembers> clubMemberList = new ArrayList<>();

    @Builder
    public MoaClub(Long balance, AccountStatus status,
                   String accountId, String name, Long clubFee, int atDate, Currency currency) {
        super(balance, status);
        this.accountId = accountId;
        this.name = name;
        this.clubFee = clubFee;
        this.atDate = atDate;
        this.currency = currency;
    }

    public MoaClub update(ClubUpdateReqDto request) {
        this.name = name.equals(request.getName()) || request.getName() == null ? name : request.getName();
        this.clubFee = request.getClubFee();
        this.atDate = request.getAtDate();

        return this;
    }
}
