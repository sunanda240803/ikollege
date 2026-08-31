package com.iitm.hosteldine.controller.hostel;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iitm.hosteldine.dto.hostel.HostelRoomInfoDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.CurrentHostelVacancyStatusService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("${url.current.hostel.vacancy.status}")
public class CurrentHostelVacancyStatusController {

	private final CommonResponseUtil commonResponseUtil;
	private final CurrentHostelVacancyStatusService service;

	@GetMapping
	public String getCurrentHostelVacancyStatusList(PaginationForm form, ModelMap map, HttpServletRequest request)
			throws Exception {
		Page<HostelRoomInfoDto> currentHostelVacancyStatusList = service.getCurrentHostelVacancyStatusList(form);
		commonResponseUtil.updateCommonModelAttributes(map, request, currentHostelVacancyStatusList, form);
		return HTMLPage.CURRENT_HOSTEL_VACANCY_STATUS;
	}
}
