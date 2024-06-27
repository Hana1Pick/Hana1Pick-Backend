package com.hana.hana1pick.domain.moaclub.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ClubMemberLeaveReqDto {

    private String accountId;
    private UUID userIdx;
}
