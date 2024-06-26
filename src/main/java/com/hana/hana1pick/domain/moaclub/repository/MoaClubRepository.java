package com.hana.hana1pick.domain.moaclub.repository;

import com.hana.hana1pick.domain.common.entity.Account;
import com.hana.hana1pick.domain.moaclub.entity.MoaClub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoaClubRepository extends JpaRepository<MoaClub, String> {
    Account findByAccountId(String accId);
}
