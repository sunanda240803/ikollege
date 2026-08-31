package com.iitm.hosteldine.controller.dashboard.student;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.dto.studentDashboard.StudentMessPriorityRegistrationDto;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentBioDataService;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.service.studentDashboard.StudentMessPriorityRegistrationService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("${url.priority.mess.widget}")
public class StudentMessPriorityRegistrationController {
	private final MessMasterCommonService messMasterCommonService;
	private final StudentBioDataService studentBioDataService;
	private final StudentMessPriorityRegistrationService studentMessPriorityRegistrationService;
	private final CommonResponseUtil commonResponseUtil;
	private final MessMasterService messMasterService;
	private final SimsConfigDataService simsConfigDataService;

	@Value("${url.priority.mess.widget}")
	private String getPriorityUrl;
	@Value("${url.priority.get.tab}")
	private String getPriorityMessTabs;
	@Value("${url.priority.mess.get}")
	private String getPriorityList;
	@Value("${url.mess.feedback}")
	private String getMessFeedback;
	@Value("${url.get.student.biodata}")
	private String getStudentBioData;
	@Value("${url.get.applicationNo}")
	private String getApplicationNo;
	
	// Priority mess tab section
	@GetMapping("${url.priority.get.tab}")
	public String getPriorityMessWidget(ModelMap map,HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
		/* Student bio data add /edit */
		boolean biodataStatus = false;
		List<MessMasterControllerDto> messControllerList = messMasterCommonService.getMessMasterControllerList();
		if (messControllerList.get(0).getStudentEditStatus() != null
				&& messControllerList.get(0).getStudentEditStatus() == true) {
			biodataStatus = studentBioDataService.checkBiodataFormByStudentId(SecurityCtxUtil.userId().toUpperCase());

		} else biodataStatus = true;
		Long previousId = null;
		MessMasterControllerDto dto = null;
		String feedbackFormStatus = Constants.DISABLE, messFeedBackStatus = null;

		// Get Mess Master controller previous and current month id and feedback status
		Optional<MessMasterControllerDto> messPeriodDetails = messMasterCommonService.getMessPeriodDetails();
		if (messPeriodDetails.isPresent()) {
			dto = messPeriodDetails.get();
			previousId = dto.getPreviousId(); // Access the previousId
		}

		// Get student feedback status for previous month
		MessMasterControllerDto studentMessFeedbackStatus = messMasterCommonService
				.getStudentMessFeedbackStatus(previousId, SecurityCtxUtil.userId().toUpperCase());
			messFeedBackStatus = studentMessFeedbackStatus != null && dto.getFeedbackStatus() ? Constants.TRUE : Constants.FALSE;
		
		// Enable or disable feedback form condition
		if (messFeedBackStatus != null && messFeedBackStatus.equalsIgnoreCase(Constants.FALSE)) {
			feedbackFormStatus = Constants.DISABLE;
		} else {
			MessMasterControllerDto messRegistrationForm = messMasterCommonService
					.getStudentDetails(SecurityCtxUtil.userId().toUpperCase(), previousId);
			feedbackFormStatus = messRegistrationForm != null && messRegistrationForm.getFeedbackStatus() ? Constants.DISABLE : Constants.ENABLE;
		}
		
		// Get student previous mess details
    	MessMasterControllerDto previousMessDetails = studentMessPriorityRegistrationService.getPreviousMessDetails();
        map.addAttribute("messId", previousMessDetails != null ? previousMessDetails.getMessId() : 0);
		commonResponseUtil.updateCommonModelAttributes(map, request);
        redirectAttributes.addFlashAttribute("fromCheck", true);
		if(!biodataStatus) {
			return "redirect:" + getPriorityUrl + getStudentBioData;
		}
		else if(feedbackFormStatus.equalsIgnoreCase(Constants.ENABLE) && previousMessDetails != null && previousMessDetails.getMessId() != 0) {
			return "redirect:" + getMessFeedback;
		}else {
			return "redirect:" + getPriorityUrl + getPriorityList;
		}
	}
	
