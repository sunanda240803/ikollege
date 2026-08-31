package com.iitm.hosteldine.dto.mess;

import java.time.LocalDate;

import lombok.Data;

@Data
public class MessBillSummaryDto {
	private Integer messPeriod;
	private Integer messName;
	private String studentId;
	private LocalDate effectiveFromDate;
	private String comments;
}
