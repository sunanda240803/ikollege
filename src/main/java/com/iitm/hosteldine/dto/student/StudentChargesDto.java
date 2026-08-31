package com.iitm.hosteldine.dto.student;

import com.iitm.hosteldine.dto.hostel.StudentPurchaseEventDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentChargesDto {
    Long penaltyChargesAmount;
    Long donationAmount;
    Long cardAmount;
    Long hostelDeposit;;
    Long leadAmount;
    Long openingBalance;
    String studentID;
    String studentName;
    String roomNo;
    String hostelName;
    String hostelFirstName;
    String donatedTo;
    List<StudentPurchaseEventDto> eventList;
    String settlementFlag;
    String allocationStatus;
    Boolean isSettlementEligible;
    Boolean isUndoSettlementEligible;
    String studentPreviousID;
    List<StudentChargesDto> hdcList;
    Long hdcAmount;
    Long hdcId;
}
