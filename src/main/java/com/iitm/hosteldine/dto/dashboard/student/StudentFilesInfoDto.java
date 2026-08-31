package com.iitm.hosteldine.dto.dashboard.student;

import com.iitm.hosteldine.model.dashboard.student.StudentFilesInfoEntity;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link StudentFilesInfoEntity}
 */
@Data
public class StudentFilesInfoDto implements Serializable {
    private Long idFileId;
    private String idStudentId;
    private Integer idRequestId;
    @Size(max = 256)
    private String filename;
    @Size(max = 256)
    private String description;
    private String activeFlag;
}