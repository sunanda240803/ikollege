package com.iitm.hosteldine.service.dashboard.student;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record HostelNightCouponReport(
        LocalDateTime submittedDate,
        String studentId,
        String studentName,
        String hostelName,
        String payBy,
        Integer noOfVegCoupon,
        Integer noOfNonVegCoupon,
        Double vegRate,
        Double nonVegRate,
        Double totalAmount,
        Double paidAmount,
        String orderNo,
        LocalDate paymentDate,
        String paymentType
) {
}
