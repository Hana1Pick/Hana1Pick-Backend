package com.hana.hana1pick.domain.autotranfer.repository;

import com.hana.hana1pick.domain.autotranfer.entity.AutoTransfer;
import com.hana.hana1pick.domain.autotranfer.entity.AutoTransferId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoTransferRepository extends JpaRepository<AutoTransfer, AutoTransferId> {

}
