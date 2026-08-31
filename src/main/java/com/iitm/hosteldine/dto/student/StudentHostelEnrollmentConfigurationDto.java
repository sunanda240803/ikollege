package com.iitm.hosteldine.dto.student;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for {@link com.iitm.hosteldine.model.student.StudentHostelEnrollmentConfigurationEntity}
 */
@Getter
@Setter
@ToString
@Builder
public class StudentHostelEnrollmentConfigurationDto{
    private Long id;
    private Double msPhsAmount;
    private Double bTechAmount;
    private Double scStAmount;
    private String description;
}