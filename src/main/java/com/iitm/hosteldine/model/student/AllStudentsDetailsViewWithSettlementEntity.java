package com.iitm.hosteldine.model.student;

import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "\"ALL_STUDENTS_DETAILS_VIEW_WITH_SETTLEMENT\"", schema = ModelConstants.SCHEMA)
public class AllStudentsDetailsViewWithSettlementEntity {

	@Id
	@Column(name = "student_id")
	private String studentId;
	
	@Column(name = "dept_name")
	private String deptName;
	
	@Column(name = "student_name")
	private String studentName;
	
	@Column(name = "student_status")
	private String studentStatus;
	
	@Column(name = "hostel_name")
	private String hostelName;
	
	@Column(name = "hostel_id")
	private Long hostelId;
	
	@Column(name = "room_number")
	private String roomNumber;
	
	@Column(name = "seat")
	private String seat;
	
	@Column(name = "room_id")
	private Long roomId;
	
	@Column(name = "room_allotment_id")
	private Long roomAllotmentId;

	@Column(name = "mess_period_id")
	private Long messPeriodId;

    @Column(name = "mess_id")
    private Long messId;

    @Column(name = "mess_name")
	private String messName;
	
	@Column(name = "dining_from_date")
	private LocalDate fromDate;
	
	@Column(name = "dining_to_date")
	private LocalDate toDate;
	
	@Column(name = "gender")
	private String gender;
	
	@Column(name = "dob")
	private LocalDate dob;
	
	@Column(name = "blood_group")
	private String bloodGroup;
	
	@Column(name = "category")
	private String category;
	
	@Column(name = "student_mobile")
	private Long studentMobile;
	
	@Column(name = "student_address")
	private String studentAddress;
	
	@Column(name = "previous_id")
	private String previousId;
	
	@Column(name = "aadhaar_number")
	private Long aadhaarNumber;
	
	@Column(name = "pan_number")
	private String panNumber;
	
	@Column(name = "guardian_status")
	private String guardianStatus;
	
	@Column(name = "signed_parent_name")
	private String signedParentName;
	
	@Column(name = "faculty_name")
	private String facultyName;
	
	@Column(name = "faculty_contact_no")
	private Long facultyContactNo;
	
	@Column(name = "faculty_email")
	private String facultyEmail;
	
	@Column(name = "settlement_flag")
	private String settlementFlag;

	@Column(name = "active_flag")
	private String activeFlag;

	@Column(name = "day_scholar")
	private String dayScholar;
	
	@Column(name = "vacation_category")
	private String vacationCategory;
	
	@Column(name = "is_missing")
	private Boolean isMissing;
	
	@Column(name = "hostel_office_email")
	private String hostelOfficeEmail;
	
	@Column(name = "auth")
	private String auth;
	
	@Column(name = "student_personal_email")
	private String studentPersonalEmail;
	
	@Column(name = "bio_data_id")
	private Long bioDataId;
	
	@Column(name = "application_number")
    private String applicationNumber;
	
	@Column(name = "student_iitm_smail")
    private String emailId;
	
	@Column(name = "city")
    private String city;
	
	@Column(name = "state")
    private String state;
	
	@Column(name = "country")
    private String country;
	
	@Column(name = "pin_code")
    private String pinCode;
	
	@Column(name = "mess_preference")
    private String messPreference;
	
}