	@GetMapping("${url.priority.mess.get}")
	public String getPriorityMessList(ModelMap map , HttpServletRequest request) throws Exception {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT); 
		Long previousId = null , currentId = null;
		MessMasterControllerDto messMasterControllerDto = null;
		
		// Get Mess Master controller previous and current month id and feedback status
		Optional<MessMasterControllerDto> messPeriodDetails = messMasterCommonService.getMessPeriodDetails();
		if (messPeriodDetails.isPresent()) {
			messMasterControllerDto = messPeriodDetails.get();
			previousId = messMasterControllerDto.getPreviousId(); 
			currentId = messMasterControllerDto.getId();
		}
		
		// Mess master controller details
		List<MessMasterControllerDto> messControllerList = messMasterCommonService.getMessMasterControllerList();
		String dinningFromDate = "", dinningToDate = "";
		if(messControllerList != null ) {
			dinningFromDate = messControllerList.get(0).getDiningFromDate().format(formatter);
			dinningToDate = messControllerList.get(0).getDiningToDate().format(formatter);
		}
		
		// get mess priority registration list
		List<StudentMessPriorityRegistrationDto> messRegistrationList = studentMessPriorityRegistrationService.getMessRegistrationList(null);
        for (StudentMessPriorityRegistrationDto dto : messRegistrationList) {
            if (dto.getMessList() == null) {
                dto.setMessList(new ArrayList<>());
            }
        }
        MessMasterDto messMasterList = messMasterService.getMessList();
        Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
        Boolean fromCheck = flashInputMap != null ? (Boolean) flashInputMap.get("fromCheck") : null;
        if (Boolean.FALSE.equals(fromCheck)) {
            return "redirect:" + getPriorityUrl + getPriorityList;
        }
        if(messMasterList != null) {
        	map.addAttribute("currentMessName", messMasterList.getMessName());
        }else {
        	map.addAttribute("currentMessName", "-");
        }
        
        commonResponseUtil.updateCommonModelAttributes(map, request);
        map.addAttribute("messRegistrationList", messRegistrationList);
		map.addAttribute("dinningFromDate", dinningFromDate);
		map.addAttribute("dinningToDate", dinningToDate);
		map.addAttribute("previousId", previousId);
		map.addAttribute("currentId", currentId);
		map.addAttribute("messId", 1);
		
