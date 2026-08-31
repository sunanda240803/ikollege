package com.iitm.hosteldine.constant.api;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ApiConstants {
	VALID_TOKEN("Valid Token"),
	AVAILABLE("available"),
	UNAVAILABLE("unavailable"),
	SIZE("size"),
	STATUS("status"),
	MESSAGE("message"),
	NO_HOSTEL("No Hostels Found"),
	HOSTEL_ID_CHECK("Hostel Id Should not be Zero"),
	ROOM_ID_CHECK("Room Id Should not be Zero"),
	NO_ROOMS("No Rooms available for the selected Hostel"),
	QR_NUMBER("qrNumber"),
	QR_ID("qrId"),
	MESS_NAME("messName"),
	COUPON_SUCCESS("Coupon validation successful"),
    TOKEN_MISSING("Token should not Empty"),
    STUDENT_MISSING("Student Id should not empty"),
    NO_STUDENT_ALLOTTED("No Students Allotted to the chosen Hostel and Room"),
    STUDENT_LIST_UNAVAILABLE("Student details unavailable"),
    STUDENT_LIST_AVAILABLE("Student details available"),
    LOGIN_ID_MISSING("Login Id should not Empty"),
    CARD_DETAILS("cardDetails"),
    MESS_ID_MISSING("Mess Id  should not Empty or Zero");


	private final String string;
	ApiConstants(String string) {
		this.string = string;
	}
}
