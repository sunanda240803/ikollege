package com.iitm.hosteldine.service.studentDashboard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.feedback.FeedbackQuestionDto;
import com.iitm.hosteldine.dto.studentDashboard.StudentMessCatererFeedbackDto;
import com.iitm.hosteldine.model.mess.StudentMessCatererFeedbackEntity;
import com.iitm.hosteldine.repository.studentDashboard.StudentMessCatererFeedbackRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentMessCatererFeedbackService {
	private final StudentMessCatererFeedbackRepository studentMessCatererFeedbackRepository;

	public String saveStudentMessFeedback(StudentMessCatererFeedbackDto feedbackDto) {
		if (feedbackDto != null && feedbackDto.getQuestions() != null) {
			List<StudentMessCatererFeedbackEntity> feedbackEntities = new ArrayList<>();
			for (FeedbackQuestionDto question : feedbackDto.getQuestions()) {
				StudentMessCatererFeedbackEntity entity = new StudentMessCatererFeedbackEntity();
				Integer feedbackScore = question.getSelectedQualifier() * question.getFeedbackWeightage();
				entity.setFeedbackScore(feedbackScore);
				entity.setFeedbackQuesId(question.getFeedbackQuesId().intValue());
				entity.setStudentId(SecurityCtxUtil.userId().toUpperCase());
				entity.setMessControllerId(feedbackDto.getMessControllerId());
				entity.setMessMasterId(feedbackDto.getMessMasterId());
				feedbackEntities.add(entity);
			}

			studentMessCatererFeedbackRepository.saveAll(feedbackEntities);
		}

		return Constants.SAVED;
	}

	public List<StudentMessCatererFeedbackEntity> checkFeedbackAlreadyExistByMessId(String studentId, Long messId,
			Long previousId) {
		return studentMessCatererFeedbackRepository.findAllByStudentIdAndMessMasterIdAndMessControllerId(studentId,
				messId, previousId);
	}

	public List<StudentMessCatererFeedbackEntity> checkFeedbackAlreadyExistByPreviousId(String studentId,
			Long previousId) {
		return studentMessCatererFeedbackRepository.findAllByStudentIdAndMessControllerId(studentId, previousId);
	}

}
