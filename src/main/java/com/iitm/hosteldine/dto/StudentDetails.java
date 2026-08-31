package com.iitm.hosteldine.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDetails {
	
	private String studentFullName;
    private String hostelName;
    private String roomNumber;
    private String seat;
    private String mess;
    private LocalDateTime sessionStartTime;
    private Long roomId;
    private Long hostelId;

}
