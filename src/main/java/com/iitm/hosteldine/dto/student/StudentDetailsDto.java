package com.iitm.hosteldine.dto.student;

import com.iitm.hosteldine.dto.StudentBioDataFamilyInfoDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StudentDetailsDto {

    private String studentId;
    private String category;
    private String bloodGroup;
    private AllStudentsDetailsViewDto allStudentsDetailsViewDto;
	private List<StudentBioDataFamilyInfoDto> familyInfoDtoList;
	private ArrayList<StudentBioDataFamilyInfoDto> familyDetails;
    private byte[] imageBytes;
}
