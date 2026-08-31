package com.iitm.hosteldine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.constant.Constants;

import lombok.Data;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;

@Data
public class StudentBioDataFormDetailDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("applicationNumber")
    private String applicationNumber;
    @JsonProperty("studentId")
    private String studentId;
    @JsonProperty("studentName")
    private String studentName;
    @JsonProperty("studentMobile")
    private Long studentMobile;
    @JsonProperty("dob")
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate dob;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("hostelName")
    private String hostelName;
    @JsonProperty("roomNumber")
    private String roomNumber;
    @JsonProperty("category")
    private String category;
    @JsonProperty("aadhaarNumber")
    private Long aadhaarNumber;
    @JsonProperty("panNum")
    private String panNum;
    @JsonProperty("studentAddress")
    private String studentAddress;
    @JsonProperty("otherInfo")
    private String otherInfo;
    @JsonProperty("registrationDate")
    private LocalDate registrationDate;
    @JsonProperty("place")
    private String place;
    @JsonProperty("messName")
    private String messName;
    @JsonProperty("imageLocation")
    private String imageLocation;
    @JsonProperty("imageBytes")
    private byte[] imageBytes;
    @JsonProperty("guardianStatus")
    private String guardianStatus;
    @JsonProperty("studentSignLocation")
    private String studentSignLocation;
    @JsonProperty("studentSignBytes")
    private byte[] studentSignBytes;
    @JsonProperty("parentSignLocation")
    private String parentSignLocation;
    @JsonProperty("parentSignBytes")
    private byte[] parentSignBytes;
    @JsonProperty("bloodGroup")
    private String bloodGroup;
    @JsonProperty("signedParentName")
    private String signedParentName;
    @JsonProperty("facultyName")
    private String facultyName = "0";
    @JsonProperty("facultyEmail")
    private String facultyEmail;
    @JsonProperty("facultyContactNo")
    private Long facultyContactNo;
    private String pwd;
    private String pwdDescription;
    private String studentPersonalEmail;

    private ArrayList<StudentBioDataFamilyInfoDto> familyDetails = new ArrayList<>();

    private StudentBioDataFamilyInfoDto guardianDetails;

    private MultipartFile studentProfile;
    private MultipartFile studentSignature;
    private MultipartFile parentSignature;
    private String accountType;
    private Integer pwdPercentage;
}