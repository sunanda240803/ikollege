package com.iitm.hosteldine.controller.hostel.reports;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.StudentBioDataService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value = "${url.student.bio.data.list.report}")
@RequiredArgsConstructor
public class StudentBioDataListReportController {
	
	private final CommonResponseUtil commonResponseUtil;
	private final StudentBioDataService studentBioDataService;
	
	@RequestMapping(method = { RequestMethod.GET })
	public String getSickFoodRequestList(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request) throws Exception {
		map.addAttribute("filters", form.getAdditionalParam());		
		commonResponseUtil.getAdditionalParams(allParams, form);
		Page<StudentBioDataFormDetailDto> studentBioDataList = studentBioDataService.getStudentBioDataFormDetailList(form);
		commonResponseUtil.updateCommonModelAttributes(map, request, studentBioDataList, form);
		return HTMLPage.STUDENT_BIO_DATA_LIST_REPORT;
	}
	
}
