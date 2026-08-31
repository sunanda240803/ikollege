package com.iitm.hosteldine.dto.hostel;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShowWiseReportDto {
    private Integer purchasedCount;
    private String studentId;
    private String studentName;
    private String hostelName;
    private String roomNo;
    private Long totalPurchase;
    private String createdAt;
    private Long claimAmount;
    private Long balanceAmount;
    private String showName;
    private String seatName;
    private String printingName;


    public ShowWiseReportDto(Integer purchasedCount, String studentId, String studentName,
                           String hostelName, String roomNo, String seatName, Long totalPurchase, String createdAt,
                           String showName, String printingName) {
        this.purchasedCount = purchasedCount;
        this.studentId = studentId;
        this.studentName = studentName;
        this.hostelName = hostelName;
        this.roomNo = roomNo;
        this.seatName = seatName;
        this.totalPurchase = totalPurchase;
        this.createdAt = createdAt;
        this.showName = showName;
        this.printingName = printingName;
    }

}

