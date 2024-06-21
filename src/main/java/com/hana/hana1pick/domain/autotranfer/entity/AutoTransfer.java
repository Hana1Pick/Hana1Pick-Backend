package com.hana.hana1pick.domain.autotranfer.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class AutoTransfer {

    @EmbeddedId
    private AutoTransferId id;

    @Column
    @NotNull
    private Long amount;

    @ManyToOne
    @MapsId("outAccId")
    @JoinColumn(name = "out_acc_id")
    @JsonManagedReference
    private Deposit deposit;
}