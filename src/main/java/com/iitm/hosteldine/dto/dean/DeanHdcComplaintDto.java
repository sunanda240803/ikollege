package com.iitm.hosteldine.dto.dean;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class DeanHdcComplaintDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String category;
	private String studentName;
	private String studentId;
	private String hostelName;
	private Integer roomNo;
	private String dateOfComplaint;
	private String violation;
	private String wardenRemarks;
	private String status;
	private List<PropertyDto> actionList;
	private List<DeanHdcComplaintInvolvedStudentsDto> involvedStudentsDto;
	private String wardenPlea;
	private String wardenDecision;
	private String dayScholarInvolve;
	private String penaltyAmount;
	private LocalDate penaltyDueDate;
	private Double paidAmount;
	private String paymentDesc;
	private String paymentRefNo;
	private String penaltyStatus;
	private Long id;
	private Double dueAmount;
	private String data;
	private String studenIds;
	private String hostelId;
	private Boolean mailToParent;
	
	private MultipartFile file;
    private String fileDescription;
    private String fileName;
    
}
