package com.iitm.hosteldine.controller.hostel;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.student.StudentRollnoChangeService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("${url.student.roll.no.history}")
public class StudentRollNumberHistoryController {

	private final StudentRollnoChangeService studentRollnoChangeService;
	private final CommonResponseUtil commonResponseUtil;

	@GetMapping
	String studentRollNumberHistory(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map,
			HttpServletRequest request) {
		commonResponseUtil.getAdditionalParams(allParams, form);
		commonResponseUtil.updateCommonModelAttributes(map, request, studentRollnoChangeService.getStudentDetailsByPreviousId(form), form);
		map.addAttribute("studentId", form.getSearch());
		return HTMLPage.STUDENT_ROLL_NO_HISTORY;
	}

}
