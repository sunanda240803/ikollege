package com.iitm.hosteldine.dto.dashboard.student;

import com.iitm.hosteldine.model.dashboard.student.StudentWorkflowEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link StudentWorkflowEntity}
 */
@Data
public class StudentWorkflowDto implements Serializable {
    private Long id;
    private Long requestId;
    @Size(max = 32)
    private String studentId;
    @Size(max = 32)
    private String authorityType;
    private Integer approvalLevel;
    @Size(max = 128)
    private String validatorEmail;
    @Size(max = 64)
    private String validatorName;
    @Size(max = 1)
    private String authenticationType;
    @Size(max = 32)
    private String category;
    @NotNull
    @Size(max = 64)
    private String status;
    @Size(max = 1024)
    private String rejectDescription;
    @Size(max = 1024)
    private String approvalNotes;
    private LocalDateTime modifiedAt;
    private LocalDateTime createdAt;
    private List<StudentWorkflowDto> workFlowList;

}