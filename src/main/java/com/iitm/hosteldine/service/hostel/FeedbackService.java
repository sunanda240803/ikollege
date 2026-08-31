package com.iitm.hosteldine.service.hostel;

import java.util.Locale;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.SimsConfigDataDto;
import com.iitm.hosteldine.dto.feedback.FeedbackQuestionDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.SimsConfigDataMapper;
import com.iitm.hosteldine.mapper.feedback.FeedbackQuestionMapper;
import com.iitm.hosteldine.model.feedback.FeedbackQuestionEntity;
import com.iitm.hosteldine.repository.feedback.FeedbackQuestionRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackService {
	private final FeedbackQuestionRepository feedbackQuestionRepository;
	private final MessageSource messageSource;
	

	public Page<FeedbackQuestionDto> getFeedbackList(PaginationForm form) {
		Page<FeedbackQuestionEntity> result = null;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("createdAt").descending());
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			result = feedbackQuestionRepository.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE, pageable);
		}
		else {
			result = feedbackQuestionRepository.getFeedbackSearchList(ModelConstants.STATUS_ACTIVE, pageable,form.getSearch());
		}
		return result.map(FeedbackQuestionMapper.INSTANCE::fromFeedbackQuestionEntity);
	}

	
	public String saveUpdateFeedback(FeedbackQuestionDto dto) {
		String result = Optional.ofNullable(dto.getFeedbackQuesId()).filter(id -> (id != null && id > 0))
				.flatMap(id -> feedbackQuestionRepository.findByFeedbackQuesIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					FeedbackQuestionMapper.INSTANCE.onUpdateEntity(existingEntity, dto);
					feedbackQuestionRepository.save(existingEntity);
					return Constants.UPDATED;
				}).orElseGet(() -> {
					FeedbackQuestionEntity newEntity = FeedbackQuestionMapper.INSTANCE.onSaveEntity(dto);
					feedbackQuestionRepository.save(newEntity);
					return Constants.SAVED;
				});
		return result;
	}
	
	
	public FeedbackQuestionDto getFeedbackById(long id) {
		return feedbackQuestionRepository.findByFeedbackQuesIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
				.map(FeedbackQuestionMapper.INSTANCE::fromFeedbackQuestionEntity)
				.orElse(new FeedbackQuestionDto());
	}

	public boolean deleteFeedback(Long id) throws RecordNotExistsException {
		return feedbackQuestionRepository.findById(id).map(entity -> {
			entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
			feedbackQuestionRepository.save(entity);
			return true;
		}).orElseThrow(() -> new RecordNotExistsException(
				messageSource.getMessage("validation.error.id.not.found", null, Locale.getDefault())));
	}
	
	
	public void validateWeightage(@Valid FeedbackQuestionDto dto, BindingResult bindingResult) {
	    if (dto.getFeedbackWeightage()==null) {
	        String errorMessage = messageSource.getMessage("message.validation.weightage.required", null, Locale.getDefault());
	        bindingResult.rejectValue("feedbackWeightage", "error.feedbackWeightage", errorMessage);
	    }
	}
	



}
