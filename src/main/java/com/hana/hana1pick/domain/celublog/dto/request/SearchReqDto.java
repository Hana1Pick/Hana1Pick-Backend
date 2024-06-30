package com.hana.hana1pick.domain.celublog.dto.request;

import com.hana.hana1pick.domain.celebrity.entity.CelubType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchReqDto {
    private UUID userIdx;
    private CelubType type;
    private String name;
}
