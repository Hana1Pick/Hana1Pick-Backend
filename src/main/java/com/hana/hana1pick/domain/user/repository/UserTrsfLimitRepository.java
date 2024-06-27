package com.hana.hana1pick.domain.user.repository;

import com.hana.hana1pick.domain.user.entity.UserTrsfLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserTrsfLimitRepository extends JpaRepository<UserTrsfLimit, UUID> {

    // 일일 이체한도 변경
    @Modifying(clearAutomatically = true) // bulk 연산 실행 후 1차 cache를 비워준다
    // 쿼리 수행 후 1차 cache와 DB의 동기화를 위해 추가
    @Query("update UserTrsfLimit utl set utl.transferLimit = :transferLimit where utl.userIdx = :userIdx")
    void updateDailyLimit(UUID userIdx, Long transferLimit);
}
