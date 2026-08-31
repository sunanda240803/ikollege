package com.iitm.hosteldine.controller.warden;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.iitm.hosteldine.dto.warden.WardenAwayDetailsDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.warden.WardenAwayDetailsService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value = "${url.warden.away.details}")
@RequiredArgsConstructor
public class WardenAwayDetailsContoller {
	
	private final WardenAwayDetailsService wardenAwayDetailsService;
	private final CommonResponseUtil commonResponseUtil;

	@RequestMapping(method = { RequestMethod.GET })
	public String getWardenAwayRequestList(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request) throws Exception {
		commonResponseUtil.getAdditionalParams(allParams, form);
		Page<WardenAwayDetailsDto> wardenAwayDetailsDto = wardenAwayDetailsService.getWardenAwayRequestList(form);
		commonResponseUtil.updateCommonModelAttributes(map, request, wardenAwayDetailsDto, form);
		return HTMLPage.HOSTEL_WARDEN_AWAY_REQUEST_LIST;
	}
	
	@GetMapping("${url.get.hostel.warden.details}")
	public String getHostelWardenDetails(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request) throws Exception {
		commonResponseUtil.getAdditionalParams(allParams, form);
		Page<WardenAwayDetailsDto> wardenAwayDetailsDto = wardenAwayDetailsService.getHostelWardenDetails(form);
		commonResponseUtil.updateCommonModelAttributes(map, request, wardenAwayDetailsDto, form);
		return HTMLPage.HOSTEL_WARDEN_DETAILS;
	}
}
