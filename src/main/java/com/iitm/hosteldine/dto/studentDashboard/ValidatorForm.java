package com.iitm.hosteldine.dto.studentDashboard;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ValidatorForm {
	private String hostelName;
	private Long wardenId;
	private String wardenName;
	private String wardenEmail;
	private String alternateEmail;
	private Long inchargeId;
	private String inchargeName;
	private String inchargeEmail;
	private String inchargAlternateEmail;

}
