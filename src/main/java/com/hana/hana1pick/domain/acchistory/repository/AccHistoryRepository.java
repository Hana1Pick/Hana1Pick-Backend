package com.hana.hana1pick.domain.acchistory.repository;

import com.hana.hana1pick.domain.acchistory.entity.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccHistoryRepository extends JpaRepository<AccountHistory, Long> {
  @Query("SELECT ah FROM AccountHistory ah " +
          "WHERE (ah.inAccId = :accountId) " +
          "OR (ah.outAccId = :accountId) " +
          "ORDER BY ah.transDate DESC")
  List<AccountHistory> findByAccCode(@Param("accountId") String accountId);

  @Query(value="SELECT * FROM account_history WHERE in_acc_id=:accountId OR out_acc_id= :accountId ORDER BY trans_date desc",  nativeQuery = true)
  List<AccountHistory> findByAccountId(@Param("accountId") String accountId);
}