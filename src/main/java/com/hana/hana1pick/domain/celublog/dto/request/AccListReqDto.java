package com.hana.hana1pick.domain.celublog.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class AccListReqDto {
    private UUID userIdx;
}
