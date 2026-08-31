package com.iitm.hosteldine.dto.hostel;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.iitm.hosteldine.constant.Constants;

import lombok.Data;

@Data
public class GeneralLedgerDto {

	private Long hostelId;
	private String accHead;
	private String bookType;
	@DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
	private LocalDate fromDate;
	@DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
	private LocalDate toDate;

	public static final String[] PARAM_KEYS = { "accHead", "hostelId", "fromDate", "toDate", "bookType" };

	@DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
	private LocalDate voucherDate;
	private String voucherNo;
	private String description;
	private String roomNo;
	private String hostelName;
	private String messName;
	private String refAccount;
	private String refSubAccount;
	private String studentName;
	private String creditOrDebit;
	private String debit;
	private String credit;
	private String closingBal;
	private Integer bf;
	private Integer ln;
	private Integer dn;
	private Integer sn;
	private Integer payableCount;
	
	private boolean isSummaryRow;
	private String totalDebitForTheDay;
	private String totalCreditForTheDay;
	private String closingBalanceForTheDay;
	private String summaryType;
}
