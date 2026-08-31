package com.iitm.hosteldine.service.student;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.model.hostel.CompleteStudentApplicationView;
import com.iitm.hosteldine.repository.hostel.CompleteStudentApplicationViewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompleteStudentApplicationViewService {
	private final CompleteStudentApplicationViewRepository completeStudentApplicationViewRepository;

	@Value("${message.validation.approved.date}")
	private String approvedDate;

	@Value("${message.validation.pending.date}")
	private String pendingDate;

	public String checkApprovalDate(LocalDate appointmentFrom, LocalDate appointmentTo, Long requestId) {
		Optional<CompleteStudentApplicationView> recentStatus = completeStudentApplicationViewRepository
				.getRecentStatus(SecurityCtxUtil.userId().toUpperCase(), appointmentFrom, appointmentTo,
						requestId != null ? requestId : 0L);
		if (recentStatus.isPresent()) {
			String status = recentStatus.get().getStatus();
			if (WorkflowStatus.APPROVED.getStatus().equals(status) || WorkflowStatus.ALLOTTED.getStatus().equals(status)
					|| WorkflowStatus.CHECKED_IN.getStatus().equals(status)) {
				return approvedDate;
			} else {
				return pendingDate;
			}
		} else {
			return null;
		}
	}
}
