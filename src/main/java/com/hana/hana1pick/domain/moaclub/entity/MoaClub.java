package com.hana.hana1pick.domain.moaclub.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hana.hana1pick.domain.common.entity.Account;
import com.hana.hana1pick.domain.common.entity.AccountStatus;
import com.hana.hana1pick.domain.moaclub.dto.request.ClubUpdateReqDto;
import com.hana.hana1pick.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hana.hana1pick.domain.moaclub.entity.MoaClubStatus.*;

@Entity
@Table(name = "moaclub")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class MoaClub extends Account {

    @Id
    @Column
    private String accountId;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @Column
    private String name;

    @Column
    private Long clubFee;

    @Column
    private int atDate;

    @ElementCollection
    @CollectionTable(name = "moaclub_invitee", joinColumns = @JoinColumn(name = "account_id"))
    @MapKeyColumn(name = "name")
    @Column(name = "status")
    private Map<String, MoaClubStatus> inviteeList;

    @Column
    @NotNull
    private Currency currency;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final List<MoaClubMembers> clubMemberList = new ArrayList<>();

    @Builder
    public MoaClub(String accPw, Long balance, AccountStatus status,
                   String accountId, User user, String name, Long clubFee, int atDate, Currency currency) {
        super(accPw, balance, status);
        this.accountId = accountId;
        this.user = user;
        this.name = name;
        this.clubFee = clubFee;
        this.atDate = atDate;
        this.currency = currency;
        this.inviteeList = new HashMap<>();
    }

    public void invite(List<String> invitees) {
        for (String name : invitees) {
            this.inviteeList.put(name, PENDING);
        }
    }

    public MoaClub update(ClubUpdateReqDto request) {
        this.name = name.equals(request.getName()) || request.getName() == null ? name : request.getName();
        this.clubFee = request.getClubFee();
        this.atDate = request.getAtDate();

        return this;
    }
}
