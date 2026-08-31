package com.iitm.hosteldine.dto.mess;

import com.iitm.hosteldine.constant.DateUtility;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FoodCourtTransactionDto {
    public String voucherNo;
    public LocalDate voucherDate;
    public String studentId;
    public String messName;
    public LocalDate diningFromDate;
    public LocalDate diningToDate;
    public Double amount;
    public String debitOrCredit;
    public Double totalPurchaseAmount;
    public Double totalCreditAmount;
    public Double balanceAmount;
    public String voucherDateString;
    public String diningFromDateString;
    public String diningToDateString;
    private LocalDateTime purchaseDate;


    // Constructors, Getters and Setters
    public FoodCourtTransactionDto() {
    }

//    public FoodCourtTransactionDto(String voucherNo, String voucherDate, String studentId, String messName,
//                                   String diningFromDate, String diningToDate, Double amount,
//                                   String debitOrCredit, Double totalPurchaseAmount,
//                                   Double totalCreditAmount, Double balanceAmount) {
//        try {
//            this.voucherNo = voucherNo;
//            this.voucherDate = voucherDate != null ?  DateUtility.parseToLocalDate(voucherDate) : null;
//            this.studentId = studentId;
//            this.messName = messName;
//            this.diningFromDate = diningFromDate != null ? DateUtility.parseToLocalDate(diningFromDate) : null;
//            this.diningToDate = diningToDate != null ? DateUtility.parseToLocalDate(diningToDate) : null;
//            this.amount = amount;
//            this.debitOrCredit = debitOrCredit;
//            this.totalPurchaseAmount = totalPurchaseAmount;
//            this.totalCreditAmount = totalCreditAmount;
//            this.balanceAmount = balanceAmount;
//            this.voucherDateString = this.voucherDate != null ? DateUtility.formatDate(this.voucherDate) : null;
//            this.diningFromDateString = this.diningFromDate != null ? DateUtility.formatDate(this.diningFromDate) : null;
//            this.diningToDateString = this.diningToDate != null ? DateUtility.formatDate(this.diningToDate) : null;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    // Getters and Setters for all fields
    // ...
}
