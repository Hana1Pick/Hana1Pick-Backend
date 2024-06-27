package com.hana.hana1pick.domain.moaclub.dto.request;

import lombok.Getter;

import java.time.YearMonth;

@Getter
public class ClubFeeStatusReqDto {

    private String accountId;
    private YearMonth checkDate;
}
