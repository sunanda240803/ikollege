package com.iitm.hosteldine.model.dashboard.student;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST\"", schema = ModelConstants.SCHEMA)
public class StudentHostelRoomVacatingRequestEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentDetailsInfoEntity student;

    @Size(max = 128)
    @Column(name = "acount_name", length = 128)
    private String acountName;

    @Column(name = "mobile_no")
    private Long mobileNo;

    @Size(max = 64)
    @Column(name = "email_id", length = 64)
    private String emailId;

    @Size(max = 128)
    @Column(name = "vacating_reason", length = 128)
    private String vacatingReason;

    @Column(name = "exchange_prog_period_from_date")
    private LocalDate exchangeProgPeriodFromDate;

    @Column(name = "exchange_prog_period_to_date")
    private LocalDate exchangeProgPeriodToDate;

    @Column(name = "vacating_date")
    private LocalDate vacatingDate;

    @Column(name = "student_address", length = Integer.MAX_VALUE)
    private String studentAddress;

    @Size(max = 32)
    @Column(name = "hostel_or_warden_name", length = 32)
    private String hostelOrWardenName;

    @Size(max = 64)
    @Column(name = "hostel_or_warden_approval_status", length = 64)
    private String hostelOrWardenApprovalStatus;

    @Size(max = 64)
    @Column(name = "caterer_approval_status", length = 64)
    private String catererApprovalStatus;

    @Column(name = "room_condition_declaration")
    private Boolean roomConditionDeclaration;

    @Column(name = "recollect_declaration")
    private Boolean recollectDeclaration;

    @Column(name = "recovery_dues_declaration")
    private Boolean recoveryDuesDeclaration;

    @Column(name = "bicycle_declaration")
    private Boolean bicycleDeclaration;

    @Column(name = "warden_room_verification_status")
    private Boolean wardenRoomVerificationStatus;

    @Column(name = "warden_penality_status")
    private Boolean wardenPenalityStatus;

    @Size(max = 32)
    @Column(name = "bank_account_no_one", length = 32)
    private String bankAccountNoOne;

    @Size(max = 64)
    @Column(name = "bank_name_one", length = 64)
    private String bankNameOne;

    @Size(max = 64)
    @Column(name = "branch_name_one", length = 64)
    private String branchNameOne;

    @Size(max = 32)
    @Column(name = "ifs_code_one", length = 32)
    private String ifsCodeOne;

    @Size(max = 32)
    @Column(name = "bank_location_one", length = 32)
    private String bankLocationOne;

    @Column(name = "bank_account_no_two")
    private Long bankAccountNoTwo;

    @Size(max = 64)
    @Column(name = "bank_name_two", length = 64)
    private String bankNameTwo;

    @Size(max = 64)
    @Column(name = "branch_name_two", length = 64)
    private String branchNameTwo;

    @Size(max = 32)
    @Column(name = "ifs_code_two", length = 32)
    private String ifsCodeTwo;

    @Size(max = 32)
    @Column(name = "bank_location_two", length = 32)
    private String bankLocationTwo;

    @Column(name = "donation_status")
    private Boolean donationStatus;

    @Column(name = "donation_amount")
    private Long donationAmount = 0L;

    @Column(name = "penality_amount")
    private Long penalityAmount;

    @Column(name = "donation_amount_collected")
    private Long donationAmountCollected;

    @Column(name = "penalty_amount_collected")
    private Long penaltyAmountCollected;

    @Column(name = "others_vacating_reason", length = Integer.MAX_VALUE)
    private String othersVacatingReason;

    @Size(max = 32)
    @Column(name = "place_of_visit", length = 32)
    private String placeOfVisit;

    @Size(max = 128)
    @Column(name = "recommended_by", length = 128)
    private String recommendedBy;

    @Size(max = 128)
    @Column(name = "checked_by", length = 128)
    private String checkedBy;

    @Size(max = 128)
    @Column(name = "employee_id", length = 128)
    private String employeeId;

    @Column(name = "furniture_status")
    private Boolean furnitureStatus;

    @Column(name = "dues_permission_required", length = Integer.MAX_VALUE)
    private String duesPermissionRequired;

    @Size(max = 32)
    @Column(name = "donator_type", length = 32)
    private String donatorType;

    @Size(max = 32)
    @Column(name = "others_description", length = 32)
    private String othersDescription;

    @Size(max = 64)
    @Column(name = "penalty_reason", length = 64)
    private String penaltyReason;

    @Column(name = "verification_charges")
    private Long verificationCharges;

    @Column(name = "inventory_charges")
    private Long inventoryCharges;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Size(max = 64)
    @Column(name = "donated_hostel", length = 64)
    private String donatedHostel;

    @Column(name = "rejoining_date")
    private LocalDate rejoiningDate;

    @Size(max = 8)
    @Column(name = "room_painting_type", length = 8)
    private String roomPaintingType;

}