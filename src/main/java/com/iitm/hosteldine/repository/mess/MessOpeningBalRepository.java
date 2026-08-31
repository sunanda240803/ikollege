package com.iitm.hosteldine.repository.mess;

import com.iitm.hosteldine.entity.mess.MessOpeningBalEntity;
import com.iitm.hosteldine.entity.mess.MessOpeningBalEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessOpeningBalRepository extends JpaRepository<MessOpeningBalEntity, MessOpeningBalEntityId> {

    @Query(value = """
            select
            	'Open Bal' tbl,
            	opn_date dt,
            	'OPENINGBAL',
            	null,
            	amount,
            	debit_or_credit,
            	'',
            	null as c_date
            from
            	schooldev."MESS_OPENING_BAL"
            where
            	active_flag = 'Y'
            	and acchead IN :accHeads;
        """,nativeQuery = true)
    Optional<List<Object[]>> getOpeningBalance(List<String> accHeads);
}