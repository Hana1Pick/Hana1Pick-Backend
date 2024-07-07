package com.hana.hana1pick.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hana.hana1pick.domain.celublog.entity.Celublog;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
import com.hana.hana1pick.domain.moaclub.dto.request.ClubUpdateReqDto;
import com.hana.hana1pick.domain.moaclub.entity.MoaClub;
import com.hana.hana1pick.domain.moaclub.entity.MoaClubMembers;
import com.hana.hana1pick.domain.user.dto.request.UserUpdateReqDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
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
    private String name;

    @Column
    private UserNation nation;

    @Column
    private LocalDate birth;

    @Column
    private String phone;

    @Column
    private String address;

    @Column
    private String profile;

    @Column
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

    // 이메일과 프로필만 설정하는 생성자 추가
    public User(String email, String profile) {
        this.email = email;
        this.profile = profile;
    }
    // 이메일 업데이트 메서드
    public void updateEmail(String email) {
        this.email = email;
    }

    // 프로필 사진 업데이트 메서드
    public void updateProfile(String profile) {
        this.profile = profile;
    }

    public User updateUserInfo(UserUpdateReqDto request, String encodedPassword) {
        this.name = request.getName();
        this.birth = request.getBirth();
        this.nation = request.getNation();
        this.phone = request.getPhone();
        this.address = request.getAddress();
        this.password = encodedPassword; // 암호화된 비밀번호를 설정

        return this;
    }
}
