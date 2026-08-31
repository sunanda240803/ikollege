package com.iitm.hosteldine.service.reports;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.reports.StudentMessSelfAllotmentReportDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.repository.mess.MessMasterControllerRepository;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.validator.common.ValidationCommon;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentMessSelfAllotmentService {
	
	private final MessMasterControllerRepository messMasterControllerRepository;
	private final MessageSource messageSource;
	private final Utility utility;
	
	public Map<Long, String> getMessPeriod(){
		List<Object[]> result = messMasterControllerRepository.getMessPeriod();
		Map<Long, String> messPeriodMap = new HashMap<>();
		for (Object[] row : result) {
		    messPeriodMap.put(((Number) row[1]).longValue(), (String) row[0]);
		}
		return messPeriodMap;
	}
	
	
	public Page<StudentMessSelfAllotmentReportDto> getStudentMessSelfAllocationList(PaginationForm form) {
		LocalDate fromDate = (form.getAdditionalParam().get("fromDate") != null && !form.getAdditionalParam().get("fromDate").equals("")) ? LocalDate.parse(form.getAdditionalParam().get("fromDate").toString()) : null;
		LocalDate toDate = (form.getAdditionalParam().get("toDate") != null && !form.getAdditionalParam().get("toDate").equals("")) ? LocalDate.parse(form.getAdditionalParam().get("toDate").toString()) : null;
		String id = (form.getAdditionalParam().get("messPeriod") != null && !form.getAdditionalParam().get("messPeriod").equals("")) ? form.getAdditionalParam().get("messPeriod").toString() : null;
		Pageable pageable = Pageable.unpaged();
		Page<Object[]> result = Page.empty();

		result = messMasterControllerRepository.getStudentMessSelfAllocationList(
				fromDate != null ? fromDate.toString() : null,
				toDate != null ? toDate.toString() : null,
				id != null ? Long.valueOf(id) : null,
				pageable);
		
		return setStudentMessSelfAllocationValues(result);
	}

	private Page<StudentMessSelfAllotmentReportDto> setStudentMessSelfAllocationValues(Page<Object[]> result){
		return result.map(record -> {
			StudentMessSelfAllotmentReportDto dto = new StudentMessSelfAllotmentReportDto();
			dto.setStudentId(record[0] != null ? utility.parseString(record[0]) : Strings.EMPTY);
			dto.setDiningFromDate(ValidationCommon.formatDateString(record[1], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
			dto.setDiningToDate(ValidationCommon.formatDateString(record[2], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
			dto.setStudentName(record[3] != null ? utility.parseString(record[3]) : Strings.EMPTY);
			dto.setMessName(record[4] != null ? utility.parseString(record[4]) : Strings.EMPTY);
			dto.setStatus(record[8] != null ? utility.parseString(record[8]) : Strings.EMPTY);			
			dto.setCreatedAt(ValidationCommon.formatTimestampDateString(record[9], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
            return dto;
		});
	}
	public Workbook getStudentMessSelfAllocationReport(List<StudentMessSelfAllotmentReportDto> studentMessSelfAllocationList) throws Exception {
		XSSFWorkbook workbook = null;
		int colCount = 0;
		ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

		try {
			workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(messageSource.getMessage("message.student.mess.self.allotment.report", null, Locale.getDefault()));

			// Create styles using ExcelUtility
			XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
			headerStyle.setWrapText(true);
			XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
			dataStyle.setWrapText(true);
			
			// Create First header row
			XSSFRow rowheadZero = sheet.createRow(1);
            excelUtility.createCell(rowheadZero, 0, messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));
			            
			// Create second header row
			XSSFRow rowheadFirst = sheet.createRow(2);
			excelUtility.createCell(rowheadFirst, 0, messageSource.getMessage("message.student.mess.self.allotment.report", null, Locale.getDefault()), headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 8));

			// Create third header row for report date
			XSSFRow rowheadSecond = sheet.createRow(3);
			SimpleDateFormat sdf = new SimpleDateFormat(messageSource.getMessage("session.date.format", null, Locale.getDefault()));
    		String reportDate = messageSource.getMessage("message.label.report.date.colon", null, Locale.getDefault()) + sdf.format(new Date());
            excelUtility.createCell(rowheadSecond, 0, reportDate, headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 8));

			// Create column headers
			XSSFRow rowhead = sheet.createRow(4);
			String[] headers = { messageSource.getMessage("message.label.date", null, Locale.getDefault()),
					messageSource.getMessage("message.label.report.student.id", null, Locale.getDefault()),
					messageSource.getMessage("message.label.student.name", null, Locale.getDefault()),
					messageSource.getMessage("message.label.dining.from.date", null, Locale.getDefault()),
					messageSource.getMessage("message.label.dining.to.date", null, Locale.getDefault()),
					messageSource.getMessage("message.label.mess.name", null, Locale.getDefault()),
					messageSource.getMessage("message.label.status", null, Locale.getDefault())

					 };

			// Add headers to the sheet
			for (String header : headers) {
				excelUtility.createCell(rowhead, colCount, header, headerStyle);
				sheet.setColumnWidth(colCount, 4000); // Set column width
				colCount++;
			}

			// Populate data rows
			int rowcount = 4;
			if (CollectionUtils.isNotEmpty(studentMessSelfAllocationList)) {
				for (StudentMessSelfAllotmentReportDto studentMessSelfAllotmentReport : studentMessSelfAllocationList) {
					rowcount++;
					XSSFRow row = sheet.createRow(rowcount);
					excelUtility.createCell(row, 0, studentMessSelfAllotmentReport.getCreatedAt(), dataStyle);
					excelUtility.createCell(row, 1, studentMessSelfAllotmentReport.getStudentId(), dataStyle);
					excelUtility.createCell(row, 2, studentMessSelfAllotmentReport.getStudentName(), dataStyle);
					excelUtility.createCell(row, 3, studentMessSelfAllotmentReport.getDiningFromDate(), dataStyle);
					excelUtility.createCell(row, 4, studentMessSelfAllotmentReport.getDiningToDate(), dataStyle);
					excelUtility.createCell(row, 5, studentMessSelfAllotmentReport.getMessName(), dataStyle);
					excelUtility.createCell(row, 6, studentMessSelfAllotmentReport.getStatus(), dataStyle);
				}
			}

			// Auto-size columns with a maximum width limit
			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
				// Cap the column width to 10000 (about 100 characters) to prevent extremely wide columns
				if (sheet.getColumnWidth(i) > 10000) {
					sheet.setColumnWidth(i, 10000);
				}
			}


		} catch (Exception exception) {
			exception.printStackTrace();
			throw new Exception("Error generating Mess Inspection List report", exception);
		}
		return workbook;
	}
}
