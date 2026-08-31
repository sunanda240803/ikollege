package com.iitm.hosteldine.dto.dean;

import java.util.List;

import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import lombok.Data;

@Data
public class DeanAccommodationRequestDto {
	
	private Long slNo;
    private String createdAt;
	private String firstName;
	private String gender;
	private String appointmentFrom;
	private String appointmentTo;
	private String stayFrom;
	private String stayTo;
	private String applicationId;
	private String validatingAuthorityEmail;
	private String category;
	private String hostelName;
	private String dining;
	private String city;
	private String phoneNumber;
	private String occupancy;
	private String state;
	private String purpose;
	private String validatingAuthority;
	private Double grossPay;
	private String categoryOthers;
	private String diningOthers;
	private String hodName;
	private String hodEmail;
	private String cancelDescription;
	private String authorityType;
	private String approvalLevel;
	private String rejectionReason;
	private String approvalDate;
	private String approvalNotes;
	private List<PropertyDto> actionList;
	private String admissionDate;
	private String thesisSubmittedDate;
	private String approveStatus;
	private String roomNo;
	private String seat;
	private String allottedHostelName;
	private String requestStatus;
	private String workflowStatus;
	private List<HostelMasterDto> hostelListGenderBased;
	private String checkInStatus;
	private String checkInDate;
}
