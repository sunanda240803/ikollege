package com.iitm.hosteldine.model;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"STUDENT_BIO_DATA_FORM_DETAILS\"", schema = ModelConstants.SCHEMA)
public class StudentBioDataFormDetailEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bio_data_id", nullable = false)
    private Long id;

    @Column(name = "application_number", length = 30)
    private String applicationNumber;

    @Column(name = "student_id", nullable = false, length = 30)
    private String studentId;

    @Column(name = "student_name", length = 60)
    private String studentName;

    @Column(name = "student_mobile")
    private Long studentMobile;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "gender", length = Integer.MAX_VALUE)
    private String gender;

    @Column(name = "hostel_name", length = 120)
    private String hostelName;

    @Column(name = "room_number", length = 32)
    private String roomNumber;

    @Column(name = "category", length = 120)
    private String category;

    @Column(name = "aadhaar_number")
    private Long aadhaarNumber;

    @Column(name = "pan_number", length = 32)
    private String panNum;

    @Column(name = "student_address", length = 300)
    private String studentAddress;

    @Column(name = "other_info", length = 300)
    private String otherInfo;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "place", length = 50)
    private String place;

    @Column(name = "mess_name", length = 120)
    private String messName;

    @Column(name = "image_location", length = Integer.MAX_VALUE)
    private String imageLocation;

    @Column(name = "image_bytes")
    private byte[] imageBytes;

    @Column(name = "guardian_status", length = 1)
    private String guardianStatus;

    @Column(name = "student_sign_location", length = Integer.MAX_VALUE)
    private String studentSignLocation;

    @Column(name = "student_sign_bytes")
    private byte[] studentSignBytes;

    @Column(name = "parent_sign_location", length = Integer.MAX_VALUE)
    private String parentSignLocation;

    @Column(name = "parent_sign_bytes")
    private byte[] parentSignBytes;

    @Column(name = "blood_group", length = Integer.MAX_VALUE)
    private String bloodGroup;

    @Column(name = "signed_parent_name", length = 64)
    private String signedParentName;

    @ColumnDefault("NULL")
    @Column(name = "faculty_name", length = 256)
    private String facultyName;

    @ColumnDefault("NULL")
    @Column(name = "faculty_email", length = 256)
    private String facultyEmail;

    @Column(name = "faculty_contact_no")
    private Long facultyContactNo;

	@Column(name = "student_personal_email", length = 128)
	private String studentPersonalEmail;

    @Column(name = "pwd")
    private String pwd;

    @Column(name = "pwd_percentage")
    private Integer pwdPercentage;

    @Column(name = "pwd_description", length = 300)
    private String pwdDescription;

}