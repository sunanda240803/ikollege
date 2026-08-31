package com.iitm.hosteldine.dto.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentAccoDetailsDTO {

    private int requestId;
    private int parentRequestId;
    private String paymentType;
    private String paymentReferenceNo;
    private int paymentAmt;
    private String paymentDate;
    private String hostelName;
    private String roomNo;
    private int noOfDays;
    private String studentId;
    private String paymentStatus;
    private int noOfPersons;
    private String studentFullName;
    private String fromDate;
    private String toDate;
    private String purposeOfVisit;
    private String dateOfBirth;
    private String gender;
    private String address;
    private String city;
    private String state;
    private String emailAddress;
    private int pinCode;
    private int calculatedAmount;
    private String checkInTime;
    private String checkOutTime;
    private String accommodationType;
    private String allotedHostelName;
    private String allotedRoomNo;
    private String wardenApproval;
    private String rejectionDescription;
    private String createdAt;

}
