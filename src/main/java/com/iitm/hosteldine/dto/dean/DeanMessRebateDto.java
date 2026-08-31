package com.iitm.hosteldine.dto.dean;

import java.util.List;

import lombok.Data;

@Data
public class DeanMessRebateDto {
	
	private Long slNo;
    private String createdAt;
	private String studentId;
	private String fromDateLeave;
	private String toDateLeave;
	private String fromDateRebate;
	private String toDateRebate;
	private int noOfDays;
	private String description;
	private String status;
	private List<PropertyDto> actionList;
	private String studentName;
	private String gender;
	private String dob;
	private String studentAddress;
	private String city;
	private String state;
	private String pincode;
	private String studentMobile;
	private String studentEmail;
	private String reason;
	private String guideName;
	private String guideEmail;
	private String documentType;
	private String approvalStatusDetails;
	private List<DeanMessRebateWorkflowDto> deanMessRebateWorkflowDtoList;
	private String guideApprovalStatus;
	private String guideApprovalDate;
	private String ccwName;
	private String ccwApprovalStatus;
	private String ccwApprovalDate;
	private Long workflowId;
	private String workflowApprovalStatus;
	private String workflowApprovalNotes;
	private String workflowRejectReason;
	private String vacateDate;
	private String homeTown;
	private String selfDeclarationDate;
	private String selfDeclartionSignature;
	private String fileName;
	private Long bioDataId;
	private byte[] imageBytes;
	private String imageStr;
	private int approvalLevel;
	private String authorityType;
	private int totalApprovalCount;
 }
