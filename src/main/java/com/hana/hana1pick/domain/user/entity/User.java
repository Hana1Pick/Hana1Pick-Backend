package com.hana.hana1pick.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hana.hana1pick.domain.celublog.entity.Celublog;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
import com.hana.hana1pick.domain.moaclub.entity.MoaClubMembers;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)", name = "user_idx")
    private UUID idx;

    @Column
    @NotNull
    private String email;

    @Column
    @NotNull
    private String name;

    @Column
    @NotNull
    private UserNation nation;

    @Column
    @NotNull
    private LocalDate birth;

    @Column
    @NotNull
    private String phone;

    @Column
    @NotNull
    private String address;

    @Column
    private String profile;

    @Column
    @NotNull
    private String password;

    @OneToOne(mappedBy = "user")
    private UserTrsfLimit userTrsfLimit;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Deposit deposit;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Celublog> celublogList = new ArrayList<>();

    // 내가 참여하고 있는 모아클럽 리스트
    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private final List<MoaClubMembers> clubList = new ArrayList<>();
}
