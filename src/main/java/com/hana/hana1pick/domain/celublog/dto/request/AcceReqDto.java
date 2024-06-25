package com.hana.hana1pick.domain.celublog.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
public class AcceReqDto {
    private UUID userIdx;
    private String accPw;
    private String name;
    private String imgSrc;
    private String outAccId;
    private Long celebrityIdx;
}
