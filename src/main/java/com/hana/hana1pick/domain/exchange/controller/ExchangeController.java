package com.hana.hana1pick.domain.exchange.controller;

import com.hana.hana1pick.domain.exchange.dto.ExchangeInfoResDto;
import com.hana.hana1pick.domain.exchange.entity.ExchangeFee;
import com.hana.hana1pick.domain.exchange.repository.ExchangeFeeRepository;
import com.hana.hana1pick.domain.exchange.service.ExchangeFeeService;
import com.hana.hana1pick.domain.exchange.service.ExchangeService;
import com.hana.hana1pick.domain.moaclub.entity.Currency;
import com.hana.hana1pick.global.exception.BaseResponse;
import com.hana.hana1pick.global.exception.BaseResponse.SuccessResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exchange")
public class ExchangeController {
    @Autowired
    private ExchangeService exchangeService;
    @Autowired
    private ExchangeFeeService exchangeFeeService;

    // 환전하기
    @GetMapping("/calculate")
    public double calculateExchange(
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency,
            @RequestParam Long amount) {
        return exchangeService.calculateExchangeAmount(toCurrency, amount);
    }

    @PostMapping("/fees")
    public void saveFee(@RequestBody ExchangeFee fee) {
        exchangeFeeService.saveFee(fee);
    }

    @Operation(summary = "환전 정보 반환")
    @GetMapping("/fee-info")
    public SuccessResult<ExchangeInfoResDto> getExchangeInfo(@RequestParam(name = "currency") Currency currency, @RequestParam(name = "amount") Long amount) {
        return exchangeService.getExchangeInfo(currency, amount);
    }
}
