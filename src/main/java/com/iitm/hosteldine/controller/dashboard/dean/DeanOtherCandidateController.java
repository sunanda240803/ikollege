package com.iitm.hosteldine.controller.dashboard.dean;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iitm.hosteldine.dto.dean.DeanApprovalDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.dean.DeanDashboardService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "${url.dean.approval.candidate.list}")
public class DeanOtherCandidateController {
	
	private final CommonResponseUtil commonResponseUtil;
	private final DeanDashboardService deanDashboardService;

	@Value("${url.dean.approval.candidate.list}")
    private String baseUrl;
	
	@GetMapping
	public String getCandidateList(PaginationForm form,ModelMap map, HttpServletRequest request) {
//		DeanApprovalDto deanApprovalTabsDto = deanDashboardService.getDeanMenuList(request);
//		map.addAttribute("deanApprovalDto", deanApprovalTabsDto);
//		DeanApprovalDto deanApprovalColumnDto = deanDashboardService.getDeanMenuListById(baseUrl.replace("/", ""));
//		map.addAttribute("columnDto", deanApprovalColumnDto);
//		Page<DeanApprovalDto> approvalDto = new PageImpl<>(new ArrayList<>());
//		commonResponseUtil.updateCommonModelAttributes(map, request,approvalDto,form);
//		return HTMLPage.DEAN_DASHBOARD_MENU_LIST;
		return "";
	}
}
