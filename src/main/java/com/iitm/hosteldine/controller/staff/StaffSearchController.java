package com.iitm.hosteldine.controller.staff;

import com.iitm.hosteldine.constant.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.dto.staff.StaffDetailsDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.staff.StaffSearchService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.staff.search}")
public class StaffSearchController {
	
	@Value("${url.staff.search}")
	private String staffSearch;

	private final StaffSearchService staffSearchService;
	private final CommonResponseUtil commonResponseUtil;

	@GetMapping
	public String staffDetails(PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		/*List<StaffDetailsDto> staffSearchList = staffSearchService.getStaffList();
		map.addAttribute("staffSearchList", staffSearchList);*/
		Page<StaffDetailsDto> staffSearchList = staffSearchService.getStaffList(form);
		commonResponseUtil.updateCommonModelAttributes(map, request,staffSearchList , form);
		return HTMLPage.STAFF_SEARCH;
	}
	
	@DeleteMapping("${id}")
	public @ResponseBody BaseResponse deleteStaffById(@PathVariable String id, ModelMap map, HttpServletRequest request,
			RedirectAttributes redirectAttrs) throws Exception {
		try {
			return CommonResponseUtil.generateDeleteResponseByStatus(staffSearchService.deleteStaffById(id));
		} catch (RecordNotExistsException e) {
			// Create a custom error response in case of an exception
			BaseResponse errorResponse = new BaseResponse();
			errorResponse.setMessage(e.getMessage());
			errorResponse.setStatus("Failure");
			return errorResponse;
		}
	}

	@PostMapping("${url.reset.password}/{staffId}")
	@ResponseBody
	public Map<String, String> resetPassword(@PathVariable String staffId) {
		String newPassword = staffSearchService.updateStaffCreds(staffId);
		return Map.of("newPassword", newPassword);
	}
}
