package com.hana.hana1pick.domain.moaclub.entity;

import com.hana.hana1pick.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "moaclub_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MoaClubMembers {

    @EmbeddedId
    private ClubMembersId id;

    @MapsId("accountId")
    @ManyToOne
    @JoinColumn(name = "account_id")
    private MoaClub club;

    @MapsId("userIdx")
    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @Column
    @NotNull
    private String userName;

    @Column
    @NotNull
    private MoaClubMemberRole role;

    public void updateUserName(String userName) {
        this.userName = userName;
    }
}
