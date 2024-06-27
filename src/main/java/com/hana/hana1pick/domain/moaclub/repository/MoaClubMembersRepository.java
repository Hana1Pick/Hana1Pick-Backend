package com.hana.hana1pick.domain.moaclub.repository;

import com.hana.hana1pick.domain.moaclub.entity.ClubMembersId;
import com.hana.hana1pick.domain.moaclub.entity.MoaClub;
import com.hana.hana1pick.domain.moaclub.entity.MoaClubMemberRole;
import com.hana.hana1pick.domain.moaclub.entity.MoaClubMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MoaClubMembersRepository extends JpaRepository<MoaClubMembers, ClubMembersId> {

    @Query(value = "select count(*) from MoaClubMembers where club.accountId = :accountId and role = :role")
    int countMembersByClubAndRole(@Param("accountId") String accountId, @Param("role") MoaClubMemberRole role);

    Optional<MoaClubMembers> findByClubAndUserName(MoaClub club, String userName);
}
