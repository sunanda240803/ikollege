package com.iitm.hosteldine.dto.student;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ConvocationAccommodationDto {
    private Long id;
    private String studentId;
    private String studentName;
    private String gender;
    private String mailId;
    private String menuType;
    private Boolean accommodationStatus;
    private String complimentaryCoupons;
    private LocalDate diningFromDate;
    private LocalDate diningToDate;
    private Integer additionalNoOfCoupons;
    private Double overallAmount;
    private String orderNo;
    private String paymentType;
    private LocalDate paymentDate;
    private String paymentReferenceNo;
    private String ccavReferenceNo;
    private Double paymentAmount;
    private String paymentStatus;
    private Double transFee;
    private Double serviceTax;
    private String statusMessage;
    private String hostelName;
    private String accommodationPreference;
    private LocalDateTime createdAt;
}
