package com.iitm.hosteldine.dto.hostel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VacantRoomDto {
	private String hostelName;
	private String hostelFloorName;
	private int roomNo;
	private int vacancy;
}
