package com.iitm.hosteldine.dto.dashboard.student;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.model.dashboard.student.StudentAppointmentRequestEntity;
import com.iitm.hosteldine.validator.fieldValidators.ValidDateField;
import com.iitm.hosteldine.validator.fieldValidators.ValidStringField;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DTO for {@link StudentAppointmentRequestEntity}
 */
@Data
public class StudentAppointmentRequestDto implements Serializable {
    private Long id;
    private String studentId;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate appointmentFrom;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate appointmentTo;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    @ValidDateField(message = "message.validation.stay.from.date.required", fieldName = "message.label.stay.from")
    private LocalDate stayFrom;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    @ValidDateField(message = "message.validation.stay.to.date.required", fieldName = "message.label.stay.to")
    private LocalDate stayTo;
    private Double grossPay;
    @ValidStringField(message = "message.validation.category.required",fieldName = "message.label.category",min = 3,max =64)
    private String category;
    private String categoryOthers;
    private String dining;
    @ValidStringField(message = "message.validation.guide.name.required",fieldName = "message.label.guide.name",min = 3,max =64)
    private String validatingAuthority;
    @ValidStringField(message = "message.validation.email.id.required",fieldName = "message.label.guide.email",min = 3,max =64)
    private String validatingAuthorityEmail;
    private Boolean hostelRules;
    private Boolean applicableCharges;
    private String status;
    private String diningOthers;
    private String occupancy;
    private String rejectDescription;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate approvalDate;
    private String approvalNotes;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate resendDate;
    private String statusNotes;
    private String purpose;
    private String thesisStatus;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate thesisSubmittedDate;
    @ValidStringField(message = "message.validation.hod.name.required",fieldName = "message.label.hod.name",min = 3,max =64)
    private String hodName;
    @ValidStringField(message = "message.validation.hod.email.required",fieldName = "message.label.hod.name",min = 3,max =64)
    private String hodEmail;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    @ValidDateField(message = "message.validation.admission.date.required", fieldName = "message.label.admission.date")
    private LocalDate admissionDate;
    private String cancelDescription;
    private String msStudent;
    private String phdStudent;
    private List<StudentWorkflowDto> studentWorkflowDto;
    private Integer stayRequestId;
    private LocalDateTime modifiedAt;
    private StudentWorkflowDto studentWorkflow;
    private String approvalStatus;
    private String studentRemarks;
    private String encryptedString;
    private String rejectionReason;
    private String tabNo;
    private List<StudentFilesInfoDto> studentFilesInfoDtoList;
    private String studentMailContent;
    private String mailSubject;

    private String requestId;
    private String studentName;
    private LocalDate dob;
    private String gender;
    private String studentIITMSmail;
    private String roomId;
    private String buildingId;
    private String createdAt;
    private String createdBy;
    private String modifiedBy;
    private String authorityType;
    private Long approvalLevel;
    private String authenticationType;
    private MultipartFile[] file;
    private List<String> fileDescription = new ArrayList<>(Arrays.asList("", "", "", ""));
    private boolean resendMailStatus;

}