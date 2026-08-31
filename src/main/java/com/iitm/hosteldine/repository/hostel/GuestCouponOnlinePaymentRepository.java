package com.iitm.hosteldine.repository.hostel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iitm.hosteldine.model.hostel.GuestCouponOnlinePaymentEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestCouponOnlinePaymentRepository extends JpaRepository<GuestCouponOnlinePaymentEntity, Long> {
	
	@Query(value="SELECT NEXTVAL('schooldev.\"IITM_GUEST_COUPON_ONLINE_PAYMENT_ORDERID\"')")
    Long getNextOrderId();
	
	GuestCouponOnlinePaymentEntity findByOrderNoAndActiveFlag(String orderNo,String activeFlag);

    Optional<GuestCouponOnlinePaymentEntity> findByPaymentAdviceRequestIdAndActiveFlagAndPaymentStatusIgnoreCase(
            Long requestId, String activeFlag, String paymentStatus);

    List<GuestCouponOnlinePaymentEntity> findByPaymentAdviceRequestIdAndActiveFlag(Long requestId, String activeFlag);
}
