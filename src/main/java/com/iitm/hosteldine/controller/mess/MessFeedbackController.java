package com.iitm.hosteldine.controller.mess;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.iitm.hosteldine.constant.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.dto.feedback.FeedbackQualifierDto;
import com.iitm.hosteldine.dto.feedback.FeedbackQuestionDto;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.dto.studentDashboard.StudentMessCatererFeedbackDto;
import com.iitm.hosteldine.service.feedback.FeedbackQualifierService;
import com.iitm.hosteldine.service.feedback.FeedbackQuestionService;
import com.iitm.hosteldine.service.studentDashboard.StudentMessCatererFeedbackService;
import com.iitm.hosteldine.service.studentDashboard.StudentMessPriorityRegistrationService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.mess.feedback}")
public class MessFeedbackController {
	
	private final CommonResponseUtil commonResponseUtil;
	private final StudentMessPriorityRegistrationService studentMessPriorityRegistrationService;
	private final FeedbackQuestionService feedbackQuestionService;
	private final FeedbackQualifierService feedbackQualifierService;
	private final StudentMessCatererFeedbackService studentMessCatererFeedbackService;
	private final MessageSource messageSource;
	
	@Value("${url.priority.mess.widget}")
	private String getPriorityUrl;
	@Value("${url.priority.get.tab}")
	private String getPriorityMessTabs;
	
	@GetMapping
	public String getPreviousMessFeedback(ModelMap model, HttpServletRequest request) {
        Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
        Boolean fromCheck = flashInputMap != null ? (Boolean) flashInputMap.get("fromCheck") : null;
        if (Boolean.FALSE.equals(fromCheck)) {
            return "redirect:" + getPriorityUrl + getPriorityMessTabs;
        }
        try {
        	// Get student previous mess details
        	MessMasterControllerDto messPeriodDetails = studentMessPriorityRegistrationService.getPreviousMessDetails();
            model.addAttribute("messDetails", messPeriodDetails);
            // Get qualifiers and questions
            List<FeedbackQualifierDto> qualifierList = feedbackQualifierService.getQualifierList();
            List<FeedbackQuestionDto> questionList = feedbackQuestionService.getQuestionList();
            StudentMessCatererFeedbackDto messCatererFeedbackDto = new StudentMessCatererFeedbackDto();
            messCatererFeedbackDto.setQualifiers(qualifierList);
            messCatererFeedbackDto.setQuestions(questionList);
            model.addAttribute("feedbackList", messCatererFeedbackDto);
            model.addAttribute("messId", 1);
        } catch (Exception e) {
            model.addAttribute("errorMessage", messageSource.getMessage("message.exception.feedback", null, Locale.getDefault()));
            return "error";
        }

        return HTMLPage.STUDENT_FEEDACK;
    }
	
	@PostMapping
	public String saveFeedback(@ModelAttribute("feedbackList") StudentMessCatererFeedbackDto feedbackDto,
		HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
			String saveStatus = studentMessCatererFeedbackService.saveStudentMessFeedback(feedbackDto);
			commonResponseUtil.updateSaveResponseByStatus(saveStatus,redirectAttrs);
			return "redirect:" + getPriorityUrl + getPriorityMessTabs;
	}
	
}
	
	


