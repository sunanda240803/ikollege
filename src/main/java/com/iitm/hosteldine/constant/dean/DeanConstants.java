package com.iitm.hosteldine.constant.dean;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum DeanConstants {
    REASON_FOR_VACATING_LIST("reasonForVacatingList"),
    APPROVAL_STATUS("approvalStatus"),
    SUBMITTED_FROM_DATE("submittedFromDate"),
    CCW_IITM("CCW IITM"),
    SUBMITTED_TO_DATE("submittedToDate"),
    VACATING_REASON("vacatingReason"),
    VACATING_FROM_DATE("vacatingFromDate"),
    VACATING_TO_DATE("vacatingToDate"),
    HOSTEL_ID("hostelId"),
    STUDENT_NAME("studentName"),
    STUDENT_ID("studentId"),
    WARDEN_APPROVAL_STATUS("wardenApprovalStatus"),
    USER_ROLE("userRole"),
    APPROVAL_LEVEL("approvalLevel"),
    APPROVAL_EMAIL("approvalEmail"),
    USER_NAME("username"),
    HM_OFFICE("HM Office"),
    HOSTEL_CHECK_IN("Hostel Check In"),
    HOSTEL_NAME("hostelName"),
    VALIDATION_STATUS_LIST("validationStatusList"),
    COLUMN_DTO("columnDto"),
    SEARCH_KEY("searchKey"),
    ROOM_NUMBER("roomNumber");

    private final String constants;
    
    DeanConstants(String constants) {
        this.constants = constants;
    }
    
}
