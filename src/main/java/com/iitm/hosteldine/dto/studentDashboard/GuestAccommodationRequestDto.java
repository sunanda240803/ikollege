package com.iitm.hosteldine.dto.studentDashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.StudentBioDataFamilyInfoDto;
import com.iitm.hosteldine.dto.student.StudentDetailsInfoDto;
import com.iitm.hosteldine.dto.student.StudentDetailsWithHostelDTO;
import com.iitm.hosteldine.dto.warden.WardenInfoDto;
import lombok.Data;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class GuestAccommodationRequestDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("parentRequestId")
    private Long parentRequestId;
    @JsonProperty("fromDate")
    private LocalDate fromDate;
    @JsonProperty("toDate")
    private LocalDate toDate;
    @JsonProperty("noOfDays")
    private Integer noOfDays;
    @JsonProperty("noOfPersons")
    private Integer noOfPersons;
    @JsonProperty("purposeOfVisit")
    private String purposeOfVisit;
    @JsonProperty("amount")
    private Integer amount;
    @JsonProperty("wardenName")
    private String wardenName;
    @JsonProperty("wardenEmail")
    private String wardenEmail;
    @JsonProperty("wardenApprovalStatus")
    private String wardenApprovalStatus;
    @JsonProperty("approvalDate")
    private LocalDate approvalDate;
    @JsonProperty("rejectionDescription")
    private String rejectionDescription;
    @JsonProperty("cancelStatus")
    private String cancelStatus;
    @JsonProperty("paymentStatus")
    private String paymentStatus;
    @JsonProperty("paymentType")
    private String paymentType;
    @JsonProperty("paymentReferenceNo")
    private String paymentReferenceNo;
    @JsonProperty("paymentAmount")
    private Integer paymentAmount;
    @JsonProperty("paymentDate")
    private LocalDateTime paymentDate;
    @JsonProperty("applicableCharges")
    private Boolean applicableCharges;
    @JsonProperty("documentsUploaded")
    private Boolean documentsUploaded;
    @JsonProperty("approvalNotes")
    private String approvalNotes;
    @JsonProperty("accommodationType")
    private String accommodationType;
    @JsonProperty("arName")
    private String arName;
    @JsonProperty("mailSentTo")
    private String mailSentTo;
    @JsonProperty("allocationStatus")
    private String allocationStatus;
    @JsonProperty("bloodRelationStatus")
    private Boolean bloodRelationStatus;
    @JsonProperty("checkinTime")
    private String checkinTime;
    @JsonProperty("checkoutTime")
    private String checkoutTime;
    @JsonProperty("studentDetailsInfo")
    private StudentDetailsInfoDto studentDetailsInfo;
    @JsonProperty("secondaryAmount")
    private Integer secondaryAmount;

    private Long currentStayExtensionId;
    private LocalDate stayTo;
    private LocalDate createdFrom;
    private String studentId;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate requestStartDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate requestEndDate;
	private String guestName;
	private String guestRelation;
	private String gender;
	private String fileDescription1;
	private String fileDescription2;
	private List<GuestAccommodationGuestDetailsDto> guestList;
    private List<String> fileDescriptions  = new ArrayList<>();
    private List<MultipartFile> chooseFiles  = new ArrayList<>();
    private String roomNo;
    private String studentName;
    private String floorName;
    private String hostelName;
    private Long hostelId;
    private ValidatorForm wardenForm;
    private ValidatorForm inchargeForm;
    private List<ValidatorForm> validatorList;
    private String encryptedRequestId;
    private List<WardenInfoDto> approvalList;
    private List<GuestFilesInformationDto> uploadFileList;
    private Long guestId;
    private String stayToDate;
    private List<GuestAccommodationRequestDto> stayExtensionList;
    private List<StudentBioDataFamilyInfoDto> familyDetails;
    private StudentDetailsWithHostelDTO studentDetailsWithHostelDTO;


    private transient LocalDateTime checkInDateTime;
    private transient LocalDateTime checkOutDateTime;
    // Helper methods to split datetime into date and time components
    public void processDateTimeFields() {
        if (checkInDateTime != null) {
            this.fromDate = checkInDateTime.toLocalDate();
            this.checkinTime = String.format("%02d:00", checkInDateTime.getHour());
        }
        if (checkOutDateTime != null) {
            this.toDate = checkOutDateTime.toLocalDate();
            this.checkoutTime = String.format("%02d:00", checkOutDateTime.getHour());
        }
    }
    // For initializing the datetime fields when loading from DB
    public void initDateTimeFields() {
        // For check-in
        if (fromDate != null) {
            int hour = 0; // Default hour
            if (checkinTime != null && !checkinTime.trim().isEmpty()) {
                hour = parseHourOrDefault(checkinTime, 0);
            }
            this.checkInDateTime = LocalDateTime.of(fromDate, LocalTime.of(hour, 0));
        }
        // else checkInDateTime remains null

        // For check-out
        if (toDate != null) {
            int hour = 0; // Default hour
            if (checkoutTime != null && !checkoutTime.trim().isEmpty()) {
                hour = parseHourOrDefault(checkoutTime, 0);
            }
            this.checkOutDateTime = LocalDateTime.of(toDate, LocalTime.of(hour, 0));
        }
        // else checkOutDateTime remains null
    }

    private int parseHourOrDefault(String timeString, int defaultHour) {
        try {
            return Integer.parseInt(timeString.split(":")[0]);
        } catch (Exception e) {
            return defaultHour;
        }
    }
}