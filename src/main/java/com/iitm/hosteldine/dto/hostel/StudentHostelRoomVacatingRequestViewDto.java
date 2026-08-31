package com.iitm.hosteldine.dto.hostel;

import lombok.Data;
import lombok.Builder;
import java.lang.Long;
import java.time.LocalDate;
import java.lang.Boolean;
import java.lang.Integer;

@Data
@Builder
public class StudentHostelRoomVacatingRequestViewDto {
    private Long id;
    private String studentId;
    private String acountName;
    private Long mobileNo;
    private String emailId;
    private String vacatingReason;
    private LocalDate exchangeProgPeriodFromDate;
    private LocalDate exchangeProgPeriodToDate;
    private LocalDate vacatingDate;
    private String studentAddress;
    private String hostelOrWardenName;
    private String hostelOrWardenApprovalStatus;
    private String catererApprovalStatus;
    private Boolean roomConditionDeclaration;
    private Boolean recollectDeclaration;
    private Boolean recoveryDuesDeclaration;
    private Boolean wardenRoomVerificationStatus;
    private Boolean wardenPenalityStatus;
    private String bankAccountNoOne;
    private String bankNameOne;
    private String branchNameOne;
    private String ifsCodeOne;
    private String bankLocationOne;
    private Long bankAccountNoTwo;
    private String bankNameTwo;
    private String branchNameTwo;
    private String ifsCodeTwo;
    private String bankLocationTwo;
    private Boolean donationStatus;
    private Long donationAmount;
    private Long penalityAmount;
    private Integer schoolId;
    private Long donationAmountCollected;
    private Long penaltyAmountCollected;
    private String othersVacatingReason;
    private String placeOfVisit;
    private String recommendedBy;
    private String checkedBy;
    private String employeeId;
    private Boolean furnitureStatus;
    private String duesPermissionRequired;
    private String donatorType;
    private String othersDescription;
    private String penaltyReason;
    private Long verificationCharges;
    private Long inventoryCharges;
    private LocalDate approvalDate;
    private String donatedHostel;
    private LocalDate rejoiningDate;
    private String roomPaintingType;
}