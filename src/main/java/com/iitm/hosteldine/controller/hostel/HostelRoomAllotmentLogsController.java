package com.iitm.hosteldine.controller.hostel;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.iitm.hosteldine.dto.hostel.HostelRoomAllotmentLogsDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.hostel.HostelRoomAllotmentLogsService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "${url.hostel.room.allotment.logs}")
@RequiredArgsConstructor
public class HostelRoomAllotmentLogsController {
	
	@Value("${url.hostel.room.allotment.logs}")
	private String baseUrl;
	
	private final HostelRoomAllotmentLogsService hostelRoomAllotmentLogsService;
	private final CommonResponseUtil commonResponseUtil;
	private final HostelMasterService hostelMasterService;
	private final SimsConfigDataService simsConfigDataService;
	
	@RequestMapping(method = { RequestMethod.GET })
	public String getRoomAllotmentLogsList(@RequestParam Map<String, String> allParams,PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		if (!form.isSearchFilter()) {
			List<String> filterList = List.of("hostelName","userName","allotedFromDate", "allotedToDate", "studentId", "email", "allocationType");
			filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
		}
		commonResponseUtil.getAdditionalParams(allParams, form);
		Page<HostelRoomAllotmentLogsDto> roomAllotmentLogsList = hostelRoomAllotmentLogsService.getRoomAllotmentList(form);
	    map.addAttribute("filters", form.getAdditionalParam());
	    map.addAttribute("hostelOrWardenList", hostelRoomAllotmentLogsService.getHostelName(hostelMasterService.getHostelList()));
		map.addAttribute("allocationTypeMap", simsConfigDataService.getSimConfigValueAsMap(SimsConfigDataService.ALLOCATION_TYPE));
		map.addAttribute("usersList" , hostelRoomAllotmentLogsService.getUserList());
		commonResponseUtil.updateCommonModelAttributes(map, request, roomAllotmentLogsList, form);
		return HTMLPage.HOSTEL_ROOM_ALLOTMENT_LOGS;
	}
}
