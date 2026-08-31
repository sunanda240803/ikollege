package com.iitm.hosteldine.dto.OtherCandidate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CandidateWorkflowDto {
    private Long id;
    private Long applicationId;
    private Long candidateId;
    private String authorityType;
    private Integer approvalLevel;
    private String email;
    private String validatorName;
    private String authenticationType;
    private String category;
    private String status;
    private String rejectionDescription;
    private String approvalNotes;
    private LocalDateTime modifiedAt;
    private Long stayId;
}