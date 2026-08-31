package com.iitm.hosteldine.repository.mess;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iitm.hosteldine.entity.mess.AccomodationLedgerBEntity;
import com.iitm.hosteldine.entity.mess.AccomodationLedgerAEntityId;

@Repository
public interface AccomodationLedgerBRepository extends JpaRepository<AccomodationLedgerBEntity, AccomodationLedgerAEntityId> {

}
