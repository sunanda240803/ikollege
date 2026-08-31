package com.iitm.hosteldine.repository.hostel;

import com.iitm.hosteldine.model.hostel.GuestCouponOnlinePaymentEntity;
import com.iitm.hosteldine.model.hostel.GuestCouponPaymentAdviceEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface GuestCouponPaymentAdviceRepository extends JpaRepository<GuestCouponPaymentAdviceEntity, Long> {
	
	@Query(value = """
		    SELECT a,b
		    FROM GuestCouponPaymentAdviceEntity a
		    LEFT JOIN GuestCouponOnlinePaymentEntity b
		        ON a.requestId = b.paymentAdvice.requestId and b.activeFlag = :statusActive 
		        and upper(b.paymentStatus)= upper(:paymentStatus)
		    WHERE a.requestId = :paymentAdviceId  and a.activeFlag = :statusActive
	""")
    List<Object[]> checkPaymentStatus(long paymentAdviceId, String statusActive, String paymentStatus);
    
	
	@Modifying
    @Transactional
    @Query("UPDATE GuestCouponPaymentAdviceEntity t " +
           "SET t.paymentStatus = :#{#transaction.paymentStatus}, " +
           "    t.paymentType = :#{#transaction.paymentMethod}, " +
           "    t.paymentReferenceNo = :#{#transaction.ccavReferenceNo}, " +
           "    t.paymentAmount = :#{#transaction.overallAmount}, " +
           "    t.paymentDate = CAST(:#{#transaction.transactionDate} AS date), " +
           "    t.modifiedBy = :userId, " +
           "    t.modifiedAt = CURRENT_TIMESTAMP " +
           "WHERE t.requestId = :#{#transaction.paymentAdvice.requestId} AND t.activeFlag = :statusActive")
    int updatePaymentGatewayResponse(@Param("transaction") GuestCouponOnlinePaymentEntity updatedTrans,String userId, String statusActive);
    
	
	@Query(value = """
		    SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
		    FROM GuestCouponPaymentAdviceEntity a
		    JOIN GuestCouponMappingsEntity b
		        ON a.requestId = b.requestId and b.activeFlag = :statusActive 
		    WHERE upper(a.studentId)= upper(:studentId) and b.toDate = :date and b.couponType = :session
	""")
    boolean checkSameDateAndSession(String studentId, LocalDate date, String session, String statusActive);
	
	@Query(value = "SELECT (total_capacity - (guest_coupon_count + student_mess_count)) AS available_count"
			+ "	 FROM "
			+ "    (SELECT COUNT(*) AS guest_coupon_count FROM schooldev.\"IITM_GUEST_COUPON_MAPPINGS\" a"
			+ "     JOIN schooldev.\"IITM_GUEST_COUPON_PAYMENT_ADVICE\" b ON (a.request_id = b.request_id AND a.active_flag = :statusActive)"
			+ "     WHERE a.validity_to_date = :date AND a.active_flag = :statusActive AND a.coupon_type = :session"
			+ "     AND (b.payment_status = 'Success' or (b.category='IITM Faculty' and b.approval_status='Approved'))"
			+ "     AND b.mess_id = :messId) guest_coupon,"
			+ "    (SELECT COUNT(*) AS student_mess_count FROM schooldev.\"STUDENT_MESS_DETAILS\" smd"
			+ "     WHERE smd.current_active_flag = :statusActive AND :date >= smd.from_date AND :date <= smd.to_date"
			+ "     AND smd.mess_id = :messId) student_mess,"
			+ "    (SELECT capacity AS total_capacity FROM schooldev.\"MESS_MASTER\" mmc"
			+ "     WHERE mmc.active_flag = :statusActive AND mmc.mess_master_id = :messId) mess_master;", nativeQuery = true)
	int checkMessAvailability(Long messId,  LocalDate date, String session,String statusActive);


    @Query(value = """
				SELECT b.toDate,b.couponType
			FROM GuestCouponPaymentAdviceEntity a
			JOIN GuestCouponMappingsEntity b ON a.requestId = b.requestId and b.activeFlag = :statusActive 
						WHERE upper(a.studentId)= upper(:studentId) and b.toDate between :fromDate and :toDate 
									and a.activeFlag =:statusActive
				""")
    List<Object[]> checkSameSessionsWithinDates(String studentId, String statusActive, LocalDate fromDate, LocalDate toDate);

    @Query(value = """
        WITH d AS (
            SELECT dd::date AS day
            FROM generate_series(:fromDate, :toDate, interval '1 day') AS gs(dd)
        ),
        sess AS (
            SELECT s AS session_code
            FROM (VALUES ('BF'), ('LC'), ('DR'), ('ET')) v(s)
        ),
        cap AS (
            SELECT capacity
            FROM schooldev."MESS_MASTER"
            WHERE mess_master_id = :messId AND active_flag = :statusActive
        ),
        guest AS (
            SELECT a.validity_to_date::date AS day, a.coupon_type AS session_code, COUNT(*) AS cnt
            FROM schooldev."IITM_GUEST_COUPON_MAPPINGS" a
            JOIN schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" b
                ON b.request_id = a.request_id
            WHERE a.active_flag = 'Y'
                AND a.validity_to_date BETWEEN :fromDate AND :toDate
                AND a.coupon_type IN ('BF','LC','DR','ET')
                AND b.mess_id = :messId
                AND b.payment_status = 'Success'
                AND (b.payment_status = 'Success'
                     OR (b.category = 'IITM Faculty' AND b.approval_status = 'Approved'))
            GROUP BY 1,2
        ),
        guest_aggr AS (
            SELECT day, session_code, SUM(cnt) AS guest_coupon_count
            FROM guest
            GROUP BY 1,2
        ),
        student_aggr AS (
            SELECT d.day, COUNT(*) AS student_mess_count
            FROM d
            JOIN schooldev."STUDENT_MESS_DETAILS" smd
                ON smd.mess_id = :messId
                AND smd.current_active_flag = 'Y'
                AND d.day BETWEEN smd.from_date AND smd.to_date
            GROUP BY d.day
        )
        SELECT
            d.day,
            s.session_code AS session,
            (
                (SELECT capacity FROM cap)
                - COALESCE(g.guest_coupon_count, 0)
                - COALESCE(st.student_mess_count, 0)
            ) AS available_count
        FROM d
        CROSS JOIN sess s
        LEFT JOIN guest_aggr g ON g.day = d.day AND g.session_code = s.session_code
        LEFT JOIN student_aggr st ON st.day = d.day
        WHERE (
           (SELECT capacity FROM cap)
           - COALESCE(g.guest_coupon_count, 0)
           - COALESCE(st.student_mess_count, 0)
        ) <= 0
        ORDER BY d.day, s.session_code;
    """, nativeQuery = true)
    List<Object[]> checkMessAvailabilityWithinDates(@Param("messId") Long messId,
                                                    @Param("statusActive") String statusActive,
                                                    @Param("fromDate") LocalDate fromDate,
                                                    @Param("toDate") LocalDate toDate);

    @Query(value = "SELECT request_id " +
            "FROM schooldev.\"IITM_GUEST_COUPON_PAYMENT_ADVICE\" " +
            "WHERE category = :category " +
            "AND student_id = :studentId " +
            "AND payment_status = :paymentStatus " +
            "AND active_flag = :activeFlag " +
            "AND created_at + (:expireDuration * interval '1 minute') < CURRENT_TIMESTAMP",
            nativeQuery = true)
    List<Long> findExpiredRequestIds(@Param("category") String category,
                                     @Param("studentId") String studentId,
                                     @Param("paymentStatus") String paymentStatus,
                                     @Param("activeFlag") String activeFlag,
                                     @Param("expireDuration") int expireDuration);

    @Query(value = """
		    SELECT a,b
		    FROM GuestCouponPaymentAdviceEntity a
		    JOIN GuestCouponOnlinePaymentEntity b
		        ON a.requestId = b.paymentAdvice.requestId and b.activeFlag = :statusActive 
		        and upper(b.paymentStatus)= upper(:paymentStatus)
		    WHERE a.paymentStatus != :paymentStatus  and a.activeFlag = :statusActive
			    and a.category=:category
	""")
    List<Object[]> getPaymentAdviceNotSuccessRequests(String category, String statusActive, String paymentStatus);

    @Query(value = """
		    SELECT a
		    FROM GuestCouponOnlinePaymentEntity a
		    JOIN GuestCouponPaymentAdviceEntity b
		        ON a.paymentAdvice.requestId=b.requestId and b.activeFlag = :statusActive 
		    WHERE b.paymentStatus != :paymentStatus  and a.activeFlag = :statusActive
			    and a.paymentStatus IN :statuses
				and a.retryCount < :retryCount
	""")
    List<GuestCouponOnlinePaymentEntity> getOnlinePaymentPendingRequests(String statusActive, String paymentStatus, int retryCount, Collection<String> statuses);

	@Query(value = """
		    SELECT a.orderNo
		    FROM GuestCouponOnlinePaymentEntity a
		    JOIN GuestCouponPaymentAdviceEntity b
		        ON a.paymentAdvice.requestId=b.requestId and b.activeFlag = :statusActive 
		    WHERE b.paymentStatus != :paymentStatus  and a.activeFlag = :statusActive
			    and a.paymentStatus IN :statuses
				and a.retryCount < :retryCount
	""")
	List<String> getOnlinePaymentPendingOrderNo(String statusActive, String paymentStatus, int retryCount, Collection<String> statuses);

	@Query("""
		    SELECT a
		    FROM GuestCouponOnlinePaymentEntity a
		    JOIN GuestCouponPaymentAdviceEntity b
		        ON a.paymentAdvice.requestId=b.requestId and b.activeFlag = :statusActive 
		    WHERE b.paymentStatus != :paymentStatus  and a.activeFlag = :statusActive
			    and a.orderNo = :orderNo
	""")
    Optional<GuestCouponOnlinePaymentEntity> checkPaymentAdviceNotSuccess(String statusActive, String paymentStatus, String orderNo);
}
