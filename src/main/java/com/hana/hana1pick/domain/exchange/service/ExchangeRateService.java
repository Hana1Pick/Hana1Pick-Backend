package com.hana.hana1pick.domain.exchange.service;

import com.hana.hana1pick.domain.exchange.entity.ExchangeRate;
import com.hana.hana1pick.domain.exchange.repository.ExchangeRateRepository;
import com.hana.hana1pick.domain.moaclub.entity.Currency;
import com.hana.hana1pick.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiUrl = "https://api.exchangerate-api.com/v4/latest/KRW";

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    public Map<String, Double> getCurrentRatesAsDoubles() {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl).toUriString();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        // 로그 추가
        System.out.println("API Response: " + response);

        Map<String, Double> rates = new HashMap<>();
        if (response != null && response.get("rates") instanceof Map) {
            Map<String, Object> ratesObject = (Map<String, Object>) response.get("rates");
            for (Map.Entry<String, Object> entry : ratesObject.entrySet()) {
                try {
                    rates.put(entry.getKey(), ((Number) entry.getValue()).doubleValue());
                } catch (Exception e) {
                    System.err.println("Error parsing rate for currency: " + entry.getKey());
                    e.printStackTrace();
                }
            }
        }
        return rates;
    }

    public double getExchangeRate(Currency currency) {
        Map<String, Double> rates = getCurrentRatesAsDoubles();
        return rates.get(currency.name());
    }

    public Map<String, Double> getRatesForDate(LocalDate date) {
        List<ExchangeRate> exchangeRates = exchangeRateRepository.findByDate(date);
        if (exchangeRates.isEmpty()) {
            return null;
        }
        Map<String, Double> rates = new HashMap<>();
        for (ExchangeRate exchangeRate : exchangeRates) {
            rates.put(exchangeRate.getCurrency(), exchangeRate.getRate());
        }
        return rates;
    }

    public void saveRates(Map<String, Double> rates) {
        LocalDate today = LocalDate.now();
        for (Map.Entry<String, Double> entry : rates.entrySet()) {
            Optional<ExchangeRate> existingRate = exchangeRateRepository.findByCurrencyAndDate(entry.getKey(), today);
            if (existingRate.isEmpty()) {
                ExchangeRate exchangeRate = ExchangeRate.builder()
                        .currency(entry.getKey())
                        .rate(entry.getValue().doubleValue())
                        .date(today)
                        .build();
                exchangeRateRepository.save(exchangeRate);
            }
        }
    }
}