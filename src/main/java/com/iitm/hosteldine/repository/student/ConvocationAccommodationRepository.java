package com.iitm.hosteldine.repository.student;

import com.iitm.hosteldine.model.hostel.GuestCouponOnlinePaymentEntity;
import com.iitm.hosteldine.model.studentDashboard.HostelNightPaymentTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.student.ConvocationAccommodationEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ConvocationAccommodationRepository extends JpaRepository<ConvocationAccommodationEntity, Long> {
    Optional<ConvocationAccommodationEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<ConvocationAccommodationEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from ConvocationAccommodationEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

    @Query(value = """
    SELECT NEXTVAL('schooldev."STUDENT_CONVOCATION_ONLINE_PAYMENT_ORDERID"')
    """, nativeQuery = true)
    Integer getNextVal();

    Optional<ConvocationAccommodationEntity> findByOrderNoAndActiveFlag(String orderNo, String activeFlag);

    @Query(value = """
		    SELECT a.orderNo
		    FROM ConvocationAccommodationEntity a
		    WHERE a.activeFlag = :statusActive
			    and a.paymentStatus IN :statuses
				and a.retryCount < :retryCount
	""")
    List<String> getConvOnlinePaymentPendingOrderNo(String statusActive, String paymentStatus, int retryCount, Collection<String> statuses);

    @Query("""
    select count(c) > 0
    from ConvocationAccommodationEntity c
    where upper(c.studentId) = upper(:studentId)
      and c.activeFlag = :activeFlag
      and not (
            c.overallAmount is not null
            and c.overallAmount > 0
            and c.paymentStatus is not null
            and upper(trim(c.paymentStatus)) <> upper(:successStatus)
      )
""")
    boolean existsBlockingConvocationAccommodation(
            String studentId,
            String activeFlag,
            String successStatus
    );



}