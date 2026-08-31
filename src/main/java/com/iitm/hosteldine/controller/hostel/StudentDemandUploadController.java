package com.iitm.hosteldine.controller.hostel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
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
import com.iitm.hosteldine.form.common.TransactionDto;
import com.iitm.hosteldine.service.hostel.StudentDemandUploadService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import com.iitm.hosteldine.validator.hostel.StudentCreditDebitUploadValidator;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("${url.student.demand.upload}")
public class StudentDemandUploadController {

	private final CommonResponseUtil commonResponseUtil;
	private final StudentDemandUploadService service;
	private final StudentCreditDebitUploadValidator validator;
	private final String dto = "messBilling";

	@Value("${url.student.demand.upload}")
	private String baseURL;

	@GetMapping
	public String getStudentDemandUpload(ModelMap map, HttpServletRequest request) throws Exception {
		Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
		TransactionDto transactionDto = flashInputMap != null && flashInputMap.get(dto) != null
				? (TransactionDto) flashInputMap.get(dto)
				: new TransactionDto();  // Only populate if not already filled (like on redirect)
	    if (transactionDto.getAccheadInputs() == null || transactionDto.getAccheadInputs().isEmpty()) {
	    	transactionDto.setAccheadInputs(service.getAccheadInputs());
	    }
		map.addAttribute(dto, transactionDto);
		commonResponseUtil.updateCommonModelAttributes(map, request);
		return HTMLPage.STUDENT_DEMAND_UPLOAD;
	}

	@GetMapping("${url.template.download}")
	public void downloadStudentDemandTemplate(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Workbook workbook = service.downloadStudentDemandTemplate();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		workbook.close();
		// Convert the ByteArrayOutputStream to a byte array
		byte[] excelBytes = bos.toByteArray();
		// Set the content type and headers for the response
		response.setContentType(FileUploadConstants.XLSX);
		response.setHeader(ExcelConstants.CONTENT_DISPOSITION, ExcelConstants.STUDENT_DEMAND_FILENAME);
		response.setContentLength(excelBytes.length);
		// Write the byte array to the response output stream
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			outputStream.write(excelBytes);
			outputStream.flush();
		}
	}

	@PostMapping(value = "${url.save}")
	public String saveStudentDemands(@ModelAttribute TransactionDto transactionDto,
			BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttrs, ModelMap map) throws Exception {
		validator.validateDemandsUpload(transactionDto, bindingResult);
		if (bindingResult.hasErrors()) {
			commonResponseUtil.updateCommonModelAttributes(map, request);
			return HTMLPage.STUDENT_DEMAND_UPLOAD;
		}
		transactionDto = service.saveStudentDemands(transactionDto);
		if (transactionDto.getErrorList().isEmpty()) {
			Object[] args = { transactionDto.getStudentCount(), transactionDto.getAmount() };
			commonResponseUtil.updateResponse(Constants.SAVED, redirectAttrs,
					"message.label.file.uploaded.successfully.student.count.total.amount", "response.save.error", args);
		} else {
			redirectAttrs.addFlashAttribute(dto, transactionDto);
		}
		return Constants.REDIRECT + baseURL;
	}

}
