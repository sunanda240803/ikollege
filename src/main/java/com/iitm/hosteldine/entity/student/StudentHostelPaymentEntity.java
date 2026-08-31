package com.iitm.hosteldine.entity.student;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"STUDENT_HOSTEL_PAYMENTS\"", schema = "schooldev")
public class StudentHostelPaymentEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max =64)
    @Column(name ="student_id")
    private String studentId;

    @Size(max = 64)
    @Column(name = "payment_type", length = 64)
    private String paymentType;

    @Size(max = 64)
    @Column(name = "payment_reference_no", length = 64)
    private String paymentReferenceNo;

    @Column(name = "payment_amount")
    private Integer paymentAmount;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "check_no")
    private Integer checkNo;

    @Size(max = 128)
    @Column(name = "bank_name", length = 128)
    private String bankName;

    @Size(max = 64)
    @Column(name = "ifsc_code", length = 64)
    private String ifscCode;

    @Size(max = 128)
    @Column(name = "branch_name", length = 128)
    private String branchName;

    @Column(name = "loan_acc_no")
    private Long loanAccNo;

    @Size(max = 64)
    @Column(name = "student_confirm_status", length = 64)
    private String studentConfirmStatus;

    @Size(max = 64)
    @Column(name = "hosteloffice_enrollment", length = 64)
    private String hostelOfficeEnrollment;

    @Size(max = 64)
    @Column(name = "payment_status", length = 64)
    private String paymentStatus;

    @Size(max = 128)
    @Column(name = "hosteloffice_rejection_reason", length = 128)
    private String hostelOfficeRejectionReason;

    @Column(name = "student_balance")
    private Double studentBalance;

    @Size(max = 64)
    @Column(name = "override_and_approve", length = 64)
    private String overrideApprove;

    @Size(max = 128)
    @Column(name = "approved_or_rejected_by", length = 128)
    private String approvedOrRejectedBy;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

}