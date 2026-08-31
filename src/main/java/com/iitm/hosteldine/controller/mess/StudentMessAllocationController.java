package com.iitm.hosteldine.controller.mess;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import com.iitm.hosteldine.service.InMemoryLogService;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.mess.MessAllottedListDTO;
import com.iitm.hosteldine.service.mess.StudentMessAllocationService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import com.iitm.hosteldine.validator.mess.MessAllotmentListValidator;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.students.mess.allocation}")
public class StudentMessAllocationController {

	private final CommonResponseUtil commonResponseUtil;
	private final StudentMessAllocationService service;
	private final MessAllotmentListValidator messAllotmentListValidator;
	private final String dto = "messAllottedListDTO";

	@Value("${url.students.mess.allocation}")
	private String baseURL;

	@GetMapping
	public String getStudentMessAllocation(ModelMap map, HttpServletRequest request, HttpSession session) throws Exception {
		Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
		MessAllottedListDTO messAllottedListDTO = flashInputMap != null && flashInputMap.get(dto) != null
				? (MessAllottedListDTO) flashInputMap.get(dto)
				: new MessAllottedListDTO();
		map.addAttribute(dto, messAllottedListDTO);
		map.addAttribute("currentMp", service.checkCurrentMessPeriod());
		map.addAttribute("nextMp", service.checkNextMessPeriod());
		commonResponseUtil.updateCommonModelAttributes(map, request);
		String logTag = (String) session.getAttribute(commonResponseUtil.getMessage("upload.log.tag"));
		// If no upload in progress, create a new idle tag
		if (logTag == null) {
			logTag = "messAllotment-" + UUID.randomUUID();
			session.setAttribute(commonResponseUtil.getMessage("upload.log.tag"), logTag);
		}
		map.put("logTag", logTag);

		return HTMLPage.STUDENTS_MESS_ALLOCATION;
	}

	@GetMapping(value = "${url.template.download}" + "${type}")
	public void downloadStudentMessAllocationTemplate(@PathVariable String type, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Workbook workbook = service.downloadStudentMessAllocationTemplate(type);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		workbook.close();
		// Convert the ByteArrayOutputStream to a byte array
		byte[] excelBytes = bos.toByteArray();
		// Set the content type and headers for the response
		response.setContentType(FileUploadConstants.XLSX);
		if (WorkflowStatus.CHANGE.getStatus().equalsIgnoreCase(type)) {
			response.setHeader(ExcelConstants.CONTENT_DISPOSITION, ExcelConstants.MESS_CHANGE_UPLOAD_FILENAME);
		} else if (WorkflowStatus.REMOVE.getStatus().equalsIgnoreCase(type)) {
			response.setHeader(ExcelConstants.CONTENT_DISPOSITION, ExcelConstants.MESS_REMOVE_UPLOAD_FILENAME);
		} else if (WorkflowStatus.REGULAR.getStatus().equalsIgnoreCase(type)) {
			response.setHeader(ExcelConstants.CONTENT_DISPOSITION, ExcelConstants.MESS_ALLOCATION_FILENAME);
		}
		response.setContentLength(excelBytes.length);
		// Write the byte array to the response output stream
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			outputStream.write(excelBytes);
			outputStream.flush();
		}
	}

	@PostMapping(value = "${url.save}")
	public String saveStudentMessAllocation(@ModelAttribute MessAllottedListDTO messAllottedListDTO,
			BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttrs, ModelMap map,HttpSession session) throws Exception {
		messAllotmentListValidator.validateBulkUpload(messAllottedListDTO, bindingResult);
		if (bindingResult.hasErrors()) {
			commonResponseUtil.updateCommonModelAttributes(map, request);
			map.addAttribute("currentMp", service.checkCurrentMessPeriod());
			map.addAttribute("nextMp", service.checkNextMessPeriod());
			return HTMLPage.STUDENTS_MESS_ALLOCATION;
		}
		String tag = "messAllotment-" + UUID.randomUUID();
		session.setAttribute(commonResponseUtil.getMessage("upload.log.tag"), tag);
		messAllottedListDTO.setLogTag(tag);

		byte[] fileBytes = messAllottedListDTO.getFile().getBytes();
		messAllottedListDTO.setFileBytes(fileBytes);
		messAllottedListDTO = service.startBulkUpload(messAllottedListDTO);
		redirectAttrs.addFlashAttribute(dto, messAllottedListDTO);
		commonResponseUtil.updateSaveResponseByStatus("message", redirectAttrs,"message.upload.process.started");

//		messAllottedListDTO = service.saveStudentMessAllocation(messAllottedListDTO);
//		if (messAllottedListDTO.getErrorList().isEmpty()) {
//			commonResponseUtil.updateSaveResponseByStatus(Constants.SAVED, redirectAttrs,
//					"message.label.file.uploaded.successfully");
//		} else {
//			redirectAttrs.addFlashAttribute(dto, messAllottedListDTO);
//		}
		return Constants.REDIRECT + baseURL;
	}
}
