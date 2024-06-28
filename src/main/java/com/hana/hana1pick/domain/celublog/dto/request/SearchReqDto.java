package com.hana.hana1pick.domain.celublog.dto.request;

import com.hana.hana1pick.domain.celebrity.entity.CelubType;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class SearchReqDto {
    private UUID userIdx;
    private CelubType type;
    private String name;
}
