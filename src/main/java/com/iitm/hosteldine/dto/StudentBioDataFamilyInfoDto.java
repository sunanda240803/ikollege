package com.iitm.hosteldine.dto;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StudentBioDataFamilyInfoDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("bioDataId")
    private Long bioDataId;
    @JsonProperty("applicationNumber")
    private String applicationNumber;
    @JsonProperty("relationName")
    private String relationName;
    @JsonProperty("relationType")
    private String relationType;
    @JsonProperty("email")
    private String email;
    @JsonProperty("occupation")
    private String occupation;
    @JsonProperty("mobileNo")
    private Long mobileNo;
    @JsonProperty("income")
    private Double income;
    @JsonProperty("address")
    private String address;
    @JsonProperty("age")
    private Integer age;
    private String proofFileName;
    private String existingProofFileName;
    private MultipartFile chooseFile;
    private String description;
    private String proofType;
}