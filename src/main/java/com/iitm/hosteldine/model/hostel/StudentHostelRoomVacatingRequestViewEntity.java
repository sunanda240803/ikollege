package com.iitm.hosteldine.model.hostel;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "\"IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST_VIEW\"", schema = ModelConstants.SCHEMA)
public class StudentHostelRoomVacatingRequestViewEntity extends CommonEntity {

	@Id
	@Column(name = "id")
	private Long id;
	
	@Column(name = "student_id")
	private String studentId;
	
	@Column(name = "acount_name")
	private String acountName;
	
	@Column(name = "mobile_no")
	private Long mobileNo;
	
	@Column(name = "email_id")
	private String emailId;
	
	@Column(name = "vacating_reason")
	private String vacatingReason;
	
	@Column(name = "exchange_prog_period_from_date")
	private LocalDate exchangeProgPeriodFromDate;
	
	@Column(name = "exchange_prog_period_to_date")
	private LocalDate exchangeProgPeriodToDate;
	
	@Column(name = "vacating_date")
	private LocalDate vacatingDate;
	
	@Column(name = "student_address")
	private String studentAddress;
	
	@Column(name = "hostel_or_warden_name")
	private String hostelOrWardenName;
	
	@Column(name = "hostel_or_warden_approval_status")
	private String hostelOrWardenApprovalStatus;
	
	@Column(name = "caterer_approval_status")
	private String catererApprovalStatus;
	
	@Column(name = "room_condition_declaration")
	private Boolean roomConditionDeclaration;
	
	@Column(name = "recollect_declaration")
	private Boolean recollectDeclaration;
	
	@Column(name = "recovery_dues_declaration")
	private Boolean recoveryDuesDeclaration;
	
	@Column(name = "warden_room_verification_status")
	private Boolean wardenRoomVerificationStatus;
	
	@Column(name = "warden_penality_status")
	private Boolean wardenPenalityStatus;
	
	@Column(name = "bank_account_no_one")
	private String bankAccountNoOne;
	
	@Column(name = "bank_name_one")
	private String bankNameOne;
	
	@Column(name = "branch_name_one")
	private String branchNameOne;
	
	@Column(name = "ifs_code_one")
	private String ifsCodeOne;
	
	@Column(name = "bank_location_one")
	private String bankLocationOne;
	
	@Column(name = "bank_account_no_two")
	private Long bankAccountNoTwo;
	
	@Column(name = "bank_name_two")
	private String bankNameTwo;
	
	@Column(name = "branch_name_two")
	private String branchNameTwo;
	
	@Column(name = "ifs_code_two")
	private String ifsCodeTwo;
	
	@Column(name = "bank_location_two")
	private String bankLocationTwo;
	
	@Column(name = "donation_status")
	private Boolean donationStatus;
	
	@Column(name = "donation_amount")
	private Long donationAmount;
	
	@Column(name = "penality_amount")
	private Long penalityAmount;
	
	@Column(name = "school_id")
	private Integer schoolId;
	
	@Column(name = "donation_amount_collected")
	private Long donationAmountCollected;
	
	@Column(name = "penalty_amount_collected")
	private Long penaltyAmountCollected;
	
	@Column(name = "others_vacating_reason")
	private String othersVacatingReason;
	
	@Column(name = "place_of_visit")
	private String placeOfVisit;
	
	@Column(name = "recommended_by")
	private String recommendedBy;
	
	@Column(name = "checked_by")
	private String checkedBy;
	
	@Column(name = "employee_id")
	private String employeeId;
	
	@Column(name = "furniture_status")
	private Boolean furnitureStatus;
	
	@Column(name = "dues_permission_required")
	private String duesPermissionRequired;
	
	@Column(name = "donator_type")
	private String donatorType;
	
	@Column(name = "others_description")
	private String othersDescription;
	
	@Column(name = "penalty_reason")
	private String penaltyReason;
	
	@Column(name = "verification_charges")
	private Long verificationCharges;
	
	@Column(name = "inventory_charges")
	private Long inventoryCharges;
	
	@Column(name = "approval_date")
	private LocalDate approvalDate;
	
	@Column(name = "donated_hostel")
	private String donatedHostel;
	
	@Column(name = "rejoining_date")
	private LocalDate rejoiningDate;
	
	@Column(name = "room_painting_type")
	private String roomPaintingType;
	
}
