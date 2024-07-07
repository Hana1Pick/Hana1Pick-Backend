package com.hana.hana1pick.domain.common.dto.request;

import com.hana.hana1pick.domain.acchistory.entity.TransType;
import com.hana.hana1pick.domain.moaclub.entity.Currency;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class CashOutReqDto {
    private UUID userIdx;
    private String outAccId;
    private String inAccId;
    private String memo;
    private String hashtag = null;
    private Long amount;
    private TransType transType;
    private Currency currency;

    public static CashOutReqDto of(String outAccId, String inAccId, Long amount, TransType type, Currency currency) {
        return CashOutReqDto.builder()
                .outAccId(outAccId)
                .inAccId(inAccId)
                .amount(amount)
                .transType(type)
                .currency(currency)
                .build();
    }
}
