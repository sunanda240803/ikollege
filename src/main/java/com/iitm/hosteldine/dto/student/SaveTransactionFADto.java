package com.iitm.hosteldine.dto.student;

import java.time.LocalDate;

import lombok.Data;

@Data
public class SaveTransactionFADto {
	
	private Long faTransactionId;
	private String screenType;
	private String transferType;
	private String hostelName;
	private String referenceNumber;
	private String description;
	private double totalAmount;
	private int numOfTransaction;
	private String transactionStatus;
	private LocalDate transferDate;
	private String debitOrCredit;
	private int schoolId;
}