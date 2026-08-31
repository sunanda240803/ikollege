package com.iitm.hosteldine.repository.mess;

import com.iitm.hosteldine.dto.mess.FoodCourtTransactionDto;
import com.iitm.hosteldine.model.mess.FoodCourtLedgerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoodCourtLedgerRepository extends JpaRepository<FoodCourtLedgerEntity, Long> {

    @Query(value = """
        SELECT c.studentName, a.messName, b.balanceAmount
            FROM CurrentMessDetailsView a
            LEFT JOIN FoodCourtLedgerView b
                ON a.messPeriodId = b.messPeriodId
                AND a.messId = b.fcMessId
                AND a.studentId = b.studentId
            LEFT JOIN AllStudentsDetailsViewEntity c
                ON c.studentId = a.studentId
            WHERE a.studentId = :studentId
              AND a.isFoodCourt = true
    """)
    Optional<Object> getStudentNameMessNameBalAmount(String studentId);


    @Query(value = "SELECT * FROM schooldev.food_court_list(" +
            " cast(:studentid as varchar), " +
            " cast(:userrole as varchar), " +
            " cast(:messperiodid as bigint), " +
            " cast(:foodcourtmessnameid as integer), " +
            " cast(:fromdate as varchar), " +
            " cast(:todate as varchar));",
            nativeQuery = true)
    Page<Object[]> getFoodCourtTransactions(
            @Param("studentid") String studentid,
            @Param("userrole") String userrole,
            @Param("messperiodid") Long messperiodid,
            @Param("foodcourtmessnameid") Integer foodcourtmessnameid,
            @Param("fromdate") String fromdate,
            @Param("todate") String todate,
			Pageable pageable);
}
