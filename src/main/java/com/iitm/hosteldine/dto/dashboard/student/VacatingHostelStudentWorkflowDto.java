package com.iitm.hosteldine.dto.dashboard.student;

import lombok.Data;

@Data
public class VacatingHostelStudentWorkflowDto {
    private Long id;
    private Long requestId;
    private String studentId;
    private String authorityType;
    private Integer approvalLevel;
    private String approvalEmail;
    private String approvalName;
    private String authenticationType;
    private String category;
    private String status;
}
