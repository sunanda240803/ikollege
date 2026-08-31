package com.iitm.hosteldine.dto.OtherCandidate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CandidateStayDateViewDto {
    private CandidateStayDateIdDto id;
    private LocalDate createdAt;
    private LocalDate appointmentFrom;
    private LocalDate appointmentTo;
    private LocalDate stayFrom;
    private LocalDate stayTo;
    private String appStatus;
    private String stayStatus;
    private String statusNotes;
    private String rejectionDescription;
    private LocalDate resendDate;
    private Integer caseStatus;
    private String encryptedKey;
}