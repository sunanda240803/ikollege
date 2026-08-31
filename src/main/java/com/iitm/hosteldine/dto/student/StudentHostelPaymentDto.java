package com.iitm.hosteldine.dto.student;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.dashboard.student.HostelPaymentTypeEnum;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.iitm.hosteldine.entity.student.StudentHostelPaymentEntity}
 */
@Data
public class StudentHostelPaymentDto implements Serializable {
    private Long id;
    private String studentId;
    private HostelPaymentTypeEnum paymentType;
    private String paymentReferenceNo;
    private Integer paymentAmount;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate paymentDate;
    private Integer checkNo;
    private String bankName;
    private String ifscCode;
    private String branchName;
    private Long loanAccNo;
    private String studentConfirmStatus;
    private String hostelOfficeEnrollment;
    private String paymentStatus;
    private String hostelOfficeRejectionReason;
    private Double studentBalance;
    private String overrideApprove;
    private String approvedOrRejectedBy;
    private LocalDate approvalDate;
}