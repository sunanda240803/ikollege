package com.iitm.hosteldine.repository.OtherCandidate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iitm.hosteldine.model.OtherCandidate.TempAccomPaymentAdviceEntity;
import com.iitm.hosteldine.model.OtherCandidate.TempAccomPaymentTransactionEntity;

import jakarta.transaction.Transactional;

public interface TempAccomPaymentAdviceRepository extends JpaRepository<TempAccomPaymentAdviceEntity, Long> {
    Optional<TempAccomPaymentAdviceEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<TempAccomPaymentAdviceEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from TempAccomPaymentAdviceEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);
    
//    @Query(value = """
//		    SELECT a,b
//		    FROM TempAccomPaymentAdviceEntity a
//		    LEFT JOIN TempAccomPaymentTransactionEntity b
//		        ON a.id = b.tempAccomPaymentAdvice.id and b.activeFlag = :statusActive and upper(b.paymentStatus)= upper(:paymentStatus) 
//		    WHERE a.candidateRequest.candidateId = :candidateId  AND a.activeFlag = :statusActive
//		    AND (:search IS NULL OR :search = '' 
//                       OR a.paymentStatus ILIKE %:search%
//                       OR b.paymentStatus ILIKE %:search%)
//	""")
//    Page<Object[]> getPaymentListForCandidate(long candidateId, String statusActive,String search, Pageable pageable,String paymentStatus);
    
    @Query(value = """
            SELECT b.id,b.hostel_pay_from_date, b.hostel_pay_to_date, b.mess_pay_from_date, b.mess_pay_to_date, 
                   b.overall_amount, c.order_no, b.payment_status, c.payment_status AS online_payment_status
            FROM schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST" a
            JOIN schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE" b 
                ON a.request_id = b.request_id 
                AND b.active_flag = :statusActive
            LEFT JOIN (
                SELECT c1.*, 
                    ROW_NUMBER() OVER (PARTITION BY c1.payment_id ORDER BY CASE WHEN upper(c1.payment_status) = upper(:paymentStatus) THEN 0 ELSE 1 END, c1.created_at DESC) AS row_num
                FROM schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_TRANSACTION_DETAILS" c1
                WHERE c1.active_flag = :statusActive
            ) c 
                ON b.id = c.payment_id 
                AND c.row_num = 1
            WHERE a.candidate_id = :candidateId
                AND a.active_flag = :statusActive
                AND (:search IS NULL OR :search = '' 
                       OR b.payment_status ILIKE %:search%
                       OR b.payment_status ILIKE %:search%)
            """, nativeQuery = true)
        Page<Object[]> getPaymentListForCandidate(@Param("candidateId") Long candidateId,
        		@Param("statusActive") String statusActive,@Param("search") String search,
        		Pageable pageable,@Param("paymentStatus") String paymentStatus);
    
    @Query(value = """
		    SELECT a.candidateRequest.id as stay_request_id,a.overallAmount,b.paymentStatus as online_payment_status
		    FROM TempAccomPaymentAdviceEntity a
		    LEFT JOIN TempAccomPaymentTransactionEntity b
		        ON a.id = b.tempAccomPaymentAdvice.id and b.activeFlag = :statusActive 
		        and upper(b.paymentStatus)= upper(:paymentStatus)
		    WHERE a.id = :paymentAdviceId  and a.activeFlag = :statusActive
	""")
    Object[] checkPaymentStatus(long paymentAdviceId, String statusActive, String paymentStatus);
    
    
    @Modifying
    @Transactional
    @Query("UPDATE TempAccomPaymentAdviceEntity t " +
           "SET t.paymentStatus = CASE WHEN :#{#transaction.paymentStatus} = 'Success' THEN 'Completed' " +
           "                            ELSE :#{#transaction.paymentStatus} END, " +
           "    t.paymentType1 = :#{#transaction.paymentMethod}, " +
           "    t.paymentReferenceNo1 = :#{#transaction.ccavReferenceNo}, " +
           "    t.paymentAmount1 = :#{#transaction.overallAmount}, " +
           "    t.paymentDate1 = :#{#transaction.transactionDate}, " +
           "    t.modifiedBy = :userId, " +
           "    t.modifiedAt = CURRENT_TIMESTAMP " +
           "WHERE t.id = :#{#transaction.tempAccomPaymentAdvice.id} AND t.activeFlag = :statusActive")
    int updatePaymentGatewayResponse(@Param("transaction") TempAccomPaymentTransactionEntity transaction,String userId, String statusActive);
    
}