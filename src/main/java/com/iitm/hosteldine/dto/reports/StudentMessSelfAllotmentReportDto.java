package com.iitm.hosteldine.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentMessSelfAllotmentReportDto {
	
	private String studentId;
	private String studentName;
	private String createdAt;
	private String diningFromDate;
	private String diningToDate;
	private String messName;
	private String status;
	
	
}
