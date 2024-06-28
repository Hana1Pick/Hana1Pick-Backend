package com.hana.hana1pick.domain.celebrity.repository;

import com.hana.hana1pick.domain.celebrity.entity.Celebrity;
import com.hana.hana1pick.domain.celebrity.entity.CelubType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CelebrityRepository extends JpaRepository<Celebrity, Long> {
    @Query(value="select * from celebrity where celebrity_idx not in (select celebrity_idx from celublog where user_idx= :userIdx) and type=:type and name like concat('%',:name,'%')", nativeQuery = true)
    List<Celebrity> findByKeyword(@Param("userIdx") UUID userIdx, @Param("type") CelubType type, @Param("name") String name);
}
