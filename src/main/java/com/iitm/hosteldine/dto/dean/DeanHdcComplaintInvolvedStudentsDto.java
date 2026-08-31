package com.iitm.hosteldine.dto.dean;

import java.io.Serializable;

import lombok.Data;

@Data
public class DeanHdcComplaintInvolvedStudentsDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String involvedStudentId;
	private String involvedStudentName;
	private String involvedStudentHostelName;
	private String involvedStudentRoomNo;
}
