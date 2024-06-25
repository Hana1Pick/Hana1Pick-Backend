package com.hana.hana1pick.domain.acchistory.repository;

import com.hana.hana1pick.domain.acchistory.entity.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccHisRepository extends JpaRepository<AccountHistory, Long> {

    @Query("select ah from AccountHistory ah " +
           "where ah.outAccId = :outAccId " +
           "and ah.inAccId = :inAccId " +
           "and function('year', ah.transDate) = :year " +
           "and function('month', ah.transDate) = :month")
    List<AccountHistory> findClubFeeHistory(@Param("outAccId") String outAccId,
                                            @Param("inAccId") String inAccId,
                                            @Param("year") int year,
                                            @Param("month") int month);
}
