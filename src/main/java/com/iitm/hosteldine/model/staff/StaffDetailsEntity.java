package com.iitm.hosteldine.model.staff;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "\"FACULTY_PERSONAL_DETAILS\"", schema = ModelConstants.SCHEMA)
public class StaffDetailsEntity extends CommonEntity {

//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Id
    @Column(name = "faculty_id", length = 32, nullable = false)
    private String facultyId;

    @Column(name = "first_name", length = 32, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 32, nullable = false)
    private String lastName;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(name = "gender", length = 16, nullable = false)
    private String gender;

    @Column(name = "birth_place", length = 64)
    private String birthPlace;

    @Column(name = "marital_status", length = 64)
    private String maritalStatus;

    @Column(name = "contact_number")
    private Long contactNumber;

    @Column(name = "mobile_number")
    private Long mobileNumber;

    @Column(name = "email_address", length = 64)
    private String emailAddress;

    @Column(name = "address_one", length = 128)
    private String addressOne;

    @Column(name = "teaching_or_nonteaching", length = 32)
    private String teachingOrNonTeaching;

    @Column(name = "date_of_joining")
    @Temporal(TemporalType.DATE)
    private Date dateOfJoining;

    @Column(name = "v_fpd_designation", length = 32)
    private String vFpdDesignation;

    @Column(name = "employment_type", length = 32)
    private String employmentType;

    @Column(name = "designation_id")
    private Long designationId;

//    @Column(name = "middle_name", length = 32)
//    private String middleName;

//    @Column(name = "religion", length = 64)
//    private String religion;

//    @Column(name = "caste", length = 64)
//    private String caste;

//    @Column(name = "address_two", length = 64)
//    private String addressTwo;
//
//    @Column(name = "address_three", length = 64)
//    private String addressThree;
//
//    @Column(name = "city", length = 64)
//    private String city;
//
//    @Column(name = "state", length = 64)
//    private String state;
//
//    @Column(name = "taluk", length = 64)
//    private String taluk;
//
//    @Column(name = "district", length = 64)
//    private String district;
//
//    @Column(name = "country", length = 64)
//    private String country;

//    @Column(name = "std_code")
//    private Integer stdCode;
//
//    @Column(name = "pin_code")
//    private Integer pinCode;

//    @Column(name = "accommodation", length = 32)
//    private String accommodation;
//
//    @Column(name = "mode_of_transport", length = 32)
//    private String modeOfTransport;

//    @Column(name = "driver_license_no", length = 32)
//    private String driverLicenseNo;

//    @Column(name = "pickup_route_id")
//    private Long pickupRouteId;

//    @Column(name = "driver_license_valid_upto", length = 32)
//    private String driverLicenseValidUpto;

//    @Column(name = "drop_route_id")
//    private Integer dropRouteId;

//    @Column(name = "stopping_name", length = 64)
//    private String stoppingName;

//    @Column(name = "faculty_level_id", nullable = false)
//    private Integer facultyLevelId;

//    @Column(name = "from_date")
//    @Temporal(TemporalType.DATE)
//    private Date fromDate;
//
//    @Column(name = "to_date")
//    @Temporal(TemporalType.DATE)
//    private Date toDate;

//    @Column(name = "city_id")
//    private Long cityId;
//
//    @Column(name = "state_id")
//    private Long stateId;
//
//    @Column(name = "country_id")
//    private Long countryId;

//    @Column(name = "file_no", length = 32)
//    private String fileNo;

//    @Column(name = "date_of_death")
//    @Temporal(TemporalType.DATE)
//    private Date dateOfDeath;

//    @Column(name = "emp_left", length = 1)
//    private String empLeft;

//    @Column(name = "leaving_reason", length = 120)
//    private String leavingReason;

//    @Column(name = "date_of_leaving")
//    @Temporal(TemporalType.DATE)
//    private Date dateOfLeaving;

//    @Column(name = "date_of_confirmation")
//    @Temporal(TemporalType.DATE)
//    private Date dateOfConfirmation;

//    @Column(name = "continuous_period")
//    private Integer continuousPeriod;

//    @Column(name = "max_period")
//    private Integer maxPeriod;

//    @Column(name = "library_card_id", length = 32, unique = true)
//    private String libraryCardId;

//    @Column(name = "department", length = 128)
//    private String department;

//    @Column(name = "date_of_retirement")
//    @Temporal(TemporalType.DATE)
//    private Date dateOfRetirement;

//    @Column(name = "staff_photo_status", length = 32)
//    private String staffPhotoStatus;

//    @Column(name = "blood_group", length = 30)
//    private String bloodGroup;

//    @Column(name = "pan_number", length = 30)
//    private String panNumber;

//    @Column(name = "photo_status", length = 32)
//    private String photoStatus;

//    @Column(name = "staff_photo_name", length = 256)
//    private String staffPhotoName;

//    @Column(name = "staff_qr_name", length = 128)
//    private String staffQrName;

//    @Column(name = "staff_photo_update_date")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date staffPhotoUpdateDate;

//    @Column(name = "designation", length = 64)
//    private String designation;

}
