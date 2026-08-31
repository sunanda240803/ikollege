package com.iitm.hosteldine.dto.hostel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HostelRoomInfoDto {
	private Long id;
	private String roomNo;
	private HostelFloorMasterDto building;
	private Integer capacity;
	private Integer occupied;
	private Boolean isVacation;
	private String roomSize;
	private Integer vacCapacity;
	private String officialGuestStatus = "0";
	private MultipartFile file;
	private String scheduleDate;

	private long hostelId;

	private long floorId;

	private ArrayList<String> errorList;
	private String excelErrorMsg;
	private String error;

	private String hostelName;
	private Map<String, Integer> occupancyData;
	private List<Integer> studentCount;
	private int totalVacantSeats;
	private LocalDate stayFromDate;
	private LocalDate stayToDate;
	private LocalDate vacateDate;
	private LocalDate shiftedDate;
	private String studentType;
	private String studentName;
	private String studentId;
	private String email;
	private String subRoomId;
	private String candidateName;
	private String category;
	private String otherCategory;
	private Long roomAllotmentId;
	private Long requestId;
	private Long roomId;
	private String status;
	private String pwdStatus;
	private Boolean dining;
	private Long stayId;
	private LocalDate dob;
	private String gender;
	private Long remainingCount;
	private Integer averageStudentCount;
	private Long totalCapacity;
	private Long noOfDays;
	private Long noOfPerson;
	private String guestRelationship;
	private String guestName;
	private List<String> seatList;
	private List<String> vacantList;
    private Boolean isGenderCorrect;

	public HostelRoomInfoDto(Long id, String roomNo) {
        this.id = id;
        this.roomNo = roomNo;
    }

	public LocalDate getStayEndDate() {
		if (stayToDate == null && vacateDate == null && shiftedDate == null) {
			return null;
		} else {
			return stayToDate != null ? stayToDate : (vacateDate != null ? vacateDate : shiftedDate);
		}
	}

}