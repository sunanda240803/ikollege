package com.iitm.hosteldine.repository.hostel;

import com.iitm.hosteldine.model.hostel.GuestCouponMappingsEntity;
import com.iitm.hosteldine.model.hostel.GuestCouponPaymentAdviceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface GuestCouponRequestRepository extends JpaRepository<GuestCouponMappingsEntity, Long> {
	@Query(value = "SELECT * FROM schooldev.guest_coupon_req_list(:name, :studentId, :category, CAST(:diningFrom AS TEXT), CAST(:diningTo AS TEXT), "
			+ ":paymentStatus, CAST(:submittedFrom AS TEXT), CAST(:submittedTo AS TEXT))", nativeQuery = true)
    Object[] getRequestCoupons(String name, String studentId, String category, LocalDate diningFrom,
                                                        LocalDate diningTo, String paymentStatus, LocalDate submittedFrom,
                                                        LocalDate submittedTo);
    
	@Query(value = "SELECT a, b, c FROM GuestCouponPaymentAdviceEntity a "
			+ "JOIN GuestCouponMappingsEntity b ON a.requestId = b.requestId AND b.activeFlag = :activeFlag "
			+ "LEFT JOIN MessMasterEntity c ON a.messId = c.id AND c.activeFlag = :activeFlag "
			+ "WHERE a.requestId = :requestId AND a.activeFlag = :activeFlag "
			+ "ORDER BY b.couponId ASC")
	List<Object[]> getGuestCouponRequestListByRequestId(Long requestId, String activeFlag);

	@Modifying
	@Query("UPDATE GuestCouponPaymentAdviceEntity g set g.approvalStatus = :approved, g.modifiedBy = :userId, "
			+ "g.modifiedAt = :now WHERE g.requestId = :requestId AND g.activeFlag = :activeFlag")
	void updateApprovalStatus(String approved, String userId, LocalDateTime now, Long requestId, String activeFlag);

	@Modifying
	@Query("UPDATE GuestCouponPaymentAdviceEntity g SET g.activeFlag = :statusInactive, "
			+ " g.modifiedAt = :now, g.modifiedBy = :userId WHERE g.requestId = :requestId")
	int deleteGuestCoupon(long requestId, String statusInactive, String userId, LocalDateTime now);

	@Modifying
	@Query("UPDATE GuestCouponMappingsEntity g SET g.activeFlag = :statusInactive, g.modifiedAt = :now, "
			+ "g.modifiedBy = :userId WHERE g.requestId = :requestId")
	int deleteGuestCouponList(long requestId, String statusInactive, String userId, LocalDateTime now);

	@Modifying
	@Query("UPDATE GuestCouponPaymentAdviceEntity g SET g.activeFlag = :statusActive, "
			+ " g.modifiedAt = :now, g.modifiedBy = :userId WHERE g.requestId = :requestId")
	int enableGuestCoupon(long requestId, String statusActive, String userId, LocalDateTime now);

	@Modifying
	@Query("UPDATE GuestCouponMappingsEntity g SET g.activeFlag = :statusActive, g.modifiedAt = :now, "
			+ "g.modifiedBy = :userId WHERE g.requestId = :requestId")
	int enableGuestCouponList(long requestId, String statusActive, String userId, LocalDateTime now);

	@Query(value = "SELECT g FROM GuestCouponPaymentAdviceEntity g "
			+ "WHERE g.requestId = :requestId ")
	GuestCouponPaymentAdviceEntity getByRequestIdAlone(Long requestId);

	@Query(value = "SELECT g FROM GuestCouponPaymentAdviceEntity g "
			+ "WHERE g.requestId = :requestId AND g.activeFlag = :activeFlag")
	GuestCouponPaymentAdviceEntity getEntityByRequestId(Long requestId, String activeFlag);

	@Modifying
	@Query("UPDATE GuestCouponPaymentAdviceEntity g SET "
			+ "g.mailStatus = :sent, g.modifiedBy = :userId, g.modifiedAt = :now WHERE g.requestId = :requestId")	
	int updateMailGuestCoupon(Long requestId, String sent, String userId, LocalDateTime now);

	static final String updateQuery = "UPDATE GuestCouponPaymentAdviceEntity g SET "
			+ "g.paymentType = :paymentType, g.paymentDate = :paymentDate, g.paymentReferenceNo = :paymentReferenceNo, "
			+ "g.paymentAmount = :paymentAmount, g.paymentStatus = :paymentStatus, g.modifiedBy = :userId, g.modifiedAt = :now ";

	static final String whererQuery = "WHERE g.requestId = :requestId AND g.activeFlag = :activeFlag";

	@Modifying
	@Query(updateQuery + whererQuery)
	int updateGuestCouponPayment(String paymentType, LocalDate paymentDate, String paymentReferenceNo,
			Integer paymentAmount, String paymentStatus, String userId, LocalDateTime now, Long requestId, String activeFlag);
	
	@Modifying
	@Query(updateQuery + ", g.approvalStatus = :approvalStatus " + whererQuery)
	int updateHostelResidentsGuestCouponPayment(String paymentType, LocalDate paymentDate, String paymentReferenceNo,
			Integer paymentAmount, String paymentStatus, String userId, LocalDateTime now, Long requestId, String activeFlag,
			String approvalStatus);

	@Query(value = "select g.requestId, g.toDate,"
			+ "	SUM(CASE WHEN g.couponType = 'BF' THEN 1 ELSE 0 END) AS bfStatus,"
			+ "	SUM(CASE WHEN g.couponType = 'LC' THEN 1 ELSE 0 END) AS lcStatus,"
			+ "	SUM(CASE WHEN g.couponType = 'DR' THEN 1 ELSE 0 END) AS drStatus,"
			+ "	SUM(CASE WHEN g.couponType = 'ET' THEN 1 ELSE 0 END) AS snacksStatus"
			+ "	FROM GuestCouponMappingsEntity g WHERE g.requestId = :requestId "
			+ "	GROUP BY g.requestId, g.toDate ORDER BY g.toDate")
	List<Object[]> getGuestCouponsList(Long requestId, String activeFlag);

	@Query(value = "SELECT g FROM GuestCouponPaymentAdviceEntity g"
			+ " WHERE UPPER(g.studentId) = UPPER(:studentId) AND g.category = :category AND g.activeFlag = :activeFlag"
			+ " ORDER BY g.createdAt DESC LIMIT 1")
	GuestCouponPaymentAdviceEntity getCouponPaymentEntityByStudentIdAndCategory(String studentId, String category,
			String activeFlag);

	@Query("SELECT a, b, c FROM GuestCouponMappingsEntity a "
			+ "JOIN GuestCouponPaymentAdviceEntity b ON a.requestId = b.requestId AND b.activeFlag = :activeFlag "
			+ "LEFT JOIN MessMasterEntity c ON b.messId = c.id AND c.activeFlag = :activeFlag "
			+ "WHERE a.couponNumber = :couponNumber AND a.couponId = :qrId AND a.activeFlag = :activeFlag ")
	List<Object[]> getCouponDetailsByQrNumberAndQrId(Long qrId, String couponNumber, String activeFlag);

//	@Query("SELECT b FROM GuestCouponMappingsEntity a "
//			+ "JOIN MessSessionEntity b ON a.couponType = b.id.sessionName AND b.activeFlag = :activeFlag "
//			+ "AND :messId = b.id.messId "
//			+ "WHERE a.couponId = :qrId AND a.activeFlag = :activeFlag "
//			+ "AND :currentTime >= b.startTime AND :currentTime <= b.endTime")
//	Optional<MessSessionEntity> getGuestCouponMessSession(Long qrId, Long messId, String activeFlag, String currentTime);

}

