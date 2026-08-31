package com.iitm.hosteldine.controller.dashboard.student;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.dto.student.StudentRoomInfoDTO;
import com.iitm.hosteldine.dto.studentDashboard.StudentMessPriorityRegistrationDto;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import com.iitm.hosteldine.service.studentDashboard.StudentMessLoginIssuePriorityService;
import com.iitm.hosteldine.service.studentDashboard.StudentMessPriorityRegistrationService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("${url.login.issue.mess.reg}")
public class StudentMessLoginIssuePriorityController {
	private final MessMasterCommonService messMasterCommonService;
	private final StudentMessPriorityRegistrationService studentMessPriorityRegistrationService;
	private final StudentMessLoginIssuePriorityService studentMessLoginIssuePriorityService;
	private final CommonResponseUtil commonResponseUtil;
	private final StudentDetailsInfoService studentService;
	private final HostelMasterService hostelService;

	@Value("${url.login.issue.mess.reg}")
	private String getLoginIssueUrl;

	@GetMapping()
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

        commonResponseUtil.updateCommonModelAttributes(map, request);
        map.addAttribute("messPriorityRegDto", new StudentMessPriorityRegistrationDto());
        map.addAttribute("messRegistrationList", messRegistrationList);
		map.addAttribute("dinningFromDate", dinningFromDate);
		map.addAttribute("dinningToDate", dinningToDate);
		map.addAttribute("previousId", previousId);
		map.addAttribute("currentId", currentId);
		map.addAttribute("messId", 1);
		map.addAttribute("hostels", hostelService.getHostelList());
		
		return HTMLPage.LOGIN_ISSUE_MESS_PRIORITY;
		
	}

	@GetMapping("${url.student}/{id}")
	public @ResponseBody StudentRoomInfoDTO getStudentRoomDetails(@PathVariable String id) {
		return studentService.getStudentRoomDetails(id);
	}

	@GetMapping("/getMessFragmentByGender")
	public String getMessFragmentByGender(@RequestParam("studentId") String studentId, ModelMap model) {

		List<StudentMessPriorityRegistrationDto> messRegistrationList = studentMessPriorityRegistrationService.getMessRegistrationList(studentId);
		for (StudentMessPriorityRegistrationDto dto : messRegistrationList) {
			if (dto.getMessList() == null) {
				dto.setMessList(new ArrayList<>());
			}
		}

		model.addAttribute("messRegistrationList", messRegistrationList);
		return "dashboard/student/login-issue-mess-list-fragment :: messListFragment";
	}

	@PostMapping("${url.login.issue.save}")
	public String saveLoginIssue(@ModelAttribute("messPriorityRegDto") StudentMessPriorityRegistrationDto dto,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		try {
			String saveStatus = null;
			// To check mess capacity
			saveStatus = studentMessPriorityRegistrationService.checkValidations(dto,Constants.LOGIN_ISSUE);
			if (saveStatus != null) {
				throw new IllegalArgumentException(saveStatus);
			} else {
				saveStatus = studentMessLoginIssuePriorityService.saveLoginIssueMessPriorityRegistration(dto);
				commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs);
			}
			return "redirect:" + getLoginIssueUrl;
		} catch (Exception e) {
			commonResponseUtil.exceptionMessageHandling(e, redirectAttrs);
		}
		return "redirect:" + getLoginIssueUrl;
	}

}
