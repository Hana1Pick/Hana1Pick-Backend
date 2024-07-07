package com.hana.hana1pick.domain.celublog.repository;

import com.hana.hana1pick.domain.celublog.entity.Celublog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface CelublogRepository extends JpaRepository<Celublog, String> {
    //Account findByAccountId(String accId);
    List<Celublog> findByUserIdx(UUID userIdx);
    Celublog findByAccountId(String accountId);

    // 개설하지 않은 연예인 인덱스 리스트
    @Query(value="select celebrity_idx from celebrity where celebrity_idx not in (select celebrity_idx from celublog where user_idx=:userIdx)", nativeQuery = true)
    List<Long> findClubNumByUserIdx(UUID userIdx);
    @Modifying
    @Transactional
    @Query(value="update celublog set name=:name where account_id=:accountId", nativeQuery = true)
    int updateName(String accountId, String name);
    @Modifying
    @Transactional
    @Query(value="update celublog set img_src=:imgSrc where account_id=:accountId", nativeQuery = true)
    int updateImgSrc(String accountId, String imgSrc);
}
