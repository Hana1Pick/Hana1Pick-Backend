package com.hana.hana1pick.domain.moaclub.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class JoinMoaClubReqDto {

    private String accountId;
    private UUID userIdx;
    private String accPw;
}
