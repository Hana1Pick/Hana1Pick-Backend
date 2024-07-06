package com.hana.hana1pick.domain.exchange.service;

import com.hana.hana1pick.domain.exchange.entity.ExchangeFee;
import com.hana.hana1pick.domain.exchange.repository.ExchangeFeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeFeeService {
    @Autowired
    private ExchangeFeeRepository repository;

    public ExchangeFee getFeeByCurrency(String currency) {
        return repository.findByCurrency(currency);
    }

    public void saveFee(ExchangeFee fee) {
        repository.save(fee);
    }
}
