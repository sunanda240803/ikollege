package com.iitm.hosteldine.dto.dashboard.student;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class HostelVacatingAllowedStudentDto {
    private String studentId;
    private Long id;
    private Integer studentBalance;
    private String accHead;
    private Double netBal;
    private Double hostelDepositAmt;
    private String duesPermissionRequired;
    private String hostelOrWardenApprovalStatus;
    private String wardenName;
    private String wardenEmail;
    private String emailAddress;
    private String facilityMasterName;
    private String hostelOfficeName;
}
