package com.iitm.hosteldine.dto.hostel;

import lombok.Data;
import java.lang.Long;
import java.lang.Integer;
import java.time.LocalDate;
import java.lang.Double;
import java.time.LocalDateTime;

@Data
public class CompleteStudentApplicationViewDto {
    private String studentId;
    private Long requestId;
    private String studentName;
    private String dob;
    private String gender;
    private String taluk;
    private String district;
    private String city;
    private String state;
    private Integer pinCode;
    private Long contactNumber;
    private String studentMobile;
    private String studentAddress;
    private String parentEmailId;
    private LocalDate appointmentFrom;
    private LocalDate appointmentTo;
    private LocalDate stayFrom;
    private LocalDate stayTo;
    private Double grossPay;
    private String category;
    private String categoryOthers;
    private String dining;
    private String diningOthers;
    private String occupancy;
    private String validatingAuthority;
    private String validatingAuthorityEmail;
    private String status;
    private String rejectDescription;
    private LocalDate approvalDate;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String approvalNotes;
    private String activeFlag;
    private Integer schoolId;
    private String statusNotes;
    private String wrkApprovalNotes;
    private String wrkRejectionDescription;
    private String groupConcat;
}