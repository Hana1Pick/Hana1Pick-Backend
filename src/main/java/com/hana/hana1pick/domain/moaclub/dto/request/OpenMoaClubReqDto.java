package com.hana.hana1pick.domain.moaclub.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class OpenMoaClubReqDto {

    private String accountId;
    private UUID userIdx;
    private String accPw;
    private String name;
    private Long clubFee;
    private int atDate;
}
