package com.hana.hana1pick.domain.moaclub.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hana.hana1pick.domain.common.entity.Account;
import com.hana.hana1pick.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
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

    @OneToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @Column
    private String name;

    @Column
    private Long clubFee;

    @Column
    private LocalDateTime atDate;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final List<MoaClubMembers> clubMemberList = new ArrayList<>();
}
