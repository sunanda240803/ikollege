package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.dto.dean.FacultyAccommodationRequestDto;
import com.iitm.hosteldine.dto.dean.MessInspectionReportDto;
import com.iitm.hosteldine.form.common.FacultyAccommodatiomRequstForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.repository.bulkappointment.StudentMasterBulkAppointmentRepository;
import com.iitm.hosteldine.repository.mess.FoodCourtLedgerRepository;
import com.iitm.hosteldine.repository.mess.MessMasterControllerRepository;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class FacultyAccommodationRequestService {

    private final MessMasterControllerRepository messMasterControllerRepository;
    private final MessMasterService messMasterService;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final FoodCourtLedgerRepository foodCourtLedgerRepository;
    private final MessageSource messageSource;
    private final ExcelUtility excelUtility;
    private final MessMasterCommonService messMasterCommonService;
    private final MessMasterRepository messMasterRepository;
    private final CommonResponseUtil commonResponseUtil;
    private final StudentMasterBulkAppointmentRepository studentMasterBulkAppointmentRepository;


    public Page<FacultyAccommodationRequestDto> getFacultyAccommodationList(PaginationForm form, FacultyAccommodatiomRequstForm searchForm) {
        // Handle null values with proper defaults

        int page = form.getPage() - 1;
        Pageable pageable = PageRequest.of(page, form.getSize());
        Page<MessInspectionReportDto> result = Page.empty();

        Page<Object[]> results = studentMasterBulkAppointmentRepository.getFacultyAccommodationRequests(
                searchForm.getEventFromDate(),
                searchForm.getEventToDate(),
                StringUtils.isEmpty(searchForm.getApprovalStatus()) ? null : searchForm.getApprovalStatus(),
                pageable);
        return results.map(this::mapToDto);
    }

    private FacultyAccommodationRequestDto mapToDto(Object[] row)  {
        FacultyAccommodationRequestDto facultyAccommodationRequestDto = null;
        try {
            facultyAccommodationRequestDto = new FacultyAccommodationRequestDto(
                    (Integer) row[0],                   // student_count
                    (String) row[1],                   // event_name
                    row[2] != null ? DateUtility.parseSqlDateToLocalDate((java.sql.Date) row[2]) : null, // from_date
                    row[3] != null ? DateUtility.parseSqlDateToLocalDate((java.sql.Date) row[3]) : null, // to_date
                    row[4] != null ? DateUtility.parseSqlDateToLocalDate((java.sql.Date) row[4]) : null,                   // stay_from
                    row[5] != null ? DateUtility.parseSqlDateToLocalDate((java.sql.Date) row[5]) : null,                   // stay_to
                    (Integer) row[6],                   // no_of_maleparticipants
                    (Integer) row[7],                   // no_of_femaleparticipants
                    (String) row[8],                   // approval_status
                    (String) row[9],                    // session_period
                    row[10] != null ? DateUtility.parseSqlDateToLocalDate((java.sql.Date) row[10]) : null, // created_at
                    (String) row[11],                  // dining
                    (Long) row[12],                 // breakfast_count
                    (Long) row[13],                 // lunch_count
                    (String) row[14],                 // created_by
                    ((Long) row[15]),    // bulk_appointment_id
                    (Long) row[16],                // dinner_count
                    (String) row[17]                 // faculty_name
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return facultyAccommodationRequestDto;
    }
    public Workbook getFacultyAccommodationReport(List<FacultyAccommodationRequestDto> content) throws Exception {
        XSSFWorkbook workbook = null;
        int colCount = 0;
        ExcelUtility excelUtility = new ExcelUtility();

        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(messageSource.getMessage("message.hostel.faculty.accommodation.report.filename", null, Locale.getDefault()));

            // Create styles
            XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
            headerStyle.setWrapText(true);
            XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
            dataStyle.setWrapText(true);

            // Create header rows
            XSSFRow rowheadZero = sheet.createRow(1);
            excelUtility.createCell(rowheadZero, 0, messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 16));

            XSSFRow rowheadFirst = sheet.createRow(2);
            excelUtility.createCell(rowheadFirst, 0, messageSource.getMessage("message.hostel.faculty.accommodation.report.filename", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 16));

            XSSFRow rowheadSecond = sheet.createRow(3);
            SimpleDateFormat sdf = new SimpleDateFormat(
    				messageSource.getMessage("session.date.format", null, Locale.getDefault()));
    		String reportDate = messageSource.getMessage("message.label.report.date.colon", null, Locale.getDefault())
    				+ sdf.format(new Date());
            excelUtility.createCell(rowheadSecond, 0, reportDate, headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 16));

            // Create column headers in specified order
            XSSFRow rowhead = sheet.createRow(5);
            String[] headers = {
                    messageSource.getMessage("message.label.sl.no", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.faculty.id", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.faculty.name", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.submitted.date", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.event.from.date", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.event.to.date", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.event.name", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.male.participants", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.female.participants", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.student.count", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.stay.from", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.stay.to", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.dining", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.session", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.breakfast.count", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.lunch.count", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.dinner.count", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.approval.status", null, Locale.getDefault())
            };

            // Add headers to the sheet
            for (String header : headers) {
                excelUtility.createCell(rowhead, colCount, header, headerStyle);
                sheet.setColumnWidth(colCount, 4000); // Set initial column width
                colCount++;
            }

            // Populate data rows in specified order
            int rowcount = 5;
            int slno = 1;
            if (CollectionUtils.isNotEmpty(content)) {
                for (FacultyAccommodationRequestDto dto : content) {
                    rowcount++;
                    XSSFRow row = sheet.createRow(rowcount);

                    // Calculate total participants
                    Integer totalParticipants = (dto.getNoOfMaleParticipants() != null ? dto.getNoOfMaleParticipants() : 0) +
                            (dto.getNoOfFemaleParticipants() != null ? dto.getNoOfFemaleParticipants() : 0);

                    excelUtility.createCell(row, 0, slno, dataStyle);
                    excelUtility.createCell(row, 1, dto.getCreatedBy(), dataStyle); // Faculty Id
                    excelUtility.createCell(row, 2, dto.getUploadedFacultyName(), dataStyle);
                    excelUtility.createCell(row, 3, DateUtility.formatDate(dto.getCreatedAt()), dataStyle);
                    excelUtility.createCell(row, 4, DateUtility.formatDate(dto.getFromDate()), dataStyle);
                    excelUtility.createCell(row, 5, DateUtility.formatDate(dto.getToDate()), dataStyle);
                    excelUtility.createCell(row, 6, dto.getEventName(), dataStyle);
                    excelUtility.createCell(row, 7, dto.getNoOfMaleParticipants() != null ? dto.getNoOfMaleParticipants().toString() : "0", dataStyle);
                    excelUtility.createCell(row, 8, dto.getNoOfFemaleParticipants() != null ? dto.getNoOfFemaleParticipants().toString() : "0", dataStyle);
                    excelUtility.createCell(row, 9, totalParticipants.toString(), dataStyle);
                    excelUtility.createCell(row, 10, dto.getStayFrom() != null ? DateUtility.formatDate(dto.getStayFrom()) : "", dataStyle);
                    excelUtility.createCell(row, 11, dto.getStayTo() != null ? DateUtility.formatDate(dto.getStayTo()) : "", dataStyle);
                    excelUtility.createCell(row, 12, dto.getDining(), dataStyle);
                    excelUtility.createCell(row, 13, dto.getSessionPeriod(), dataStyle);
                    excelUtility.createCell(row, 14, dto.getBreakfastCount() != null ? dto.getBreakfastCount().toString() : "", dataStyle);
                    excelUtility.createCell(row, 15, dto.getLunchCount() != null ? dto.getLunchCount().toString() : "", dataStyle);
                    excelUtility.createCell(row, 16, dto.getDinnerCount() != null ? dto.getDinnerCount().toString() : "", dataStyle);
                    excelUtility.createCell(row, 17, dto.getApprovalStatus(), dataStyle);

                    slno++;
                }
            }

            // Auto-size columns with maximum width limit
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                if (sheet.getColumnWidth(i) > 8000) {
                    sheet.setColumnWidth(i, 8000);
                } else if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000); // Minimum width
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Error generating faculty accommodation report", exception);
        }
        return workbook;
    }
}
