package com.iitm.hosteldine.controller.dashboard.dean;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.form.hostel.RoomInventoryForm;
import com.iitm.hosteldine.repository.dashboard.student.VacatingHostelStudentWorkflowRepository;
import com.iitm.hosteldine.service.CommonService;
import com.iitm.hosteldine.service.hostel.HostelUserMappingService;
import com.iitm.hosteldine.service.hostel.RoomInventoryService;
import com.iitm.hosteldine.util.*;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.constant.dean.DeanConstants;
import com.iitm.hosteldine.dto.dashboard.student.StudentHostelRoomVacatingRequestDto;
import com.iitm.hosteldine.dto.dean.DeanApprovalDto;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.dashboard.student.StudentHostelRoomVacatingRequestService;
import com.iitm.hosteldine.service.dean.DeanDashboardService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "${url.student.vacating}")
public class StudentVacatingController {

	private final CommonResponseUtil commonResponseUtil;
	private final DeanDashboardService deanDashboardService;

	private final HostelMasterService hostelMasterService;
	private final SimsConfigDataService simsConfigDataService;
	private final StudentHostelRoomVacatingRequestService studentHostelRoomVacatingRequestService;
	private final MessageSource messageSource;
	private final RoomInventoryService roomInventoryService;
	private final HostelUserMappingService hostelUserMappingService;
	private final VacatingHostelStudentWorkflowRepository vacatingHostelStudentWorkflowRepository;
	private final CommonService commonService;

	@Value("${url.student.vacating}")
	private String baseUrl;

