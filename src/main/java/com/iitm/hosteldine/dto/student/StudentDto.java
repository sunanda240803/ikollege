package com.iitm.hosteldine.dto.student;

import java.util.List;

import com.iitm.hosteldine.dto.StudentBioDataFamilyInfoDto;
import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.dto.collegeInfo.CourseMasterDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.hostel.HostelRoomAllotmentInfoDto;
import com.iitm.hosteldine.dto.hostel.HostelRoomInfoDto;

import lombok.Data;

@Data
public class StudentDto {

	private StudentBioDataFormDetailDto studentBioDataFormDetailDto;
	private StudentDetailsInfoDto studentDetailsInfoDto;
	private HostelRoomInfoDto hostelRoomInfoDto;
	private HostelMasterDto hostelMasterDto;
	private HostelRoomAllotmentInfoDto hostelRoomAllotmentInfoDto;
	private CourseMasterDto courseMasterDto;
	private List<StudentBioDataFamilyInfoDto> familyInfoDtoList;

}
