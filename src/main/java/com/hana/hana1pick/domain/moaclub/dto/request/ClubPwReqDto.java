package com.hana.hana1pick.domain.moaclub.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ClubPwReqDto {

    private String accountId;
    private UUID userIdx;
    private String accPw;
}
