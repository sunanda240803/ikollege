package com.iitm.hosteldine.dto.student.wellness;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.iitm.hosteldine.constant.Constants;

import lombok.Data;

@Data
public class StudentWellnessFollowupDataDto {
    private Long id;
    private StudentWellnessCategoricalDataDto wellness;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate visitDate;
    private Integer noOfVisit;
    private String interactionMode;
    private LocalDateTime sessionStartTime;
    private Short duration;
    private String concernsDiscussed;
    private String futureActionPlan;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate followUpDate;
    private String mailStatus;
    private String visitStatus;
}