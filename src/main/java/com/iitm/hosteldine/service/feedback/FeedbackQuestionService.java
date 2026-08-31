package com.iitm.hosteldine.service.feedback;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.feedback.FeedbackQuestionDto;
import com.iitm.hosteldine.mapper.feedback.FeedbackQuestionMapper;
import com.iitm.hosteldine.repository.feedback.FeedbackQuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackQuestionService {
	private final FeedbackQuestionRepository feedbackQuestionRepository;
	
	
	public List<FeedbackQuestionDto> getQuestionList() {
		return Optional.ofNullable(feedbackQuestionRepository.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE))
				.orElse(Collections.emptyList()).stream().map(FeedbackQuestionMapper.INSTANCE::fromFeedbackQuestionEntity)
				.collect(Collectors.toList());
	}

}
