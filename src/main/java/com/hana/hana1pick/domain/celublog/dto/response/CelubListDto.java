package com.hana.hana1pick.domain.celublog.dto.response;

import com.hana.hana1pick.domain.celebrity.entity.CelubType;
import com.hana.hana1pick.domain.moaclub.dto.response.ClubResDto;
import com.hana.hana1pick.domain.moaclub.entity.MoaClub;
import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class CelubListDto {
    private CelubType type;
    private long idx;
    private String name;
    private String thumbnail;
//    of이용해서 dto build 해놓기
    public static CelubListDto of(CelubType type, long idx, String name, String thumbnail) {
        return CelubListDto.builder()
                .type(type)
                .idx(idx)
                .name(name)
                .thumbnail(thumbnail)
                .build();
    }
}