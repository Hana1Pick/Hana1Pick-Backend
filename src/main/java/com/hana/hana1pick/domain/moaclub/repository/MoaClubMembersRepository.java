package com.hana.hana1pick.domain.moaclub.repository;

import com.hana.hana1pick.domain.moaclub.entity.ClubMembersId;
import com.hana.hana1pick.domain.moaclub.entity.MoaClubMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MoaClubMembersRepository extends JpaRepository<MoaClubMembers, ClubMembersId> {

}
