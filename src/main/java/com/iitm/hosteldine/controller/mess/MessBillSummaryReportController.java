package com.iitm.hosteldine.controller.mess;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mess.MessBillSummaryDto;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.service.mess.MessAllottedListService;
import com.iitm.hosteldine.service.mess.MessBillSummaryReportService;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import com.iitm.hosteldine.service.mess.MessMasterService;
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
@RequestMapping("${url.mess.bill.summary.report}")
public class MessBillSummaryReportController {

	private final MessBillSummaryReportService service;
	private final MessMasterService messMasterService;
	private final MessAllottedListService messPeriodService;
	private final CommonResponseUtil commonResponseUtil;
	private final MessageSource messageSource;
	private final MessAllotmentListValidator validator;
	private final MessMasterCommonService messMasterCommonService;

	@Value("${url.mess.bill.summary.report}")
	private String baseURL;

	@GetMapping
	public String getMessBillSummaryReport(ModelMap map, HttpServletRequest request) throws Exception {
		MessBillSummaryDto dto = new MessBillSummaryDto();
		map.addAttribute("messPeriodList", messPeriodService.getMessPeriodList());
		map.addAttribute("messMasterList", messMasterService.getMessMasterList());
		map.addAttribute("messBillSummary", dto);
		commonResponseUtil.updateCommonModelAttributes(map, request);
		HeaderForm headerForm = (HeaderForm) map.getAttribute(ModelConstants.HEADER_FORM);
		if (headerForm != null) {
			headerForm.setAdditionalButtonProperties(true, ModelConstants.BUTTON_PINK,
					messageSource.getMessage("message.label.send.mail.to.students", null, Locale.getDefault()),
					ModelConstants.FA_FILE_EXCEL);
		}
		return HTMLPage.MESS_BILL_SUMMARY;
	}

	@GetMapping("${url.download.mess.bill.summary.report}" + "${messPeriod}" + "${messName}")
	public void downloadMessBillSummaryReport(@PathVariable Integer messPeriod, @PathVariable Integer messName,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Workbook workbook = service.downloadMessBillSummaryReport(messPeriod, messName);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		workbook.close();
		byte[] excelBytes = bos.toByteArray();
		response.setContentType(FileUploadConstants.XLSX);
		response.setHeader(ExcelConstants.CONTENT_DISPOSITION, ExcelConstants.MESS_BILL_SUMMARY_FILENAME);
		response.setContentLength(excelBytes.length);
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			outputStream.write(excelBytes);
			outputStream.flush();
		}
	}

	@GetMapping("${url.pdf.download}" + "${messPeriod}" + "${messName}")
	public void downloadMessBillSummaryPDF(@PathVariable Integer messPeriod,
			@PathVariable Integer messName, HttpServletResponse response) throws Exception {
		service.downloadMessBillSummaryPDF(messPeriod, messName, response);
	}

	@GetMapping("${url.mess.mail}" + "${messPeriod}" + "${messName}")
	public String getSendMailToStudents(@PathVariable Integer messPeriod, @PathVariable Integer messName, ModelMap map,
			HttpServletRequest request) throws Exception {
		map.addAttribute("messPeriod", messPeriod);
		map.addAttribute("messName", messName);
		return HTMLPage.MESS_BILL_SUMMARY_MAIL;
	}

	@PostMapping("${url.mess.mail}")
	public String sendMailToStudents(@ModelAttribute MessBillSummaryDto messBillSummary, HttpServletRequest request,
			RedirectAttributes redirectAttrs) throws Exception {
		String saveStatus = service.sendMailToStudents(messBillSummary);
		commonResponseUtil.updateResponse(saveStatus, redirectAttrs, "response.mail.success",
				"message.label.no.student.records.for.mess.period.and.mess.name");
		redirectAttrs.addFlashAttribute(Constants.MODAL_ERROR, null);
		return Constants.REDIRECT + baseURL;
	}

	@GetMapping(value = "${url.update}")
	public String getMessEffectiveFromDate(ModelMap model, HttpServletRequest request) throws Exception {
		String formKey = "messBillSummaryDto";
		MessBillSummaryDto messBillSummaryDto = commonResponseUtil.handleModalFormError(request, model, formKey,
				MessBillSummaryDto.class);
		model.addAttribute("currentMessPeriod", messMasterCommonService.getCurrentMessPeriod());
		model.addAttribute(formKey, messBillSummaryDto);
		return HTMLPage.MESS_BILL_SUMMARY_MODAL;
	}

	@PostMapping(value = "${url.save}")
	public String saveStudentMessChangeFromDate(@ModelAttribute MessBillSummaryDto messBillSummaryDto,
			BindingResult bindingResult, RedirectAttributes redirectAttributes, ModelMap model) throws Exception {
		validator.validateMessBill(messBillSummaryDto, bindingResult);
		if (bindingResult.hasErrors()) {
			commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes, bindingResult, messBillSummaryDto);
			return Constants.REDIRECT + baseURL;
		}
		String status = service.saveStudentMessChangeFromDate(messBillSummaryDto);
		commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, "message.update.effective.from.date");
		return Constants.REDIRECT + baseURL;
	}

}
