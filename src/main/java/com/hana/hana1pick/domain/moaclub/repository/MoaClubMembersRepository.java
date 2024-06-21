package com.hana.hana1pick.domain.moaclub.repository;

import com.hana.hana1pick.domain.moaclub.entity.ClubMembersId;
import com.hana.hana1pick.domain.moaclub.entity.MoaClubMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoaClubMembersRepository extends JpaRepository<MoaClubMembers, ClubMembersId> {

}
