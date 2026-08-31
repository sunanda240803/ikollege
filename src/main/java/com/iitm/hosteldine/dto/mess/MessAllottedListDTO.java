package com.iitm.hosteldine.dto.mess;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class MessAllottedListDTO {
	private Long id;
	private String studentId;
	private String studentName;
	private LocalDate fromDate;
	private LocalDate toDate;
	private String messName;
	private String messHead;
	private String requestedMessHead;
	private String approvalStatus;
	private LocalDate changeFromDate;
	private LocalDate changeToDate;
	private LocalDate diningFromDate;
	private LocalDate diningToDate;
	private String pushRemoveStatus;
	private String pushStatus;
	private String currentActiveFlag;
	private String remarks;
	private String messStatus;
	private String toChange;
	private String toRemove;
	private String gender;
	private Long messId;
	private Long changeMessId;
	private LocalDate effectiveFromDate;
	private LocalDate effectiveTill;
	private String description;
	private String type;
	private Long mmcId;

	private String allocationType;
	private String messPeriod;
	private MultipartFile file;
	private String error;
	private String excelErrorMsg;
	private ArrayList<String> errorList;
	private byte[] fileBytes;
	private String logTag;
}
