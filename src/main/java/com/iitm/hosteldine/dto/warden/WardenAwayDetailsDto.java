package com.iitm.hosteldine.dto.warden;

import java.time.LocalDate;

import lombok.Data;

@Data
public class WardenAwayDetailsDto {
	
	private String hostelName;
	private Long hostelId;
	private String wardenName;
	private Long wardenId;
	private Long inchargerId;
	private Long id;
	private LocalDate awayFrom;
	private LocalDate awayTo;
	private String awayDescription;
	private String officeNo;
	private String phoneNumber;
	private String wardenEmail;
	private String alternateWardenName;
	private String alternatePhoneNumber;
	private String alternateWardenEmail;	
	
}
