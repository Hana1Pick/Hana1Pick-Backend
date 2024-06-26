package com.hana.hana1pick.domain.common.dto.request;

import com.hana.hana1pick.domain.acchistory.entity.TransType;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class CashOutReqDto {
    private String outAccId;
    private String inAccId;
    private String memo;
    private String hashtag = null;
    private Long amount;
    private TransType transType;

    public static CashOutReqDto of(String outAccId, String inAccId, Long amount, TransType type) {
        return CashOutReqDto.builder()
                .outAccId(outAccId)
                .inAccId(inAccId)
                .amount(amount)
                .transType(type)
                .build();
    }
}
