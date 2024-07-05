package com.hana.hana1pick.domain.exchange.repository;
import com.hana.hana1pick.domain.exchange.entity.ExchangeFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeFeeRepository extends JpaRepository<ExchangeFee, Long> {
    ExchangeFee findByCurrency(String currency);
}
