package com.iitm.hosteldine.model.OtherCandidate;

import java.time.LocalDateTime;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "\"IIT_PS_TEMP_ACCOM_PAYMENT_TRANSACTION_DETAILS\"", schema = ModelConstants.SCHEMA)
@Getter
@Setter
public class TempAccomPaymentTransactionEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", referencedColumnName = "id" )
    private TempAccomPaymentAdviceEntity tempAccomPaymentAdvice; 

    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "overall_amount")
    private Long overallAmount;

    @Column(name = "net_payable")
    private Double netPayable;

    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(name = "order_no", length = 50)
    private String orderNo;

    @Column(name = "transaction_ref_number", length = 50)
    private String transactionRefNumber;

    @Column(name = "ccav_reference_no", length = 50)
    private String ccavReferenceNo;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway;

    @Column(name = "received_amount", length = 50)
    private String receivedAmount;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "payment_status", length = 50)
    private String paymentStatus;

    @Column(name = "trans_fee")
    private Double transFee;

    @Column(name = "service_tax")
    private Double serviceTax;
    
    @Column(name = "status_message", length = 50)
    private String statusMessage;
    
    @Column(name = "retry_count")
    private Integer retryCount;

}
