package com.iitm.hosteldine.form.student;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentRoomRequestForm {
    private LocalDate approvalFromDate;
    private LocalDate approvalToDate;
    private LocalDate submittedFromDate;
    private LocalDate submittedToDate;
    private Integer hostelId;
    private LocalDate paymentFromDate;
    private LocalDate paymentToDate;
    private String studentFullName;
    private String studentID;
    private String validatorName;
    private String validatorEmail1;
    private String approvalStatus;
    private String paymentApprovalStatus;
    private String stayType;
    private String listType;
    private String ohmlogin;

}
