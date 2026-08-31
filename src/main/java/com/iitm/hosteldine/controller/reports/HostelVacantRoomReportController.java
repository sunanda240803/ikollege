package com.iitm.hosteldine.controller.reports;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.dto.hostel.HostelFloorMasterDto;
import com.iitm.hosteldine.dto.hostel.VacantRoomDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.HostelFloorMasterService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.hostel.HostelVacantRoomReportService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value = "${url.hostel.room.vacant.report}")
@RequiredArgsConstructor
public class HostelVacantRoomReportController {

	@Value("${url.hostel.room.vacant.report}")
	private String baseUrl;
	private String fileName = "HostelVacantRooms.pdf";

	private final CommonResponseUtil commonResponseUtil;
	private final HostelMasterService hostelMasterService;
	private final HostelFloorMasterService hostelFloorMasterService;
	private final HostelVacantRoomReportService hostelVacantRoomReportService;

	@RequestMapping(method = { RequestMethod.GET })
	public String getHostelRoomVacantList(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request) throws Exception {
		if (!form.isSearchFilter()) {
			List<String> filterList = List.of("hostelId", "hostelFloorId", "stableRoomId");
			filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
		}
		
		map.addAttribute("filters", form.getAdditionalParam());
		commonResponseUtil.getAdditionalParams(allParams, form);
		
		Long hostelId = (form.getAdditionalParam().get("hostelId") != null && !form.getAdditionalParam().get("hostelId").equals("")) ? 
				Long.valueOf(form.getAdditionalParam().get("hostelId").toString()): null;
		Long floorId = (form.getAdditionalParam().get("hostelFloorId") != null && !form.getAdditionalParam().get("hostelFloorId").equals("")) ? 
				Long.valueOf(form.getAdditionalParam().get("hostelFloorId").toString()): null;
		String stableRoom = (form.getAdditionalParam().get("stableRoomId") != null && !form.getAdditionalParam().get("stableRoomId").equals("")) ? 
				form.getAdditionalParam().get("stableRoomId").toString(): "";
		
		Page<VacantRoomDto> vacantRoomList = null;
		
		if (hostelId != null && floorId != null) {
			vacantRoomList = hostelVacantRoomReportService.getVacantRoomList(hostelId, floorId, stableRoom, form, false);
		}
		
		map.addAttribute("hostelList", hostelMasterService.getHostelList());
		commonResponseUtil.updateCommonModelAttributes(map, request, vacantRoomList, form);
		return HTMLPage.HOSTEL_ROOM_VACANT_REPORT;
	}

	@GetMapping("${url.get.hostel.floor.list}")
	public @ResponseBody BaseResponse getHostelFloorList(@RequestParam Long hostelId) throws Exception {
		List<HostelFloorMasterDto> hostelFloorList = hostelFloorMasterService.getFloorListByHostelId(hostelId);
		return CommonResponseUtil.updateResponseByValue(hostelFloorList);
	}

	@GetMapping("${url.download}")
	public void downloadPdf(@RequestParam String hostelName, @RequestParam Long hostelId, @RequestParam Long floorId, @RequestParam String stableRoom, PaginationForm form,
			HttpServletResponse response) throws Exception {
		Page<VacantRoomDto> vacantRoomList = hostelVacantRoomReportService.getVacantRoomList(hostelId, floorId, stableRoom, form, true);
		byte[] pdfBytes = hostelVacantRoomReportService.generatePdf(hostelName, vacantRoomList.getContent());
		response.reset();
		response.setContentType(Constants.PDF_APPLICATION_TYPE);
		response.setHeader(ExcelConstants.CONTENT_DISPOSITION, "inline; filename=" + fileName);
		response.setContentLength(pdfBytes.length);
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			outputStream.write(pdfBytes);
			outputStream.flush();
		}
	}
}
