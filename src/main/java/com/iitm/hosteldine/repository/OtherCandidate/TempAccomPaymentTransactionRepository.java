package com.iitm.hosteldine.repository.OtherCandidate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.OtherCandidate.TempAccomPaymentTransactionEntity;

public interface TempAccomPaymentTransactionRepository extends JpaRepository<TempAccomPaymentTransactionEntity, Long> {
    Optional<TempAccomPaymentTransactionEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<TempAccomPaymentTransactionEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value="SELECT NEXTVAL('schooldev.\"IIT_W_TEMP_ACCOM_PAYMENT_ORDERID\"')")
    Long getNextOrderId();
    
    
    @Query(value = """
		    SELECT a,b
		    FROM TempAccomPaymentTransactionEntity a
		    LEFT JOIN CandidateProfileEntity  b
		        ON a.tempAccomPaymentAdvice.candidateRequest.candidateId = b.id and b.activeFlag = :statusActive
		    WHERE a.tempAccomPaymentAdvice.id = :paymentAdviceId AND a.orderNo = :orderNo  AND a.activeFlag = :statusActive
	""")
    Object[] getCandidateTransactionDetails(long paymentAdviceId, String orderNo,String statusActive);
    
    TempAccomPaymentTransactionEntity findByOrderNoAndActiveFlag(String orderNo,String activeFlag);
    
    @Query(value = """
     		SELECT createdat, paymentadviceid, order_no, transaction_ref_number, paymentmethod, net_payable, onlinepaymentstatus
	 		FROM schooldev.temp_online_payment_list( :submittedFrom, :submittedTo, :paymentStatus);
     """, nativeQuery = true)
	Page<Object[]> getTemporaryOnlinePaymentDetails(String submittedFrom, String submittedTo,
			String paymentStatus, Pageable pageable);
}