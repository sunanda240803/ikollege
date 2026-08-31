package com.iitm.hosteldine.dto.student;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OverrideApproveGuestAccomDTO {

    private String studentId;
    private String requestId;
    private String firstName;
    private String lastName;
    private String dob;
    private String gender;
    private String address;
    private String city;
    private String state;
    private String country;
    private Integer pincode;
    private String parentEmailId;
    private String hostelName;
    private String roomNo;
    private String alternateContactNumber;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String checkInTime;
    private String checkOutTime;
    private String approvalNotes;
    private String rejectionDescription;
    private String wardenEmail;
    private String status;
    private String accommodationType;
    private String studentDetailString;
    private Long wardenId;

}
