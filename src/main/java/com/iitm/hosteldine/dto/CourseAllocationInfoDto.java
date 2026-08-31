package com.iitm.hosteldine.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CourseAllocationInfoDto {
    private Integer courseAllocationId;
    private String studentId;
    private Integer courseId;
    private Integer coursePeriodId;
    private Integer sectionId;
    private Integer batchId;
    private LocalDate joiningDate;
    private Integer joiningPeriodId;
    private String studentRegistrationNumber;
}
