package com.iitm.hosteldine.controller.reports;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.iitm.hosteldine.dto.reports.StudentMessSelfAllotmentReportDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.reports.StudentMessSelfAllotmentService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value = "${url.student.mess.self.allotment.report}")
@RequiredArgsConstructor
public class StudentMessSelfAllotmentReportController {
	
	private final CommonResponseUtil commonResponseUtil;
	private final StudentMessSelfAllotmentService studentMessSelfAllotmentService;
	private final MessageSource messageSource;
	
	@RequestMapping(method = { RequestMethod.GET })
	public String getMessInspectionList(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request) throws Exception {
		if (!form.isSearchFilter()) {
			List<String> filterList = List.of("messPeriod", "fromDate", "toDate");
			filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
		}
		map.addAttribute("filters", form.getAdditionalParam());
		map.addAttribute("messPeriodMap", studentMessSelfAllotmentService.getMessPeriod());
		commonResponseUtil.getAdditionalParams(allParams, form);				
		commonResponseUtil.updateCommonModelAttributes(map, request, null, form);
		return HTMLPage.STUDENT_MESS_SELF_ALLOTMENT_REPORT;
	}
	
	@GetMapping("${url.excel.report.download}")
	public void downloadRequestReport(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map,
			HttpServletRequest request, HttpServletResponse response) {
		commonResponseUtil.getAdditionalParams(allParams, form);
		Page<StudentMessSelfAllotmentReportDto> studentMessSelfAllocationList = studentMessSelfAllotmentService.getStudentMessSelfAllocationList(form);
		try {
			Workbook workbook = studentMessSelfAllotmentService.getStudentMessSelfAllocationReport(studentMessSelfAllocationList.getContent());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			workbook.write(bos);
			workbook.close();
			byte[] excelBytes = bos.toByteArray();
			response.setContentType(FileUploadConstants.XLSX);
			String fileName = messageSource.getMessage("message.student.mess.self.allotment.list.filename", null, Locale.getDefault()) + "" + FileUploadConstants.XLSX_EXTENSION;
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
	
}
