package com.iitm.hosteldine.dto.student;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;

@SqlResultSetMapping(
        name = "FacultyDashBoardDTOMapping",
        classes = @ConstructorResult(
                targetClass = FacultyDashBoardDTO.class,
                columns = {
                        @ColumnResult(name = "id", type = Integer.class),
                        @ColumnResult(name = "parent_request_id", type = Integer.class),
                        @ColumnResult(name = "payment_type", type = String.class),
                        @ColumnResult(name = "payment_reference_no", type = String.class),
                        @ColumnResult(name = "payment_amount", type = Integer.class),
                        @ColumnResult(name = "payment_date", type = String.class),
                        @ColumnResult(name = "n_fm_facility_master_name", type = String.class),
                        @ColumnResult(name = "v_hri_roomno", type = String.class),
                        @ColumnResult(name = "no_of_days", type = Integer.class),
                        @ColumnResult(name = "student_id", type = String.class),
                        @ColumnResult(name = "payment_status", type = String.class),
                        @ColumnResult(name = "no_of_persons", type = Integer.class),
                        @ColumnResult(name = "v_sdi_firstname", type = String.class),
                        @ColumnResult(name = "from_date", type = String.class),
                        @ColumnResult(name = "to_date", type = String.class),
                        @ColumnResult(name = "purpose_of_visit", type = String.class),
                        @ColumnResult(name = "v_sdi_dob", type = String.class),
                        @ColumnResult(name = "v_sdi_gender", type = String.class),
                        @ColumnResult(name = "v_sdi_studentaddress", type = String.class),
                        @ColumnResult(name = "v_sdi_city", type = String.class),
                        @ColumnResult(name = "v_sdi_state", type = String.class),
                        @ColumnResult(name = "v_sdi_parent_emailid", type = String.class),
                        @ColumnResult(name = "n_sdi_pincode", type = Integer.class),
                        @ColumnResult(name = "Amount", type = Integer.class),
                        @ColumnResult(name = "checkin_time", type = String.class),
                        @ColumnResult(name = "checkout_time", type = String.class),
                        @ColumnResult(name = "accommodation_type", type = String.class),
                        @ColumnResult(name = "facility_master_name", type = String.class),
                        @ColumnResult(name = "roomno", type = String.class),
                        @ColumnResult(name = "warden_approval_status", type = String.class),
                        @ColumnResult(name = "created_at", type = String.class)
                }
        )
)
public class FacultyDashBoardDTO {
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

    // Constructor that matches the result set columns
    public FacultyDashBoardDTO(int requestId, int parentRequestId, String paymentType,
                               String paymentReferenceNo, int paymentAmt, String paymentDate,
                               String hostelName, String roomNo, int noOfDays, String studentId,
                               String paymentStatus, int noOfPersons, String studentFullName,
                               String fromDate, String toDate, String purposeOfVisit,
                               String dateOfBirth, String gender, String address, String city,
                               String state, String emailAddress, int pinCode, int calculatedAmount,
                               String checkInTime, String checkOutTime, String accommodationType,
                               String allotedHostelName, String allotedRoomNo, String wardenApproval,
                               String rejectionDescription, String createdAt) {
        this.requestId = requestId;
        this.parentRequestId = parentRequestId;
        this.paymentType = paymentType;
        this.paymentReferenceNo = paymentReferenceNo;
        this.paymentAmt = paymentAmt;
        this.paymentDate = paymentDate;
        this.hostelName = hostelName;
        this.roomNo = roomNo;
        this.noOfDays = noOfDays;
        this.studentId = studentId;
        this.paymentStatus = paymentStatus;
        this.noOfPersons = noOfPersons;
        this.studentFullName = studentFullName;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.purposeOfVisit = purposeOfVisit;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.city = city;
        this.state = state;
        this.emailAddress = emailAddress;
        this.pinCode = pinCode;
        this.calculatedAmount = calculatedAmount;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.accommodationType = accommodationType;
        this.allotedHostelName = allotedHostelName;
        this.allotedRoomNo = allotedRoomNo;
        this.wardenApproval = wardenApproval;
        this.rejectionDescription = rejectionDescription;
        this.createdAt = createdAt;
    }

    // Getters and setters for all the fields...
}
