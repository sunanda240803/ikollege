package com.iitm.hosteldine.dto.collegeInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Integer;

@Data
public class CourseMasterDto {
    @JsonProperty("courseMasterId")
    private Long courseMasterId;
    @JsonProperty("courseMasterName")
    private String courseMasterName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("degreeAwarded")
    private String degreeAwarded;
    @JsonProperty("affiliation")
    private String affiliation;
    @JsonProperty("schoolId")
    private int schoolId;
    @JsonProperty("displayCount")
    private Integer displayCount;
    @JsonProperty("departmentId")
    private Integer departmentId;
    @JsonProperty("departmentName")
    private String departmentName;
    private String courseMasterHead;
}