		return HTMLPage.PRIORITY_MESS_LIST;
		
	}
	
	@PostMapping("${url.priority.mess.save}")
	public String savePriorityMess(@ModelAttribute("messRegistrationList") StudentMessPriorityRegistrationDto dto,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		try {
			String saveStatus = null;
			// To check mess capacity
			saveStatus = studentMessPriorityRegistrationService.checkValidations(dto,null);
			if (saveStatus != null) {
				throw new IllegalArgumentException(saveStatus);
			} else {
				saveStatus = studentMessPriorityRegistrationService.saveStudentMessPriorityRegistration(dto);
				commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs);
			}
			return "redirect:" + getPriorityUrl + getPriorityList;
		} catch (Exception e) {
			commonResponseUtil.exceptionMessageHandling(e, redirectAttrs);
		}
		return "redirect:" + getPriorityUrl + getPriorityList;
	}

	// Student dashboard - priority mess registration widget
	@GetMapping
	public String getPriorityMessStatus(ModelMap map, HttpServletRequest request) throws Exception {
		/* Student bio data add /edit */
		String dinningFromDate = "", dinningToDate = "";
		String regBeginDate = "", regEndDate = "";
		String beginTime = "", endTime = "";
        
		boolean biodataStatus = false;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
		List<MessMasterControllerDto> messControllerList = messMasterCommonService.getMessMasterControllerList();
		dinningFromDate = messControllerList != null ? messControllerList.get(0).getDiningFromDate().format(formatter): ModelConstants.EMPTY_STRING;
		dinningToDate = messControllerList != null ? messControllerList.get(0).getDiningToDate().format(formatter): ModelConstants.EMPTY_STRING ;
		regBeginDate = messControllerList != null ? messControllerList.get(0).getRegBeginDate().toString(): ModelConstants.EMPTY_STRING ;
		regEndDate = messControllerList != null ? messControllerList.get(0).getRegEndDate().toString(): ModelConstants.EMPTY_STRING ;
		beginTime = messControllerList != null ? messControllerList.get(0).getRegBeginTime(): ModelConstants.EMPTY_STRING ;
		endTime = messControllerList != null ? messControllerList.get(0).getRegEndTime(): ModelConstants.EMPTY_STRING ;
		
		String beginDateTimeString = regBeginDate + Constants.TIME + beginTime + ":00";
		String endDateTimeString = regEndDate + Constants.TIME + endTime + ":00";
		LocalDateTime beginDateTime = LocalDateTime.parse(beginDateTimeString);
        LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeString);
        
     // Calculate the days difference
        long daysDifferenceBegin = Duration.between(LocalDateTime.now(), beginDateTime).toDays();
        long daysDifferenceEnd = Duration.between(LocalDateTime.now(), endDateTime).toDays();

        
		if (messControllerList != null && messControllerList.get(0).getStudentEditStatus() != null
				&& messControllerList.get(0).getStudentEditStatus() == true) {
			biodataStatus = studentBioDataService.checkBiodataFormByStudentId(SecurityCtxUtil.userId().toUpperCase());
		}
		Long previousId = null;
		MessMasterControllerDto dto = null;
		String feedbackFormStatus = Constants.DISABLE, messFeedBackStatus = null;

		// Get Mess Master controller previous and current month id and feedback status
		Optional<MessMasterControllerDto> messPeriodDetails = messMasterCommonService.getMessPeriodDetails();
		if (messPeriodDetails.isPresent()) {
			dto = messPeriodDetails.get();
			previousId = dto.getPreviousId(); // Access the previousId
		}

		// Get student feedback status for previous month
		MessMasterControllerDto studentMessFeedbackStatus = messMasterCommonService
				.getStudentMessFeedbackStatus(previousId, SecurityCtxUtil.userId().toUpperCase());
		messFeedBackStatus = studentMessFeedbackStatus != null && dto.getFeedbackStatus() ? Constants.TRUE : Constants.FALSE;
		
		// Enable or disable feedback form condition
		if (messFeedBackStatus != null && messFeedBackStatus.equalsIgnoreCase(Constants.FALSE)) {
			feedbackFormStatus = Constants.DISABLE;
		} else {
			MessMasterControllerDto messRegistrationForm = messMasterCommonService
					.getStudentDetails(SecurityCtxUtil.userId().toUpperCase(), previousId);
			feedbackFormStatus = messRegistrationForm != null && messRegistrationForm.getFeedbackStatus() ? Constants.DISABLE : Constants.ENABLE;
			
		}
		// Get student previous mess details
		MessMasterControllerDto previousMessDetails = studentMessPriorityRegistrationService.getPreviousMessDetails();
		
		// get mess priority registration list
		List<StudentMessPriorityRegistrationDto> messRegistrationList = studentMessPriorityRegistrationService.getMessRegistrationList(null);
		
        map.addAttribute("currentMessName", messRegistrationList != null && messRegistrationList.get(0) != null ? messRegistrationList.get(0).getMessName() : "");
		map.addAttribute("messId", previousMessDetails != null ? previousMessDetails.getMessId() : 0);
		map.addAttribute("feedbackFormStatus", feedbackFormStatus);
		map.addAttribute("studDetailEditStatus", biodataStatus);
        map.addAttribute("applicationNumber", studentBioDataService.getStudentDetails(SecurityCtxUtil.userId().toUpperCase()).getApplicationNumber());
        map.addAttribute("dinningFromDate", dinningFromDate);
		map.addAttribute("dinningToDate", dinningToDate);
		map.addAttribute("currentDateTime", LocalDateTime.now());
		map.addAttribute("beginDateTime", beginDateTime);
        map.addAttribute("endDateTime", endDateTime);
        map.addAttribute("daysDifferenceBegin", daysDifferenceBegin);
        map.addAttribute("daysDifferenceEnd", daysDifferenceEnd);
		return HTMLPage.PRIORITY_MESS_REGISTRATION_WIDGET;
	}
	

	@GetMapping("${url.get.student.biodata}")
    public String studentRegistration(ModelMap map, HttpServletRequest request) {
        Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
        map.addAttribute("saveStatus", flashInputMap != null ? flashInputMap.get("saveStatus") : null);
        map.addAttribute("form", new StudentBioDataFormDetailDto());
		LocalDate dobMax = LocalDate.of(LocalDate.now().getYear() - 16, 12, 31);
		map.addAttribute("dobMax", java.sql.Date.valueOf(dobMax));
        map.addAttribute("declarationMin", LocalDate.now());
        return HTMLPage.STUDENT_BIO_DATA_REGISTRATION;
    }
	
	@PostMapping("${url.student.biodata.registration}")
    public String saveStudentBiodataForm(@ModelAttribute StudentBioDataFormDetailDto form, RedirectAttributes redirectAttributes) throws Exception {
		form.setStudentId(SecurityCtxUtil.userId().toUpperCase());
        String saveStatus = studentBioDataService.saveStudentRegistration(form);
        redirectAttributes.addFlashAttribute("saveStatus", saveStatus);
        return "redirect:" + getPriorityUrl + getPriorityMessTabs;
    }

