package com.iitm.hosteldine.form;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.iitm.hosteldine.dto.OtherCandidate.CandidateAppointmentRequestDto;
import com.iitm.hosteldine.dto.OtherCandidate.CandidateFilesInformationDto;
import com.iitm.hosteldine.dto.OtherCandidate.CandidateProfileDto;
import com.iitm.hosteldine.dto.OtherCandidate.CandidateWorkflowDto;
import com.iitm.hosteldine.dto.OtherCandidate.StayExtensionRequestDto;
import com.iitm.hosteldine.dto.OtherCandidate.StayExtensionRequestWorkflowDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StayExtensionRequestForm {
    private CandidateProfileDto candidateProfileDto;
    private CandidateAppointmentRequestDto candidateAppointmentRequestDto;
    private List<CandidateFilesInformationDto> candidateFilesInformationDto;
    private List<CandidateWorkflowDto> candidateWorkflowList;
    private StayExtensionRequestDto stayExtensionRequestDto;
    private StayExtensionRequestWorkflowDto requestWorkflowDto;
    private String encryptedKey;
    private MultipartFile file;
    private String fileDescription;
    private List<StayExtensionRequestWorkflowDto> workflowList;
}