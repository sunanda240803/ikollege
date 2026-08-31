package com.iitm.hosteldine.dto.warden;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class GuestAccommodationChargesDto {

	private Long id;
	private Integer amount;
	private String description;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate fromDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate toDate;
	private Integer individualRoomAmount;
	private Integer individualRoomMultipleAmount;
}