//    @GetMapping("${url.get.mess.period}")
//    @ResponseBody
//    public boolean checkMessPeriod() {
//        List<MessMasterControllerDto> messControllerList = messMasterCommonService.getMessMasterControllerList();
//        String regBeginDate = Objects.nonNull(messControllerList) ? messControllerList.getFirst().getRegBeginDate().toString(): ModelConstants.EMPTY_STRING ;
//        String regEndDate = Objects.nonNull(messControllerList) ? messControllerList.getFirst().getRegEndDate().toString(): ModelConstants.EMPTY_STRING ;
//        String beginTime = Objects.nonNull(messControllerList) ? messControllerList.getFirst().getRegBeginTime(): ModelConstants.EMPTY_STRING ;
//        String endTime = Objects.nonNull(messControllerList) ? messControllerList.getFirst().getRegEndTime(): ModelConstants.EMPTY_STRING ;
//        String beginDateTimeString = regBeginDate + Constants.TIME + beginTime + ":00";
//        String endDateTimeString = regEndDate + Constants.TIME + endTime + ":00";
//        LocalDateTime beginDateTime = LocalDateTime.parse(beginDateTimeString);
//        LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeString);
//        LocalDateTime currentDateTime = LocalDateTime.now();
//        return ((currentDateTime.isEqual(beginDateTime) || currentDateTime.isAfter(beginDateTime))
//                && (currentDateTime.isBefore(endDateTime)));
//    }

    @GetMapping("${url.get.feedback.details}")
    @ResponseBody
    public Boolean checkFeedBackDetails() {
        Optional<MessMasterControllerDto> messPeriodOpt = messMasterCommonService.getMessPeriodDetails();
        if (messPeriodOpt.isEmpty()) {
            return false;
        }
        MessMasterControllerDto dto = messPeriodOpt.get();
        MessMasterControllerDto studentMessFeedbackStatus = messMasterCommonService
                .getStudentMessFeedbackStatus(dto.getPreviousId(), SecurityCtxUtil.userId().toUpperCase());
        return Objects.nonNull(studentMessFeedbackStatus) && dto.getFeedbackStatus();
    }
}
