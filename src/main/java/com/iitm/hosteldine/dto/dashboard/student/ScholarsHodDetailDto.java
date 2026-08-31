package com.iitm.hosteldine.dto.dashboard.student;

import com.iitm.hosteldine.model.dashboard.student.ScholarsHodDetailEntity;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link ScholarsHodDetailEntity}
 */
@Data
public class ScholarsHodDetailDto implements Serializable {
    private Long id;
    @Size(max = 1024)
    private String departmentCode;
    @Size(max = 1024)
    private String departmentName;
    @Size(max = 128)
    private String hodName;
    @Size(max = 128)
    private String hodEmail;
}