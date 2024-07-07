package com.hana.hana1pick.domain.acchistory.repository;

import com.hana.hana1pick.domain.acchistory.entity.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccHistoryRepository extends JpaRepository<AccountHistory, Long> {

  @Query(value="SELECT * FROM account_history WHERE in_acc_id=:accountId OR out_acc_id= :accountId ORDER BY trans_date desc",  nativeQuery = true)
  List<AccountHistory> findByAccountId(@Param("accountId") String accountId);

  @Query("SELECT ah FROM AccountHistory ah " +
          "WHERE (ah.inAccId = :accountId and ah.isFx = :isFx) " +
          "OR (ah.outAccId = :accountId and ah.isFx = :isFx) " +
          "ORDER BY ah.transDate DESC")
  List<AccountHistory> findByAccCodeAndIsFx(@Param("accountId") String accountId, @Param("isFx") boolean isFx);

    @Query("select ah from AccountHistory ah " +
            "where ah.outAccId = :outAccId " +
            "and ah.inAccId = :inAccId " +
            "and ah.isFx = :isFx " +
            "and function('year', ah.transDate) = :year " +
            "and function('month', ah.transDate) = :month")
    List<AccountHistory> findClubFeeHistory(@Param("outAccId") String outAccId,
                                          @Param("inAccId") String inAccId,
                                          @Param("year") int year,
                                          @Param("month") int month,
                                          @Param("isFx") boolean isFx);

    @Query("SELECT DISTINCT ah.inAccId, ah.inAccName FROM AccountHistory ah WHERE ah.outAccId = :outAccId ORDER BY ah.transDate DESC LIMIT 4")
    List<Object[]> findDistinctInAccIdAndNameByOutAccIdOrderByTransDateDesc(String outAccId);

    @Query("SELECT DISTINCT ah.inAccId, ah.inAccName FROM AccountHistory ah WHERE ah.outAccId = :outAccId AND (ah.inAccId LIKE %:query% OR ah.inAccName LIKE %:query%) ORDER BY ah.transDate DESC")
    List<Object[]> findDistinctInAccIdAndNameByOutAccIdAndQueryOrderByTransDateDesc(String outAccId, String query);

  @Query("SELECT ah FROM AccountHistory ah " +
          "WHERE ah.transDate >= :startDate " +
          "AND (ah.inAccId = :accountId OR ah.outAccId = :accountId)")
    List<AccountHistory> findRecentHistoryForAccount(LocalDateTime startDate, String accountId);
}