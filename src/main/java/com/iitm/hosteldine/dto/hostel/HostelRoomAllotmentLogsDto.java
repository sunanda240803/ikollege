package com.iitm.hosteldine.dto.hostel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HostelRoomAllotmentLogsDto {
	private String allotmentDate;
	private String studentType;
	private String allocationType;
	private String studentId;
	private String studentName;
	private String previousHostelName;
	private String currentHostelName;
	private int previousRoomNo;
	private int currentRoomNo;
	private String user;
	private String shiftedDate;
	private String vacateDate;
	private String email;
	private String createdBy;
	private String stayFrom;
	private String stayTo;
	private String type;
}
