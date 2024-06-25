package com.hana.hana1pick.domain.user.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PwCheckReqDto {

    private UUID userIdx;
    private String password;
}
