package com.iitm.hosteldine.model.studentDashboard;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"HOSTEL_NIGHT_PAYMENT_TRANSACTION\"", schema = ModelConstants.SCHEMA)
public class HostelNightPaymentTransactionEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 16)
    @NotNull
    @Column(name = "student_id", nullable = false, length = 16)
    private String studentId;

    @NotNull
    @Column(name = "hostel_id", nullable = false)
    private Long hostelId;

    @Column(name = "no_of_veg_coupon")
    private Integer noOfVegCoupon;

    @Column(name = "no_of_nonveg_coupon")
    private Integer noOfNonvegCoupon;

    @NotNull
    @Column(name = "veg_rate", nullable = false)
    private Double vegRate;

    @NotNull
    @Column(name = "nonveg_rate", nullable = false)
    private Double nonvegRate;

    @NotNull
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Size(max = 32)
    @NotNull
    @Column(name = "pay_by", nullable = false, length = 32)
    private String payBy;

    @Size(max = 50)
    @Column(name = "order_no", length = 50)
    private String orderNo;

    @Size(max = 64)
    @Column(name = "payment_type", length = 64)
    private String paymentType;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "paid_amount")
    private Double paidAmount;

    @Size(max = 50)
    @Column(name = "transaction_ref_number", length = 50)
    private String transactionRefNumber;

    @Size(max = 50)
    @Column(name = "ccav_reference_no", length = 50)
    private String ccavReferenceNo;

    @Size(max = 32)
    @Column(name = "payment_status", length = 32)
    private String paymentStatus;

    @Column(name = "trans_fee")
    private Double transFee;

    @Column(name = "service_tax")
    private Double serviceTax;

    @Column(name = "status_message", length = Integer.MAX_VALUE)
    private String statusMessage;

    @ColumnDefault("0")
    @Column(name = "retry_count")
    private Integer retryCount;

    @Size(max = 16)
    @Column(name = "mail_status", length = 16)
    private String mailStatus;

}