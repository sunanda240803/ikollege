package com.iitm.hosteldine.dto.student.wellness;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.iitm.hosteldine.constant.Constants;

import lombok.Data;

@Data
public class StudentWellnessDto {

    private String studentId;
    private String studentName;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate referralDateFrom;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate referralDateTo;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate visitDateFrom;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate visitDateTo;
    private String referralType;
    private String concernType;
    private String coordinatorName;
    private String department;
    private String status;
    private String role;
    
    private String hostelName;
    private String roomNumber;
    private String contactNumber;
    private String studentEmail;
    private String visitDate;
    private int numberOfVisit;
    private String interationMode;
    private String concernsDiscussed;
    private String followUpDate;
    private String futureActionPlan;
    
    
	// Static constant for parameter keys
	public static final String[] PARAM_KEYS = { "studentId", "studentName", "referralDateFrom", "referralDateTo",
			"visitDateFrom", "visitDateTo", "referralType", "concernType", "coordinatorName", "department", "status" };

}
