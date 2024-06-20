package com.hana.hana1pick.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
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
    private Long celebrity;

    @Column
    private String rankPredict;
}
