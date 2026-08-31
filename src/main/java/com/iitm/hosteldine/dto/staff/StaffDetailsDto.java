package com.iitm.hosteldine.dto.staff;

import com.iitm.hosteldine.form.common.AddressForm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffDetailsDto {
    private Long employeeId;
    private String facultyId;
    private String newFacultyId;
    private String firstName;
    private String lastName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;
    private String gender;
    private String birthPlace;
    private String maritalStatus;
    private Long contactNumber;
    private Long mobileNumber;
    private String emailAddress;
    private String addressOne;
    private String teachingOrNonTeaching;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfJoining;
    private String vFpdDesignation;
    private String employmentType;
    private Long designationId;

    private String designation;
    private String userName;
    private String accountCreationStatus;
    private Long roleId;
    private Long secondaryRoleId;
    private String roleName;

    private MultipartFile staffProfile;
	private AddressForm addressForm = new AddressForm(this);

    public StaffDetailsDto(String facultyId, String firstName, String lastName, String emailAddress) {
        this.facultyId = facultyId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
    }
}
