package com.hana.hana1pick.domain.common.repository;

import com.hana.hana1pick.domain.common.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, String> {
  Optional<Accounts> findByAccountId(String accountId);
}
