package com.hana.hana1pick.domain.common.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class AccountForCashOutReqDto {
    private UUID userIdx;
    private String outAccId;
}
