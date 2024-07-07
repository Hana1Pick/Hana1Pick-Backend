package com.hana.hana1pick.domain.celublog.repository;

import com.hana.hana1pick.domain.celublog.entity.Rules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RulesRepository extends JpaRepository<Rules, Long> {
    @Transactional
    @Modifying
    @Query(value = "delete from rules where account_id=:accountId", nativeQuery = true)
    int deleteRules(String accountId);
}
