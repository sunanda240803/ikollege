package com.iitm.hosteldine.controller.dashboard.dean;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.dto.OptionDto;
import com.iitm.hosteldine.dto.dean.*;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.dean.DeanDashboardService;
import com.iitm.hosteldine.service.dean.DeanMessRebateService;
import com.iitm.hosteldine.service.dean.StudentBulkAccommodationService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping(value = "${url.dean.student.bulk.accommodation}")
@RequiredArgsConstructor
public class StudentBulkAccommodationController {

	@Value("${url.dean.student.bulk.accommodation}")
    private String baseUrl;

	private final CommonResponseUtil commonResponseUtil;
	private final DeanDashboardService deanDashboardService;
	private final DeanMessRebateService deanMessRebateService;
	private final StudentBulkAccommodationService studentBulkAccommodationService;
	private final MessageSource messageSource;

	static boolean isFacultyLoggedin = false;

	@GetMapping
	public String getStudentBulkAccommodation(@RequestParam Map<String, String> allParams,PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		String loggedInUserName = SecurityCtxUtil.userName();
		String loggedInUserRole = SecurityCtxUtil.userRole();
		isFacultyLoggedin = StringUtils.equalsIgnoreCase(loggedInUserRole, Constants.USER_ROLE_FACULTY);

		//Getting dynamic tabs
		DeanApprovalDto deanApprovalDto = deanDashboardService.getDeanMenuList();
		map.addAttribute("deanApprovalDto", deanApprovalDto);
		DeanApprovalDto columnList = deanDashboardService.getDeanMenuListById(baseUrl.replace("/", ""));

		map.addAttribute("columnDto", columnList);
		//Setting dynamic filter values
		if(!form.isSearchFilter()) {
			List<String> filterList = List.of("validationStatus","approvalFromDate","approvalToDate","submittedFromDate",
					"fileName","eventName");
			filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
		}
		commonResponseUtil.getAdditionalParams(allParams, form);

		map.addAttribute("showUploadCard", true);
		map.addAttribute("isDeanLogin", !isFacultyLoggedin);
		map.addAttribute("isFacultyLogin", isFacultyLoggedin);
		map.addAttribute("diningStatusList", Stream.of("Required", "Not Required").map(d-> new OptionDto(d.toLowerCase(),d)).collect(Collectors.toList()));
		map.addAttribute("sessionList", Stream.of("All", "Limited Session").map(d-> new OptionDto(d.toLowerCase(),d)).collect(Collectors.toList()));
		StudentBulkAccommodationDto dto = new StudentBulkAccommodationDto();
		dto.getRows().add(new Rows()); // Add one blank row
		map.addAttribute("studentBulkAccommodationDto", dto);
		map.addAttribute("validationStatusList", deanDashboardService.getValidationStatusList(SimsConfigDataService.VALIDATION_STATUS));

		//fetch method
		List<StudentAccomBulkRequestDto> studentAccomBulkRequestDto = studentBulkAccommodationService.getBulkUploadList(form,baseUrl.replace("/", ""), isFacultyLoggedin, columnList);

		commonResponseUtil.updateCommonModelAttributes2(map, request,studentAccomBulkRequestDto,form);
		return HTMLPage.DEAN_DASHBOARD_MENU_LIST;
	}

	@GetMapping(value = "${url.template.download}")
	public void downloadStudentBulkRequestUploadTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Workbook workbook = studentBulkAccommodationService.downloadStudentBulkRequestUploadTemplate();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		workbook.close();

		// Convert the ByteArrayOutputStream to a byte array
		byte[] excelBytes = bos.toByteArray();

		// Set the content type and headers for the response
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=StudentBulkUploadTemplate.xlsx");
		response.setContentLength(excelBytes.length);

