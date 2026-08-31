package com.iitm.hosteldine.service.reports;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections4.CollectionUtils;
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
import com.iitm.hosteldine.dto.reports.StudentWiseLogReportDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.repository.hostel.HostelBiometricTerminalRepository;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.Utility;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentWiseLogService {

	private final MessageSource messageSource;
	private final HostelBiometricTerminalRepository hostelBiometricTerminalRepository;
	private final Utility utility;

	public Page<StudentWiseLogReportDto> getStudentWiseLogDetailsList(PaginationForm form) {
		String studentId = (form.getAdditionalParam().get("studentId") != null && !form.getAdditionalParam().get("studentId").equals("")) ? form.getAdditionalParam().get("studentId").toString()
				: null;
		String fromDate = (form.getAdditionalParam().get("fromDate") != null && !form.getAdditionalParam().get("fromDate").equals(""))
				? form.getAdditionalParam().get("fromDate").toString()
				: null;
		String toDate = (form.getAdditionalParam().get("toDate") != null && !form.getAdditionalParam().get("toDate").equals(""))
				? form.getAdditionalParam().get("toDate").toString()
				: null;

		Page<Object[]> result = hostelBiometricTerminalRepository.getStudentWiseLogDetailsList(studentId, fromDate, toDate, Pageable.unpaged());

		return result.map(objects -> {
			return setStudentWiseLogDetailsList(objects);
		});
	}

	private StudentWiseLogReportDto setStudentWiseLogDetailsList(Object[] objects) {
		StudentWiseLogReportDto dto = new StudentWiseLogReportDto();
		dto.setId(utility.parseLong(objects[0]));
		dto.setStudentId(utility.parseString(objects[2]));
		dto.setStudentName(utility.parseString(objects[26]));
		dto.setTerminal_location(utility.parseString(objects[12]));
		dto.setRefId(utility.parseString(objects[7]));
		dto.setSwipeDay(utility.parseString(objects[28]));
		dto.setSwipeDate(objects[6] != null ? (utility.convertToLocalDate(objects[6]).format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT))) : null);
		dto.setSwipeTime(utility.parseString(objects[5]));
		return dto;
	}

	public Workbook getStudentWiseLogReport(List<StudentWiseLogReportDto> studentWiseLogReportDtoList) throws Exception {
		XSSFWorkbook workbook = null;
		int colCount = 0;
		ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

		try {
			workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(messageSource.getMessage("message.student.wise.log.report", null, Locale.getDefault()));

			// Create styles using ExcelUtility
			XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
			headerStyle.setWrapText(true);
			XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
			dataStyle.setWrapText(true);
			
			// Create First header row
			XSSFRow rowheadZero = sheet.createRow(1);
            excelUtility.createCell(rowheadZero, 0, messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));
			            
			// Create second header row
			XSSFRow rowheadFirst = sheet.createRow(2);
			excelUtility.createCell(rowheadFirst, 0, messageSource.getMessage("message.student.wise.log.report", null, Locale.getDefault()), headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 7));

			// Create third header row for report date
			XSSFRow rowheadSecond = sheet.createRow(3);
			DateFormat dateFormat = new SimpleDateFormat(Constants.BACKEND_DATETIME_FORMAT_2);
			Date date = new Date();
			excelUtility.createCell(rowheadSecond, 0, messageSource.getMessage("message.label.report.date", null, Locale.getDefault()) + " "+ dateFormat.format(date), headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 7));

			// Create column headers
			XSSFRow rowhead = sheet.createRow(4);
			String[] headers = { messageSource.getMessage("message.label.mess.rebate.sl.no", null, Locale.getDefault()),
					messageSource.getMessage("message.label.report.student.id", null, Locale.getDefault()),
					messageSource.getMessage("message.label.student.name", null, Locale.getDefault()),
					messageSource.getMessage("message.label.hostel.name", null, Locale.getDefault()),
					messageSource.getMessage("message.label.swipe.day", null, Locale.getDefault()),
					messageSource.getMessage("message.label.swipe.date", null, Locale.getDefault()),
					messageSource.getMessage("message.label.swipe.time", null, Locale.getDefault())

			};

			// Add headers to the sheet
			for (String header : headers) {
				excelUtility.createCell(rowhead, colCount, header, headerStyle);
				sheet.setColumnWidth(colCount, 4000); // Set column width
				colCount++;
			}

			// Populate data rows
			int rowcount = 4;
			int count = 0;
			if (CollectionUtils.isNotEmpty(studentWiseLogReportDtoList)) {
				for (StudentWiseLogReportDto dto : studentWiseLogReportDtoList) {
					rowcount++;
					count++;
					XSSFRow row = sheet.createRow(rowcount);
					excelUtility.createCell(row, 0, count, dataStyle);
					excelUtility.createCell(row, 1, dto.getStudentId(), dataStyle);
					excelUtility.createCell(row, 2, dto.getStudentName(), dataStyle);
					excelUtility.createCell(row, 3, dto.getTerminal_location(), dataStyle);
					excelUtility.createCell(row, 4, dto.getSwipeDay(), dataStyle);
					excelUtility.createCell(row, 5, dto.getSwipeDate(), dataStyle);
					excelUtility.createCell(row, 6, dto.getSwipeTime(), dataStyle);
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
