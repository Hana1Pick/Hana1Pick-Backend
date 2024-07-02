package com.hana.hana1pick.domain.autotranfer.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
import com.hana.hana1pick.domain.moaclub.entity.Currency;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class AutoTransfer {

    @EmbeddedId
    private AutoTransferId id;

    @Column
    @NotNull
    private Long amount;

    @Column
    private Currency currency;

    @ManyToOne
    @MapsId("outAccId")
    @JoinColumn(name = "out_acc_id")
    @JsonManagedReference
    private Deposit outAcc;

    public AutoTransfer updateAutoTransfer(int atDate, Long amount) {
        this.id.updateAtDate(atDate);
        this.amount = amount;

        return this;
    }
}
