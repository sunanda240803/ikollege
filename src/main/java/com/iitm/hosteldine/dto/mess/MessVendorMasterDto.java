package com.iitm.hosteldine.dto.mess;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Integer;
import java.time.LocalDateTime;
import java.lang.Long;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

@Data
public class MessVendorMasterDto {
    @JsonProperty("vendorCode")
    private String vendorCode;
    @JsonProperty("vendorName")
    private String vendorName;
    @JsonProperty("address1")
    private String address1;
    @JsonProperty("address2")
    private String address2;
    @JsonProperty("pincode")
    private Integer pincode;
    @JsonProperty("email")
    private String email;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("password")
    private String password;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    @JsonProperty("modifiedBy")
    private String modifiedBy;
    @JsonProperty("modifiedAt")
    private LocalDateTime modifiedAt;
    @JsonProperty("activeFlag")
    private String activeFlag;
    @JsonProperty("mobileNo")
    private Long mobileNo;
    @JsonProperty("contactPerson")
    private String contactPerson;
    @JsonProperty("licenseNo")
    private String licenseNo;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("licenseValidUpto")
    private LocalDate licenseValidUpto;
    @JsonProperty("panNo")
    private String panNo;
    @JsonProperty("isCaterer")
    private String isCaterer;
    @JsonProperty("managingDirector")
    private String managingDirector;
    @JsonProperty("branchManager")
    private String branchManager;
    @JsonProperty("branch")
    private String branch;
    @JsonProperty("place")
    private String place;
    @JsonProperty("totalNoOfWorkers")
    private Integer totalNoOfWorkers;
    @JsonProperty("mobileNoMask")
    private String mobileNoMask;
}