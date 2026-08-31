package com.iitm.hosteldine.controller.hostel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpSession;
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
import com.iitm.hosteldine.service.hostel.AccountHeadService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.hostel.StudentCreditDebitUploadService;
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
@RequestMapping("${url.student.credit.debit.upload}")
public class StudentCreditDebitUploadController {

	private final CommonResponseUtil commonResponseUtil;
	private final StudentCreditDebitUploadService service;
	private final StudentCreditDebitUploadValidator validator;
	private final AccountHeadService accountHeadService;
    private final HostelMasterService hostelMasterService;
	private final String dto = "messBilling";

	@Value("${url.student.credit.debit.upload}")
	private String baseURL;

	@GetMapping
	public String getStudentCreditDebitUpload(ModelMap map, HttpServletRequest request, HttpSession session) throws Exception {
		Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
		TransactionDto transactionDto = flashInputMap != null && flashInputMap.get(dto) != null
				? (TransactionDto) flashInputMap.get(dto)
				: new TransactionDto();
		map.addAttribute("accHeadList", accountHeadService.getAccountHeadListByFinYear());
		map.addAttribute("hostelList", hostelMasterService.getHostelList());
		map.addAttribute(dto, transactionDto);
		commonResponseUtil.updateCommonModelAttributes(map, request);

		String logTag = (String) session.getAttribute(commonResponseUtil.getMessage("upload.log.tag"));
		// If no upload in progress, create a new idle tag
		if (logTag == null) {
			logTag = "crDrUpload-" + UUID.randomUUID();
			session.setAttribute(commonResponseUtil.getMessage("upload.log.tag"), logTag);
		}
		map.put("logTag", logTag);

		return HTMLPage.STUDENT_CREDIT_DEBIT_UPLOAD;
	}

	@GetMapping("${url.template.download}")
	public void downloadStudentCreditDebitTemplate(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Workbook workbook = service.downloadStudentCreditDebitTemplate();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		workbook.close();
		// Convert the ByteArrayOutputStream to a byte array
		byte[] excelBytes = bos.toByteArray();
		// Set the content type and headers for the response
		response.setContentType(FileUploadConstants.XLSX);
		response.setHeader(ExcelConstants.CONTENT_DISPOSITION, ExcelConstants.STUDENT_CREDIT_DEBIT_FILENAME);
		response.setContentLength(excelBytes.length);
		// Write the byte array to the response output stream
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			outputStream.write(excelBytes);
			outputStream.flush();
		}
	}

	@PostMapping(value = "${url.save}")
	public String saveStudentCreditDebitDetails(@ModelAttribute TransactionDto messBilling, BindingResult bindingResult,
			HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs, ModelMap map,HttpSession session)
			throws Exception {
		validator.validate(messBilling, bindingResult);
		if (bindingResult.hasErrors()) {
			commonResponseUtil.updateCommonModelAttributes(map, request);
			return HTMLPage.STUDENT_CREDIT_DEBIT_UPLOAD;
		}
		String tag = "crDrUpload-" + UUID.randomUUID();
		session.setAttribute(commonResponseUtil.getMessage("upload.log.tag"), tag);
		messBilling.setLogTag(tag);

		byte[] fileBytes = messBilling.getFile().getBytes();
		messBilling.setFileBytes(fileBytes);
		messBilling = service.startBulkUpload(messBilling);
		redirectAttrs.addFlashAttribute(dto, messBilling);
		commonResponseUtil.updateSaveResponseByStatus("message", redirectAttrs,"message.upload.process.started");

//		messBilling = service.saveStudentCreditDebitDetails(messBilling);
//		if (messBilling.getErrorList().isEmpty()) {
//			Object[] args = { messBilling.getStudentCount(), messBilling.getAmount() };
//			commonResponseUtil.updateResponse(Constants.SAVED, redirectAttrs,
//					"message.label.file.uploaded.successfully.student.count.total.amount", "response.save.error", args);
//		} else {
//			redirectAttrs.addFlashAttribute(dto, messBilling);
//		}
		return Constants.REDIRECT + baseURL;
	}

}
