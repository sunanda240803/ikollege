package com.iitm.hosteldine.repository.hostel;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.GuestCouponIssuedResultDTO;
import com.iitm.hosteldine.model.hostel.GuestCouponMappingsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestCouponMappingsRepository extends JpaRepository<GuestCouponMappingsEntity, Long> {

    @Query(value = "SELECT coup.coupon_id, coup.request_id, coup.coupon_number, coup.candidate_name, " +
            "coup.validity_from_date, coup.validity_to_date, coup.coupon_type, coup.coupon_used_status, " +
            "coup.Submitted_Date, coup.mess_name FROM schooldev.guest_coupon_issued_list(:name, :usedStatus, :requestId, :diningFrom, " +
            ":diningTo, :submittedFrom, :submittedTo, :messId, :userRole, :loggedInUser) coup", nativeQuery = true)
    Page<GuestCouponIssuedResultDTO> getIssuedCoupons(String name, String usedStatus, Long requestId, String diningFrom,
                                                      String diningTo, String submittedFrom, String submittedTo,
                                                      Integer messId, String userRole, String loggedInUser, Pageable pageable);

    @Query(value = "SELECT coup.coupon_id, coup.request_id, coup.coupon_number, coup.candidate_name, " +
            "coup.validity_from_date, coup.validity_to_date, coup.coupon_type, coup.coupon_used_status, " +
            "coup.Submitted_Date, coup.mess_name,coup.student_id FROM schooldev.guest_coupon_issued_list(:name, :usedStatus, :requestId, :diningFrom, " +
            ":diningTo, :submittedFrom, :submittedTo, :messId, :userRole, :loggedInUser) coup", nativeQuery = true)
    List<GuestCouponIssuedResultDTO> getIssuedCoupons(String name, String usedStatus, Long requestId, String diningFrom,
                                                      String diningTo, String submittedFrom, String submittedTo,
                                                      Integer messId, String userRole, String loggedInUser);

    List<GuestCouponMappingsEntity> findByCouponIdIn(List<Long> couponIds);


        // Fetch session counts based on mess ID
        @Query(value = "SELECT "
                + "SUM(CASE WHEN coupon_type = 'BF' THEN 1 ELSE 0 END) AS bf_count, "
                + "SUM(CASE WHEN coupon_type = 'LC' THEN 1 ELSE 0 END) AS lc_count, "
                + "SUM(CASE WHEN coupon_type = 'DR' THEN 1 ELSE 0 END) AS dr_count "
                + "FROM schooldev.\"IITM_GUEST_COUPON_PAYMENT_ADVICE\" a "
                + "JOIN schooldev.\"IITM_GUEST_COUPON_MAPPINGS\" b "
                + "ON a.request_id = b.request_id AND b.active_flag = 'Y' "
                + "WHERE a.mess_id = :messId AND (NOW()::Date + INTERVAL '1 day') BETWEEN dining_from_date AND dining_to_date "
                + "AND (NOW()::Date + INTERVAL '1 day') = validity_from_date "
                + "AND a.active_flag = 'Y' "
                + " AND (a.payment_status = 'Success' or (a.category='IITM Faculty' and a.approval_status='Approved'))" , nativeQuery = true)
        Object[] getSessionCountsByMessId(int messId);

}
