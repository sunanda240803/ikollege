package com.iitm.hosteldine.dto.studentDashboard;

import lombok.Data;

import java.lang.Integer;

@Data
public class GuestFilesInformationDto {
	private Long fileId;
	private Long requestId;
    private String filename;
    private String description;
    private String activeFlag;
    private Integer schoolId;
}