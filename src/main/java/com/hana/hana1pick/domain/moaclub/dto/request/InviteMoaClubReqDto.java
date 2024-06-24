package com.hana.hana1pick.domain.moaclub.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class InviteMoaClubReqDto {

    private String accountId;
    private List<String> inviteeList;
}
