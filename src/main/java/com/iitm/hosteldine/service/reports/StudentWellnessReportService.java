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

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.student.wellness.StudentWellnessDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.repository.student.wellness.StudentWellnessFollowupDataRepository;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.Utility;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentWellnessReportService {
	
	private final MessageSource messageSource;
	private final Utility utility;
	private final StudentWellnessFollowupDataRepository studentWellnessFollowupDataRepository;

	public Page<StudentWellnessDto> getStudentWellnessList(PaginationForm form) {
		String studentId = (form.getAdditionalParam().get("studentId") != null && !form.getAdditionalParam().get("studentId").equals("")) ? form.getAdditionalParam().get("studentId").toString()
				: null;
		String visitFromDate = (form.getAdditionalParam().get("visitFromDate") != null && !form.getAdditionalParam().get("visitFromDate").equals(""))
				? form.getAdditionalParam().get("visitFromDate").toString()
				: null;
		String visitToDate = (form.getAdditionalParam().get("visitToDate") != null && !form.getAdditionalParam().get("visitToDate").equals(""))
				? form.getAdditionalParam().get("visitToDate").toString()
				: null;
		String followUpFromDate = (form.getAdditionalParam().get("followUpFromDate") != null && !form.getAdditionalParam().get("followUpFromDate").equals(""))
				? form.getAdditionalParam().get("followUpFromDate").toString()
				: null;
		String followUpToDate = (form.getAdditionalParam().get("followUpToDate") != null && !form.getAdditionalParam().get("followUpToDate").equals(""))
				? form.getAdditionalParam().get("followUpToDate").toString()
				: null;
		
		String userName = SecurityCtxUtil.userName();
		userName = "wellness.coordinator"; //TODO
		Page<Object[]> result = studentWellnessFollowupDataRepository.getStudentWelnessList(studentId, visitFromDate, visitToDate, followUpFromDate, followUpToDate, null, null, userName , Pageable.unpaged());

		return result.map(objects -> {
			return setStudentWellnessList(objects);
		});
	}

	private StudentWellnessDto setStudentWellnessList(Object[] objects) {
		StudentWellnessDto dto = new StudentWellnessDto();
		dto.setStudentId(utility.parseString(objects[1]));
		dto.setStudentName(utility.parseString(objects[2]));
		dto.setHostelName(utility.parseString(objects[3]));
		dto.setRoomNumber(utility.parseString(objects[4]));
		dto.setContactNumber(utility.parseString(objects[5]));
		dto.setStudentEmail(utility.parseString(objects[6]));
		dto.setReferralType(utility.parseString(objects[8]));
		dto.setConcernType(utility.parseString(objects[9]));
		dto.setCoordinatorName(utility.parseString(objects[10]));
		dto.setNumberOfVisit(utility.parseInt(objects[12]));		
		dto.setVisitDate(objects[13] != null ? (utility.convertToLocalDate(objects[13]).format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT))) : null);		
		dto.setInterationMode(utility.parseString(objects[14]));
		dto.setConcernsDiscussed(utility.parseString(objects[17]));
		dto.setFutureActionPlan(utility.parseString(objects[18]));
		dto.setFollowUpDate(objects[19] != null ? (utility.convertToLocalDate(objects[19]).format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT))) : null);		
		dto.setStatus(utility.parseString(objects[20]));
		return dto;
	}
	
	public Workbook getStudentWellnessReport(List<StudentWellnessDto> studentWellnessDtoReportDtoList) throws Exception {
		XSSFWorkbook workbook = null;
		int colCount = 0;
		ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

		try {
			workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(messageSource.getMessage("message.student.wellness.report", null, Locale.getDefault()));

			// Create styles using ExcelUtility
			XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
			headerStyle.setWrapText(true);
			XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
			dataStyle.setWrapText(true);
			
			// Create First header row
			XSSFRow rowheadZero = sheet.createRow(1);
            excelUtility.createCell(rowheadZero, 0, messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 12));
			            
			// Create second header row
			XSSFRow rowheadFirst = sheet.createRow(2);
			excelUtility.createCell(rowheadFirst, 0, messageSource.getMessage("message.student.wellness.report", null, Locale.getDefault()), headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 12));

			// Create third header row for report date
			XSSFRow rowheadSecond = sheet.createRow(3);
			DateFormat dateFormat = new SimpleDateFormat(Constants.BACKEND_DATETIME_FORMAT_2);
			Date date = new Date();
			excelUtility.createCell(rowheadSecond, 0, messageSource.getMessage("message.label.report.date", null, Locale.getDefault()) + " "+ dateFormat.format(date), headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 12));

			// Create column headers
			XSSFRow rowhead = sheet.createRow(4);
			String[] headers = {
					messageSource.getMessage("message.label.report.student.id", null, Locale.getDefault()),
					messageSource.getMessage("message.label.student.name", null, Locale.getDefault()),
					messageSource.getMessage("message.label.hostel.name", null, Locale.getDefault()),
					messageSource.getMessage("message.label.room.number", null, Locale.getDefault()),
					messageSource.getMessage("message.label.contact.number", null, Locale.getDefault()),
					messageSource.getMessage("message.label.student.mail", null, Locale.getDefault()),
					messageSource.getMessage("message.label.visit.date", null, Locale.getDefault()),
					messageSource.getMessage("message.label.no.of.visit", null, Locale.getDefault()),
					messageSource.getMessage("message.label.interaction.mode", null, Locale.getDefault()),
					messageSource.getMessage("message.label.referral.type", null, Locale.getDefault()),
					messageSource.getMessage("message.label.concern.type", null, Locale.getDefault()),
					messageSource.getMessage("message.label.coordinator.name", null, Locale.getDefault()),
					messageSource.getMessage("message.label.concern.discussed", null, Locale.getDefault()),
					messageSource.getMessage("message.label.follow.up.date", null, Locale.getDefault()),
					messageSource.getMessage("message.label.future.action.plan", null, Locale.getDefault()),
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
			if (CollectionUtils.isNotEmpty(studentWellnessDtoReportDtoList)) {
				for (StudentWellnessDto dto : studentWellnessDtoReportDtoList) {
					rowcount++;
					XSSFRow row = sheet.createRow(rowcount);
					excelUtility.createCell(row, 0, dto.getStudentId(), dataStyle);
					excelUtility.createCell(row, 1, dto.getStudentName(), dataStyle);
					excelUtility.createCell(row, 2, dto.getHostelName(), dataStyle);
					excelUtility.createCell(row, 3, dto.getRoomNumber(), dataStyle);
					excelUtility.createCell(row, 4, dto.getContactNumber(), dataStyle);
					excelUtility.createCell(row, 5, dto.getStudentEmail(), dataStyle);
					excelUtility.createCell(row, 6, dto.getVisitDate(), dataStyle);
					excelUtility.createCell(row, 7, dto.getNumberOfVisit(), dataStyle);
					excelUtility.createCell(row, 8, decrypt(dto.getInterationMode()), dataStyle);
					excelUtility.createCell(row, 9, decrypt(dto.getReferralType()), dataStyle);
					excelUtility.createCell(row, 10, decrypt(dto.getConcernType()), dataStyle);
					excelUtility.createCell(row, 11, decrypt(dto.getCoordinatorName()), dataStyle);
					excelUtility.createCell(row, 12, decrypt(dto.getConcernsDiscussed()), dataStyle);
					excelUtility.createCell(row, 13, dto.getFollowUpDate(), dataStyle);
					excelUtility.createCell(row, 14, decrypt(dto.getFutureActionPlan()), dataStyle);
					excelUtility.createCell(row, 15, decrypt(dto.getStatus()), dataStyle);
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
			throw new Exception("Error generating Student Wellness report", exception);
		}
		return workbook;
	}
	
	private String decrypt(String value) throws Exception {
        return (value != null) ? MCrypt.getInstance().decryptToString(value) : null;
    }
	
}
