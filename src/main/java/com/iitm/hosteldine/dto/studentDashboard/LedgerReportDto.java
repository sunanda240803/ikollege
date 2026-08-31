package com.iitm.hosteldine.dto.studentDashboard;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class LedgerReportDto {
    private String voucherNo;
    private LocalDate voucherDate;
    private String studentId;
    private String messName;
    private LocalDate messDiningFromDate;
    private LocalDate messDiningToDate;
    private Double amount;
    private String debitOrCredit;
    private Double totalPurchaseAmount;
    private Double totalCreditAmount;
    private String description;
    private Double balanceAmount;
    private String refNo;
    private Double closingBalance;
    private String formattedClosingBalance;
    private String closingDirection;
    private int slNo;
    private LocalDateTime createdAt;
}
