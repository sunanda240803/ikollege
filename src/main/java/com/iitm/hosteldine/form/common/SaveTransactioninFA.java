package com.iitm.hosteldine.form.common;

import lombok.Data;

@Data
public class SaveTransactioninFA {

	private String referenceNumber;
	private double amount;
	private int studentCount;
	private String transferStatus;
	private String iKollegeTransferType;
	private String screenType;
	private String hostelName;
	private String description;
}
