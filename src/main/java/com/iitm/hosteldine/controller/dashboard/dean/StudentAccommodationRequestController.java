package com.iitm.hosteldine.controller.dashboard.dean;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentAppointmentRequestDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentBlackListDetailDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentWorkflowDto;
import com.iitm.hosteldine.dto.dean.BulkApprovalRejectDto;
import com.iitm.hosteldine.dto.dean.DeanAccommodationRequestDto;
import com.iitm.hosteldine.dto.dean.DeanApprovalDto;
import com.iitm.hosteldine.dto.dean.FilterCriteriaDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.hostel.HostelRoomInfoDto;
import com.iitm.hosteldine.dto.student.AllStudentsDetailsViewDto;
import com.iitm.hosteldine.dto.student.StudentDetailsDto;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.dashboard.student.StudentWorkflowEntity;
import com.iitm.hosteldine.service.CommonService;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentBioDataService;
import com.iitm.hosteldine.service.dashboard.student.StudentAppointmentRequestService;
import com.iitm.hosteldine.service.dashboard.student.StudentWorkflowService;
import com.iitm.hosteldine.service.dean.DeanDashboardService;
import com.iitm.hosteldine.service.dean.StudentAccommodationRequestService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.hostel.HostelRoomInfoService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.RoleEnum;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.util.*;


@Controller
@RequiredArgsConstructor
@RequestMapping(value = { "${url.dean.student.accommodation.request}", "${url.dean.scholars}",
		"${url.dean.student.stay.extension}" })
public class StudentAccommodationRequestController {

	private final CommonService commonService;
	@Value("${url.dean.student.accommodation.request}")
	private String studentAccommodationUrl;

	@Value("${url.dean.scholars}")
	private String scholarsUrl;

	@Value("${url.dean.student.stay.extension}")
	private String extensionUrl;

	private final CommonResponseUtil commonResponseUtil;
	private final DeanDashboardService deanDashboardService;
	private final StudentAccommodationRequestService service;
    private final HostelMasterService hostelMasterService;
	private final MessageSource messageSource;
	private final StudentWorkflowService studentWorkflowService;
	private final StudentBioDataService studentBioDataService;
	private final StudentAppointmentRequestService studentAppointmentRequestService;
    private final SimsConfigDataService simsConfigDataService;
	private final HostelRoomInfoService hostelRoomInfoService;
	private final StudentAccommodationRequestService studentAccommodationRequestService;
	private final FileService fileService;

	private String getBaseUrl(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		if (requestUri.contains(studentAccommodationUrl)) {
			return studentAccommodationUrl;
		} else if (requestUri.contains(scholarsUrl)) {
			return scholarsUrl;
		} else {
			return extensionUrl;
		}
	}

