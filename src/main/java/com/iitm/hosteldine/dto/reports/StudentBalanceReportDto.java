package com.iitm.hosteldine.dto.reports;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for student balance report data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentBalanceReportDto {
    private Long id;
    private String studentId;
    private String studentName;
    private String hostelName;
    private String roomNumber;
    private BigDecimal totalFee;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private LocalDate lastPaymentDate;
    private String paymentStatus;
    private BigDecimal netBal;

    public StudentBalanceReportDto(String studentId, String studentName, String hostelName, String roomNumber,
            BigDecimal netBal) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.hostelName = hostelName;
        this.roomNumber = roomNumber;
        this.netBal = netBal;
        this.totalFee = BigDecimal.ZERO; // Default value, can be set later
        this.paidAmount = BigDecimal.ZERO; // Default value, can be set later
        this.balanceAmount = BigDecimal.ZERO; // Default value, can be set later
        this.lastPaymentDate = LocalDate.now(); // Default value, can be set later
        this.paymentStatus = "Pending"; // Default value, can be set later
    }
}