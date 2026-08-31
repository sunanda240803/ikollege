package com.iitm.hosteldine.controller.hostel;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.GeneralLedgerDto;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.AccountHeadService;
import com.iitm.hosteldine.service.hostel.GeneralLedgerService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("${url.general.ledger}")
public class GeneralLedgerController {

	private final GeneralLedgerService service;
	private final AccountHeadService accHeadService;
	private final HostelMasterService hostelService;
	private final MessageSource messageSource;
	private final Utility utility;
	private final CommonResponseUtil commonResponseUtil;

	@GetMapping
	public String getGeneralLedgerList(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map,
			HttpServletRequest request) throws Exception {

		String[] keys = GeneralLedgerDto.PARAM_KEYS;
		Page<GeneralLedgerDto> generalLedgerList = Page.empty();
		commonResponseUtil.getAdditionalParams(allParams, form);

		Map<String, Object> additionalParams = form.getAdditionalParam();
		Map<String, Object> extractedParams = new HashMap<>();

		for (String key : keys) {
			extractedParams.put(key, additionalParams.get(key));
		}

		boolean allRequiredFieldsValid = extractedParams.entrySet().stream()
				.filter(entry -> !"hostelId".equals(entry.getKey())).allMatch(entry -> isValid(entry.getValue()));

		GeneralLedgerDto dto = new GeneralLedgerDto();
		if (allRequiredFieldsValid) {
			GeneralLedgerDto filter = service.getFilterData(form);
			Double opBal = service.getOpeningBalance(form, filter);
			String openingBalance = openingBalance(opBal);
			map.addAttribute("opBal", openingBalance);
			generalLedgerList = service.getGeneralLedgerList(form, filter, opBal);
			dto = service.getFilterData(form);
		} else {
			Arrays.stream(keys).forEach(key -> additionalParams.put(key, ""));
		}

		commonResponseUtil.updateCommonModelAttributes(map, request, generalLedgerList, form);
		HeaderForm headerForm = (HeaderForm) map.getAttribute(ModelConstants.HEADER_FORM);
		if (headerForm != null) {
			headerForm.setAdditionalButtonProperties(true, ModelConstants.BUTTON_PINK,
					messageSource.getMessage("message.label.general.ledger.report", null, Locale.getDefault()),
					ModelConstants.FA_FILE_EXCEL);
		}
		map.addAttribute("generalLedger", dto);
		map.addAttribute("hostelList", hostelService.getHostelList());
		map.addAttribute("accHeadList", accHeadService.getAccountHeadListByFinYear());
		return HTMLPage.GENERAL_LEDGER;
	}

	private String openingBalance(Double opBal) {
		if (opBal == null || opBal < 0.00) {
			return "0.00" + ModelConstants.SPACE
					+ messageSource.getMessage("message.label.dr", null, Locale.getDefault());
		} else {
			return utility.formatCommaSeperatedCurrency(opBal) + ModelConstants.SPACE
					+ messageSource.getMessage("message.label.cr", null, Locale.getDefault());
		}
	}

	@GetMapping("${url.download.general.ledger.report}")
	public void downloadGeneralLedgerReport(@RequestParam Map<String, String> allParams, PaginationForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		commonResponseUtil.getAdditionalParams(allParams, form);
		GeneralLedgerDto filter = service.getFilterData(form);
		Double opBal = service.getOpeningBalance(form, filter);
		Workbook workbook = service.downloadGeneralLedgerReport(filter, opBal, openingBalance(opBal));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		workbook.close();
		byte[] excelBytes = bos.toByteArray();
		response.setContentType(FileUploadConstants.XLSX);
		response.setHeader(ExcelConstants.CONTENT_DISPOSITION, ExcelConstants.GENERAL_LEDGER_FILENAME);
		response.setContentLength(excelBytes.length);
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			outputStream.write(excelBytes);
			outputStream.flush();
		}
	}

	private boolean isValid(Object value) {
		if (value == null) {
			return false;
		}
		if (value instanceof String) {
			String strValue = (String) value;
			return !strValue.trim().isEmpty();
		}
		return false;
	}

}
