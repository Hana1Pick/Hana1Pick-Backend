package com.hana.hana1pick.domain.acchistory.repository;

import com.hana.hana1pick.domain.acchistory.entity.AccountHistory;
import com.hana.hana1pick.domain.common.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccHistoryRepository extends JpaRepository<AccountHistory, Long> {
  @Query("SELECT ah FROM AccountHistory ah " +
          "WHERE (ah.inAccId = :accountId) " +
          "OR (ah.outAccId = :accountId) " +
          "ORDER BY ah.transDate DESC")
  List<AccountHistory> findByAccCode(@Param("accountId") String accountId);
}