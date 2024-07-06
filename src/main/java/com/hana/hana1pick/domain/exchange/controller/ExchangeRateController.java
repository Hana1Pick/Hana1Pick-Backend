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

    //   getExchangeRate(Currency currency)
//    @GetMapping("/current")
//    public Map<String, Double> getExchangeRate() {
//        return exchangeRateService.getExchangeRate();
//    }

    @GetMapping("/previous-day")
    public Map<String, Double> getPreviousDayRates() {
        LocalDate previousDay = LocalDate.now().minusDays(1);
        Map<String, Double> rates = exchangeRateService.getRatesForDate(previousDay);
        if (rates == null || rates.isEmpty()) {
            // Handle case where no rates are found for the previous day
            Map<String, Double> defaultRates = new HashMap<>();
            defaultRates.put("USD", 0.0);
            defaultRates.put("JPY", 0.0);
            defaultRates.put("CNY", 0.0);
            return defaultRates;
        }
        return rates;
    }

    @GetMapping("/date")
    public Map<String, Double> getRatesForDate(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return exchangeRateService.getRatesForDate(localDate);
    }
}
