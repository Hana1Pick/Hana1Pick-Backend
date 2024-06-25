package com.hana.hana1pick.domain.user.repository;

import com.hana.hana1pick.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("select password from User where idx = :userIdx")
    Optional<String> findPasswordByUserIdx(@Param("userIdx") UUID userIdx);
}