	@GetMapping
	public String getStudentAccommodationList(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map,
			HttpServletRequest request) throws Exception {
		// Getting dynamic tabs
		DeanApprovalDto deanApprovalDto = deanDashboardService.getDeanMenuList();
		map.addAttribute("deanApprovalDto", deanApprovalDto);
		String baseUrl = getBaseUrl(request);
		String splitBaseUrl = baseUrl.replace("/", "");
		String validationStatus = getValidationStatus();
		DeanApprovalDto columnDto = deanDashboardService.getDeanMenuListByRoleAndUserIdAndValue(SecurityCtxUtil.userRole(),
				SecurityCtxUtil.userName(), splitBaseUrl);
		map.addAttribute("columnDto", columnDto);

		commonResponseUtil.getAdditionalParams(allParams, form);

		// Setting dynamic filter values
		List<String> filterList = List.of("validationStatus", "category", "approvalFromDate", "approvalToDate",
				"submittedFromDate", "submittedToDate", "appointmentFromDate", "appointmentToDate", "stayFromDate",
				"stayToDate", "studentName", "studentId", "hostelName","currentDayStayFlag");
		if (!form.isSearchFilter()) {
			filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
		}
		else if (Constants.VACATING_LINK.equals(form.getAdditionalParam().get("currentDayStayFlag"))) {
			List<String> excludedFilters = List.of("validationStatus", "stayToDate", "currentDayStayFlag");
			filterList.stream()
					.filter(filter -> !excludedFilters.contains(filter))
					.forEach(filter -> form.getAdditionalParam().put(filter, ""));
		}
		List<HostelMasterDto> hostelMasterDtos = commonService.getHostelListForUser();
        map.addAttribute(HostelConstants.HOSTEL_OR_WARDEN_LIST.getConstants(), hostelMasterDtos);
		map.addAttribute("validationStatusList", deanDashboardService.getValidationStatusList(validationStatus));
        map.addAttribute("categoryList", simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.NATURE_OF_APPOINTMENT));
        map.addAttribute("url", baseUrl);
		map.addAttribute("isBulkApproveRejectNeeded", true);
		map.addAttribute("isRoleDean", RoleEnum.DEAN.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole()));
		FilterCriteriaDto filterCriteria = service.getFilterData(form, splitBaseUrl);
		List<DeanAccommodationRequestDto> accommodationRequestDto = service.getStudentAccommodationRequestList(form,
				splitBaseUrl, filterCriteria, columnDto);
		commonResponseUtil.updateCommonModelAttributes2(map, request, accommodationRequestDto, form);
		HeaderForm headerForm = (HeaderForm) map.getAttribute(ModelConstants.HEADER_FORM);
		if (headerForm != null) {
			/** For Excel download button **/
			headerForm.setAdditionalButtonProperties(true, ModelConstants.BUTTON_PINK, service.getReportHeaderName(baseUrl),
					ModelConstants.FA_FILE_EXCEL);
			map.addAttribute("excelUrl", splitBaseUrl
					+ messageSource.getMessage("url.download.accommodation.request.report", null, Locale.getDefault()));

			if(RoleEnum.HOSTEL_CHECK_IN.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole())) {
				/** For today vacating list button **/
				headerForm.setAdditionalButton2Properties(true, ModelConstants.BUTTON_PURPLE,
						messageSource.getMessage("message.label.today.vacating.list", null, Locale.getDefault()),null);
			}
		}
		return RoleEnum.CCW_OFFICE.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole()) ? HTMLPage.DEAN_ALLOTMENT_LIST : HTMLPage.DEAN_DASHBOARD_MENU_LIST;
	}

	private String getValidationStatus() {
		return RoleEnum.CCW_OFFICE.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole()) ?
				SimsConfigDataService.VALIDATION_STATUS_CCW_OFFICE :
				(SecurityCtxUtil.isHostelRole() || SecurityCtxUtil.isWardenRole() ?
						SimsConfigDataService.VALIDATION_STATUS_HOSTEL_CHECK_IN :
						SimsConfigDataService.VALIDATION_STATUS);
	}

	@GetMapping("${url.download.accommodation.request.report}")
	public void downloadStudentAccommodationRequestReport(@RequestParam Map<String, String> allParams, PaginationForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		commonResponseUtil.getAdditionalParams(allParams, form);
		String baseUrl = getBaseUrl(request);
		String splitBaseUrl = baseUrl.replace("/", "");
		FilterCriteriaDto filterCriteria = service.getFilterData(form, splitBaseUrl);
		Workbook workbook = service.downloadStudentAccommodationRequestReport(form, splitBaseUrl, filterCriteria);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		workbook.close();
		byte[] excelBytes = bos.toByteArray();
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=List of Students.xlsx");
		response.setContentLength(excelBytes.length);
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			outputStream.write(excelBytes);
			outputStream.flush();
		}
	}

	@DeleteMapping("${url.delete}")
	public @ResponseBody BaseResponse deleteAccommodationRequest(@RequestParam String data, ModelMap map,
			HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
		Boolean status = Utility.checkRequestType(data, 3, 2);
		if (status) {
			try {
				return CommonResponseUtil.generateDeleteResponseByStatus(service.deleteAccommodationRequest(data));
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
	public @ResponseBody BaseResponse saveResendMail(@RequestParam String data, HttpServletRequest request,
			RedirectAttributes redirectAttrs) throws Exception {
		Boolean status = Utility.checkRequestType(data, 3, 2);
		if (status) {
			String saveStatus = service.resendMail(data, request);
			return CommonResponseUtil.updateResponseByStatus(saveStatus != null ? true : false, "response.mail.success", "response.mail.error");
		} else {
			commonResponseUtil.invalidAccess(redirectAttrs);
		}
		return null;
	}

	@GetMapping("${url.pdf.download}")
	public ResponseEntity<Resource> downloadAccommodationDetailsPDF(@RequestParam String data,
			RedirectAttributes redirectAttrs) throws Exception {
		Boolean status = Utility.checkRequestType(data, 3, 2);
		if (status) {
			try {
				Resource resource = service.downloadAccommodationDetailsPDF(data);
				return Utility.prepareDownloadFile(resource);
			} catch (Exception e) {
				e.printStackTrace();
				commonResponseUtil.exceptionMessageHandling(e, redirectAttrs);
			}
		} else {
			commonResponseUtil.invalidAccess(redirectAttrs);
		}
		return null;
	}


	@GetMapping("${url.view}")
	public String getStudentAccommodationRequestView(@RequestParam String data, ModelMap map, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
		Boolean status = Utility.checkRequestType(data, 4, 2);
//		if (status) {
			try {
				List<String> split = List.of(MCrypt.getInstance().decryptToString(data).split(Constants.BACKTICK));
				String studentId = service.getNonNullValue(split.getFirst());
				Long requestId = Long.parseLong(split.get(1));
				StudentAppointmentRequestDto studentAppointmentRequestDto = studentAppointmentRequestService.getStudentAccommodationRequestDetails(studentId, requestId, Constants.CCW);
				StudentDetailsDto studentDetails = studentBioDataService.getFullStudentDetails(studentId);
				AllStudentsDetailsViewDto allStudentsDetailsViewDto = service.getAllStudentDetails(studentId);
				StudentBlackListDetailDto studentBlackListDetailDto = service.getBlackListDetails(studentId);
				List<StudentWorkflowDto> studentWorkflowDtoList = studentAppointmentRequestService.getByStudentIdAndRequestIdAndActiveFlag(requestId, studentId, WorkflowStatus.DEFAULT.getStatus());
				StudentWorkflowEntity studentWorkflowEntity = studentWorkflowService.getStudentWorkflowDetails(requestId, studentId);
				service.setDtoValues(studentAppointmentRequestDto, split, studentWorkflowEntity, studentWorkflowDtoList);
				studentAppointmentRequestDto.setStudentWorkflowDto(studentWorkflowDtoList);
				commonResponseUtil.updateCommonModelAttributes(map, request);
				map.addAttribute("studentAppointmentRequestDto", studentAppointmentRequestDto);
				map.addAttribute("studentBlackListDetailDto", studentBlackListDetailDto);
				map.addAttribute("allStudentsDetailsViewDto", allStudentsDetailsViewDto);
				map.addAttribute("studentDetails", studentDetails);
				map.addAttribute("status", RoleEnum.CCW_OFFICE.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole())  ? "v" : ModelConstants.EMPTY_STRING);
				return HTMLPage.STUDENT_APPOINTMENT_REQUEST_VIEW;
			} catch (Exception e) {
				e.printStackTrace();
				commonResponseUtil.exceptionMessageHandling(e, redirectAttributes);
			}
//		}
//		else {
//			commonResponseUtil.invalidAccess(redirectAttributes);
//		}
		return Constants.REDIRECT + ModelConstants.SLASH + getBaseUrl(request);
	}

	@PostMapping(value = "${url.save}")
	public ResponseEntity<String> saveDetails(@ModelAttribute StudentAppointmentRequestDto studentAppointmentRequestDto) throws Exception {
		StudentWorkflowDto studentWorkflowDto = new StudentWorkflowDto();
		StudentAppointmentRequestDto studentAppointmentRequestDto1 = studentAppointmentRequestService.getStudentAppointmentRequestById(studentAppointmentRequestDto.getId());
		studentWorkflowDto.setRequestId(studentAppointmentRequestDto.getId());
		studentWorkflowDto.setStudentId(studentAppointmentRequestDto.getStudentId());
		studentAppointmentRequestDto1.setStayFrom(studentAppointmentRequestDto.getStayFrom());
		studentAppointmentRequestDto1.setStayTo(studentAppointmentRequestDto.getStayTo());
		studentAppointmentRequestDto1.setOccupancy(studentAppointmentRequestDto.getOccupancy());
		String status = service.saveDetails(studentAppointmentRequestDto1, studentWorkflowDto);
		return ResponseEntity.ok(status);
	}

	@GetMapping("${url.room.list}")
	public ResponseEntity<List<HostelRoomInfoDto>> getRooms(
			@RequestParam Long hostelId,
			@RequestParam Long requestId,@RequestParam String encryptedKey,@RequestParam String screenType) throws Exception {

		Long stayId = getStayId(encryptedKey);

		List<HostelRoomInfoDto> rooms = hostelRoomInfoService.getAvailableRooms(hostelId, requestId, stayId,screenType);
		return ResponseEntity.ok(rooms);
	}

	@GetMapping("${url.seat.list}")
	public ResponseEntity<List<HostelRoomInfoDto>> getSeats(
			@RequestParam Long hostelId,
			@RequestParam String roomNo,
			@RequestParam Long requestId,@RequestParam String encryptedKey,@RequestParam String screenType) throws Exception {

		Long stayId = getStayId(encryptedKey);
		List<HostelRoomInfoDto> seats = hostelRoomInfoService.getAvailableSeats(hostelId, roomNo,  requestId,stayId,screenType);
		return ResponseEntity.ok(seats);
	}

	@PostMapping(value = "${url.allocate}"  + "${id}")
	public ResponseEntity<String> allotStudent(@PathVariable("id") String data,
											   @RequestParam Long hostelId,
											   @RequestParam String roomNo,
											   @RequestParam String status,
											   @RequestParam String seatName) throws Exception {
		List<String> split = List.of(MCrypt.getInstance().decryptToString(data).split(Constants.BACKTICK));
		String studentId = studentAccommodationRequestService.getNonNullValue(split.getFirst());
		long requestId = Long.parseLong(split.get(1));
		String saveStatus = studentAccommodationRequestService.allotStudent(studentId, requestId, hostelId, roomNo, seatName, status);
		return ResponseEntity.ok(saveStatus);
	}
	
	@PostMapping(value = "${url.checkIn.checkOut}")
	public ResponseEntity<String> updateCheckInOrCheckOut(@RequestParam String data,
											   @RequestParam String status) throws Exception {
		List<String> split = List.of(MCrypt.getInstance().decryptToString(data).split(Constants.BACKTICK));
		long requestId = Long.parseLong(split.get(1));
		String saveStatus = studentAccommodationRequestService.updateCheckInCheckOut(requestId,status);
		return ResponseEntity.ok(saveStatus);
	}

	private Long getStayId(String encryptedKey) throws Exception {
		if(Objects.nonNull(encryptedKey) && !encryptedKey.isEmpty()) {
			List<String> split = List.of(MCrypt.getInstance().decryptToString(encryptedKey).split(Constants.BACKTICK));
			if (split.size() > 4) {
				return Long.parseLong(split.getLast());
			} else {
				return 0L;
			}
		}
		else{
			return 0L;
		}
	}

	@PostMapping({"${url.bulk.approve.reject}", "${url.bulk.approve.reject}" + "${url.stay.extension}"})
	@ResponseBody
	public BaseResponse bulkApproveReject(@RequestBody BulkApprovalRejectDto dto, HttpServletRequest request) {
		String saveStatus = service.bulkApproveReject(dto, request);
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
}
