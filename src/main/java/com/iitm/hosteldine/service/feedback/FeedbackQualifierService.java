package com.iitm.hosteldine.service.feedback;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.feedback.FeedbackQualifierDto;
import com.iitm.hosteldine.mapper.feedback.FeedbackQualifierMapper;
import com.iitm.hosteldine.repository.feedback.FeedbackQualifierRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackQualifierService {
	
	private final FeedbackQualifierRepository feedbackQualifierRepository;
	
	public List<FeedbackQualifierDto> getQualifierList() {
		return Optional.ofNullable(feedbackQualifierRepository.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE))
				.orElse(Collections.emptyList()).stream()
				.map(FeedbackQualifierMapper.INSTANCE::fromFeedbackQualifierEntity).collect(Collectors.toList());
	}
	

}
