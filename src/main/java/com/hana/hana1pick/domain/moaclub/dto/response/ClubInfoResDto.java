package com.hana.hana1pick.domain.moaclub.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ClubInfoResDto {

    private String managerName;
    private String moaclubName;
}
