package com.hana.hana1pick.domain.autotranfer.repository;

import com.hana.hana1pick.domain.autotranfer.entity.AutoTransfer;
import com.hana.hana1pick.domain.autotranfer.entity.AutoTransferId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutoTransferRepository extends JpaRepository<AutoTransfer, AutoTransferId> {

    @Query("select a from AutoTransfer a where a.id.atDate = :atDate")
    List<AutoTransfer> findByAtDate(@Param("atDate") int atDate);

    @Query("select a from AutoTransfer a where a.id.inAccId = :inAccId")
    List<AutoTransfer> findByInAccId(@Param("inAccId") String inAccId);

    @Query("select a from AutoTransfer a where a.id.inAccId = :inAccId and a.id.outAccId = :outAccId")
    Optional<AutoTransfer> findByInAccIdAndOutAccId(@Param("inAccId") String inAccId, @Param("outAccId") String outAccId);
}
