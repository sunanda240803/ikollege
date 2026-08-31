package com.iitm.hosteldine.dto.hostel;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HostelRoomAllotmentInfoDto {
	
    private Long roomAllotmentId;
    private Long hostelId;
    private String hostelName;
    private Long buildingId;
    private Long floorId;
    private String floorName;
    private Long roomId;
    private String roomNo;
    private String subRoomId;

    private String studentId;
    private String studentType;
    private LocalDate vacateDate;
    private LocalDate stayFromDate;
    private LocalDate stayToDate;
    private LocalDate shiftedDate;
    private Integer newRoomAllotmentId;
    private String status;
    private String errorMessage;
    private LocalDateTime checkedInDate;
    private LocalDateTime checkedOutDate;
    private Boolean isMissing;
    private String vacationCheckoutStatus;
    private LocalDate vacationCheckoutDate;
    private LocalDate dob;
    private Long requestId;
    private String studentName;
    private String email;
    private String natureOfAppointment;
    private String diningRequired;
    private String allocationType;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;
    private String activeFlag;
    private String vacationCategory;


}
