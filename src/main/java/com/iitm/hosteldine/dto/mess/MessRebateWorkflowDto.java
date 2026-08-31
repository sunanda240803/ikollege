package com.iitm.hosteldine.dto.mess;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Long;
import java.lang.Integer;

@Data
public class MessRebateWorkflowDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("requestId")
    private Long requestId;
    @JsonProperty("studentId")
    private String studentId;
    @JsonProperty("authorityType")
    private String authorityType;
    @JsonProperty("approvalLevel")
    private Integer approvalLevel;
    @JsonProperty("guideName")
    private String guideName;
    @JsonProperty("guideEmail")
    private String guideEmail;
    @JsonProperty("authenticationType")
    private String authenticationType;
    @JsonProperty("approvalStatus")
    private String approvalStatus;
    @JsonProperty("cancelStatus")
    private String cancelStatus;
    @JsonProperty("rejectionDescription")
    private String rejectionDescription;
    @JsonProperty("approvalNotes")
    private String approvalNotes;
}