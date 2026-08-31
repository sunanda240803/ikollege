package com.iitm.hosteldine.service.studentDashboard;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iitm.hosteldine.repository.studentDashboard.StudentMessDetailsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentMessDetailsService {
	private final StudentMessDetailsRepository studentMessDetailsRepository;

	public List<Object[]> fetchStudentDetails(String studentId, Long previousId, String statusActive) {
		List<Object[]> results = studentMessDetailsRepository.fetchStudentDetails(studentId,previousId,statusActive);
		return (results != null && !results.isEmpty()) ? results : Collections.emptyList();
	}
	
}
