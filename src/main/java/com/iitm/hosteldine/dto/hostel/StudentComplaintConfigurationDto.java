package com.iitm.hosteldine.dto.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.validator.fieldValidators.ValidStringField;
import lombok.Data;
import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.iitm.hosteldine.model.student.StudentComplaintConfigurationEntity}
 */
@Data
public class StudentComplaintConfigurationDto implements Serializable {
    private Long id;
    @ValidStringField(message = "message.validation.complaint.type.required",fieldName = "message.label.complaint.type",min = 3,max =64)
    private String complaintType;
    private String complaintName;
    private String configMailId;
    private Integer complaintOrder;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDateTime createdAt;
}