package com.hana.hana1pick.domain.common.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CashOutReqDto {
    private String outAccId;
    private String inAccId;
    private String memo;
    private String hashtag = null;
    private Long amount;
}
