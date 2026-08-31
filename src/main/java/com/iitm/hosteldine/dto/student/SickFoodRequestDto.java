package com.iitm.hosteldine.dto.student;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.model.student.SickFoodDeliveryStatusEntity;

import jakarta.persistence.Column;
import lombok.Data;

import java.lang.Long;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.lang.Integer;

@Data
public class SickFoodRequestDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("studentId")
    private String studentId;
    @JsonProperty("mobileNum")
    private String mobileNum;
    @JsonProperty("deliveryAddress")
    private String deliveryAddress;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate requestDate;
    @JsonProperty("messType")
    private String messType;
    @JsonProperty("messId")
    private Integer messId;
    @JsonProperty("medicalReason")
    private String medicalReason;
    @JsonProperty("medicalProofDoc")
    private String medicalProofDoc;
    @JsonProperty("deliveryOption")
    private String deliveryOption;

    @JsonProperty("uploadedFileName")
    private MultipartFile uploadedFileName;
    @JsonProperty("messSession")
    private String messSession;
    @JsonProperty("catererStatus")
    private String catererStatus;
    @JsonProperty("studentDeliveryStatus")
    private String studentDeliveryStatus;


    private List<SickFoodDeliveryStatusDto> messSessionStatus;

    private String studentName;
    private String messName;
    private boolean anyAccepted;

}