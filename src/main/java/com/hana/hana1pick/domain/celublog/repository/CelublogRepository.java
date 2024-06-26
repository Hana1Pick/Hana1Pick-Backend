package com.hana.hana1pick.domain.celublog.repository;

import com.hana.hana1pick.domain.celublog.entity.Celublog;
import com.hana.hana1pick.domain.common.entity.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CelublogRepository extends JpaRepository<Celublog, String> {
    Account findByAccountId(String accId);
}