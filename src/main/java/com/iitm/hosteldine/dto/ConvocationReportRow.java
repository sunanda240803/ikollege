package com.iitm.hosteldine.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ConvocationReportRow(
        Long couponId,
        LocalDateTime createdAt,
        LocalDate diningDate,
        Integer noOfBreakfastCoupon,
        Integer noOfLunchCoupon,
        Integer noOfDinnerCoupon,
        Long convocationId,
        String studentId,
        String studentName,
        String gender,
        String mailId,
        String menuType,
        Boolean accommodationStatus,
        String complimentaryCoupons,
        Integer additionalNoOfCoupons,
        Double overallAmount,
        String paymentStatus,
        String hostelName
) {
}
