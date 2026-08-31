package com.iitm.hosteldine.dto.OtherCandidate;

import lombok.Data;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.iitm.hosteldine.constant.Constants;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;

@Data
public class CandidateProfileDto {
    private Long id;
    private String firstName;
    private String lastName;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate dob;
    private String gender;
    private String address;
    private String address2;
    private String city;
    private Integer pin;
    private String state;
    private String phoneNumber;
    private String mobileNumber;
    private String email;
    private String postSelect;
    private String postOthers;
    private Integer schoolId=1;
    private String employeeId;
    private String designation;
    private Long userId;
    private Date maxDate;
    private Map<String, String> postData;
    private MultipartFile studentImg;
    private String studentProfile;
    private String imagePath;
    private String imageName;
	private Boolean accommodationRequest = false;
    private String candiateFullName;
}