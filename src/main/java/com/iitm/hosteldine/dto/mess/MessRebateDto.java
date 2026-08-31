package com.iitm.hosteldine.dto.mess;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.constant.Constants;

import lombok.Data;
import java.lang.Long;
import java.time.LocalDateTime;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.lang.Integer;
import java.lang.Double;

@Data
public class MessRebateDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("studentId")
    private String studentId;
    @JsonProperty("rebateFrom")
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate rebateFrom;
    @JsonProperty("rebateTo")
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate rebateTo;
    @JsonProperty("noOfDays")
    private Integer noOfDays;
    @JsonProperty("guideName")
    private String guideName;
    @JsonProperty("guideEmail")
    private String guideEmail;
    @JsonProperty("rebateReason")
    private String rebateReason;
    @JsonProperty("cancelStatus")
    private String cancelStatus;
    @JsonProperty("approvalStatus")
    private String approvalStatus;
    @JsonProperty("rebateStatus")
    private String rebateStatus;
    @JsonProperty("claimAmount")
    private Double claimAmount;
    @JsonProperty("validatorName")
    private String validatorName;
    @JsonProperty("validatorEmail")
    private String validatorEmail;
    @JsonProperty("rejectionDescription")
    private String rejectionDescription;
    @JsonProperty("leaveFrom")
    private LocalDate leaveFrom;
    @JsonProperty("leaveTo")
    private LocalDate leaveTo;
    @JsonProperty("approvalDate")
    private LocalDate approvalDate;
    @JsonProperty("rebateDiningFrom")
    private LocalDate rebateDiningFrom;
    @JsonProperty("rebateDiningTo")
    private LocalDate rebateDiningTo;
    @JsonProperty("docStatus")
    private String docStatus;
    @JsonProperty("vacateDate")
    private LocalDate vacateDate;
    @JsonProperty("hometown")
    private String hometown;
    @JsonProperty("selfDecDate")
    private LocalDate selfDecDate;
    @JsonProperty("selfDecSignature")
    private String selfDecSignature;
    @JsonProperty("modifiedAt")
    private LocalDateTime modifiedAt;
    
    @JsonProperty("fileUpload")
    private String fileUpload;
    @JsonProperty("file") 
    private MultipartFile file;
    @JsonProperty("approvedCount")
    private String approvedCount;
    @JsonProperty("pendingCount")
    private String pendingCount;
    @JsonProperty("rejectedCount")
    private String rejectedCount;
    private LocalDateTime createdAt;
}