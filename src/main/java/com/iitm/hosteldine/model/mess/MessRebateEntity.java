package com.iitm.hosteldine.model.mess;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"IIT_A_MESS_REBATE\"", schema = ModelConstants.SCHEMA)
public class MessRebateEntity extends CommonEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

    @Column(name = "student_id", nullable = false, length = 32)
    private String studentId;

    @Column(name = "rebate_from")
    private LocalDate rebateFrom;
    
    @Column(name = "rebate_to")
    private LocalDate rebateTo;

	@Column(name = "no_of_days")
	private Integer noOfDays;

	@Column(name = "guide_name", length = 50)
	private String guideName;

	@Column(name = "guide_email", length = 128)
	private String guideEmail;

	@Column(name = "rebate_reason", length = 1000)
	private String rebateReason;

	@Column(name = "cancel_status", nullable = false, length = 1)
	private String cancelStatus;

	@Column(name = "approval_status", length = 16)
	private String approvalStatus;

	@Column(name = "rebate_status", length = 16)
	private String rebateStatus;

	@Column(name = "claim_amount")
	private Double claimAmount;

	@Column(name = "validator_name", length = 50)
	private String validatorName;

	@Column(name = "validator_email", length = 128)
	private String validatorEmail;

	@Column(name = "rejection_description", length = 1024)
	private String rejectionDescription;

	@Column(name = "leave_from")
	private LocalDate leaveFrom;

	@Column(name = "leave_to")
	private LocalDate leaveTo;

	@Column(name = "approval_date")
	private LocalDate approvalDate;

	@Column(name = "rebate_dining_from")
	private LocalDate rebateDiningFrom;

	@Column(name = "rebate_dining_to")
	private LocalDate rebateDiningTo;

	@Column(name = "doc_status", length = 4)
	private String docStatus;

	@Column(name = "vacate_date")
	private LocalDate vacateDate;

	@Column(name = "hometown", length = 64)
	private String hometown;

	@Column(name = "self_dec_date")
	private LocalDate selfDecDate;

	@Column(name = "self_dec_signature", length = 64)
	private String selfDecSignature;

	@Column(name = "file_upload", length = 300)
	private String fileUpload;

}
