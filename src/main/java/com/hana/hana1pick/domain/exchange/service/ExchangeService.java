package com.hana.hana1pick.domain.exchange.service;

import com.hana.hana1pick.domain.exchange.entity.ExchangeFee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeService {
    @Autowired
    private ExchangeRateService exchangeRateService;

    @Autowired
    private ExchangeFeeService exchangeFeeService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiUrl = "https://api.exchangerate-api.com/v4/latest/KRW";

    //  환율 테이블의 수수료를 가져오고 외부 환율 API를 호출해서 환전하기
    public Long calculateExchangeAmount(String toCurrency, Long amount) {
        Map<String, Double> currentRates = exchangeRateService.getCurrentRatesAsDoubles();

        Double rate = currentRates.get(toCurrency);
        if (rate == null || rate == 0) {
            throw new IllegalArgumentException("Invalid exchange rate for currency: " + toCurrency);
        }

        // 환율: (1/당일 환율) 로 계산
        // by zero 오류를 방지하기 위해 0으로 나누는 것을 방지=> rate: double로 형변환
        Double exchangeRate = 1 / rate;

        ExchangeFee fee = exchangeFeeService.getFeeByCurrency(toCurrency);
        Double feeRate = (fee != null) ? fee.getFeeRate() : 0.0;

        Double exchangeAmount = amount * exchangeRate;
        Double feeAmount = exchangeAmount * feeRate;

        return Math.round(exchangeAmount - feeAmount);
    }

    private Map<String, Double> getCurrentRatesAsDoubles() {
        Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
        Map<String, Double> rates = new HashMap<>();
        for (Map.Entry<String, Object> entry : ((Map<String, Object>) response.get("rates")).entrySet()) {
            rates.put(entry.getKey(), ((Number) entry.getValue()).doubleValue());
        }
        return rates;
    }
}
