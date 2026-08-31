package com.iitm.hosteldine.dto.OtherCandidate;

import com.iitm.hosteldine.constant.Constants;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CandidateAppointmentRequestDto {
    private Long id;
    private Long candidateId;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate appointmentFrom;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate appointmentTo;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate stayFrom;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate stayTo;
    private Double grossPay;
    private String category;
    private String categoryOthers;
    private Boolean dining;
    private String occupancy;
    private String validatingAuthority;
    private String validatingAuthorityEmail;
    private Boolean hostelManagement;
    private Boolean documentsUploaded;
    private Boolean applicableCharges;
    private String approvalStatus;
    private String rejectionDescription;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate approvalDate;
    private LocalDateTime resendDate;
    private String statusNotes;
    private String programDept;
    private String applicationNo;
    private String purpose;
    private Long accomPriority;
    private String messOption;

    private String newCategory;
    private String newCategoryOthers;
    private String notes;
}