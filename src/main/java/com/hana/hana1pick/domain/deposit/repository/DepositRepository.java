package com.hana.hana1pick.domain.deposit.repository;

import com.hana.hana1pick.domain.common.entity.Account;
import com.hana.hana1pick.domain.deposit.entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, String> {
    Account findByAccountId(String accId);
}
