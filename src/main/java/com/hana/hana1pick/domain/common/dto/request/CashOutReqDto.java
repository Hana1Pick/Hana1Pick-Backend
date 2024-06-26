package com.hana.hana1pick.domain.common.dto.request;

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

    public static CashOutReqDto of(String outAccId, String inAccId, Long amount) {
        return CashOutReqDto.builder()
                .outAccId(outAccId)
                .inAccId(inAccId)
                .amount(amount)
                .build();
    }
}
