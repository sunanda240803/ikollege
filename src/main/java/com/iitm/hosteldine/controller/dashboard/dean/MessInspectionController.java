package com.iitm.hosteldine.controller.dashboard.dean;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.dean.DeanApprovalDto;
import com.iitm.hosteldine.dto.dean.MessInspectionReportDto;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.dto.warden.WardenInfoDto;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.dean.DeanDashboardService;
import com.iitm.hosteldine.service.dean.MessInspectionService;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.service.warden.WardenInfoService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping(value = "${url.dean.mess.inspection}")
@RequiredArgsConstructor
public class MessInspectionController {

	@Value("${url.dean.mess.inspection}")
	private String baseUrl;

	private final CommonResponseUtil commonResponseUtil;
	private final DeanDashboardService deanDashboardService;
	private final MessInspectionService messInspectionService;
	private final MessageSource messageSource;
	private final MessMasterService messMasterService;
	private final WardenInfoService wardenInfoService;

	@GetMapping
	public String getMessInspectionList(@RequestParam Map<String, String> allParams,PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		//Getting dynamic tabs
		DeanApprovalDto deanApprovalDto = deanDashboardService.getDeanMenuList();
		map.addAttribute("deanApprovalDto", deanApprovalDto);
		DeanApprovalDto columnList = deanDashboardService.getDeanMenuListById(baseUrl.replace("/", ""));

		map.addAttribute("columnDto", columnList);

		//Setting dynamic filter values
		if(!form.isSearchFilter()) {
			List<String> filterList = List.of("wardenName","messName","inspectionFromDate","inspectionToDate");
			filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
		}
		if(Constants.USER_ROLE_WARDEN.equals(SecurityCtxUtil.userRole())){
			WardenInfoDto wardenDetails = wardenInfoService.getWardenDetailsByUserName(SecurityCtxUtil.userName());
			form.getAdditionalParam().put("wardenName", wardenDetails.getId());
		}

		commonResponseUtil.getAdditionalParams(allParams, form);
		List<MessMasterDto> messMasterList = messMasterService.getMessMasterList();
		List<WardenInfoDto> wardenDetailsList = wardenInfoService.getAllWardenDetailsList();
		map.addAttribute("messMasterList", messMasterList);
		map.addAttribute("wardenDetailsList",wardenDetailsList);

		List<MessInspectionReportDto> messInspectionDto = messInspectionService.getMessInspection(form,baseUrl.replace("/", ""));
		commonResponseUtil.updateCommonModelAttributes2(map, request,messInspectionDto,form);
		setExportButton(map, baseUrl.replace("/", ""));
		return HTMLPage.DEAN_DASHBOARD_MENU_LIST;
	}
	private void setExportButton(ModelMap map, String splitBaseUrl) {
		HeaderForm headerForm = (HeaderForm) map.getAttribute(ModelConstants.HEADER_FORM);
		if (headerForm != null) {
			headerForm.setAdditionalButtonProperties(true, ModelConstants.BUTTON_PINK, messageSource.getMessage("message.label.mess.inspection.report", null, Locale.getDefault()),
					ModelConstants.FA_FILE_EXCEL);
			map.addAttribute("excelUrl", splitBaseUrl + messageSource.getMessage("url.dean.approval.excel.report.download", null, Locale.getDefault()));
			headerForm.setNeedAddNew(true);
			map.addAttribute("addNewUrl", splitBaseUrl + messageSource.getMessage("url.add", null, Locale.getDefault()));

		}
	}
	@GetMapping("${url.view}"+"${id}")
	public String getStudentRoomRequest(@PathVariable String id, PaginationForm form, ModelMap map, HttpServletRequest request) {
		MessInspectionReportDto messInspectionReportDto = messInspectionService.getMessInspectionById(id);
		map.addAttribute("messInspectionDto", messInspectionReportDto);
		return HTMLPage.MEES_INSPECTION_VIEW; // Thymeleaf template name
	}

	@GetMapping("${url.dean.approval.excel.report.download}")
	public void downloadRequestReport(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request, HttpServletResponse response) {
		//Setting dynamic filter values
		if(!form.isSearchFilter()) {
			List<String> filterList = List.of("wardenName","messName","inspectionFromDate","inspectionToDate");
			filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
		}
		commonResponseUtil.getAdditionalParams(allParams, form);
		List<MessInspectionReportDto> messInspectionDto = messInspectionService.getMessInspection(form,baseUrl.replace("/", ""));
		try {
			Workbook workbook = messInspectionService.getMessInspectionReport(messInspectionDto);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			workbook.write(bos);
			workbook.close();
			byte[] excelBytes = bos.toByteArray();
			response.setContentType(FileUploadConstants.XLSX);
			String fileName = messageSource.getMessage("message.mess.inspection.list.filename", null, Locale.getDefault()) + "" + FileUploadConstants.XLSX_EXTENSION;
			response.setHeader(FileUploadConstants.CONTENT_DISPOSITION, messageSource.getMessage("message.attachment.filename", new Object[] { fileName }, Locale.getDefault()));
			response.setContentLength(excelBytes.length);

			try (ServletOutputStream outputStream = response.getOutputStream()) {
				outputStream.write(excelBytes);
				outputStream.flush();
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GetMapping(value = {"/add"})
	public String showAddOrEditForm(ModelMap model) {
			// Adding new configuration
		String loggedInUserRole = SecurityCtxUtil.userRole();
		Boolean isWardenLogin = StringUtils.equalsIgnoreCase(loggedInUserRole, Constants.USER_ROLE_WARDEN);
		Boolean isDeanLogin = StringUtils.equalsIgnoreCase(loggedInUserRole, Constants.USER_ROLE_DEAN);
		model.addAttribute("isWardenLogin", isWardenLogin);

		List<MessMasterDto> messMasterList = messMasterService.getMessMasterList();
		List<WardenInfoDto> wardenDetailsList = wardenInfoService.getAllWardenDetailsList();

		model.addAttribute("messMasterList", messMasterList);
		model.addAttribute("wardenDetailsList",wardenDetailsList);
		model.addAttribute("messInspectionDto", new MessInspectionReportDto());
		return HTMLPage.MESS_INSPECTION_ADD_MODAL;
	}

	@PostMapping
	public String saveOrUpdate(@Valid @ModelAttribute MessInspectionReportDto messInspectionDto,
												BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		String message = null, status = null;
		String s = "redirect:" + baseUrl;
		Boolean isSaved = messInspectionService.saveOrUpdate(messInspectionDto);
		if (!isSaved) {
			redirectAttributes.addFlashAttribute("Error",
					messageSource.getMessage("response.mess.inspection.status.status.failed", null, Locale.getDefault()));
			return s ;
		} else {
			message = messageSource.getMessage("response.mess.inspection.status.updated", null, Locale.getDefault());
			status = messageSource.getMessage("response.status.success", null, Locale.getDefault());
			redirectAttributes.addFlashAttribute("response", new BaseResponse(message, status));
			return s ;
		}
	}

}
