package com.iitm.hosteldine.dto.dean;

import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OtherCandidateRequestDto {
    private String slNo;
    private String createdAt;
    private String candidateName;
    private String gender;
    private String appointmentFrom;
    private String appointmentTo;
    private String stayFrom;
    private String stayTo;
    private String category;
    private String requestType;
    private String purpose;
    private String validatingAuthorityEmail;
    private String messOption;
    private String dob;
    private Double grossPay;
    private String validatingAuthority;
    private String city;
    private String state;
    private String phoneNumber;
    private String facilityMasterName;
    private String roomNo;
    private String subRoom;
    private String email;
    private String applicationNo;
    private String address1;
    private String address2;
    private Long pin;
    private String programOrDept;
    private String designation;
    private String occupancy;
    private String categoryOthers;
    private String approvalNotes;
    private String approvalDate;
    private String rejectionReason;
    private String description;
    private String postSelect;
    private String postOthers;
    private String appStatus;
    private String workStatus;
    private String approvalStatus;
    private Long candidateId;
    private Boolean dining;
    private String empId;
    private Long stayId;
    private String hostelName;
    private Long workflowId;
    private Boolean checkInStatus;
    private List<PropertyDto> actionList;
    private String modifiedAt;

    private String priorityApplicant;
    private String notes;
    private String type;

    private List<HostelMasterDto> hostelListGenderBased;
    private String seat;
    private String allottedHostelName;
}