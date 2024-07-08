package com.hana.hana1pick.domain.celublog.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class AccOutReqDto {
    private UUID userIdx;
    private String outAccId; //셀럽로그 계좌
    private String inAccId; //입출금 계좌
    private String memo; //규칙
    private long amount;
}
