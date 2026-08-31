package com.iitm.hosteldine.controller.hostel;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.SimsConfigDataDto;
import com.iitm.hosteldine.dto.feedback.FeedbackQuestionDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.FeedbackService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.feedback}")
public class FeedbackController {
	
	private final FeedbackService feedbackService;
	private final CommonResponseUtil commonResponseUtil;
	
	@Value("${url.feedback}")
	private String getFeedback;

	@GetMapping
	public String getFeedbackList(PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		Page<FeedbackQuestionDto> feedbackList = feedbackService.getFeedbackList(form);
		commonResponseUtil.updateCommonModelAttributes(map, request ,feedbackList , form);
		return HTMLPage.FEEDBACK_QUESTION;
	}
	
	
	@PostMapping
	public String saveUpdateFeedback(@Valid @ModelAttribute  FeedbackQuestionDto dto,BindingResult bindingResult,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		feedbackService.validateWeightage(dto, bindingResult);
		 if(bindingResult.hasErrors()){
	            commonResponseUtil.updateModalFormErrorAttributes(redirectAttrs,bindingResult,dto);
	            return Constants.REDIRECT + getFeedback;
	        }
		String saveStatus = feedbackService.saveUpdateFeedback(dto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus,redirectAttrs);
		return Constants.REDIRECT + getFeedback;
	}
	
	@GetMapping("${id}")
	public String getFeedbackById(@PathVariable long id, ModelMap map, HttpServletRequest request)
			throws Exception {
		 String formKey = "feedbackQuestionDto";
		 FeedbackQuestionDto feedbackQuestionDto = commonResponseUtil.handleModalFormError(request,map,formKey, FeedbackQuestionDto.class);

	        if (Objects.nonNull(id) && id > 0) {
	        	feedbackQuestionDto = feedbackService.getFeedbackById(id);
	        }

	        map.addAttribute(formKey, feedbackQuestionDto);
		return HTMLPage.FEEDBACK_QUESTION_MODAL;
	}
	
	
	@DeleteMapping("${id}")
    public @ResponseBody BaseResponse deleteFeedback(@PathVariable("id") Long id) {
        try {
            return CommonResponseUtil.generateDeleteResponseByStatus(feedbackService.deleteFeedback(id));
        } catch (RecordNotExistsException e) {
            BaseResponse errorResponse = new BaseResponse();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatus(Constants.FAILURE);
            return errorResponse;
        }
    }
	
	

	
}
