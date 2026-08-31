package com.iitm.hosteldine.dto.dashboard.student;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.model.dashboard.student.StudentAppointmentRequestEntity;
import com.iitm.hosteldine.validator.fieldValidators.ValidDateField;
import com.iitm.hosteldine.validator.fieldValidators.ValidStringField;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link StudentAppointmentRequestEntity}
 */
@Data
public class StudentDetailsPdfDto implements Serializable {
    private Long id;
    private String studentId;
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
    private String dining;
    private String validatingAuthority;
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
    private String requestId;
    private String firstName;
    private Long studentMobile;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate dob;
    private String gender;
    private String studentAddress;
    private String lastName;
    private String studentPersonalEmail;
    private String studentName;
    

}