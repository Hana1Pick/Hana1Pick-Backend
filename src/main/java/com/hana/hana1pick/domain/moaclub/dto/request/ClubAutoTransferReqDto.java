package com.hana.hana1pick.domain.moaclub.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ClubAutoTransferReqDto {

    private String outAccId;
    private String inAccId; // 모아클럽 계좌번호
    private UUID userIdx;
}
