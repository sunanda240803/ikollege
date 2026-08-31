package com.iitm.hosteldine.service.studentDashboard;

import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.studentDashboard.StudentExcessMessDetailsDto;
import com.iitm.hosteldine.mapper.studentDashboard.StudentExcessMessDetailsMapper;
import com.iitm.hosteldine.repository.studentDashboard.StudentExcessMessDetailsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentExcessMessDetailsService {
	private final StudentExcessMessDetailsRepository studentExcessMessDetailsRepository;

	public StudentExcessMessDetailsDto getExcessMessDetails(int priorityMessId, String studentId, int currentId) {
		return studentExcessMessDetailsRepository.findByActiveFlagAndMmcIdAndStudentDetailsInfoStudentIdAndMessMasterId(ModelConstants.STATUS_ACTIVE,currentId,studentId,priorityMessId)
				.map(StudentExcessMessDetailsMapper.INSTANCE::fromStudentExcessMessDetailsEntity).orElse(null);
	}

}
