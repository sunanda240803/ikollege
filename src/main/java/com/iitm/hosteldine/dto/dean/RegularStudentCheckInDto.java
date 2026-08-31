package com.iitm.hosteldine.dto.dean;

import lombok.Data;

@Data
public class RegularStudentCheckInDto {
	private Long totalRooms;
	private Long roomsAllotted;
	private Long roomsFree;
	private Long totalNoOfSeats;
	private Long allotted;
	private Long exactVacancy;
	private Long occupied;
	private Long checkInCount;
	private Long vacatedCount;
	private Long checkOutCount;
}
