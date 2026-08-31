package com.iitm.hosteldine.dto.dean;

import java.time.LocalDate;

import lombok.Data;

@Data
public class FilterCriteriaDto {
	private Integer hostelId;
	private String studentId;
	private String validationStatus;
	private String category;
	private String studentName;
	private String tabNo;
	private LocalDate fromDate;
	private LocalDate toDate;
	private LocalDate approvalFromDate;
	private LocalDate approvalToDate;
	private LocalDate submittedFromDate;
	private LocalDate submittedToDate;
	private LocalDate appointmentFromDate;
	private LocalDate appointmentToDate;
	private LocalDate stayFromDate;
	private LocalDate stayToDate;
	private String currentDayStayFlag;
	private Long messPeriod;
	private Long messName;
	private String registrationType;
}
