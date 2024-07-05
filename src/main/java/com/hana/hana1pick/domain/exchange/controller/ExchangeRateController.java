package com.hana.hana1pick.domain.exchange.controller;

import com.hana.hana1pick.domain.exchange.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange-rates")
public class ExchangeRateController {
    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping("/current")
    public Map<String, Double> getExchangeRate() {
        return exchangeRateService.getCurrentRatesAsDoubles();
    }

    @GetMapping("/previous-day")
    public Map<String, Double> getPreviousDayRates() {
        return exchangeRateService.getPreviousDayRates();
    }

    @GetMapping("/date")
    public Map<String, Double> getRatesForDate(@RequestParam String date) {
        return exchangeRateService.getRatesForDate(date);
    }
}
