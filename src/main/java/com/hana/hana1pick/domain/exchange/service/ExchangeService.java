package com.hana.hana1pick.domain.exchange.service;

import com.hana.hana1pick.domain.exchange.dto.ExchangeInfoResDto;
import com.hana.hana1pick.domain.exchange.entity.ExchangeFee;
import com.hana.hana1pick.domain.moaclub.entity.Currency;
import com.hana.hana1pick.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.hana.hana1pick.domain.moaclub.entity.Currency.JPY;
import static com.hana.hana1pick.global.exception.BaseResponse.success;
import static com.hana.hana1pick.global.exception.BaseResponseStatus.EXCHANGE_INFO_FETCH_SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeService {

    private final ExchangeRateService exchangeRateService;
    private final ExchangeFeeService exchangeFeeService;

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

        Double exchangeAmount = amount * exchangeRate;
        Double feeAmount = exchangeAmount * getFeeRate(toCurrency);

        return Math.round(exchangeAmount + feeAmount);
    }

    public BaseResponse.SuccessResult<ExchangeInfoResDto> getExchangeInfo(Currency currency, Long amount) {
        // 적용 환율: 외화가 1일때 KRW 값
        Double fxRate = getCurrentRatesAsDoubles(currency.name());
        Double appliedExchangeRate = 1 / fxRate;
        String appliedExchangeRateValue = String.format("%.2f", appliedExchangeRate);

        // 환전 수수료
        Double feeRate = getFeeRate(currency.name());
        double exchangeFee = amount * appliedExchangeRate * feeRate;
        String appliedExchangeFeeValue = String.format("%.2f", exchangeFee);

        // 결제금액
        Long paymentAmount = calculateExchangeAmount(currency.name(), amount);

        return success(EXCHANGE_INFO_FETCH_SUCCESS, new ExchangeInfoResDto(appliedExchangeRateValue, appliedExchangeFeeValue, paymentAmount));
    }

    private Double getCurrentRatesAsDoubles(String currency) {
        Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
        Map<String, Double> rates = new HashMap<>();
        for (Map.Entry<String, Object> entry : ((Map<String, Object>) response.get("rates")).entrySet()) {
            rates.put(entry.getKey(), ((Number) entry.getValue()).doubleValue());
        }
        return rates.get(currency);
    }

    private Double getFeeRate(String currency) {
        ExchangeFee fee = exchangeFeeService.getFeeByCurrency(currency);
        return (fee != null) ? fee.getFeeRate() : 0.0;
    }
}
