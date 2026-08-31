package com.iitm.hosteldine.dto.student.wellness;

import lombok.Data;
import lombok.Builder;
import java.lang.Long;

@Data
@Builder
public class DostElectionDepartmentDto {
    private Long id;
    private String deptCode;
    private String altDeptCode;
    private String deptName;
    private String altDeptCode2;
}