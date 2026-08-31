package com.iitm.hosteldine.controller.reports;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.iitm.hosteldine.dto.hostel.AccountHeadDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.AccountHeadService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value = "${url.hostel.account.head.summary.report}")
@RequiredArgsConstructor
public class HostelAccountHeadSummaryReportController {
	
	private final CommonResponseUtil commonResponseUtil;
	private final AccountHeadService accountHeadService;
	
	@RequestMapping(method = { RequestMethod.GET })
	public String getHostelAccountHeadSummaryList(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request) throws Exception {
		if (!form.isSearchFilter()) {
			List<String> filterList = List.of("fromDate", "toDate");
			filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
		}
		map.addAttribute("filters", form.getAdditionalParam());
		commonResponseUtil.getAdditionalParams(allParams, form);
		Page<AccountHeadDto> accountHeadDtoList = accountHeadService.getAccountHeadSummaryList(form);				
		commonResponseUtil.updateCommonModelAttributes(map, request, accountHeadDtoList, form);
		return HTMLPage.HOSTEL_ACCOUNT_HEAD_SUMMARY_REPORT;
	}
}
