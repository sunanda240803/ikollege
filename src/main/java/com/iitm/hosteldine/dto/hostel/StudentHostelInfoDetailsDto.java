package com.iitm.hosteldine.dto.hostel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class StudentHostelInfoDetailsDto {
    private Long hostelId;
    private Long floorId;
    private Long roomId;
    private String hostelName;
    private String floorName;
    private Long roomNo;
    private String subRoomId;
    private LocalDate stayFromDate;
    private LocalDate stayToDate;
    private String hostelGenderType;
    private String status;
    private String errorMessage;
    private Boolean hasPrivilege;
    private String screenType;
    private String currentStudentId;
    private String newStudentId;
    private Long newHostelId;
    private Long newFloorId;
    private Long newRoomId;
    private String newSubRoomId;
    private String emailId;
    private String studentType;
    private Boolean isMissing;
    private Long roomAllotmentId;
    private String changedStudentId;
}