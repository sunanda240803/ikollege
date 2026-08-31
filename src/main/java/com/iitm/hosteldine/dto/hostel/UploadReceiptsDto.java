package com.iitm.hosteldine.dto.hostel;

import com.iitm.hosteldine.form.common.TransactionDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;

@Builder
@Getter
@Setter
public class UploadReceiptsDto extends TransactionDto {
    private String receiptType;
    private String feeTypes;
    private String bank;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String bankRefNo;
    private String studentName;
    private String roomNo;
    private String hostelName;
    private String hostelId;
    private String accNo;
    private Double ccwFee;
    private String sanctionPeriod;
    private String bookTypeFilter;
    private String feeForSemester;
    private String year;
    private Long rebateDays;
    private LocalDate transactionDate;
    private byte[] fileBytes;
    private String logTag;
    @Override
    public ArrayList<String> getErrorList() {
        if (super.getErrorList() == null) {
            super.setErrorList(new ArrayList<>());
        }
        return super.getErrorList();
    }
}
