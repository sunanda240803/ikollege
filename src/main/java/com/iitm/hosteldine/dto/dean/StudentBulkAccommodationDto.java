package com.iitm.hosteldine.dto.dean;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class StudentBulkAccommodationDto {
    private MultipartFile file;
    private String studentId;
    private String studentName;
    private LocalDate eventFromDate;
    private LocalDate eventToDate;
    private String validatingAuthorityName;
    private String validatingAuthorityEmail;
    private String studentDesignation;
    private String rolePlayedDuringStay;
    private String excelErrorMsg;
    private String error;
    private List<String> errorList;
    private String eventName;
    private String description;
    private List<Rows> rows = new ArrayList<Rows>();
    private LocalDate stayFrom;
    private LocalDate stayTo;
    private LocalDate appointmentFrom;
    private LocalDate appointmentTo;
    private Integer maleParticipants;
    private Integer femaleParticipants;
    private String dining;
    private String session;
    private Long id;
    private String fileDownloadLink;
    private String createdBy;
    private String fileName;
    private Integer count;
    private String approvalNotes;
    private String approvalStatus;
    private String gender;
    private Integer breakfastCount;
    private Integer lunchCount;
    private Integer dinnerCount;
}

