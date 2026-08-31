package com.iitm.hosteldine.dto.dean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class FacultyAccommodationRequestDto {
    private Integer studentCount;
    private String eventName;
    private LocalDate fromDate;
    private LocalDate toDate;
    private LocalDate stayFrom;
    private LocalDate stayTo;
    private Integer noOfMaleParticipants;
    private Integer noOfFemaleParticipants;
    private String approvalStatus;
    private String sessionPeriod;
    private LocalDate createdAt;
    private String dining;
    private Long breakfastCount;
    private Long lunchCount;
    private String createdBy;
    private Long bulkAppointmentId;
    private Long dinnerCount;
    private String uploadedFacultyName;

    public FacultyAccommodationRequestDto(
            Integer studentCount,
            String eventName,
            LocalDate fromDate,
            LocalDate toDate,
            LocalDate stayFrom,
            LocalDate stayTo,
            Integer noOfMaleParticipants,
            Integer noOfFemaleParticipants,
            String approvalStatus,
            String sessionPeriod,
            LocalDate createdAt,
            String dining,
            Long breakfastCount,
            Long lunchCount,
            String createdBy,
            Long bulkAppointmentId,
            Long dinnerCount,
            String uploadedFacultyName) {
        this.studentCount = studentCount;
        this.eventName = eventName;
        this.fromDate =  fromDate;
        this.toDate = toDate;
        this.stayFrom = stayFrom;
        this.stayTo = stayTo;
        this.noOfMaleParticipants = noOfMaleParticipants;
        this.noOfFemaleParticipants = noOfFemaleParticipants;
        this.approvalStatus = approvalStatus;
        this.sessionPeriod = sessionPeriod;
        this.createdAt = createdAt;
        this.dining = dining;
        this.breakfastCount = breakfastCount;
        this.lunchCount = lunchCount;
        this.createdBy = createdBy;
        this.bulkAppointmentId = bulkAppointmentId;
        this.dinnerCount = dinnerCount;
        this.uploadedFacultyName = uploadedFacultyName;
    }
}