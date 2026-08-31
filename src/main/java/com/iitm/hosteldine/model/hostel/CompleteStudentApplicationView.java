package com.iitm.hosteldine.model.hostel;

import java.time.LocalDate;

import com.iitm.hosteldine.constant.ModelConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"COMPLETE_STUDENT_APPLICATION_VIEW\"", schema = ModelConstants.SCHEMA)
public class CompleteStudentApplicationView {
	
	@Column(name = "student_id", length = 30)
	private String studentId;

	@Id
	@Column(name = "request_id")
	private Long requestId;
	
	@Column(name = "student_name", length = 120)
	private String studentName;
	
	@Column(name = "dob", length = 30)
	private String dob;
	
	@Column(name = "gender", length = 1)
	private String gender;
	
	@Column(name = "city", length = 120)
	private String city;
	
	@Column(name = "state", length = 120)
	private String state;
	
	@Column(name = "pin_code")
	private Integer pinCode;
	
	@Column(name = "country")
	private String country;
	
	@Column(name = "student_mobile", length = 64)
	private String studentMobile;
	
	@Column(name = "student_address")
	private String studentAddress;
	
	@Column(name = "student_iitm_smail", length = 60)
	private String parentEmailId;
	
	@Column(name = "appointment_from")
	private LocalDate appointmentFrom;
	
	@Column(name = "appointment_To")
	private LocalDate appointmentTo;
	
	@Column(name = "stay_from")
	private LocalDate stayFrom;
	
	@Column(name = "stay_To")
	private LocalDate stayTo;
	
	@Column(name = "gross_pay")
	private Double grossPay;
	
	@Column(name = "category", length = 16)
	private String category;
	
	@Column(name = "category_others", length = 64)
	private String categoryOthers;
	
	@Column(name = "dining", length = 32)
	private String dining;
	
	@Column(name = "dining_others", length = 128)
	private String diningOthers;
	
	@Column(name = "occupancy", length = 64)
	private String occupancy;
	
	@Column(name = "validating_authority", length = 64)
	private String validatingAuthority;
	
	@Column(name = "validating_authority_email", length = 128)
	private String validatingAuthorityEmail;
	
	@Column(name = "status", length = 32)
	private String status;
	
	@Column(name = "reject_description", length = 1024)
	private String rejectDescription;
	
	@Column(name = "approval_date")
	private LocalDate approvalDate;

	@Column(name = "approval_notes")
	private String approvalNotes;

	@Column(name = "status_notes")
	private String statusNotes;

	@Column(name = "wrk_approval_notes")
	private String wrkApprovalNotes;

	@Column(name = "wrk_rejection_description")
	private String wrkRejectionDescription;

	@Column(name = "group_concat")
	private String groupConcat;
}
