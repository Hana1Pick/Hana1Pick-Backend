package com.hana.hana1pick.domain.autotranfer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Builder
@Getter
public class AutoTransferId implements Serializable {

    @Column
    private int atDate;

    @Column
    private String inAccId;

    @Column
    private String outAccId;
}