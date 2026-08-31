package com.iitm.hosteldine.constant.hostel;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum BulkAllotmentStudentsConstants {

	STUDENT_ID("Student ID"), HOSTEL_NAME("Hostel Name"), ROOM_NUMBER("Room Number"), SEAT("Seat"),
	ALPHANUMERIC_CHARACTERS_LONG("Student ID must contain only alphanumeric characters and be between 6 to 10 characters long."),
	SHOULD_NOT_REPEAT_WITHIN_SAME_EXCEL("should not be repeated within the same Excel sheet."),
	STUDENT_ID_CHANGED("Student ID has been changed for this student: "), DOES_NOT_EXIT(" does not exist."),
	SETTLEMENT_COMPLETED_FOR_THE_STUDENT("Settlement has been completed for this Student ID: "),
	APPLIED_VACATING_RECEVIED_APPROVAL_STUDENT(" - This student has applied for a vacating form and received 'Approval'. "
		+ "To allocate, please use the 'Direct allotment' option."),
	SHOULD_NOT_BE_EMPTY("should not be empty."), IN_THE("In the"), HOSTEL("hostel"),
	STUDENT_CANNOT_BE_ALLOCATED("students cannot be allocated."), HOSTEL_NOT_FOUND("Hostel not found:"),
	UNEXPECTED_CELL_TYPE("Unexpected cell type:"),
	SEAT_NOT_EMPTY_MUST_BE_SINGLE_CHARACTER("Seat should not be empty or must be a single character like 'A', 'B', or 'C'."),
	CAP_HOSTEL("Hostel"), ROOM("Room"), A_CAPACTIY_OF("has a capacity of"), SEAT_SHOULD_BE(". So, Seat should be "),
	ONLY(" only."), SHOULD_NOT_REPEAT("should not be repeated"), WITHCHECKHOSTEL("WithCheckHostel"),
	CAPACITY_EXCEEDS_IN_HOSTEL(" - Capacity exceeds in Hostel: "),
	DOES_NOT_EXIST_IN_HOSTEL("does not exist in the Hostel:"),
	ROOM_NO_SHOULD_NOT_EMPTY("Room Number should not be empty."),
	EMPTY_DATA_IN_EXCEL_FILE("Empty data in the Excel file"),
	INVALID_EXCEL_TEMPLATE("Invalid Bulk Hostel Room Allotment Excel Template"), COURSE_COMPLETED("Course Completed"),
	STUDENT_COMPLAINT_CONFIG_FORM_KEY("studentComplaintConfigurationDto");

	private final String string;

	BulkAllotmentStudentsConstants(String string) {
		this.string = string;
	}
}
