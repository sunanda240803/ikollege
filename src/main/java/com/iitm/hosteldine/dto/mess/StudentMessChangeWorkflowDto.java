package com.iitm.hosteldine.dto.mess;

import lombok.Data;
import lombok.Builder;
import java.lang.Long;
import java.time.LocalDate;

@Data
@Builder
public class StudentMessChangeWorkflowDto {
    private Long id;
    private String studentId;
    private LocalDate originallyMessFromDate;
    private LocalDate originallyMessToDate;
    private Long originallyMessId;
    private LocalDate requestedMessFromDate;
    private LocalDate requestedMessToDate;
    private Long requestedMessId;
    private String approvedBy;
    private String approvalStatus;
    private String approvalDate;
    private String requestedDate;
    private String description;
    private String description1;
    private Long mmcId;
}