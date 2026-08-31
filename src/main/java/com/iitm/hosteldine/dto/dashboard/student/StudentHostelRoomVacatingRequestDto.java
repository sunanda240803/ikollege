package com.iitm.hosteldine.dto.dashboard.student;

import com.iitm.hosteldine.dto.dean.PropertyDto;
import com.iitm.hosteldine.dto.dean.VacatingStudentInventoryDto;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentHostelRoomVacatingRequestDto {
    private Long id;
    private StudentDetailsInfoEntity student;
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
    private Boolean bicycleDeclaration;
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

    private String submittedFromDate;
    private String submittedToDate;
    private Integer hostelId;
    private String hostelName;
    private String studentName;
    private String studentId;
    private String userRole;
    private Integer approvalLevel;
    private String approvalEmail;
    private String username;
    private String status;
    private String authorityType;
    private Long requestId;
    private Integer roomNo;
    private Long penaltyAmount;
    private List<PropertyDto> actionList;
    private String approvalStatus;
    private String gender;
    private String dob;
    private String vacatingDateStr;
    private String exchangeProgPeriodFromDateStr;
    private String exchangeProgPeriodToDateStr;
    private int roomId;
    private String approvalStatusDetails;
    private int totalApprovalCount;
    private List<VacatingStudentInventoryDto> inventoryDtoList;
    private Long totalAmount;
    private String roomCondition;
    private String approverName;    
    private String seat;
}
