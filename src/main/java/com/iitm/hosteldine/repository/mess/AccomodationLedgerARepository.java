package com.iitm.hosteldine.repository.mess;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iitm.hosteldine.entity.mess.AccomodationLedgerAEntity;
import com.iitm.hosteldine.entity.mess.AccomodationLedgerAEntityId;

@Repository
public interface AccomodationLedgerARepository extends JpaRepository<AccomodationLedgerAEntity, AccomodationLedgerAEntityId> {
	
	@Query(value = "SELECT NEXTVAL('schooldev.IIT_PS_TEMP_ACCOM_LEDGER_A_SEQ')", nativeQuery = true)
	int getNextValAccomodationLedger();
}
