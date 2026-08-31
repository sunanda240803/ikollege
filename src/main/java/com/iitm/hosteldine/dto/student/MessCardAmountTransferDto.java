package com.iitm.hosteldine.dto.student;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.constant.Constants;

import lombok.Data;

@Data
public class MessCardAmountTransferDto {
    @JsonProperty("messtocardId")
    private Integer messtocardId;
    @JsonProperty("studentId")
    private String studentId;
    @JsonProperty("transferAmount")
    private Double transferAmount;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate requestDate;
    @JsonProperty("requestedStatus")
    private String requestedStatus;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate transferredDate;
    @JsonProperty("checkboxTransfer")
    private String checkboxTransfer;
    @JsonProperty("studentName")
    private String studentName;
    private String currentBalance;
    private Double netBalance;
    private Double totalFinalAmount;
    private Double maxAmount;
    private String studentIdData;
    private String messCardIdData;
    private String transferAmountData;
    private String search;
    private int listSize;
    
}