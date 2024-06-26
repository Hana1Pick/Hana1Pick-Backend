package com.hana.hana1pick.domain.moaclub.dto.request;

import com.hana.hana1pick.domain.moaclub.entity.Currency;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ClubOpeningReqDto {

    private String accountId;
    private UUID userIdx;
    private String name;
    private Long clubFee;
    private int atDate;
    private Currency currency;
}
