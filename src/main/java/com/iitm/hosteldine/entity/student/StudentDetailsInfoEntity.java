package com.iitm.hosteldine.entity.student;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"STUDENT_DETAILS_INFO\"", schema = ModelConstants.SCHEMA)
public class StudentDetailsInfoEntity extends CommonEntity {
	@Id
	@Column(name = "student_id")
	private String studentId;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "gender")
	private String gender;

	@Column(name = "contact_number")
	private Long contactNumber;

	@Column(name = "day_scholar")
	private String dayScholar = ModelConstants.NO;

	@Column(name = "vacation_category")
	private String vacationCategory = ModelConstants.NO;

	@Column(name = "settlement_flag")
	private String settlementFlag = ModelConstants.NO;

	@Column(name = "previous_id")
	private String previousId;

	@Column(name = "hostel_status")
	private String hostelStatus = ModelConstants.YES;

	@Column(name = "parent_email_id")
	private String emailId;

    @Column(name = "student_address")
	private String studentAddress;

	@Column(name = "student_personal_email", length = 128)
	private String studentPersonalEmail;

	@Column(name = "city")
	private String city;

	@Column(name = "state")
	private String state;

	@Column(name = "pin_code")
	private Long pinCode;

	public StudentDetailsInfoEntity(StudentDetailsInfoEntity entity) {
		this.firstName = entity.getFirstName();
		this.lastName = entity.getLastName();
		this.gender = entity.getGender();
		this.contactNumber = entity.getContactNumber();
		this.dayScholar = entity.getDayScholar();
		this.vacationCategory = entity.getVacationCategory();
		this.settlementFlag = entity.getSettlementFlag();
		this.hostelStatus = entity.hostelStatus;
		this.emailId = entity.getEmailId();
		this.studentAddress = entity.getStudentAddress();
		this.studentPersonalEmail = studentAddress;
	}

	public StudentDetailsInfoEntity() {

	}

	public StudentDetailsInfoEntity(String studentId, String firstName, String lastName, String gender, Long contactNumber, String dayScholar, String vacationCategory, String settlementFlag, String previousId, String hostelStatus, String emailId, String studentAddress, String studentPersonalEmail) {
		this.studentId = studentId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.contactNumber = contactNumber;
		this.dayScholar = dayScholar;
		this.vacationCategory = vacationCategory;
		this.settlementFlag = settlementFlag;
		this.previousId = previousId;
		this.hostelStatus = hostelStatus;
		this.emailId = emailId;
		this.studentAddress = studentAddress;
		this.studentPersonalEmail = studentPersonalEmail;
	}
}
