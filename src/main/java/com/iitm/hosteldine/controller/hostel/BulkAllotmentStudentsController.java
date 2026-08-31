package com.iitm.hosteldine.controller.hostel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.iitm.hosteldine.service.InMemoryLogService;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.dto.hostel.StudentsHostelAllotmentDto;
import com.iitm.hosteldine.service.hostel.BulkAllotmentStudentsService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import com.iitm.hosteldine.validator.hostel.BulkAllotmentValidator;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.bulk.allotment.students}")
public class BulkAllotmentStudentsController {
	private final MessageSource messageSource;
	private final BulkAllotmentStudentsService allotmentStudentsService;
	private final CommonResponseUtil commonResponseUtil;
	private final BulkAllotmentValidator bulkAllotmentValidator;
	private final String dto = "studentsHostelAllotmentDto";

	@Value("${url.bulk.allotment.students}")
	private String getBulkStudentsAllotment;
	
	@GetMapping
	public String getBulkAllotmentStudents(ModelMap map, HttpServletRequest request,HttpSession session) throws Exception {
		Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
		StudentsHostelAllotmentDto studentsHostelAllotmentDto = flashInputMap != null && flashInputMap.get(dto) != null
				? (StudentsHostelAllotmentDto) flashInputMap.get(dto)
				: new StudentsHostelAllotmentDto();
		map.addAttribute(dto, studentsHostelAllotmentDto);
		commonResponseUtil.updateCommonModelAttributes(map, request);

		String logTag = (String) session.getAttribute(commonResponseUtil.getMessage("upload.log.tag"));
		// If no upload in progress, create a new idle tag
		if (logTag == null) {
			logTag = "roomAllotment-" + UUID.randomUUID();
			session.setAttribute(commonResponseUtil.getMessage("upload.log.tag"), logTag);
		}
		map.put("logTag", logTag);

		return HTMLPage.BULK_ALLOTMENT_STUDENTS;
	}

	@GetMapping(value = "${url.template.download}")
	public void downloadBulkStudentsHostelAllotmentTemplate(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Workbook workbook = allotmentStudentsService.downloadBulkStudentsHostelAllotmentTemplate();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		workbook.close();
		// Convert the ByteArrayOutputStream to a byte array
		byte[] excelBytes = bos.toByteArray();
		// Set the content type and headers for the response
		response.setContentType(FileUploadConstants.XLSX);
		response.setHeader(ExcelConstants.CONTENT_DISPOSITION, ExcelConstants.HOSTEL_ROOM_ALLOTMENT_UPLOAD_FILENAME);
		response.setContentLength(excelBytes.length);
		// Write the byte array to the response output stream
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			outputStream.write(excelBytes);
			outputStream.flush();
		}
	}

	@PostMapping(value = "${url.save}")
	public String saveStudentsHostelAllotmentBulkUpload(
			@ModelAttribute StudentsHostelAllotmentDto studentsHostelAllotmentDto, BindingResult bindingResult,
			HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs, ModelMap model, HttpSession session)
			throws Exception {
		bulkAllotmentValidator.validate(studentsHostelAllotmentDto, bindingResult);
		if (bindingResult.hasErrors()) {
			commonResponseUtil.updateHeaderForm(request, model, 
					messageSource.getMessage("message.label.bulk.allotment.hypen.students", null, Locale.getDefault()), false);
			return HTMLPage.BULK_ALLOTMENT_STUDENTS;
		}
		String tag = "roomAllotment-" + UUID.randomUUID();
		session.setAttribute(commonResponseUtil.getMessage("upload.log.tag"), tag);
		studentsHostelAllotmentDto.setLogTag(tag);

		byte[] fileBytes = studentsHostelAllotmentDto.getFile().getBytes();
		studentsHostelAllotmentDto.setFileBytes(fileBytes);
		studentsHostelAllotmentDto = allotmentStudentsService.startBulkUpload(studentsHostelAllotmentDto);
		commonResponseUtil.updateSaveResponseByStatus("message", redirectAttrs,"message.upload.process.started");

//		if (studentsHostelAllotmentDto.getErrorList().isEmpty()) {
//			commonResponseUtil.updateSaveResponseByStatus(Constants.SAVED, redirectAttrs);
//		} else {
//			redirectAttrs.addFlashAttribute(dto, studentsHostelAllotmentDto);
//		}
		return Constants.REDIRECT + getBulkStudentsAllotment;
	}

}
