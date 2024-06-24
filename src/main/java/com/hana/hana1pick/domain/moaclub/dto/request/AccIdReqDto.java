package com.hana.hana1pick.domain.moaclub.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class AccIdReqDto {

    private String accountId;
    private UUID userIdx;
}
