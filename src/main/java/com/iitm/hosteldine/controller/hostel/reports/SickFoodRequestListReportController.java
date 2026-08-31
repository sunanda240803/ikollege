package com.iitm.hosteldine.controller.hostel.reports;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.student.SickFoodDeliveryStatusDto;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.student.SickFoodDeliveryStatusService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value = "${url.sick.food.request.list.report}")
@RequiredArgsConstructor
public class SickFoodRequestListReportController {
	
	@Value("${url.sick.food.request.list.report}")
	private String baseUrl;
	
	private final CommonResponseUtil commonResponseUtil;
	private final SimsConfigDataService simsConfigDataService;
	private final SickFoodDeliveryStatusService sickFoodDeliveryStatusService;
	private final MessageSource messageSource;
	
	@RequestMapping(method = { RequestMethod.GET })
	public String getSickFoodRequestList(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request) throws Exception {
		String splitBaseUrl = baseUrl.replace("/", "");
		if(!form.isSearchFilter()) {
			List<String> filterList = List.of("requestFromDate","requestToDate","catererStatusId","studentStatusId","studentName","studentId","messSession");
			filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
		}
		map.addAttribute("filters", form.getAdditionalParam());		
		commonResponseUtil.getAdditionalParams(allParams, form);
		map.addAttribute("catererStatusMap", simsConfigDataService.getSimConfigValueAsMap(SimsConfigDataService.CATERER_STATUS));
		map.addAttribute("messSessionMap", simsConfigDataService.getSimConfigValueAsMap(SimsConfigDataService.MESS_SESSION));
		Page<SickFoodDeliveryStatusDto> sickFoodRequestList = sickFoodDeliveryStatusService.getSickFoodRequestList(form, false);		
		commonResponseUtil.updateCommonModelAttributes(map, request, sickFoodRequestList, form);
		setExportButton(map, splitBaseUrl);
		return HTMLPage.SICK_FOOD_REQUEST_LIST_REPORT;
	}
	
	@GetMapping("${url.excel.report.download}")
	public void downloadRequestReport(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map,
			HttpServletRequest request, HttpServletResponse response) {
		commonResponseUtil.getAdditionalParams(allParams, form);
		Page<SickFoodDeliveryStatusDto> sickFoodRequestList = sickFoodDeliveryStatusService.getSickFoodRequestList(form, true);
		try {
			Workbook workbook = sickFoodDeliveryStatusService.getSickFoodRequestReport(sickFoodRequestList.getContent());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			workbook.write(bos);
			workbook.close();
			byte[] excelBytes = bos.toByteArray();
			response.setContentType(FileUploadConstants.XLSX);
			String fileName = messageSource.getMessage("message.label.sick.food.request.list", null, Locale.getDefault()) + "" + FileUploadConstants.XLSX_EXTENSION;
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
	
	private void setExportButton(ModelMap map , String splitBaseUrl) {
		HeaderForm headerForm = (HeaderForm) map.getAttribute(ModelConstants.HEADER_FORM);
		if (headerForm != null) {
			headerForm.setAdditionalButtonProperties(true, ModelConstants.BUTTON_PINK,
					messageSource.getMessage("message.label.sick.food.request.report", null, Locale.getDefault()),
					ModelConstants.FA_FILE_EXCEL);
			map.addAttribute("excelUrl", splitBaseUrl
					+ messageSource.getMessage("url.excel.report.download", null, Locale.getDefault()));
		}
	}
	
	
}
