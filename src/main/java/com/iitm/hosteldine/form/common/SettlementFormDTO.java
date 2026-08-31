package com.iitm.hosteldine.form.common;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.hostel.AccountHeadDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementFormDTO {
    Integer settlementCount;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    LocalDate vacatingDate;
    String clearanceReason;
    List<AccountHeadDto> bankList;
    String bankId="SBI";
    String bankName="";
    String StudentId;
    Double amount;
    String messCard;
    Integer ddNo;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    LocalDate ddDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    LocalDate systemDate;
    String accountHead;
    Integer hostelId;
    String hostelName;
    String description;
    Boolean duesIfAny;
    List<String> clearanceReasonList;
    String referenceNo;
}
