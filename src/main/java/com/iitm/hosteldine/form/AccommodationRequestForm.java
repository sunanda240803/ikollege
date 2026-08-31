package com.iitm.hosteldine.form;

import com.iitm.hosteldine.dto.OtherCandidate.*;
import com.iitm.hosteldine.dto.dean.OtherCandidateRequestDto;
import com.iitm.hosteldine.form.common.FileForm;
import com.iitm.hosteldine.service.OtherCandidate.CurrentStayExtensionDetails;
import com.iitm.hosteldine.service.OtherCandidate.PreviousStayExtensionDetails;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AccommodationRequestForm {
    private CandidateProfileDto candidateProfileDto;
    private CandidateAppointmentRequestDto candidateAppointmentRequestDto;
    private List<CandidateFilesInformationDto> candidateFilesInformationList;
    private List<CandidateWorkflowDto> candidateWorkflowList;
    private String encryptedKey;
    private List<FileForm> files;
    private OtherCandidateRequestDto otherCandidateRequestDto;
    private String approvalStatus;
    private String emailStatus;
    private String rejectReason;
    private List<PreviousStayExtensionDetails> previousStayExtensionDetailsList;
    private CurrentStayExtensionDetails currentStayExtensionDetails;
    private StayExtensionRequestForm stayExtensionRequestForm;
}