package com.iitm.hosteldine.dto.collegeInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Long;

@Data
public class DepartmentDto {
    @JsonProperty("department_id")
    private Long departmentId;
    @JsonProperty("department_name")
    private String departmentName;
    @JsonProperty("department_description")
    private String departmentDescription;
    @JsonProperty("department_head")
    private String departmentHead;
    @JsonProperty("department_assistant_head")
    private String departmentAssistantHead;
    @JsonProperty("school_id")
    private int schoolId;
}