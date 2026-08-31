package com.iitm.hosteldine.service.mess;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.dean.MessInspectionReportDto;
import com.iitm.hosteldine.dto.mess.StudentAttendance;
import com.iitm.hosteldine.form.common.MessDineSummaryForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.mess.MessMasterControllerEntity;
import com.iitm.hosteldine.repository.mess.CurrentMessDetailsViewRepository;
import com.iitm.hosteldine.repository.mess.FoodCourtLedgerRepository;
import com.iitm.hosteldine.repository.mess.MessMasterControllerRepository;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.student.StudentWithRemarksService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MessDineSummaryService {

    private final MessMasterControllerRepository messMasterControllerRepository;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final FoodCourtLedgerRepository foodCourtLedgerRepository;
    private final MessageSource messageSource;
    private final ExcelUtility excelUtility;
    private final StudentWithRemarksService studentWithRemarksService;
    private final MessMasterCommonService messMasterCommonService;
    private final MessMasterRepository messMasterRepository;
    private final CurrentMessDetailsViewRepository currentMessDetailsViewRepository;
    private final CommonResponseUtil commonResponseUtil;


    public Workbook getMessDineSummaryData(PaginationForm form, MessDineSummaryForm searchForm) throws Exception {
        // Handle null values with proper defaults
        String userRole = SecurityCtxUtil.userName();
        Long messPeriodId = searchForm.getMessPeriodId() != null ? searchForm.getMessPeriodId().longValue() : null;
        Long messNameId = searchForm.getMessId() != null ? searchForm.getMessId().longValue() : 0;

        int page = form.getPage() - 1;
        Pageable pageable = PageRequest.of(page, form.getSize());
        Page<MessInspectionReportDto> result = Page.empty();


        // Add validation if needed
        if (messPeriodId == null) {
            throw new IllegalArgumentException("Mess period is required");
        }

        // Parse the raw data
        List<Object[]> messAttendanceDataNative = messMasterRepository.findMessAttendanceDataNative(messPeriodId, messNameId);
        List<StudentAttendance> studentAttendances = parseData(messAttendanceDataNative);

        // Generate the report

        Optional<MessMasterControllerEntity> messPeriod = messMasterControllerRepository.findByIdAndActiveFlag(messPeriodId, ModelConstants.STATUS_ACTIVE);
        // Convert LocalDate to Date for compatibility with existing code
        Date start = Date.from(messPeriod.get().getDiningFromDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = (Date) Date.from(messPeriod.get().getDiningToDate().atStartOfDay(ZoneId.systemDefault()).toInstant());

        return generateReport(studentAttendances,start, end);
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy")  ;


    public static List<StudentAttendance> parseData(List<Object[]> rawData) throws Exception {
        Map<String, StudentAttendance> studentMap = new HashMap<>();


        for (Object[] row : rawData) {
            String studentName = (String) row[0];
            String studentId = (String) row[1];
            String messHead = (String) row[2];
            String messName = (String) row[3];
            int dinedDays = Math.toIntExact((Long) row[4]);
            java.sql.Date date;
            if (row[5] instanceof java.sql.Date) {
                date = new java.sql.Date(((java.sql.Date) row[5]).getTime());
            } else {
                date = new java.sql.Date(dateFormat.parse(row[5].toString()).getTime());
            }
            int breakfastCount = Math.toIntExact((Long) row[6]);
            int lunchCount = Math.toIntExact((Long) row[7]);
            int dinnerCount = Math.toIntExact((Long) row[8]);

            StudentAttendance student = studentMap.computeIfAbsent(studentId, k -> {
                StudentAttendance s = new StudentAttendance();
                s.setStudentId(studentId);
                s.setStudentName(studentName);
                s.setDinedDays(dinedDays);
                s.setMessHead(messHead);
                s.setMessName(messName);
                return s;
            });

            // Update dinedDays if the current row has a higher value
            if (dinedDays > student.getDinedDays()) {
                student.setDinedDays(dinedDays);
            }

            // Add or update day attendance
            student.getAttendanceMap().put(date, new StudentAttendance.DayAttendance(
                    breakfastCount, lunchCount, dinnerCount));
        }

        return new ArrayList<>(studentMap.values());
    }

    private static final SimpleDateFormat headerDateFormat = new SimpleDateFormat(Constants.FRONTEND_DATE_FORMAT);

    public Workbook generateReport(List<StudentAttendance> studentAttendances,
                                   Date startDate,
                                   Date endDate) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet(messageSource.getMessage("message.mess.dine.summary.report.filename", null, Locale.getDefault()));

            // Create styles
            CellStyle mainHeaderStyle = createMainHeaderStyle(workbook);
            CellStyle dateHeaderStyle = createDateHeaderStyle(workbook);
            CellStyle subHeaderStyle = createSubHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle emptyDataStyle = createEmptyDataStyle(workbook);
            CellStyle periodHeaderStyle = createPeriodHeaderStyle(workbook);

            // Generate all dates in the range
            List<Date> dateRange = getDatesBetween(startDate, endDate);

            // Create header rows
            Row periodHeaderRow = sheet.createRow(0);
            periodHeaderRow.setHeightInPoints(20); // Reduced row height

            Row mainHeaderRow = sheet.createRow(1);
            Row dateHeaderRow = sheet.createRow(2);
            Row subHeaderRow = sheet.createRow(3);

            // Create Mess Period header
            String periodHeader =messageSource.getMessage("message.mess.period", null, Locale.getDefault()) + "( " + headerDateFormat.format(startDate) + " to " + headerDateFormat.format(endDate) + ")";
            Cell periodCell = periodHeaderRow.createCell(0);
            periodCell.setCellValue(periodHeader);
            periodCell.setCellStyle(periodHeaderStyle);

            // Merge only the visible columns (not entire row)
            int totalColumns = 3 + (dateRange.size() * 3);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, totalColumns - 1));

            int colNum = 0;

            // Fixed headers (main header row)
            createHeaderCell(mainHeaderRow, colNum, commonResponseUtil.getMessage("message.label.student.id"), mainHeaderStyle, 4000);
            sheet.addMergedRegion(new CellRangeAddress(1, 3, colNum, colNum));
            colNum++;

            createHeaderCell(mainHeaderRow, colNum, commonResponseUtil.getMessage("message.label.studentName"), mainHeaderStyle, 6000);
            sheet.addMergedRegion(new CellRangeAddress(1, 3, colNum, colNum));
            colNum++;

            createHeaderCell(mainHeaderRow, colNum, commonResponseUtil.getMessage("message.label.mess.dined.days"), mainHeaderStyle, 3000);
            sheet.addMergedRegion(new CellRangeAddress(1, 3, colNum, colNum));
            colNum++;

            // Date headers with sub-columns
            for (Date date : dateRange) {
                String dateStr = headerDateFormat.format(date);
                // Create merged date cell spanning 3 columns (BR, LC, DR)
                createHeaderCell(dateHeaderRow, colNum, dateStr, dateHeaderStyle, 3000);
                sheet.addMergedRegion(new CellRangeAddress(2, 2, colNum, colNum+2));

                // Create sub-headers with double width (4000)
                createHeaderCell(subHeaderRow, colNum, commonResponseUtil.getMessage("message.label.mess.br"), subHeaderStyle, 2000);
                createHeaderCell(subHeaderRow, colNum+1, commonResponseUtil.getMessage("message.label.mess.lc"), subHeaderStyle, 2000);
                createHeaderCell(subHeaderRow, colNum+2, commonResponseUtil.getMessage("message.label.mess.dr"), subHeaderStyle, 2000);

                // Explicitly set column widths
                sheet.setColumnWidth(colNum, 2000);
                sheet.setColumnWidth(colNum+1, 2000);
                sheet.setColumnWidth(colNum+2, 2000);

                colNum += 3;
            }

            // Fill data rows
            int rowNum = 4;
            for (StudentAttendance student : studentAttendances) {
                Row row = sheet.createRow(rowNum++);
                colNum = 0;

                // Student info
                createDataCell(row, colNum++, student.getStudentId(), dataStyle);
                createDataCell(row, colNum++, student.getStudentName(), dataStyle);
                createDataCell(row, colNum++, student.getDinedDays(), dataStyle);

                // Attendance data
                for (Date date : dateRange) {
                    StudentAttendance.DayAttendance attendance = student.getAttendanceMap().get(date);
                    if (attendance != null) {
                        createDataCell(row, colNum++, attendance.getBreakfastCount(), dataStyle);
                        createDataCell(row, colNum++, attendance.getLunchCount(), dataStyle);
                        createDataCell(row, colNum++, attendance.getDinnerCount(), dataStyle);
                    } else {
                        createDataCell(row, colNum++, "-", emptyDataStyle);
                        createDataCell(row, colNum++, "-", emptyDataStyle);
                        createDataCell(row, colNum++, "-", emptyDataStyle);
                    }
                }
            }

            // Set freeze pane (after 4 header rows)
            sheet.createFreezePane(0, 4);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return workbook;
    }

    // Updated period header style with smaller font
    private static CellStyle createPeriodHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short)12); // Reduced from 14 to 12
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        return style;
    }
    // Helper method to get all dates in range
    private static List<Date> getDatesBetween(Date startDate, Date endDate) {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (!calendar.getTime().after(endDate)) {
            dates.add((Date) calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

    // Style for empty cells
    private static CellStyle createEmptyDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFont(font);
        return style;
    }

    private static CellStyle createMainHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short)12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        return style;
    }

    private static CellStyle createDateHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short)11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static CellStyle createSubHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short)10);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static void createHeaderCell(Row row, int colNum, String value, CellStyle style, int width) {
        Cell cell = row.createCell(colNum);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        row.getSheet().setColumnWidth(colNum, width);
    }

    private static void createDataCell(Row row, int colNum, String value, CellStyle style) {
        Cell cell = row.createCell(colNum);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private static void createDataCell(Row row, int colNum, int value, CellStyle style) {
        Cell cell = row.createCell(colNum);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}


