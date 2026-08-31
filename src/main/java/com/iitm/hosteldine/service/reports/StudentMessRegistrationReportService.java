package com.iitm.hosteldine.service.reports;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.repository.studentDashboard.StudentMessLoginIssuePriorityRepository;
import com.iitm.hosteldine.repository.studentDashboard.StudentMessPriorityRegistrationRepository;
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
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StudentMessRegistrationReportService {

    private final StudentMessPriorityRegistrationRepository studentMessPriorityRegistrationRepository;
    private final CommonResponseUtil commonResponseUtil;
    private final StudentMessLoginIssuePriorityRepository studentMessLoginIssuePriorityRepository;
    private final Utility utility;

    public Workbook getStudentPriorityReport(Integer messPeriodId) throws Exception {
        List<MessPriorityRecord> messPriorityDetailsForReport = studentMessPriorityRegistrationRepository
                .getMessPriorityDetailsForReport(messPeriodId, ModelConstants.STATUS_ACTIVE)
                .stream()
                .map(this::mapToMessPriorityRecord)
                .toList();
        return generateMessPriorityExcel(messPriorityDetailsForReport);
    }

    public Workbook getStudentGroupReport(Integer messPeriodId) throws Exception {
        List<MessGroupRecord> list = studentMessPriorityRegistrationRepository.getStudentMessGroupList(messPeriodId)
                .stream()
                .map(this::mapToMessGroupRecord)
                .toList();
        return generateMessGroupExcel(list);
    }

    public Workbook getStudentLoginIssueReport(Integer messPeriodId) throws Exception {
        List<MessPriorityRecord> studentLoginIssueForReport = studentMessLoginIssuePriorityRepository.getStudentLoginIssueForReport(messPeriodId,ModelConstants.STATUS_ACTIVE)
                .stream().map(this::mapToMessLoginIssue).toList();
        return generateStudentMessLoginIssueExcel(studentLoginIssueForReport);
    }

    private MessPriorityRecord mapToMessPriorityRecord(Object[] o){
        return  new MessPriorityRecord(
                Objects.nonNull(o[1]) ? String.valueOf(o[1]) : Strings.EMPTY,
                Objects.nonNull(o[2]) ? String.valueOf(o[2]) : Strings.EMPTY,
                Objects.nonNull(o[3]) ? Constants.MALE.equalsIgnoreCase(String.valueOf(o[3])) ? Constants.MALE_FULL_FORM :
                        Constants.FEMALE_FULL_FORM  : Strings.EMPTY,
                Objects.nonNull(o[4]) ? String.valueOf(o[4]) : Strings.EMPTY,
                Objects.nonNull(o[5]) ? String.valueOf(o[5]) : Strings.EMPTY,
                Objects.nonNull(o[8]) ? String.valueOf(o[8]) : Strings.EMPTY,
                Objects.nonNull(o[6]) ? String.valueOf(o[6]) : Strings.EMPTY,
                Objects.nonNull(o[9]) ? String.valueOf(o[9]) : Strings.EMPTY,
                "Monthly"
        );
    }

    private MessGroupRecord mapToMessGroupRecord(Object[] o){
        return new MessGroupRecord(
                Objects.nonNull(o[0]) ? String.valueOf(o[0]) : Strings.EMPTY,
                Objects.nonNull(o[1]) ? String.valueOf(o[1]) : Strings.EMPTY,
                Objects.nonNull(o[2]) ? String.valueOf(o[2]) : Strings.EMPTY,
                Objects.nonNull(o[3]) ? Constants.MALE.equalsIgnoreCase(String.valueOf(o[3])) ? Constants.MALE_FULL_FORM :
                        Constants.FEMALE_FULL_FORM  : Strings.EMPTY,
                Objects.nonNull(o[4]) ? String.valueOf(o[4]) : Strings.EMPTY,
                Objects.nonNull(o[5]) ? String.valueOf(o[5]) : Strings.EMPTY
        );
    }

    private MessPriorityRecord mapToMessLoginIssue(Object[] o){
        return new MessPriorityRecord(
                Objects.nonNull(o[0]) ? String.valueOf(o[0]) : Strings.EMPTY,
                Objects.nonNull(o[1]) ? String.valueOf(o[1]) : Strings.EMPTY,
                Objects.nonNull(o[2]) ? Constants.MALE.equalsIgnoreCase(String.valueOf(o[2])) ? Constants.MALE_FULL_FORM :
                        Constants.FEMALE_FULL_FORM  : Strings.EMPTY,
                Objects.nonNull(o[3]) ? String.valueOf(o[3]) : Strings.EMPTY,
                Objects.nonNull(o[4]) ? String.valueOf(o[4]) : Strings.EMPTY,
                Objects.nonNull(o[5]) ? String.valueOf(o[5]) : Strings.EMPTY,
                null,
                Objects.nonNull(o[6]) ? String.valueOf(o[6]) : Strings.EMPTY,
                null
        );
    }


    private Workbook generateMessPriorityExcel(List<MessPriorityRecord> messPriorityRecords) throws Exception {
        XSSFWorkbook workbook;
        int colCount = 0;
        ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

        String[] headerList = ModelConstants.MESS_REGISTRATION_PRIORITY_HEADER;

        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(commonResponseUtil.getMessage("message.mess.registration.priority.report"));

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
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));

            // Create second header row
            XSSFRow rowheadSecond = sheet.createRow(2);
            excelUtility.createCell(rowheadSecond, 0, commonResponseUtil.getMessage("message.mess.registration.priority.report"), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 9));

            // Create third header row for report date
            XSSFRow rowheadthird = sheet.createRow(3);
            SimpleDateFormat sdf = new SimpleDateFormat(commonResponseUtil.getMessage("session.date.format"));
            String reportDate = commonResponseUtil.getMessage("message.label.report.date.colon") + sdf.format(new Date());
            excelUtility.createCell(rowheadthird, 0, reportDate, headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 9));

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
            if (CollectionUtils.isNotEmpty(messPriorityRecords)) {
                int sNo = 0;
                for (MessPriorityRecord mess : messPriorityRecords) {
                    XSSFRow row = sheet.createRow(++rowCount);
                    excelUtility.createCell(row, 0, ++sNo, dataStyle);
                    excelUtility.createCell(row, 1, mess.studentId(), dataStyle);
                    excelUtility.createCell(row, 2,mess.studentName(), dataStyle);
                    excelUtility.createCell(row, 3, mess.gender(), dataStyle);
                    excelUtility.createCell(row, 4, mess.messPriority(), dataStyle);
                    excelUtility.createCell(row, 5, mess.messName(), dataStyle);
                    excelUtility.createCell(row, 6, mess.messOption(), dataStyle);
                    excelUtility.createCell(row, 7, utility.convertTimeStamp(mess.lastModifiedTime()), dataStyle);
                    excelUtility.createCell(row, 8, utility.convertTimeStamp(mess.joiningTime()), dataStyle);
                    excelUtility.createCell(row, 9, mess.registrationType(), dataStyle);
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
            throw new Exception("Error generating Mess Registration Priority report", exception);
        }
        return workbook;
    }

    private Workbook generateMessGroupExcel(List<MessGroupRecord> messGroupRecords) throws Exception {
        XSSFWorkbook workbook;
        int colCount = 0;
        ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

        String[] headerList = ModelConstants.MESS_STUDENT_GROUP_HEADER;

        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(commonResponseUtil.getMessage("message.mess.student.group.report"));

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
            excelUtility.createCell(rowheadSecond, 0, commonResponseUtil.getMessage("message.mess.student.group.report"), headerStyle);
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
            if (CollectionUtils.isNotEmpty(messGroupRecords)) {
                int sNo = 0;
                for (MessGroupRecord groupRecord : messGroupRecords) {
                    XSSFRow row = sheet.createRow(++rowCount);
                    excelUtility.createCell(row, 0, ++sNo, dataStyle);
                    excelUtility.createCell(row, 1, groupRecord.groupName(), dataStyle);
                    excelUtility.createCell(row, 2,groupRecord.leader(), dataStyle);
                    excelUtility.createCell(row, 3, groupRecord.leaderName(), dataStyle);
                    excelUtility.createCell(row, 4, groupRecord.leaderGender(), dataStyle);
                    excelUtility.createCell(row, 5, groupRecord.member(), dataStyle);
                    excelUtility.createCell(row, 6, groupRecord.memberName(), dataStyle);
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
            throw new Exception("Error generating Student Mess Group Registration report", exception);
        }
        return workbook;
    }

    private Workbook generateStudentMessLoginIssueExcel(List<MessPriorityRecord> loginIssues) throws Exception {
        XSSFWorkbook workbook;
        int colCount = 0;
        ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

        String[] headerList = ModelConstants.MESS_STUDENT_LOGIN_ISSUE_HEADER;

        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(commonResponseUtil.getMessage("message.mess.student.login.issue.report"));

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
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));

            // Create second header row
            XSSFRow rowheadSecond = sheet.createRow(2);
            excelUtility.createCell(rowheadSecond, 0, commonResponseUtil.getMessage("message.mess.student.login.issue.report"), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 7));

            // Create third header row for report date
            XSSFRow rowheadthird = sheet.createRow(3);
            SimpleDateFormat sdf = new SimpleDateFormat(commonResponseUtil.getMessage("session.date.format"));
            String reportDate = commonResponseUtil.getMessage("message.label.report.date.colon") + sdf.format(new Date());
            excelUtility.createCell(rowheadthird, 0, reportDate, headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 7));

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
            if (CollectionUtils.isNotEmpty(loginIssues)) {
                int sNo = 0;
                for (MessPriorityRecord loginIssue : loginIssues) {
                    XSSFRow row = sheet.createRow(++rowCount);
                    excelUtility.createCell(row, 0, ++sNo, dataStyle);
                    excelUtility.createCell(row, 1, loginIssue.studentId(), dataStyle);
                    excelUtility.createCell(row, 2,loginIssue.studentName(), dataStyle);
                    excelUtility.createCell(row, 3, loginIssue.gender(), dataStyle);
                    excelUtility.createCell(row, 4, loginIssue.messPriority(), dataStyle);
                    excelUtility.createCell(row, 5, loginIssue.messName(), dataStyle);
                    excelUtility.createCell(row, 6, loginIssue.messOption(), dataStyle);
                    excelUtility.createCell(row, 7, utility.convertTimeStamp(loginIssue.joiningTime()), dataStyle);
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
            throw new Exception("Error generating Student Mess Login Issue report", exception);
        }
        return workbook;
    }
}