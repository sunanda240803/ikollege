package com.iitm.hosteldine.repository.student;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iitm.hosteldine.model.student.MessCardAmountTransferEntity;

public interface MessCardAmountTransferRepository extends JpaRepository<MessCardAmountTransferEntity, Long> {

    @Query(value = """
            		SELECT mess_to_card_id, student_id, student_name, request_date, net_bal, transfer_amount, requested_status, transferred_date
                	FROM schooldev.mess_to_card_req_list( :studentId, :requestStatus, :requestDate, :transferDate) 
            """, nativeQuery = true)
    Page<Object[]> getMessToCardRequestList(String studentId, String requestStatus, LocalDate requestDate, LocalDate transferDate, Pageable pageable);


    @Query(value = """
                SELECT mess_to_card_id, student_id, student_name, request_date, net_bal, transfer_amount, requested_status, transferred_date
                FROM schooldev.mess_to_card_req_list(:studentId, NULL, NULL, NULL)
                WHERE (:search IS NULL OR LOWER(student_id) LIKE LOWER(CONCAT('%', :search, '%')) 
                OR LOWER(CAST(transfer_amount AS VARCHAR)) LIKE LOWER(CONCAT('%', :search, '%')))
            """, nativeQuery = true)
    Page<Object[]> searchMessToCardRequests(String studentId, String search, Pageable pageable);

	@Query(value = """
                SELECT mess_to_card_id, student_id, student_name, request_date, net_bal, transfer_amount, requested_status, transferred_date
              	FROM schooldev.mess_to_card_req_list( :studentId, :requestStatus, :requestDate, :transferDate) 
              	WHERE (:search IS NULL OR student_id ILIKE '%' || :search || '%' OR student_name ILIKE '%' || :search || '%')
            """, nativeQuery = true)
	Page<Object[]> searchMessToCardRequestsList(String studentId, String requestStatus, LocalDate requestDate, LocalDate transferDate,
												String search, Pageable pageable);

    @Query(value = "SELECT * FROM schooldev.student_balance(:studentId)", nativeQuery = true)
    List<Object[]> getStudentBalance(String studentId);

    @Query("SELECT SUM(mct.transferAmount) FROM MessCardAmountTransferEntity mct " +
            "LEFT JOIN MessCardAmountTransferControllerEntity mctc " +
            "ON mct.activeFlag = mctc.activeFlag " +
            "WHERE mct.requestedStatus != :rejected " +
            "AND mct.transferredDate BETWEEN mctc.openingDate AND mctc.closingDate " +
            "AND mctc.activeFlag = :statusActive " +
            "AND mct.studentId = :studentId")
        // Add studentId to the WHERE clause
    Double getTotalTransferAmount(String statusActive, String rejected, String studentId);


    Optional<MessCardAmountTransferEntity> findByMesstocardIdAndRequestedStatusAndActiveFlag(Long id, String status,
                                                                                             String statusActive);


    Optional<MessCardAmountTransferEntity> findByMesstocardIdAndStudentIdAndActiveFlag(Long id, String studentId,
                                                                                       String statusActive);


}