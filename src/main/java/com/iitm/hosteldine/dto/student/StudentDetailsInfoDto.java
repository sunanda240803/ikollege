package com.iitm.hosteldine.dto.student;

import com.iitm.hosteldine.dto.dashboard.student.StudentWorkflowDto;
import com.iitm.hosteldine.dto.mess.FoodCourtLedgerDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
public class StudentDetailsInfoDto {
	private MultipartFile file;
	private String studentId;
	private String firstName;
	private String lastName;
	private String gender;
	private String course;
	private String previousId;
	private String emailId;
	private String ipAddress;
	private List<StudentWorkflowDto> workFlowList;
	private Long contactNumber;
	private String studentAddress;
	private String studentPersonalEmail;
	private String vacationCategory;
	private FoodCourtLedgerDto foodCourtLedgerDto;
	private String settlementFlag;
	private String dayScholar;

	public String getStudentName() {
		if (firstName == null && lastName == null) {
			return null;
		}
		if (firstName == null) {
			return lastName;
		}
		if (lastName == null) {
			return firstName;
		}
		return firstName + " " + lastName;
	}
}
