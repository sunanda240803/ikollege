package com.iitm.hosteldine.dto.hostel;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CheckerApprovalCreditDebitDto {
    private Integer slNo;
    private String voucherNumber;
    private String description;
    private String voucherDate;
    private List<CheckerApprovalDto> debitRecord;
    private List<CheckerApprovalDto> creditRecord;
}
