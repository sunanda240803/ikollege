package com.iitm.hosteldine.dto.dean;

import com.iitm.hosteldine.constant.Constants;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class HostelEnrollmentDto {
    private Double balance;
    private Long id;
    private String studentId;
    private String studentName;
    private String facilityMasterName;
    private Long roomNo;
    private String subRoomId;
    private String messHead;
    private String paymentReferenceNo;
    private String paymentDate;
    private Double paymentAmount;
    private String hostelOfficeEnrollment;
    private String pushStatus;
    private String overrideAndApprove;
    private String approvedBy;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate approvalDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate lastPaymentDate;
    private Double totalPaymentAmount;
    private String viewStudentId;
    private String studentStatus;
    private String bioMetricStatus;
    private List<PropertyDto> actionList;
}