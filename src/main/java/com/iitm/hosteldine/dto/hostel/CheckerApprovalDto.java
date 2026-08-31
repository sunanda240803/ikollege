package com.iitm.hosteldine.dto.hostel;

import com.iitm.hosteldine.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class CheckerApprovalDto {
    private String voucherNumber;
    private String voucherDate;
    private Double amount;
    private String accountHead;
    private String description;
    private String debitOrCredit;
    private Integer slNo;
    private String screenType;

	public CheckerApprovalDto() {
	}

    public CheckerApprovalDto(String voucherNumber, LocalDate voucherDate, Double amount, String accountHead, String description, String debitOrCredit
                            ,String screenType) {
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
        this.voucherNumber = voucherNumber;
        this.voucherDate = voucherDate != null ? voucherDate.format(DATE_TIME_FORMATTER): "N/A";
        this.amount = amount;
        this.accountHead = accountHead;
        this.description = description;
        this.debitOrCredit = debitOrCredit;
        this.screenType = screenType;
    }
}