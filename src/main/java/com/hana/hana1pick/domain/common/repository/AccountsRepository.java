package com.hana.hana1pick.domain.common.repository;

import com.hana.hana1pick.domain.common.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, String> {
  @Query(value = "SELECT * FROM accounts WHERE user_idx = :userIdx AND account_id != :outAccId", nativeQuery = true)
  List<Accounts> findByUserIdxAndNotOutAccId(@Param("userIdx") UUID userIdx, @Param("outAccId") String outAccId);

  Accounts findAccountsByAccountId(String accountId);

  // 사용자 id로 전체 계좌 조회
  List<Accounts> findByUserIdx(UUID userIdx);

}
