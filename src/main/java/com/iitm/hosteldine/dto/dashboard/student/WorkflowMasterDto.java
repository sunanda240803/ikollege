package com.iitm.hosteldine.dto.dashboard.student;

import com.iitm.hosteldine.model.dashboard.student.WorkflowMasterEntity;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link WorkflowMasterEntity}
 */
@Data
public class WorkflowMasterDto implements Serializable {
    private Long id;
    @Size(max = 32)
    private String category;
    @Size(max = 32)
    private String authorityType;
    private Integer approvalLevel;
    @Size(max = 128)
    private String email;
    @Size(max = 64)
    private String validatorName;
    @Size(max = 1)
    private String authenticationType;
}