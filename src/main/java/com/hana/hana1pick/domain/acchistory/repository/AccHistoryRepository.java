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

  @Query("select ah from AccountHistory ah " +
          "where ah.outAccId = :outAccId " +
          "and ah.inAccId = :inAccId " +
          "and function('year', ah.transDate) = :year " +
          "and function('month', ah.transDate) = :month")
  List<AccountHistory> findClubFeeHistory(@Param("outAccId") String outAccId,
                                          @Param("inAccId") String inAccId,
                                          @Param("year") int year,
                                          @Param("month") int month);

  @Query("SELECT DISTINCT ah.inAccId, ah.inAccName FROM AccountHistory ah WHERE ah.outAccId = :outAccId ORDER BY ah.transDate DESC LIMIT 4")
  List<Object[]> findDistinctInAccIdAndNameByOutAccIdOrderByTransDateDesc(String outAccId);

  @Query("SELECT DISTINCT ah.inAccId, ah.inAccName FROM AccountHistory ah WHERE ah.outAccId = :outAccId AND (ah.inAccId LIKE %:query% OR ah.inAccName LIKE %:query%) ORDER BY ah.transDate DESC")
  List<Object[]> findDistinctInAccIdAndNameByOutAccIdAndQueryOrderByTransDateDesc(String outAccId, String query);
}