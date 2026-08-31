package com.iitm.hosteldine.dto.dean;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAccomBulkRequestDto {

	private Long slNo;
	private Integer bulkAppointmentId;
	private String createdAt;
	private String createdBy;
	private String eventName;
	private String fromDate;
	private String toDate;
	private String approvalStatus;
	private String approvalNotes;
	private String fileName;
	private Long count;
	private String approvedBy;
	private Integer noOfMaleParticipants;
	private Integer noOfFemaleParticipants;
	private List<PropertyDto> actionList;

	public StudentAccomBulkRequestDto(Long slNo, Integer bulkAppointmentId, Date createdAt,
									  String createdBy, String eventName, Date fromDate, Date toDate,
									  String approvalStatus, String approvalNotes, String fileName, Long count) {


	    SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(Constants.FRONTEND_DATE_FORMAT);
		this.slNo = slNo;
		this.bulkAppointmentId = bulkAppointmentId;
		this.createdAt = createdAt != null ? DateUtility.formatSqlDateToStringWithFormat(createdAt) : "N/A";
		this.createdBy = createdBy;
		this.eventName = eventName;
		this.fromDate = fromDate != null ? DateUtility.formatSqlDateToStringWithFormat(fromDate) : "N/A";
		this.toDate = toDate != null ? DateUtility.formatSqlDateToStringWithFormat(toDate) : "N/A";
		this.approvalStatus = approvalStatus;
		this.approvalNotes = approvalNotes;
		this.fileName = fileName;
		this.count = count;
	}

	/*public StudentBulkRequestUploadDto(Object[] result) {
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		this.slNo = result[0] != null ? Long.valueOf(result[0].toString()) : null;
		this.bulkAppointmentId = result[1] != null ? Long.valueOf(result[1].toString()) : null;
		this.createdAt = result[0] != null ? LocalDate.parse((String) result[2], formatter2) : null;
		this.createdBy =  (String) result[3];
		this.eventName =  (String) result[4];
		this.fromDate = result[0] != null ? LocalDate.parse((String) result[5], formatter2) : null;
		this.toDate = result[6] != null ? LocalDate.parse((String) result[6], formatter2) : null;
		this.approvalStatus =  (String) result[7];
		this.approvalNotes =  (String) result[8];
		this.fileName =  (String) result[9];
		this.count = result[10] != null ? Long.valueOf(result[10].toString()) : null;;
	}*/
}