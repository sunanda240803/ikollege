package com.iitm.hosteldine.repository.dashboard.student;

import com.iitm.hosteldine.model.studentDashboard.HostelNightPaymentTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface HostelNightPaymentTransactionRepository extends JpaRepository<HostelNightPaymentTransactionEntity, Long> {

    List<HostelNightPaymentTransactionEntity> findAllByStudentIdAndActiveFlagAndPaymentStatusOrderByModifiedAtDesc(String studentId, String activeFlag, String paymentStatus);

    Optional<HostelNightPaymentTransactionEntity> findFirstByStudentIdAndActiveFlagAndModifiedAtAfterOrderByModifiedAtDesc(String studentId, String activeFlag, LocalDateTime modifiedAt);

    @Query(value = """
    select sum(hnpt.noOfVegCoupon + hnpt.noOfNonvegCoupon) from HostelNightPaymentTransactionEntity hnpt 
        where hnpt.studentId = :studentId and hnpt.activeFlag = :activeFlag and hnpt.paymentStatus = :status
    """)
    Optional<Integer> getTotalNumberOfCoupons(String studentId, String status, String activeFlag);

    @Query(value = """
    SELECT NEXTVAL('schooldev."HOSTEL_NIGHT_ONLINE_PAYMENT_ORDERID"')
    """, nativeQuery = true)
    Integer getNextVal();

    Optional<HostelNightPaymentTransactionEntity> findByOrderNoAndActiveFlag(String orderNo, String activeFlag);
}