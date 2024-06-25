package com.hana.hana1pick.domain.common.repository;

import com.hana.hana1pick.domain.common.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, UUID> {
  @Query("SELECT a FROM Accounts a WHERE a.userIdx = :userIdx AND a.accountId = :accountId")

  Optional<Accounts> findByUserIdxAndAccountId(@Param("userIdx") UUID userIdx, @Param("accountId") String accountId);

  Optional<Object> findByAccountId(String accountId);
}
