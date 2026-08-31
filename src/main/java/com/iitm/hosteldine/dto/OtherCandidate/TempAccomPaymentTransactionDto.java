package com.iitm.hosteldine.dto.OtherCandidate;

import java.time.LocalDateTime;

import com.iitm.hosteldine.model.OtherCandidate.TempAccomPaymentAdviceEntity;

import lombok.Data;

@Data
public class TempAccomPaymentTransactionDto {
    private Long id;
    private TempAccomPaymentAdviceEntity tempAccomPaymentAdvice;
    private Long requestId;
    private Long overallAmount;
    private Double netPayable;
    private String userId;
    private String orderNo;
    private String transactionRefNumber;
    private String ccavReferenceNo;
    private String paymentMethod;
    private String paymentGateway;
    private String receivedAmount;
    private LocalDateTime transactionDate;
    private String paymentStatus;
    private Double transFee;
    private Double serviceTax;
    private int schoolId;
    private String statusMessage;
    private Integer retryCount;
}