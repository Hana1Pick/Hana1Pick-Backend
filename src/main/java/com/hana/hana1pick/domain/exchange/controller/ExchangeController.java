package com.hana.hana1pick.domain.exchange.controller;

import com.hana.hana1pick.domain.exchange.entity.ExchangeFee;
import com.hana.hana1pick.domain.exchange.repository.ExchangeFeeRepository;
import com.hana.hana1pick.domain.exchange.service.ExchangeFeeService;
import com.hana.hana1pick.domain.exchange.service.ExchangeService;
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
}
