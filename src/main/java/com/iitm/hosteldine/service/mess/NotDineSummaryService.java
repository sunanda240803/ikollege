package com.iitm.hosteldine.service.mess;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mess.StudentNotDinedDto;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotDineSummaryService {

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



    public Workbook getNotDineSummaryData(PaginationForm form, MessDineSummaryForm searchForm) throws Exception {
        // Handle null values with proper defaults
        String userRole = SecurityCtxUtil.userName();
        Long messPeriodId = searchForm.getMessPeriodId() != null ? searchForm.getMessPeriodId().longValue() : null;
        Long messNameId = searchForm.getMessId() != null ? searchForm.getMessId().longValue() : 0;

        // Add validation if needed
        if (messPeriodId == null) {
            throw new IllegalArgumentException(messageSource.getMessage("message.validation.mess.period.required", null, Locale.getDefault()));
        }

        Optional<MessMasterControllerEntity> messPeriod = messMasterControllerRepository.findByIdAndActiveFlag(messPeriodId, ModelConstants.STATUS_ACTIVE);

        // Parse the raw data
        List<Object[]> notDineStudents = messMasterRepository.findStudentsNotDinedRaw(messPeriod.get().getDiningFromDate(), messPeriod.get().getDiningToDate(),
                messNameId);
        List<StudentNotDinedDto> studentNotDinedDtos = notDineStudents.stream()
                .map(row -> {
                    try {
                        return new StudentNotDinedDto(
                                ((Number) row[0]).intValue(),  // serialNumber
                                (String) row[1],               // studentId
                                (String) row[2],              // studentName
                                (String) row[3],               // messName
                                DateUtility.parseSqlDateToLocalDate(((java.sql.Date) row[4])), // fromDate
                                DateUtility.parseSqlDateToLocalDate(((java.sql.Date) row[5]))  // toDate
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());


        // Generate the report
        return generateReport(studentNotDinedDtos);
    }

    public Workbook generateReport(List<StudentNotDinedDto> students) throws Exception {
        XSSFWorkbook workbook = null;
        int colCount = 0;
        ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(messageSource.getMessage("message.not.dine.students.report.filename", null, Locale.getDefault()));

            // Create styles using ExcelUtility
            XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
            headerStyle.setWrapText(true);
            XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
            dataStyle.setWrapText(true);

            // Create second header row
            XSSFRow rowheadZero = sheet.createRow(1);
            excelUtility.createCell(rowheadZero, 0, messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

            // Create second header row
            XSSFRow rowheadFirst = sheet.createRow(2);
            excelUtility.createCell(rowheadFirst, 0, messageSource.getMessage("message.not.dine.students.report.filename", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

            // Create third header row for report date
            XSSFRow rowheadSecond = sheet.createRow(3);
            DateFormat dateFormat = new SimpleDateFormat(Constants.BACKEND_DATETIME_FORMAT_2);
            Date date = new Date();
            excelUtility.createCell(rowheadSecond, 0, messageSource.getMessage("message.label.report.date", null, Locale.getDefault()) + DateUtility.formatDate(date), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 6));

            XSSFRow rowheadThird = sheet.createRow(4);
            // Create column headers
            XSSFRow rowhead = sheet.createRow(5);
            String[] headers = { messageSource.getMessage("message.label.mess.rebate.sl.no", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.studentID", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.student.name", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.mess.name", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.dining.from.date", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.dining.to.date", null, Locale.getDefault()),
            };

            // Add headers to the sheet
            for (String header : headers) {
                excelUtility.createCell(rowhead, colCount, header, headerStyle);
                sheet.setColumnWidth(colCount, 8000); // Set column width
                colCount++;
            }

            // Populate data rows
            int rowcount = 5;int slno = 1;
            if (CollectionUtils.isNotEmpty(students)) {
                for (StudentNotDinedDto student : students) {
                    rowcount++;
                    XSSFRow row = sheet.createRow(rowcount);
                    excelUtility.createCell(row, 0, slno, dataStyle);
                    excelUtility.createCell(row, 1, student.getRollNo(), dataStyle);
                    excelUtility.createCell(row, 2, student.getStudentName(), dataStyle);
                    excelUtility.createCell(row, 3, student.getMessName(), dataStyle);
                    excelUtility.createCell(row, 4, DateUtility.formatDate(student.getDiningFromDate()), dataStyle);
                    excelUtility.createCell(row, 5, DateUtility.formatDate(student.getDiningToDate()), dataStyle);
                    slno++;
                }
            }

            // Auto-size columns with a maximum width limit
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // Cap the column width to 10000 (about 100 characters) to prevent extremely wide columns
                if (sheet.getColumnWidth(i) > 15000) {
                    sheet.setColumnWidth(i, 15000);
                }
            }


        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception(messageSource.getMessage("message.label.not.dine.students.report.error", null, Locale.getDefault()), exception);
        }
        return workbook;
    }

}


