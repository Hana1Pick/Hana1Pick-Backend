package com.hana.hana1pick.domain.moaclub.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ClubFeeStatusResDto {
    private String name;
    private Long amount;
    private ClubFeeStatus status;

    public enum ClubFeeStatus {
        PAID, UNPAID;
    }
}
