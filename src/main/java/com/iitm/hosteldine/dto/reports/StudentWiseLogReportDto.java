package com.iitm.hosteldine.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentWiseLogReportDto {
	
	private Long id;
	private String studentId;
	private String studentName;
	private String terminal_location;
	private String refId;
	private String swipeDate;
	private String swipeTime;
	private String swipeDay;
}
