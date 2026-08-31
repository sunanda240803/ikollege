package com.iitm.hosteldine.controller.dashboard.dean;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.dto.dean.BulkApprovalRejectDto;
import com.iitm.hosteldine.dto.student.StudentDetailsDto;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.StudentBioDataService;
import com.iitm.hosteldine.service.mess.MessRebateService;
import com.iitm.hosteldine.util.RoleEnum;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.dean.DeanApprovalDto;
import com.iitm.hosteldine.dto.dean.DeanMessRebateDto;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.dean.DeanDashboardService;
import com.iitm.hosteldine.service.dean.DeanMessRebateService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.UrlUtility;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value = "${url.dean.approval.mess.rebate}")
@RequiredArgsConstructor
public class DeanMessRebateController {

    private final MessRebateService messRebateService;
    @Value("${url.dean.approval.mess.rebate}")
	private String baseUrl;

	private final CommonResponseUtil commonResponseUtil;
	private final DeanDashboardService deanDashboardService;
	private final DeanMessRebateService deanMessRebateService;
	private final MessageSource messageSource;
	private final StudentBioDataService studentBioDataService;
	private final FileService fileService;

	@GetMapping
	public String getMessRebateList(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession();
		// Getting dynamic tabs
		DeanApprovalDto deanApprovalDto = getRebateMenuList(session);
		DeanApprovalDto columnList = getRebateColumnList(session);
		map.addAttribute("deanApprovalDto", deanApprovalDto);
		map.addAttribute("columnDto", columnList);
		String splitBaseUrl = baseUrl.replace("/", "");

		// Setting dynamic filter values
		if (!form.isSearchFilter()) {
			List<String> filterList = List.of("validationStatus", "approvalFromDate", "approvalToDate", "submittedFromDate", "submittedToDate", "rebateFromDate", "rebateToDate", "studentName",
					"studentId", "hodName", "hodEmail", "siNoFrom", "siNoTo");
			filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
		}
		commonResponseUtil.getAdditionalParams(allParams, form);

		map.addAttribute("validationStatusList", deanMessRebateService.getValidationStatusList(SimsConfigDataService.VALIDATION_STATUS));
		map.addAttribute("isBulkApproveRejectNeeded", true);
		map.addAttribute("isRoleDean", RoleEnum.DEAN.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole()));
		List<DeanMessRebateDto> messRebateDto = deanMessRebateService.getMessRebateList(form, splitBaseUrl, false);
		commonResponseUtil.updateCommonModelAttributes2(map, request, messRebateDto, form);
		setExportButton(map, splitBaseUrl);
		return HTMLPage.DEAN_DASHBOARD_MENU_LIST;
	}

	@GetMapping(value = "${url.view}")
	public String getMessRebateDetails(@RequestParam String data, ModelMap map, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
		Boolean status = Utility.checkRequestType(data, 4, 3);
		if (status) {
			try {
				DeanMessRebateDto messRebateDto = new DeanMessRebateDto();
				String[] split = Utility.decryptData(data);
				String studentId = Utility.getValueOrDefault(split, 0, Strings.EMPTY);
				Long id = Utility.getLongValueOrDefault(split, 1, null);
				String authorityType = Utility.getValueOrDefault(split, 2, Strings.EMPTY);
				messRebateDto = deanMessRebateService.getMessRebateDetails(studentId, id, authorityType);
				map.addAttribute("deanMessRebateDto", messRebateDto);
                map.addAttribute("mail", false);
				commonResponseUtil.updateCommonModelAttributes(map, request);
				return HTMLPage.DEAN_DASHBOARD_MESS_REBATE_DETAILS;
			} catch (Exception e) {
				e.printStackTrace();
				commonResponseUtil.exceptionMessageHandling(e, redirectAttributes);
			}
		} else {
			commonResponseUtil.invalidAccess(redirectAttributes);
		}
		return null;
	}

	@PutMapping("${url.approve}")
	public @ResponseBody BaseResponse updateMessRebateStatus(@RequestParam String data, ModelMap map, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
		Boolean status = Utility.checkRequestType(data, 4, 3);
		if (status) {
			try {
				String[] split = Utility.decryptData(data);
				String studentId = Utility.getValueOrDefault(split, 0, Strings.EMPTY);
				Long requestId = Utility.getLongValueOrDefault(split, 1, null);
				String url = UrlUtility.getBaseURL(request) + baseUrl;
				return CommonResponseUtil.updateResponseByStatus(deanMessRebateService.updateMessRebateStatus(studentId, requestId, WorkflowStatus.APPROVED.getStatus(), url), "response.update.success","response.update.error");
			} catch (Exception e) {
				e.printStackTrace();
				commonResponseUtil.exceptionMessageHandling(e, redirectAttributes);
			}
		} else {
			commonResponseUtil.invalidAccess(redirectAttributes);
		}
		return null;
	}

	@DeleteMapping("${url.delete}")
	public @ResponseBody BaseResponse deleteMessRebateStatus(@RequestParam String data, ModelMap map, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
		Boolean status = Utility.checkRequestType(data, 4, 3);
		if (status) {
			try {
				String[] split = Utility.decryptData(data);
				String studentId = Utility.getValueOrDefault(split, 0, Strings.EMPTY);
				Long requestId = Utility.getLongValueOrDefault(split, 1, null);
				return CommonResponseUtil.generateDeleteResponseByStatus(deanMessRebateService.updateMessRebateStatus(studentId, requestId, WorkflowStatus.DELETED.getStatus(), null));
			} catch (Exception e) {
				e.printStackTrace();
				commonResponseUtil.exceptionMessageHandling(e, redirectAttributes);
			}
		} else {
			commonResponseUtil.invalidAccess(redirectAttributes);
		}
		return null;
	}

	@PostMapping("${url.resend.mail}")
	public @ResponseBody BaseResponse resendMail(@RequestParam String data, HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		Boolean status = Utility.checkRequestType(data, 3, 2);
		if (status) {
			String[] split = Utility.decryptData(data);
			String studentId = Utility.getValueOrDefault(split, 0, Strings.EMPTY);
			Long requestId = Utility.getLongValueOrDefault(split, 1, null);
			String mailStatus = deanMessRebateService.resendMail(studentId, requestId, UrlUtility.getBaseURL(request) + baseUrl, request);
			return CommonResponseUtil.updateResponseByStatus(mailStatus != null ? true : false, "response.mail.success", "response.mail.error");
		} else {
			commonResponseUtil.invalidAccess(redirectAttrs);
		}
		return null;
	}

	@GetMapping("${url.dean.approval.excel.report.download}")
	public void downloadRequestReport(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request, HttpServletResponse response) {
		commonResponseUtil.getAdditionalParams(allParams, form);
		List<DeanMessRebateDto> messRebateDto = deanMessRebateService.getMessRebateList(form, baseUrl.replace("/", ""), true);
		try {
			Workbook workbook = deanMessRebateService.getMessRebateReport(messRebateDto);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			workbook.write(bos);
			workbook.close();
			byte[] excelBytes = bos.toByteArray();
			response.setContentType(FileUploadConstants.XLSX);
			String fileName = messageSource.getMessage("message.mess.rebate.list.filename", null, Locale.getDefault()) + "" + FileUploadConstants.XLSX_EXTENSION;
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

    @GetMapping("${url.download}" + "${fileName}")
    public ResponseEntity<Resource> downloadReportDocument(@PathVariable String fileName) throws Exception {
        ByteArrayResource resource = messRebateService.downloadFile(fileName);
		return Utility.prepareDownloadFile(resource, fileName);
    }

	@PostMapping("${url.bulk.approve.reject}")
	@ResponseBody
	public BaseResponse bulkApproveReject(@RequestBody BulkApprovalRejectDto dto, HttpServletRequest request) {
		String url = UrlUtility.getBaseURL(request) + baseUrl;
		String saveStatus = messRebateService.bulkApproveReject(dto, url, request);
		String status = Constants.ERROR, message;
		if (WorkflowStatus.APPROVED.getStatus().equals(dto.getApprovalStatus())) {
			if (Constants.SAVED.equals(saveStatus)) {
				status = Constants.SUCCESS;
				message = commonResponseUtil.getMessage("message.request.approved.successfully");
			} else {
				message = commonResponseUtil.getMessage("message.request.approved.failed");
			}
		} else {
			if (Constants.SAVED.equals(saveStatus)) {
				status = Constants.SUCCESS;
				message = commonResponseUtil.getMessage("message.request.rejected.successfully");
			} else {
				message = commonResponseUtil.getMessage("message.request.reject.failed");
			}
		}
		return new BaseResponse(message, status);
	}

	private DeanApprovalDto getRebateColumnList(HttpSession session) {
		DeanApprovalDto columnList;
		if (session.getAttribute("rebateColumnList") == null) {
			columnList = deanDashboardService.getDeanMenuListById(baseUrl.replace("/", ""));
			session.setAttribute("rebateColumnList", columnList);
		} else {
			columnList = (DeanApprovalDto) session.getAttribute("rebateColumnList");
		}
		return columnList;
	}

	private DeanApprovalDto getRebateMenuList(HttpSession session) {
		DeanApprovalDto deanApprovalDto;
		if (session.getAttribute("rebateMenuList") == null) {
			deanApprovalDto = deanDashboardService.getDeanMenuList();
			session.setAttribute("rebateMenuList", deanApprovalDto);
		} else {
			deanApprovalDto = (DeanApprovalDto) session.getAttribute("rebateMenuList");
		}
		return deanApprovalDto;
	}

	private void setExportButton(ModelMap map, String splitBaseUrl) {
		HeaderForm headerForm = (HeaderForm) map.getAttribute(ModelConstants.HEADER_FORM);
		if (headerForm != null) {
			headerForm.setAdditionalButtonProperties(true, ModelConstants.BUTTON_PINK, messageSource.getMessage("message.label.mess.rebate.report", null, Locale.getDefault()),
					ModelConstants.FA_FILE_EXCEL);
			map.addAttribute("excelUrl", splitBaseUrl + messageSource.getMessage("url.dean.approval.excel.report.download", null, Locale.getDefault()));
		}
	}
	
}
