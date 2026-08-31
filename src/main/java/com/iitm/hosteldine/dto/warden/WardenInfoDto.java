package com.iitm.hosteldine.dto.warden;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.constant.Constants;

import lombok.Data;
import java.lang.Long;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

@Data
public class WardenInfoDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("wardenName")
    private String wardenName;
    @JsonProperty("officeNo")
    private String officeNo;
    @JsonProperty("wardenEmail")
    private String wardenEmail;
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    @JsonProperty("ldapUsername")
    private String ldapUsername;
    @JsonProperty("alternateEmail")
    private String alternateEmail;
    @JsonProperty("imageBytes")
    private byte[] imageBytes;
    @JsonProperty("imageName")
    private String imageName;
    @JsonProperty("wardenInfoUrl")
    private String wardenInfoUrl;
    @JsonProperty("associateWardenName")
    private String associateWardenName;
    @JsonProperty("associateLdapUsername")
    private String associateLdapUsername;
    
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate awayFrom;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate awayTo;
    private String awayDescription;
    private Long inchargeId;
    private Long wardenId;
    private String inchargeName;
    private String inchargeEmail;
    private String inchargePhone;
    private String inchargeOfficeNo;
    private Long hostelId;
    private String hostelName;
    
    private Long inchargeDetailsId;
    private String validDate;
    
    private String editUrl;
    private String deleteUrl;
    private String imageContent;
    private String imageFileName;
}