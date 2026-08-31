package com.iitm.hosteldine.form.common;

import java.util.List;

import lombok.Data;

@Data
public class StudentDebitForm {

	private String referenceNumber;
	private double amount;
	private int studentCount;
	private String transferStatus;
	private String iKollegeTransferType;
	private String screenType;
	private String hostelName;
	private String description;
	private int numOfTransaction;
	private List<String> studentIds;
}
