package com.iitm.hosteldine.controller.dashboard.office;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.iitm.hosteldine.util.response.BaseResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.dean.DeanAccommodationRequestDto;
import com.iitm.hosteldine.dto.dean.DeanApprovalDto;
import com.iitm.hosteldine.dto.dean.FilterCriteriaDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.dean.DeanDashboardService;
import com.iitm.hosteldine.service.dean.RegularStudentCheckInService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("${url.office.regular.student.check.in}")
public class RegularStudentCheckInController {

	@Value("${url.office.regular.student.check.in}")
	private String baseUrl;
	
	private final CommonResponseUtil commonResponseUtil;
	private final DeanDashboardService deanDashboardService;
    private final HostelMasterService hostelMasterService;
    private final RegularStudentCheckInService service;
	private final SimsConfigDataService simsConfigDataService;
	private final MessageSource messageSource;

	@GetMapping
	public String getRegularStudentCheckInList(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map,
			HttpServletRequest request) throws Exception {
		String splitBaseUrl = baseUrl.replace("/", "");
		DeanApprovalDto columnDto = deanDashboardService.getDeanMenuListByRoleAndUserIdAndValue(SecurityCtxUtil.userRole(),
				SecurityCtxUtil.userName(), splitBaseUrl);
		map.addAttribute("columnDto", columnDto);
		if (!form.isSearchFilter()) {
			List<String> filterList = service.getFilterList(SecurityCtxUtil.userName());
			filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
		}
		commonResponseUtil.getAdditionalParams(allParams, form);
		List<HostelMasterDto> hostelList = null;
		ArrayList<String> listOfUser = simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.REGULAR_CHECKIN_USER_LIST);
		if (listOfUser.contains(SecurityCtxUtil.userName())) {
			hostelList =hostelMasterService.getHostelList();
		} else {
			hostelList = hostelMasterService.getHostelListByUser();
		}
        map.addAttribute(HostelConstants.HOSTEL_OR_WARDEN_LIST.getConstants(), hostelList);
		FilterCriteriaDto filterCriteria = service.getFilterData(form);
		map.addAttribute("cardDetails", service.getCardDetails(filterCriteria));
		List<DeanAccommodationRequestDto> regularStudentCheckInList = service.getRegularStudentCheckInList(form,
				filterCriteria, splitBaseUrl, columnDto);
		commonResponseUtil.updateCommonModelAttributes2(map, request, regularStudentCheckInList, form);
		return HTMLPage.REGULAR_STUDENT_CHECKIN_LIST;
	}

	@PostMapping("${url.status}")
	public @ResponseBody BaseResponse saveChecKInAndCheckOutStatus(ModelMap map, @RequestParam String data, HttpServletRequest request)
			throws Exception {
		try {
			String status = service.saveChecKInAndCheckOutStatus(data);
			BaseResponse baseResponse = new BaseResponse();
			String message;
			if (status!=null) {
				message =status;
				status = "response.status.success";
			} else {
				message = "message.label.checkin.checkout.error";
				status = "response.status.failure";
			}
			baseResponse.setMessage(messageSource.getMessage(message, null, Locale.getDefault()));
			baseResponse.setStatus(messageSource.getMessage(status, null, Locale.getDefault()));
			return baseResponse;
		} catch (Exception e) {
			e.printStackTrace();
			return commonResponseUtil.errorExceptionHandling(e);
		}
	}

//	@PostMapping("${url.status}")
//	public String saveChecKInAndCheckOutStatus(@ModelAttribute DeanApprovalDto deanApprovalDto,
//			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
//		String saveStatus = service.saveChecKInAndCheckOutStatus(deanApprovalDto.getData());
//		commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs, saveStatus);
//		return Constants.REDIRECT + baseUrl;
//	}
}
