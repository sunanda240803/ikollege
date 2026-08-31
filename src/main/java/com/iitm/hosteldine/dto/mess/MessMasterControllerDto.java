package com.iitm.hosteldine.dto.mess;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.constant.Constants;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class MessMasterControllerDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("month")
    private String month;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate regBeginDate;
    @JsonProperty("regBeginTime")
    private String regBeginTime;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate regEndDate;
    @JsonProperty("regEndTime")
    private String regEndTime;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("modifiedBy")
    private String modifiedBy;
    @JsonProperty("activeFlag")
    private String activeFlag;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate diningFromDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate diningToDate;
    @JsonProperty("studentEditStatus")
    private Boolean studentEditStatus;
    @JsonProperty("studentDeviceRegistrationStatus")
    private Boolean studentDeviceRegistrationStatus;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate exchangeFromDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate exchangeToDate;
    @JsonProperty("feedbackStatus")
    private Boolean feedbackStatus;
    @JsonProperty("currentActiveFlag")
    private String currentActiveFlag;
    @JsonProperty("pushingTime")
    private String pushingTime;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate pushingDate;
    @JsonProperty("semesterBegin")
    private Boolean semesterBegin;
    @JsonProperty("bulkMailSubject")
    private String bulkMailSubject;
    @JsonProperty("bulkMailContent")
    private String bulkMailContent;
    @JsonProperty("mailStatus")
    private String mailStatus;
    @JsonProperty("regExtendStartTime")
    private String regExtendStartTime;
    @JsonProperty("regExtendEndTime")
    private String regExtendEndTime;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate regExtendStartDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate regExtendEndDate;
    @JsonProperty("foodCourtAmount")
    private Double foodCourtAmount;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate allotmentFromDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate allotmentToDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate semStartDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate semEndDate;
    private Long previousId;
    private String previousMonth;
    private boolean prevFbStatus;
    private String messName;
    private Long messId;
    private Long messMasterControllerId;
    private String logTag;
}