package com.iitm.hosteldine.constant;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ClearanceReasonEnum {
	COURSE_COMPLETED("Course Completed"),
	QUARTERS_ALLOTMENT("Quarters Allotment"),
	REGISTRATION_KEPT_ALIVE("Registration kept alive"),
	DISCONTINUATION_OF_COURSE("Discontinuation of course"),
	FOREIGN_STUDENT("Foreign student"),
	CLINICAL_PROG("Clinical Prog"),
	OTHERS("Others"),
	DAY_SCHOLAR("Day scholar"),
	EXCHANGE_INTERNSHIP_PROG("Exchange/Internship prog"),
	HOSTEL_DISCIPLINARY_ACTION("Hostel disciplinary action");

	private final String value;

	ClearanceReasonEnum(String value) {
		this.value = value;
	}
}