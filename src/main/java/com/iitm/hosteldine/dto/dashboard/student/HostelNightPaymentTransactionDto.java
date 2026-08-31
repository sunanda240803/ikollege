package com.iitm.hosteldine.dto.dashboard.student;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * DTO for {@link com.iitm.hosteldine.model.studentDashboard.HostelNightPaymentTransactionEntity}
 */
@Data
@Builder
public class HostelNightPaymentTransactionDto implements Serializable {
    private Long id;
    private String studentId;
    private Long hostelId;
    private Integer noOfVegCoupon;
    private Integer noOfNonvegCoupon;
    private Double vegRate;
    private Double nonvegRate;
    private Double totalAmount;
    private String payBy;
    private String orderNo;
    private String paymentType;
    private LocalDate paymentDate;
    private Double paidAmount;
    private String transactionRefNumber;
    private String ccavReferenceNo;
    private String paymentStatus;
    private Double transFee;
    private Double serviceTax;
    private String statusMessage;
    private Integer retryCount;
    private String mailStatus;
    public LocalDateTime createdAt;
}