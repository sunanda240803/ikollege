package com.iitm.hosteldine.constant.mess;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum MessAllotmentConstants {
	STUDENT_ID("Student ID"),
	MESS_NAME("Mess Name"),
	MESS_HEAD("Mess Head"),
	FROM_DATE("From Date"),
	TO_DATE("To Date"),
	MESS_NAME_NOT_FOUND("Mess name not found:"),
	MESS_HEAD_NOT_FOUND("Mess head not found:"),
	INVALID_EXCEL_TEMPLATE("Invalid allocation excel template"),
	STUDENT_ID_EXIST_IN_MESS_PERIOD("Student ID already added to this mess period :"),
	NO_MESS_AVAILABLE("No mess available"), 
	NO_MESS_AVAILABLE_FOR_STUDENT_ID("No mess available for this Student ID"), 
	IS_INVALID_FOR_STUDENT_ID("is invalid for this Student ID"), 
	MESS("Mess"), 
	CURRENT_MESS_PERIOD_NOT_CONFIGURED("Current mess period not configured"), 
	NEXT_MESS_PERIOD_NOT_CONFIGURED("Next mess period not configured"),
	SELECTED_FROM_DATE_OUT_OF_MESS_PERIOD("You selected the From date which is out of Mess Period :"),
	SELECTED_TO_DATE_OUT_OF_MESS_PERIOD("You selected the To date which is out of Mess Period :"),
	CHANGE_MESS_SHOULD_NOT_BE_SAME_AS_CURRENT_MESS("Change mess should not be same as current mess :"),
	STUDENT_ID_NOT_EXIST_IN_MESS_PERIOD("Student ID not exist in the current mess period :"),
	EXCEL_DATE_FORMAT("<YYYY-MM-DD>"),
	NEW_MESS_NAME("New Mess Name"),
	NEW_MESS_HEAD("New Mess Head"),
	EFFECTIVE_FROM_DATE("Effective From Date"),
	EFFECTIVE_TO_DATE("Effective To Date"),
	EFFECTIVE_TILL_DATE("Effective Till Date"),
	INVALID_DATE_FORMAT("Invalid date format :"),
	DESCRIPTION("Description"),
	TO("to"),
	EFFECTIVE_FROM_DATE_SHOULD_BE_BETWEEN("Effective From Date should be between"),
	EFFECTIVE_TO_DATE_SHOULD_BE_AFTER_FROM_DATE("Effective To Date should be after from date"),
	EFFECTIVE_TILL_DATE_SHOULD_BE_BETWEEN("Effective Till Date should be between"),
	FROM_DATE_SHOULD_BE_FUTURE_DATE("From Date should be current or future date"),
	TO_DATE_SHOULD_NOT_BE_BEFORE_FROM_DATE("To Date should not be before From Date");

	private final String string;
	MessAllotmentConstants(String string) {
		this.string = string;
	}
}
