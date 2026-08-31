package com.iitm.hosteldine.controller.dashboard.student;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.student.StudentComplaintDetailsDto;
import com.iitm.hosteldine.dto.student.StudentRollnoChangeDto;
import com.iitm.hosteldine.service.student.StudentComplaintFormService;
import com.iitm.hosteldine.service.student.StudentRollnoChangeService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.dashboard.RollNumberValidator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentComplaintFormWidgetController {
	
	private final StudentComplaintFormService studentComplaintFormService;
	
	
	@GetMapping("${url.student.complaint.list.widget}")
	public String getStudentComplaintList(ModelMap map, HttpServletRequest request) throws Exception {
		String studentId = SecurityCtxUtil.userId().toUpperCase(); 
		StudentComplaintDetailsDto complaintDto = studentComplaintFormService.getComplaintCounts(studentId);
		map.addAttribute("complaintDto", complaintDto);
		return HTMLPage.STUDENT_COMPLAINT_WIDGET_MODAL;
	}
	
	

	
}
