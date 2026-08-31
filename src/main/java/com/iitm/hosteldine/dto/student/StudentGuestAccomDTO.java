package com.iitm.hosteldine.dto.student;

import com.iitm.hosteldine.dto.studentDashboard.GuestAccommodationGuestDetailsDto;
import com.iitm.hosteldine.dto.warden.WardenInfoDto;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StudentGuestAccomDTO {
    private Integer requestId;
    private String createdAt;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String purposeOfVisit;
    private Integer noOfDays;
    private Integer noOfPersons;
    private String wardenId;
    private String wardenEmail;
    private String wardenName;
    private String wardenApprovalStatus;
    private String paymentStatus;
    private String cancelStatus;
    private String studentId;
    private String approvalDate;
    private Boolean applicableCharges;
    private Boolean documentsUploaded;
    private String accommodationType;
    private String approvalNotes;
    private String rejectionDescription;
    private Integer parentReqId;
    private Boolean bloodRelationStatus;
    private String allocationStatus;
    private String checkInTime;
    private String checkOutTime;
    private String arName;
    private String appFromDate;
    private String appToDate;
    private Integer appnoOfDays;
    private Integer appnoOfPersons;
    private String givenStr;
    private String saveStatus;
    private List<GuestAccommodationGuestDetailsDto> guestReqList;
    private List<StudentGuestAccomDTO> prevGuestReqList;
    private List<FileInfoDTO> uploadFileList;
    private Integer approvalCount;
    private Integer individualApprovalCount;
    private List<WardenInfoDto> approvalList;
    private StudentDetailsWithHostelDTO studentInfo;
    private String minToDate;
    private LocalDateTime paymentDate;
    private String paymentReferenceNumber;
    private Integer paymentAmount;
    private Integer paidAmount;
    private String paymentType;
    private String studentDetailString;
    private String mailSentTo;
}
