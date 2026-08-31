package com.iitm.hosteldine.repository.studentDashboard;

import com.iitm.hosteldine.model.studentDashboard.HostelNightPaymentOnlineViewEntity;
import com.iitm.hosteldine.service.dashboard.student.HostelNightCouponReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface HostelNightPaymentOnlineViewRepository extends JpaRepository<HostelNightPaymentOnlineViewEntity, String> {

    @Query("""
            select new com.iitm.hosteldine.service.dashboard.student.HostelNightCouponReport(
                           hnpte.createdAt,
                           sdie.studentId,
                           concat(sdie.firstName, sdie.lastName),
                           hme.hostelName,
                           hnpte.payBy,
                           hnpte.noOfVegCoupon,
                           hnpte.noOfNonvegCoupon,
                           hnpte.vegRate,
                           hnpte.nonvegRate,
                           hnpte.totalAmount,
                           hnpte.paidAmount,
                           hnpte.orderNo,
                           hnpte.paymentDate,
                           hnpte.paymentType
                       ) from HostelNightPaymentTransactionEntity hnpte
            left join HostelMasterEntity hme on (hnpte.hostelId = hme.id and hme.activeFlag = :activeFlag)
            left join StudentDetailsInfoEntity sdie on hnpte.studentId = sdie.studentId
            where upper(hnpte.payBy) = upper(:type) and 
            hnpte.activeFlag = :activeFlag and hnpte.paymentStatus = :paymentStatus
            and (:hostelId IS NULL OR hnpte.hostelId = :hostelId)
            and hnpte.createdAt between :submittedFromDate and :submittedToDate
""")
    List<HostelNightCouponReport> getHostelNightCouponList(String type, LocalDateTime submittedFromDate, LocalDateTime submittedToDate,
                                                           Long hostelId, String activeFlag, String paymentStatus);
}