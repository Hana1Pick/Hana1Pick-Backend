package com.hana.hana1pick.domain.exchange.repository;

import com.hana.hana1pick.domain.exchange.entity.ExchangeRate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Table(name = "exchange_rate", uniqueConstraints = {@UniqueConstraint(columnNames = {"currency", "date"})})
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    List<ExchangeRate> findByDate(LocalDate date);
    @Query("SELECT er FROM ExchangeRate er WHERE er.currency = :currency AND er.date = :date")
    Optional<ExchangeRate> findByCurrencyAndDate(@Param("currency") String currency, @Param("date") LocalDate date);
}