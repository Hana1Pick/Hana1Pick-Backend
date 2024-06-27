package com.hana.hana1pick.domain.celublog.repository;

import com.hana.hana1pick.domain.celublog.entity.Rules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RulesRepository extends JpaRepository<Rules, Long> {

}
