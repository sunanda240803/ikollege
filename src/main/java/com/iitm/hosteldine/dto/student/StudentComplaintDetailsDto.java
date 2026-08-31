package com.iitm.hosteldine.dto.student;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.validator.fieldValidators.ValidStringField;

import lombok.Data;

@Data
public class StudentComplaintDetailsDto {
    @JsonProperty("complaintId")
    private Long complaintId;
    @JsonProperty("studentId")
    private String studentId;
    @ValidStringField(message = "message.validation.complaint.type.required",fieldName = "message.label.complaint.type")
    private String stuComplaintType;
    @ValidStringField(message = "message.validation.complaint.about.required",fieldName = "message.label.complaint.about",min = 3,max =128)
    private String complaints;
    @ValidStringField(message = "message.validation.complaint.description.required",fieldName = "message.label.complaint.description",min = 3,max =300)
    private String complaintDesc;
    @JsonProperty("fileUpload")
    private String fileUpload;
    @JsonProperty("uploadedFileName") 
    private MultipartFile uploadedFileName;
    @JsonProperty("mailId")
    private String mailId;
    @JsonProperty("schoolId")
    private Integer schoolId;
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    private Long complaintCount;
    private Long hostelCount; 
    private Long messCount;
    
    
}