package com.hana.hana1pick.domain.celebrity.repository;

import com.hana.hana1pick.domain.celebrity.entity.Celebrity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CelebrityRepository extends JpaRepository<Celebrity, Long> {
}
