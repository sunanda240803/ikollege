package com.iitm.hosteldine.controller.mess;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.iitm.hosteldine.dto.dean.FilterCriteriaDto;
import com.iitm.hosteldine.dto.mess.MessAllottedListDTO;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.mess.MessAllottedListService;
import com.iitm.hosteldine.service.mess.MessChangeRequestsService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.mess.change.requests.list}")
public class MessChangeRequestsController {

	private final CommonResponseUtil commonResponseUtil;
	private final MessChangeRequestsService service;
	private final MessAllottedListService messAllottedListService;

	@GetMapping
	public String getMessChangeRequestsList(@RequestParam Map<String, String> allParams, PaginationForm form,
			ModelMap map, HttpServletRequest request) throws Exception {

		String key = "messPeriod";

		Page<MessAllottedListDTO> messChangeRequestsList = null;
		commonResponseUtil.getAdditionalParams(allParams, form);

		Map<String, Object> additionalParams = form.getAdditionalParam();
		Object value = additionalParams.get(key);

		FilterCriteriaDto filter = new FilterCriteriaDto();

		if (isValid(value)) {
			messChangeRequestsList = service.getMessChangeRequestsList(form);
			filter = service.getFilterData(form);
		} else {
			additionalParams.put(key, "");
			messChangeRequestsList = service.getMessChangeRequestsList(form);
		}

		map.addAttribute("messChangeRequestsList", filter);
		map.addAttribute("messPeriodList", messAllottedListService.getMessPeriodList());
		commonResponseUtil.updateCommonModelAttributes(map, request, messChangeRequestsList, form);

		return HTMLPage.MESS_CHANGE_REQUESTS;
	}

	private boolean isValid(Object value) {
		if (value == null) {
			return false;
		}
		if (value instanceof String) {
			String strValue = (String) value;
			return !strValue.trim().isEmpty();
		}
		return false;
	}

}