		// Write the byte array to the response output stream
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			outputStream.write(excelBytes);
			outputStream.flush();
		}
	}

	@PostMapping(value = "${url.upload}")
	public String saveStudentBulkUpload(@ModelAttribute StudentBulkAccommodationDto studentBulkAccommodationDto, HttpServletRequest request,
										HttpServletResponse response, RedirectAttributes redirectAttrs) throws IOException {
		String message = null, status = null;
		BaseResponse baseResponse = new BaseResponse();
		String s = "redirect:" + baseUrl;
		if (studentBulkAccommodationDto.getId() == null && studentBulkAccommodationDto.getFile().isEmpty()) {
			redirectAttrs.addFlashAttribute("excelErrorList", "Please choose file to upload.");
			return "redirect:" + baseUrl;
		}

		studentBulkAccommodationDto = studentBulkAccommodationService.saveOrUpdateStudentBulkUpload(studentBulkAccommodationDto, request);


		if (CollectionUtils.isNotEmpty(studentBulkAccommodationDto.getErrorList())) {
			redirectAttrs.addFlashAttribute("excelErrorList", studentBulkAccommodationDto.getErrorList());
			return s ;
		} else {
			message = messageSource.getMessage("response.student.bulk.upload.success", null, Locale.getDefault());
			status = messageSource.getMessage("response.status.success", null, Locale.getDefault());
			redirectAttrs.addFlashAttribute("response", new BaseResponse(message, status));
			return s ;
		}
	}

	@GetMapping("${url.download}"+"${id}")
	public void downloadExcel(@PathVariable String id, @ModelAttribute PaginationForm pageForm,
									  HttpServletResponse response, HttpServletRequest request) {
		HSSFWorkbook hSSFWorkbook = new HSSFWorkbook();
		pageForm.setPage(1);
		pageForm.setSize(500);
		try {
            String fileName = ExcelConstants.STUDENT_BULK_UPLOAD_SHEET_NAME + FileUploadConstants.XLSX_EXTENSION;
            Workbook workbook = studentBulkAccommodationService.downloadFile(id);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			workbook.write(bos);
			workbook.close();
			byte[] excelBytes = bos.toByteArray();
			response.setContentType(FileUploadConstants.XLSX);
			response.setHeader(FileUploadConstants.CONTENT_DISPOSITION, messageSource.getMessage(
					"message.attachment.filename", new Object[]{fileName}, Locale.getDefault()));
			response.setContentLength(excelBytes.length);

			try (ServletOutputStream outputStream = response.getOutputStream()) {
				outputStream.write(excelBytes);
				outputStream.flush();
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GetMapping("${url.edit}"+"${id}")
	public String edit(@PathVariable String id, @RequestParam Map<String, String> allParams,PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		//Getting dynamic tabs
		DeanApprovalDto deanApprovalDto = deanDashboardService.getDeanMenuList();
		map.addAttribute("deanApprovalDto", deanApprovalDto);
		DeanApprovalDto columnList = deanDashboardService.getDeanMenuListById(baseUrl.replace("/", ""));

		map.addAttribute("columnDto", columnList);
		//Setting dynamic filter values
		if(!form.isSearchFilter()) {
			List<String> filterList = List.of("validationStatus","approvalFromDate","approvalToDate","submittedFromDate",
					"fileName","eventName");
			filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
		}
		commonResponseUtil.getAdditionalParams(allParams, form);

		map.addAttribute("showUploadCard", true);
		map.addAttribute("isDeanLogin", false);
		map.addAttribute("isFacultyLogin", true);
		map.addAttribute("diningStatusList", Stream.of("Required", "Not Required").map(d-> new OptionDto(d.toLowerCase(),d)).collect(Collectors.toList()));
		map.addAttribute("sessionList", Stream.of("All", "Limited Session").map(d-> new OptionDto(d.toLowerCase(),d)).collect(Collectors.toList()));

		StudentBulkAccommodationDto dto = studentBulkAccommodationService.getStudentBulkAccommodation(id);
		map.addAttribute("studentBulkAccommodationDto", dto);
		map.addAttribute("validationStatusList", deanDashboardService.getValidationStatusList(SimsConfigDataService.VALIDATION_STATUS));

		//fetch method
		List<StudentAccomBulkRequestDto> studentAccomBulkRequestDto = studentBulkAccommodationService.getBulkUploadList(form,baseUrl.replace("/", ""),isFacultyLoggedin, columnList);
		commonResponseUtil.updateCommonModelAttributes2(map, request,studentAccomBulkRequestDto,form);
		return HTMLPage.DEAN_DASHBOARD_MENU_LIST;
	}

	@PostMapping("${url.status}"+"${id}")
	@ResponseBody
	public BaseResponse updateStatus(@PathVariable String id, @ModelAttribute StatusUpdateDto StatusUpdateDto,
									 HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs) throws IOException {
		String message = null, status = null;
		BaseResponse baseResponse = new BaseResponse();
		String s = "redirect:" + baseUrl;
		if (StringUtils.isEmpty(StatusUpdateDto.getApprovalStatus())) {
			message = "response.status.not.found";
			status = "response.status.failure";
			baseResponse.setMessage(messageSource.getMessage(message, null, Locale.getDefault()));
			baseResponse.setStatus(messageSource.getMessage(status, null, Locale.getDefault()));

			return baseResponse;
		}

		Boolean isUpdated = studentBulkAccommodationService.updateStatus(id, StatusUpdateDto, request);
		if (isUpdated) {
			message = "response.student.bulk.upload.status.updated";
			status = "response.status.success";
		} else {
			message = "response.student.bulk.upload.status.failed";
			status = "response.status.failure";
		}
		baseResponse.setMessage(messageSource.getMessage(message, null, Locale.getDefault()));
		baseResponse.setStatus(messageSource.getMessage(status, null, Locale.getDefault()));

		return baseResponse;
	}


	@DeleteMapping("${url.delete}" + "${id}")
	public @ResponseBody BaseResponse deleteById(@PathVariable String id, ModelMap map,
															 HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		try {
			Boolean isDeleted = studentBulkAccommodationService.deleteBulkAccommodation(id);
			return CommonResponseUtil.generateDeleteResponseByStatus(isDeleted);
		} catch (RecordNotExistsException e) {
			// Create a custom error response in case of an exception
			BaseResponse errorResponse = new BaseResponse();
			errorResponse.setMessage(e.getMessage());
			errorResponse.setStatus("Failure");
			return errorResponse;
		}
	}


	@PostMapping("${url.send.message}"+"${id}")
	@ResponseBody
	public BaseResponse sendMessage(@PathVariable String id, @ModelAttribute SendMessageDto sendMessageDto,
									HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs) throws IOException {
		String message = null, status = null;
		BaseResponse baseResponse = new BaseResponse();
		String s = "redirect:" + baseUrl;
		if (StringUtils.isEmpty(sendMessageDto.getSubject()) ||StringUtils.isEmpty(sendMessageDto.getMessage())) {
			message = "message.label.form.invalid";
			status = "response.status.failure";
			baseResponse.setMessage(messageSource.getMessage(message, null, Locale.getDefault()));
			baseResponse.setStatus(messageSource.getMessage(status, null, Locale.getDefault()));

			return baseResponse;
		//	return s;
		}
		Boolean isUpdated = studentBulkAccommodationService.sendMessage(id, sendMessageDto, request);
		if (isUpdated) {
			message = "response.student.bulk.upload.send.message.success";
			status = "response.status.success";
		} else {
			message = "response.student.bulk.upload.send.message.failed";
			status = "response.status.failure";
		}
		baseResponse.setMessage(messageSource.getMessage(message, null, Locale.getDefault()));
		baseResponse.setStatus(messageSource.getMessage(status, null, Locale.getDefault()));

		return baseResponse;
	}


	@GetMapping( "${url.view}" + "${id}")
	public String getRoomDetails(@PathVariable String id, PaginationForm form, ModelMap map, HttpServletRequest request) {

		map.addAttribute("isDeanLogin", !isFacultyLoggedin);
		map.addAttribute("isFacultyLogin", isFacultyLoggedin);

		StudentBulkAccommodationDto dto = studentBulkAccommodationService.getStudentBulkAccommodation(id);
		map.addAttribute("studentBulkAccommodationDto", dto);
        map.addAttribute("isMail", false);
		commonResponseUtil.updateCommonModelAttributes(map, request);
		return HTMLPage.STUDENT_BULK_UPLOAD_VIEW;
	}

	@PostMapping("${url.student.bulk.upload.update.and.approve}" + "${status}")
	@ResponseBody
	public BaseResponse updateAndApprove(@PathVariable String status, @ModelAttribute StudentBulkAccommodationDto studentBulkAccommodationDto, HttpServletRequest request,
										 HttpServletResponse response, RedirectAttributes redirectAttrs) throws IOException {
		String message = null;String status1;
		BaseResponse baseResponse = new BaseResponse();
		String s = "redirect:" + baseUrl;

		Boolean updateAndApprove = studentBulkAccommodationService.updateAndApprove(studentBulkAccommodationDto, status, request);

		if (updateAndApprove) {
			message = "response.student.bulk.upload.status.updated";
			status1 = "response.status.success";
		} else {
			message = "response.student.bulk.upload.status.failed";
			status1 = "response.status.failure";
		}
		baseResponse.setMessage(messageSource.getMessage(message, null, Locale.getDefault()));
		baseResponse.setStatus(messageSource.getMessage(status1, null, Locale.getDefault()));

		return baseResponse;
	}
}
