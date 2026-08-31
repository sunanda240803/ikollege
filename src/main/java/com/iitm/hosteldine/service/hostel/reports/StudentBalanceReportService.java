package com.iitm.hosteldine.service.hostel.reports;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.dto.reports.StudentBalanceReportDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.validator.common.ValidationCommon;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentBalanceReportService {

    private final MessageSource messageSource;
    private final CommonResponseUtil commonResponseUtil;
    private final ExcelUtility excelUtility;
	private final Utility utility;

    private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;

    public Page<StudentBalanceReportDto> generateStudentBalanceReport(Map<String, String> allParams,
            PaginationForm form, boolean needSize) {
        String hostelIdStr = form.getAdditionalParam().get("hostelId").toString();
        String studentBalance = form.getAdditionalParam().get("studentBalance").toString();
        Long hostelId = null;
        int size = needSize ? Math.max(form.getSize(), 10) : Integer.MAX_VALUE;

        int page = Math.max((form.getPage() - 1), 0);
        PageRequest pageRequest = PageRequest.of(page, size);

        try {
            if (hostelIdStr != null && !hostelIdStr.isEmpty() && !"all".equals(hostelIdStr)) {
                try {
                    hostelId = Long.valueOf(hostelIdStr);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid search parameter: " + hostelIdStr);
                }
            }
            Page<Object[]> resultPage = allStudentsDetailsViewRepository.getStudentDetailsViewReport(hostelId,
                    studentBalance, pageRequest);

            return resultPage.map(this::createStudentBalanceReportDto);
        } catch (Exception e) {
            System.err.println("Error in generateStudentBalanceReport: " + e);
            return Page.empty(pageRequest);
        }
    }

    public StudentBalanceReportDto createStudentBalanceReportDto(Object[] row) {
        String studentId = Objects.nonNull(row[0]) ? (String) row[0] : "";
        String studentName = Objects.nonNull(row[2]) ? (String) row[2] : "";
        String hostelName = Objects.nonNull(row[3]) ? (String) row[3] : "";
        String roomNumber = Objects.nonNull(row[4]) ? (String) row[4] : "";
        BigDecimal netBal = Objects.nonNull(row[1]) ? (BigDecimal) row[1] : BigDecimal.ZERO;
        return new StudentBalanceReportDto(studentId, studentName, hostelName, roomNumber, netBal);
    }

    public Workbook generateExcelStudentBalanceReport(java.util.List<StudentBalanceReportDto> reportList) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(ExcelConstants.STUDENT_BALANCE_REPORT_SHEET_NAME);

        // Define constants
        int columnCount = ExcelConstants.STUDENT_BALANCE_REPORT_HEADER_DATA.length;
        String[] headerData = ExcelConstants.STUDENT_BALANCE_REPORT_HEADER_DATA;
        String[] headerDataWidth = ExcelConstants.STUDENT_BALANCE_REPORT_HEADER_DATA_WIDTH;

        // Create styles
        XSSFCellStyle style3 = excelUtility.setDataStyle(workbook);
        XSSFCellStyle centerAlignStyle = excelUtility.setCenterAlignStyle(workbook);

        // Row 0: Title row - "Office of the Hostel Management - IITMADRAS CAMPUS"
        Row row0 = sheet.createRow(0);
        Cell cell0 = row0.createCell(0);
        cell0.setCellValue(messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null,
                Locale.getDefault()));
        cell0.setCellStyle(centerAlignStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));

        // Row 1: Report title
        Row row1 = sheet.createRow(1);
        Cell cell1 = row1.createCell(0);
        cell1.setCellValue(messageSource.getMessage("message.label.student.balance.report", null, Locale.getDefault()));
        cell1.setCellStyle(centerAlignStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, columnCount - 1));

        // Row 2: Report date
        Row row2 = sheet.createRow(2);
        SimpleDateFormat sdf = new SimpleDateFormat(
                messageSource.getMessage("session.date.format", null, Locale.getDefault()));
        String reportDate = messageSource.getMessage("message.label.report.date.colon", null, Locale.getDefault())
                + sdf.format(new Date());
        Cell cell2 = row2.createCell(0);
        cell2.setCellValue(reportDate);
        cell2.setCellStyle(centerAlignStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, columnCount - 1));

        // Row 3: Header row
        Row row3 = sheet.createRow(3);
        excelUtility.createHeader(row3, 0, headerData, workbook);

        // Set column widths
        IntStream.range(0, headerData.length).forEach(i -> {
            int width = Integer.parseInt(headerDataWidth[i]);
            sheet.setColumnWidth(i, width);
        });

        // If list has items, print first item as sample
        if (!reportList.isEmpty()) {
            System.out.println("Sample first item: " + reportList.get(0));
        }

        int rowNum = 4;
        for (StudentBalanceReportDto report : reportList) {
            Row row = sheet.createRow(rowNum++);
            String[] values = {report.getStudentId(), report.getStudentName(), report.getHostelName(),
                report.getRoomNumber(), setNetBalance(report), setCrDr(report)};

            for (int colIdx = 0; colIdx < values.length; colIdx++) {
                excelUtility.createAndSetColumn(sheet, row, colIdx, -1, values[colIdx], style3);
            }
        }

        // Auto-size columns
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }

    private String setCrDr(StudentBalanceReportDto report) {
        return ModelConstants.SPACE + (report.getNetBal().compareTo(BigDecimal.ZERO) >= 0
                ? commonResponseUtil.getMessage("message.label.dr.no.bracket")
                : commonResponseUtil.getMessage("message.label.cr.no.bracket"));
    }

    private String setNetBalance(StudentBalanceReportDto report) {
        BigDecimal netBal = report.getNetBal();
        BigDecimal value = netBal.compareTo(BigDecimal.ZERO) >= 0
                ? netBal
                : netBal.negate();
        return utility.formatCommaSeperatedCurrency(value.doubleValue());
    }

}