	@GetMapping
	public String getStudentVacatingList(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession();
		// Getting dynamic tabs
		DeanApprovalDto deanApprovalDto = getStudentVacatingMenuList(session);
		DeanApprovalDto columnList = getStudentVacatingColumnList(session);
		map.addAttribute("deanApprovalDto", deanApprovalDto);
		map.addAttribute("columnDto", columnList);
		String splitBaseUrl = baseUrl.replace("/", "");
		boolean isHostelUser = SecurityCtxUtil.isHostelRole();
		map.addAttribute("isHostelUser", isHostelUser);

		// Setting dynamic filter values
		if (!form.isSearchFilter()) {
			List<String> filterList = List.of(DeanConstants.APPROVAL_STATUS.getConstants(), DeanConstants.VACATING_REASON.getConstants(), DeanConstants.SUBMITTED_FROM_DATE.getConstants(),
					DeanConstants.SUBMITTED_TO_DATE.getConstants(), DeanConstants.VACATING_FROM_DATE.getConstants(), DeanConstants.VACATING_TO_DATE.getConstants(),
					DeanConstants.STUDENT_NAME.getConstants(), DeanConstants.STUDENT_ID.getConstants(), DeanConstants.HOSTEL_NAME.getConstants());
			filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
		}
		commonResponseUtil.getAdditionalParams(allParams, form);

		String[] approvalStatus = new String[] { WorkflowStatus.APPROVED.getStatus(), WorkflowStatus.PENDING.getStatus() };
		String[] vacatingList = studentHostelRoomVacatingRequestService.getDropdownList(simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.REASON_FOR_VACATION));
		map.addAttribute("approvalStatusList", approvalStatus);
		map.addAttribute("hostelId", studentHostelRoomVacatingRequestService.getHostelId(form));
		map.addAttribute(DeanConstants.REASON_FOR_VACATING_LIST.getConstants(), vacatingList);
		List<HostelMasterDto> hostelMasterDtoList = commonService.getHostelListForUser();
		map.addAttribute("hostelOrWardenList", studentHostelRoomVacatingRequestService.getHostelName(hostelMasterDtoList));
		List<StudentHostelRoomVacatingRequestDto> studentVacatingList = studentHostelRoomVacatingRequestService.getStudentVacatingHostelList(form, splitBaseUrl, false);
		commonResponseUtil.updateCommonModelAttributes2(map, request, studentVacatingList, form);
		setExportButton(map, splitBaseUrl);
		return HTMLPage.DEAN_DASHBOARD_MENU_LIST;
	}

	@GetMapping(value = "${url.view}")
	public String getVacatingStudentDetails(@RequestParam String data, ModelMap map, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
		Boolean status = Utility.checkRequestType(data, 4, 3);
		if (status) {
			try {
				StudentHostelRoomVacatingRequestDto studentHostelRoomVacatingRequestDto = new StudentHostelRoomVacatingRequestDto();
				String[] split = Utility.decryptData(data);
				String studentId = Utility.getValueOrDefault(split, 0, Strings.EMPTY);
				Long id = Utility.getLongValueOrDefault(split, 1, null);
				String authorityType = Utility.getValueOrDefault(split, 2, Strings.EMPTY);
				if(Constants.USER_ROLE_DEAN.equalsIgnoreCase(authorityType)) {  //Need to remove
					authorityType = Constants.USER_ROLE_WARDEN; 
				}				
				studentHostelRoomVacatingRequestDto = studentHostelRoomVacatingRequestService.getVacatingStudentDetails(studentId, id, authorityType);
				Map<String, String> paintingTypeMap = simsConfigDataService.getSimConfigValueAsMap(SimsConfigDataService.PAINTING_TYPE);
//				VacatingHostelStudentWorkflowDto vacatingHostelStudentWorkflowDto = studentHostelRoomVacatingRequestService.getVacatingHostelStudentWorkflow(Long.valueOf(workflowId));
				map.addAttribute("paintingTypeMap", paintingTypeMap);
				map.addAttribute("studentHostelRoomVacatingRequestDto", studentHostelRoomVacatingRequestDto);
				commonResponseUtil.updateCommonModelAttributes(map, request);
				return HTMLPage.DEAN_DASHBOARD_VACATING_STUDENT_DETAILS;
			} catch (Exception e) {
				e.printStackTrace();
				commonResponseUtil.exceptionMessageHandling(e, redirectAttributes);
			}
		} else {
			commonResponseUtil.invalidAccess(redirectAttributes);
		}
		return null;
	}

	@GetMapping("${url.asset.cost}")
	public @ResponseBody BaseResponse getAssestCost(@RequestParam String assetCategory, @RequestParam String assetCondition, HttpServletRequest request, RedirectAttributes redirectAttrs)
			throws Exception {
		Double cost = studentHostelRoomVacatingRequestService.getCost(assetCategory, assetCondition);
		return CommonResponseUtil.updateResponseByValue(cost);
	}

	@GetMapping("${url.faculty.check}")
	public @ResponseBody BaseResponse checkFaculty(@RequestParam String employeeId) throws Exception {
		boolean status = studentHostelRoomVacatingRequestService.isFacultyAvailable(employeeId);
		return CommonResponseUtil.updateResponseByValue(status);
	}

	@PostMapping("${url.student.vacating.update.status}")
	public @ResponseBody BaseResponse updateStudentVacatingApprovalStatus(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
		StudentHostelRoomVacatingRequestDto dto = new ObjectMapper().convertValue(requestBody.get("dto"), StudentHostelRoomVacatingRequestDto.class);
		boolean updateStatus = false;
		String url = UrlUtility.getBaseURL(request) + baseUrl;
		updateStatus = studentHostelRoomVacatingRequestService.updateStudentVacatingApprovalStatus(dto, WorkflowStatus.APPROVED.getStatus(), url);
		return CommonResponseUtil.updateResponseByStatus(updateStatus, "response.update.success", "response.update.error");
	}

	@GetMapping("${url.dean.approval.excel.report.download}")
	public void downloadRequestReport(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request, HttpServletResponse response) {
		commonResponseUtil.getAdditionalParams(allParams, form);
		List<StudentHostelRoomVacatingRequestDto> studentVacatingList = studentHostelRoomVacatingRequestService.getStudentVacatingHostelList(form, baseUrl.replace("/", ""), true);
		try {
			Workbook workbook = studentHostelRoomVacatingRequestService.getVacatingStudentReport(studentVacatingList);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			workbook.write(bos);
			workbook.close();
			byte[] excelBytes = bos.toByteArray();
			response.setContentType(FileUploadConstants.XLSX);
			String fileName = messageSource.getMessage("message.vacating.student.list.filename", null, Locale.getDefault()) + "" + FileUploadConstants.XLSX_EXTENSION;
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

	private DeanApprovalDto getStudentVacatingMenuList(HttpSession session) {
		DeanApprovalDto deanApprovalDto;
		if (session.getAttribute("studentVactingMenuList") == null) {
			deanApprovalDto = deanDashboardService.getDeanMenuList();
			session.setAttribute("studentVactingMenuList", deanApprovalDto);
		} else {
			deanApprovalDto = (DeanApprovalDto) session.getAttribute("studentVactingMenuList");
		}
		return deanApprovalDto;
	}

	private DeanApprovalDto getStudentVacatingColumnList(HttpSession session) {
		DeanApprovalDto columnList;
		if (session.getAttribute("studentVactingColumnList") == null) {
			columnList = deanDashboardService.getDeanMenuListById(baseUrl.replace("/", ""));
			session.setAttribute("studentVactingColumnList", columnList);
		} else {
			columnList = (DeanApprovalDto) session.getAttribute("studentVactingColumnList");
		}
		return columnList;
	}

	private void setExportButton(ModelMap map, String splitBaseUrl) {
		HeaderForm headerForm = (HeaderForm) map.getAttribute(ModelConstants.HEADER_FORM);
		if (headerForm != null) {
			headerForm.setAdditionalButtonProperties(true, ModelConstants.BUTTON_PINK, messageSource.getMessage("message.label.vacating.student.report", null, Locale.getDefault()),
					ModelConstants.FA_FILE_EXCEL);
			map.addAttribute("excelUrl", splitBaseUrl + messageSource.getMessage("url.dean.approval.excel.report.download", null, Locale.getDefault()));
		}
	}
	@DeleteMapping("${url.delete}")
	public @ResponseBody BaseResponse deleteStudentVacatingRequest(@RequestParam String data, RedirectAttributes redirectAttributes) throws Exception {
			try {
				return CommonResponseUtil.generateDeleteResponseByStatus(studentHostelRoomVacatingRequestService.deleteStudentVacatingRequest(data));
			} catch (Exception e) {
				BaseResponse errorResponse = new BaseResponse();
				errorResponse.setMessage(e.getMessage());
				errorResponse.setStatus("Failure");
				commonResponseUtil.exceptionMessageHandling(e, redirectAttributes);
				return errorResponse;
			}
	}

	@GetMapping("${url.room.inventory}")
	public String getRoomInventoryDetails(@RequestParam Map<String, String> allParams, PaginationForm form,ModelMap map,
										  HttpServletRequest request) throws Exception {
		Page<RoomInventoryForm> roomInventoryList = null;
		commonResponseUtil.getAdditionalParams(allParams, form);
		if (form.getAdditionalParam().get("hostelId") != null) {
			roomInventoryList = roomInventoryService.getRoomInventoryList(form);
		} else {
			form.getAdditionalParam().put("hostelId", 0l);
			form.getAdditionalParam().put("assetCondition", "");
		}
		map.addAttribute("hostelList", hostelUserMappingService.getHostelDetailsList(SecurityCtxUtil.userName()));
		map.addAttribute("assetConditionList", simsConfigDataService.getSimConfigValueFromJsonArray("ASSET_CONDITION"));
		commonResponseUtil.updateCommonModelAttributes(map, request, roomInventoryList, form);
		return HTMLPage.ROOM_INVENTORY;
	}

	@GetMapping("${url.pdf.download}")
	public ResponseEntity<Resource> downloadStudentBioDataPDF(@RequestParam String data) throws Exception {
		List<String> split = List.of(MCrypt.getInstance().decryptToString(data).split(Constants.BACKTICK));
		Resource resource = studentHostelRoomVacatingRequestService.generatePdf(split.getFirst());
		return Utility.prepareDownloadFile(resource);
	}

	@GetMapping("${url.admin.report}")
	public String getAdminReportScreen(ModelMap map, HttpServletRequest request) {
		map.addAttribute(FilterEnum.REPORT_TYPE.getValue(), simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.REPORT_TYPE));
		commonResponseUtil.updateCommonModelAttributes(map, request);
		return HTMLPage.VACATING_STUDENTS_REPORT;
	}

	@GetMapping("${url.admin.excel.report.download}")
	void downloadAdminExcelReport(@RequestParam String params, HttpServletResponse response) {
		List<String> split = List.of(params.split(ModelConstants.COMMA));
		try {
			Workbook workbook = studentHostelRoomVacatingRequestService.getWorkbook(split);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			workbook.write(bos);
			workbook.close();
			byte[] excelBytes = bos.toByteArray();
			response.setContentType(ExcelConstants.CONTENT_TYPE);
			response.setHeader(ExcelConstants.CONTENT_DISPOSITION, ExcelConstants.ATTACHMENT_FILE_NAME + ExcelConstants.VACATING_STUDENTS_REPORT + ExcelConstants.EXCEL_EXTENSION);
			response.setContentLength(excelBytes.length);
			try (ServletOutputStream outputStream = response.getOutputStream()) {
				outputStream.write(excelBytes);
				outputStream.flush();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@PostMapping("${url.day.scholar.update}")
	public ResponseEntity<String> updateDayScholarStatus() {
		return ResponseEntity.ok(studentHostelRoomVacatingRequestService.checkAndUpdateDayScholarStatus());
	}
}
