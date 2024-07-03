package com.hana.hana1pick.domain.moaclub.dto.response;

import com.hana.hana1pick.domain.autotranfer.entity.AutoTransfer;
import com.hana.hana1pick.domain.moaclub.entity.Currency;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class ClubAutoTransferResDto {
    private int atDate;
    private Long amount;
    private String inAccId;
    private String outAccId;
    private Currency currency;
    private LocalDate createDate;

    public static ClubAutoTransferResDto from(AutoTransfer autoTransfer) {
        return ClubAutoTransferResDto.builder()
                .atDate(autoTransfer.getId().getAtDate())
                .amount(autoTransfer.getAmount())
                .inAccId(autoTransfer.getId().getInAccId())
                .outAccId(autoTransfer.getId().getOutAccId())
                .currency(autoTransfer.getCurrency())
                .createDate(autoTransfer.getCreateDate())
                .build();
    }
}
