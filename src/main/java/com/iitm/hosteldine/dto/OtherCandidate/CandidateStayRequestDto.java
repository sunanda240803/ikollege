package com.iitm.hosteldine.dto.OtherCandidate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CandidateStayRequestDto {
    private Long stayId;
    private Long candidateId;
    private Long appointmentId;
    private LocalDate stayFrom;
    private LocalDate stayTo;
    private Boolean dining;
    private String description;
    private String validatingAuthority;
    private String validatingAuthorityEmail;
    private String approvalStatus;
    private String rejectionDescription;
    private LocalDate approvalDate;
    private String statusNotes;
    private LocalDateTime resendDate;
    private String messOption;
    private Integer hostelId;
    private Integer roomNo;

}