package com.hana.hana1pick.domain.moaclub.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hana.hana1pick.domain.common.entity.Account;
import com.hana.hana1pick.domain.common.entity.AccountStatus;
import com.hana.hana1pick.domain.user.entity.User;
import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @Column
    private String name;

    @Column
    private Long clubFee;

    @Column
    private int atDate;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final List<MoaClubMembers> clubMemberList = new ArrayList<>();

    @Builder
    public MoaClub(String accPw, Long balance, AccountStatus status,
                   String accountId, User user, String name, Long clubFee, int atDate) {
        super(accPw, balance, status);
        this.accountId = accountId;
        this.user = user;
        this.name = name;
        this.clubFee = clubFee;
        this.atDate = atDate;
    }
}
