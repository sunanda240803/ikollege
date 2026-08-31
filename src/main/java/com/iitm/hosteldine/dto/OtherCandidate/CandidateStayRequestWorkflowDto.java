package com.iitm.hosteldine.dto.OtherCandidate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CandidateStayRequestWorkflowDto {
    private Long id;
    private Long candidateId;
    private Long appointmentId;
    private Long stayId;
    private String category;
    private String authorityType;
    private Integer approvalLevel;
    private String validatorName;
    private String validatorEmail;
    private String approvalStatus;
    private String authenticationType;
    private String approvalNotes;
    private String rejectionDescription;
}