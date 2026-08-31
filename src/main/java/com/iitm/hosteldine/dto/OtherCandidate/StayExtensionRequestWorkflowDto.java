package com.iitm.hosteldine.dto.OtherCandidate;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StayExtensionRequestWorkflowDto {
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
    private LocalDateTime modifiedAt;
}