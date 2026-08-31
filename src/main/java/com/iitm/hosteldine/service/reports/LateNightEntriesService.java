package com.iitm.hosteldine.service.reports;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.repository.hostel.HostelBiometricTerminalRepository;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LateNightEntriesService {

    private final HostelBiometricTerminalRepository hostelBiometricTerminalRepository;
    private final Utility utility;
    private final CommonResponseUtil commonResponseUtil;

    public Page<LateNightEntryRecord> getLateNightEntries(PaginationForm form) {
        String hostelName = (form.getAdditionalParam().get("hostelName") != null && !form.getAdditionalParam().get("hostelName").equals("")) ?
                form.getAdditionalParam().get("hostelName").toString() : null;
        String timing = (form.getAdditionalParam().get("timing") != null && !form.getAdditionalParam().get("timing").equals(""))
                ? form.getAdditionalParam().get("timing").toString()
                : null;
        if (Objects.nonNull(timing) && Objects.nonNull(hostelName)) {
            var pageRequest = PageRequest.of(form.getPage() - 1, form.getSize());
            Long fromTime = Long.parseLong(timing.split("to")[0]);
            Long toTime = Long.parseLong(timing.split("to")[1]);
            if ("-1".equals(hostelName)) {
                return hostelBiometricTerminalRepository.getLateEntriesListOnlyByTime(fromTime, toTime, pageRequest)
                        .map(this::mapToLateNightEntryRecord);
            } else {
                return hostelBiometricTerminalRepository.getLateEntriesListByTerminalIdAndTime(Long.parseLong(hostelName), fromTime, toTime, pageRequest)
                        .map(this::mapToLateNightEntryRecord);
            }

        } else {
            return null;
        }
    }

    private LateNightEntryRecord mapToLateNightEntryRecord(Object[] o) {
        return new LateNightEntryRecord(
                Objects.nonNull(o[17]) ? String.valueOf(o[17]) : Strings.EMPTY,
                Objects.nonNull(o[29]) ? String.valueOf(o[29]) : Strings.EMPTY,
                Objects.nonNull(o[1]) ? String.valueOf(o[1]) : Strings.EMPTY,
                Objects.nonNull(o[o.length - 1]) ? String.valueOf(o[o.length - 1]) : Strings.EMPTY,
                Objects.nonNull(o[21]) ? utility.dateFormatter(utility.convertToLocalDate(o[21])) : null,
                Objects.nonNull(o[20]) ? String.valueOf(o[20]) : Strings.EMPTY
        );
    }

    public Workbook generateExcelReport(List<LateNightEntryRecord> lateNightEntryRecords, String report) throws Exception {
        XSSFWorkbook workbook;
        int colCount = 0;
        ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

        String[] headerList = ModelConstants.LATE_NIGHT_ENTRIES_HEADER;

        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(commonResponseUtil.getMessage(report));

            // Create styles using ExcelUtility
            XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
            headerStyle.setWrapText(true);
            XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
            dataStyle.setWrapText(true);

            XSSFCellStyle headerStyle2 = excelUtility.setHeaderStyle(workbook);
            headerStyle2.setWrapText(true);
            headerStyle2.setAlignment(HorizontalAlignment.LEFT);
            headerStyle2.setVerticalAlignment(VerticalAlignment.BOTTOM);

            // Create second header row
            XSSFRow rowheadFirst = sheet.createRow(1);
            excelUtility.createCell(rowheadFirst, 0, commonResponseUtil.getMessage("message.iitm.report.header"), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

            // Create second header row
            XSSFRow rowheadSecond = sheet.createRow(2);
            excelUtility.createCell(rowheadSecond, 0, commonResponseUtil.getMessage(report), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

            // Create third header row for report date
            XSSFRow rowheadthird = sheet.createRow(3);
            SimpleDateFormat sdf = new SimpleDateFormat(commonResponseUtil.getMessage("session.date.format"));
            String reportDate = commonResponseUtil.getMessage("message.label.report.date.colon") + sdf.format(new Date());
            excelUtility.createCell(rowheadthird, 0, reportDate, headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 6));

            // Create column headers
            XSSFRow rowhead = sheet.createRow(4);

            // Add headers to the sheet
            for (String p : headerList) {
                excelUtility.createCell(rowhead, colCount, p, headerStyle2);
                sheet.setColumnWidth(colCount, 4000); // Set column width
                colCount++;
            }

            // Populate data rows
            int rowCount = 4;
            if (CollectionUtils.isNotEmpty(lateNightEntryRecords)) {
                int sNo = 0;
                for (LateNightEntryRecord late : lateNightEntryRecords) {
                    XSSFRow row = sheet.createRow(++rowCount);
                    excelUtility.createCell(row, 0, ++sNo, dataStyle);
                    excelUtility.createCell(row, 1, late.studentId(), dataStyle);
                    excelUtility.createCell(row, 2,late.studentName(), dataStyle);
                    excelUtility.createCell(row, 3, late.hostelName(), dataStyle);
                    excelUtility.createCell(row, 4, late.swipeDay(), dataStyle);
                    excelUtility.createCell(row, 5, late.swipeDate(), dataStyle);
                    excelUtility.createCell(row, 6, late.swipeTime(), dataStyle);
                }
            }

            // Auto-size columns with a maximum width limit
            for (int i = 0; i < headerList.length; i++) {
                sheet.autoSizeColumn(i);
                // Cap the column width to 10000 (about 100 characters) to prevent extremely wide columns
                if (sheet.getColumnWidth(i) > 10000) {
                    sheet.setColumnWidth(i, 10000);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Error in generating report", exception);
        }
        return workbook;
    }
}