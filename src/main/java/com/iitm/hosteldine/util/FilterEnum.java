package com.iitm.hosteldine.util;

import lombok.Getter;

@Getter
public enum FilterEnum {
    VALIDATION_STATUS("validationStatus"),
    CATEGORY("category"),
    APPROVAL_FROM_DATE("approvalFromDate"),
    APPROVAL_TO_DATE("approvalToDate"),
    SUBMITTED_FROM_DATE("submittedFromDate"),
    SUBMITTED_TO_DATE("submittedToDate"),
    APPOINTMENT_FROM_DATE("appointmentFromDate"),
    APPOINTMENT_TO_DATE("appointmentToDate"),
    STAY_FROM_DATE("stayFromDate"),
    STAY_TO_DATE("stayToDate"),
    CANDIDATE_NAME("candidateName"),
    CANDIDATE_EMAIL("candidateEmail"),
    HOSTEL_NAME("hostelName"),
    APPLICATION_TYPE("applicationType"),
    REPORT_TYPE("reportType"),
    STAY_TYPE("stayType"),
    CURRENT_STAY_FLAG("currentDayStayFlag"),
    REQUEST_FROM_DATE("requestFromDate"),
    REQUEST_TO_DATE("requestToDate"),
    CATERER_STATUS("catererStatus"),
    MESS_SESSION("messSession"),
    STUDENT_STATUS("studentStatus"),
    STUDENT_ID("studentId"),
    STUDENT_NAME("studentName");

    private final String value;
    FilterEnum(String value) {
        this.value = value;
    }
}
