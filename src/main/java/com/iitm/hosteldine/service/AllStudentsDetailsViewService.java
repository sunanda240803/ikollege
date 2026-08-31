package com.iitm.hosteldine.service;

import org.springframework.stereotype.Service;

import com.iitm.hosteldine.dto.student.AllStudentsDetailsViewDto;
import com.iitm.hosteldine.mapper.student.AllStudentsDetailsViewMapper;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AllStudentsDetailsViewService {

	private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;

	public AllStudentsDetailsViewDto getCompleteStudentDetails(String studentId) {
		AllStudentsDetailsViewEntity studentDetails = allStudentsDetailsViewRepository.getCompleteStudentDetails(studentId);
		if (studentDetails != null) {
			return AllStudentsDetailsViewMapper.INSTANCE.fromAllStudentsDetailsViewEntity(studentDetails);
		}
		return null;
	}
	
}
