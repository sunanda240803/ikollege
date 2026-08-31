package com.iitm.hosteldine.dto.hostel;

import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentsHostelAllotmentDto {

	private String studentId;
	private Long hostelId;
	private String hostelName;
	private Long roomId;
	private String roomNo;
	private String seat;
	private Integer capacity;
	private String uploadType;
	
	private MultipartFile file;
	private String error;
	private String excelErrorMsg;
	private ArrayList<String> errorList;
	private byte[] fileBytes;
	private String logTag;
}
