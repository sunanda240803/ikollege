package com.iitm.hosteldine.dto.OtherCandidate;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;

import lombok.Data;

@Getter
@Setter
@Builder
public class StayExtensionRequestDto {
    private Long stayId;
    private Long candidateId;
    private Long appointmentId;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate stayFrom;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate stayTo;
    private Boolean dining;
    private String description;
    private String validatingAuthority;
    private String validatingAuthorityEmail;
    private String approvalStatus;
    private String rejectionDescription;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate approvalDate;
    private String statusNotes;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate resendDate;
    private String messOption;
    private Integer hostelId;
    private Integer roomNo;
    private List<HostelMasterDto> hostelList;
    private String hostelName;
}