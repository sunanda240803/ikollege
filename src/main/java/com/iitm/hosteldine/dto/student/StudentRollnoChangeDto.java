package com.iitm.hosteldine.dto.student;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Long;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

@Data
public class StudentRollnoChangeDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("studentId")
    private String studentId;
    @JsonProperty("newRollNo")
    private String newRollNo;
    @JsonProperty("fileUpload")
    private String fileUpload;
    @JsonProperty("status")
    private String status;
    private String studentFullName;
    private MultipartFile file;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}