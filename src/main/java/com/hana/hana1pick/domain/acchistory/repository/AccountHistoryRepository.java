package com.hana.hana1pick.domain.acchistory.repository;

import com.hana.hana1pick.domain.acchistory.entity.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountHistoryRepository extends JpaRepository<AccountHistory, Long> {
    @Query("SELECT DISTINCT ah.inAccId, ah.inAccName FROM AccountHistory ah WHERE ah.outAccId = :outAccId ORDER BY ah.transDate DESC LIMIT 4")
    List<Object[]> findDistinctInAccIdAndNameByOutAccIdOrderByTransDateDesc(String outAccId);

    @Query("SELECT DISTINCT ah.inAccId, ah.inAccName FROM AccountHistory ah WHERE ah.outAccId = :outAccId AND (ah.inAccId LIKE %:query% OR ah.inAccName LIKE %:query%) ORDER BY ah.transDate DESC")
    List<Object[]> findDistinctInAccIdAndNameByOutAccIdAndQueryOrderByTransDateDesc(String outAccId, String query);
}
