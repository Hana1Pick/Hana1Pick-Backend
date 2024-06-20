package com.hana.hana1pick.domain.celublog.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Rules {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_idx")
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @JsonBackReference
    private Celublog celublog;

    @Column
    private String ruleName;

    @Column
    private Long ruleMoney;
}
