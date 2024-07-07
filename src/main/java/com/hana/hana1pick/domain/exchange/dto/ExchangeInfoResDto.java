package com.hana.hana1pick.domain.exchange.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ExchangeInfoResDto {

    private String appliedExchangeRate;
    private String exchangeFee;
    private Long paymentAmount;
}
