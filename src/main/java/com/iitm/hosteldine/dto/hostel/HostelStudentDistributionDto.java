package com.iitm.hosteldine.dto.hostel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostelStudentDistributionDto {
    private Long hostelId;
    private String hostelName;
    private String courseCode;
    private String courseName;
    private String batchYear;
    private Long studentCount;
}
