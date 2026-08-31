package com.iitm.hosteldine.service.reports;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.model.mess.MessMasterControllerEntity;
import com.iitm.hosteldine.repository.feedback.FeedbackQuestionRepository;
import com.iitm.hosteldine.repository.mess.MessMasterControllerRepository;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentMessFeedbackReportService {

    private final FeedbackQuestionRepository feedbackQuestionRepository;
    private final Utility utility;
    private final CommonResponseUtil commonResponseUtil;
    private final MessMasterControllerRepository messMasterControllerRepository;

    public Workbook generateRawScoreReport(Long messPeriodId) throws Exception {
        FeedbackRecord feedbackRecord = feedbackQuestionRepository.getFeedbackCountAndWeightageCount(ModelConstants.STATUS_ACTIVE).orElse(null);
        List<FeedbackWeightageViewRecord> feedbackWeightageView = feedbackQuestionRepository.getFeedbackWeightageView(messPeriodId);
        List<FeedbackCatererDetailsRecord> feedbackAndMessMasterAndMessCatererDetails = feedbackQuestionRepository.getFeedbackAndMessMasterAndMessCatererDetails(messPeriodId)
                        .stream()
                                .map(this::createFeedbackCatererRecord)
                                        .toList();

        Map<FeedbackWeightageViewRecord, List<FeedbackCatererDetailsRecord>> map =
                feedbackWeightageView.stream()
                        .collect(Collectors.toMap(
                                Function.identity(),
                                a -> feedbackAndMessMasterAndMessCatererDetails.stream()
                                        .filter(b -> a.messMasterId().equals(b.messMasterId()))
                                        .toList()
                        ));


        MessMasterControllerEntity messMasterControllerEntity = messMasterControllerRepository.findByIdAndActiveFlag(messPeriodId, ModelConstants.STATUS_ACTIVE).orElse(null);
        String fromDate, toDate;
        if(Objects.nonNull(messMasterControllerEntity)){
            fromDate = utility.dateFormatter(messMasterControllerEntity.getDiningFromDate());
            toDate = utility.dateFormatter(messMasterControllerEntity.getDiningToDate());
        }
        else{
            fromDate = Strings.EMPTY;
            toDate = Strings.EMPTY;
        }

        List<FeedbackWeightageViewRecord> feedbackWeightageList = feedbackQuestionRepository.getFeedbackWeightageView(messPeriodId);

        XSSFWorkbook workbook;
        ExcelUtility excelUtility = new ExcelUtility();
        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(commonResponseUtil.getMessage("message.catered.feedback.raw.score.report"));

            // Create styles using ExcelUtility
            XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
            headerStyle.setWrapText(true);

            XSSFCellStyle headerStyle2 = excelUtility.setHeaderStyle(workbook);
            headerStyle2.setWrapText(true);
            headerStyle2.setAlignment(HorizontalAlignment.LEFT);
            headerStyle2.setVerticalAlignment(VerticalAlignment.BOTTOM);

            XSSFCellStyle headerStyle3 = excelUtility.setHeaderStyle(workbook);
            headerStyle3.setWrapText(true);
            headerStyle3.setAlignment(HorizontalAlignment.RIGHT);
            headerStyle3.setVerticalAlignment(VerticalAlignment.BOTTOM);

            XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
            dataStyle.setWrapText(true);
            dataStyle.setAlignment(HorizontalAlignment.RIGHT);

            XSSFCellStyle dataStyle2 = excelUtility.setDataStyle(workbook);
            dataStyle2.setWrapText(true);
            dataStyle2.setAlignment(HorizontalAlignment.LEFT);

            // Create second header row
            XSSFRow rowheadFirst = sheet.createRow(1);
            excelUtility.createCell(rowheadFirst, 0, commonResponseUtil.getMessage("message.catered.feedback.raw.score.report"), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));

            // Create third header row for report date
            XSSFRow rowheadSecond = sheet.createRow(2);
            SimpleDateFormat sdf = new SimpleDateFormat(commonResponseUtil.getMessage("session.date.format"));
            String reportDate = commonResponseUtil.getMessage("message.label.report.date.colon") + sdf.format(new Date());
            excelUtility.createCell(rowheadSecond, 0, reportDate, headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 4));

            // Create column headers 1
            XSSFRow rowhead = sheet.createRow(3);
            excelUtility.createCell(rowhead, 0, "S.No", headerStyle2);
            sheet.setColumnWidth(0, 4000);

            excelUtility.createCell(rowhead, 1, "Caterer Name", headerStyle2);
            sheet.setColumnWidth(1, 13000);

            excelUtility.createCell(rowhead, 2, "Mess Period - ("+fromDate+")"+" to "+"("+toDate+")", headerStyle2);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 2, 4));
            sheet.setColumnWidth(2, 5000);
            sheet.setColumnWidth(3, 5000);
            sheet.setColumnWidth(4, 5000);

            //column header 2
            XSSFRow rowhead2 = sheet.createRow(4);
            excelUtility.createCell(rowhead2, 0, "", headerStyle2);
            sheet.setColumnWidth(0, 4000);
            excelUtility.createCell(rowhead2, 1, "", headerStyle2);
            sheet.setColumnWidth(0, 4000);

            excelUtility.createCell(rowhead2, 2, "Total No. of Students", headerStyle2);
            sheet.setColumnWidth(2, 7000);

            excelUtility.createCell(rowhead2, 3, "Total Score", headerStyle2);
            sheet.setColumnWidth(3, 5000);

            excelUtility.createCell(rowhead2, 4, "Score of 25", headerStyle2);
            sheet.setColumnWidth(3, 5000);

            // Populate data rows
            AtomicInteger rowCount = new AtomicInteger(4);
            AtomicInteger slNo = new AtomicInteger(0);
            if (CollectionUtils.isNotEmpty(feedbackWeightageList)) {
                map.forEach((k, v) -> {
                    XSSFRow row1 = sheet.createRow(rowCount.incrementAndGet());
                    excelUtility.createCell(row1, 0, slNo.incrementAndGet(), headerStyle3);
                    excelUtility.createCell(row1, 1, k.messName(), headerStyle2);
                    for (FeedbackCatererDetailsRecord feedbackCatererDetailsRecord : v) {
                        XSSFRow row2 = sheet.createRow(rowCount.incrementAndGet());
                        excelUtility.createCell(row2, 0, Strings.EMPTY, dataStyle);
                        excelUtility.createCell(row2, 1, feedbackCatererDetailsRecord.feedQuesDesc(), dataStyle2);
                        excelUtility.createCell(row2, 2, String.valueOf(feedbackCatererDetailsRecord.studentCount()), dataStyle);
                        excelUtility.createCell(row2, 3, String.valueOf(feedbackCatererDetailsRecord.feedbackScore()), dataStyle);
                        excelUtility.createCell(row2, 4, String.format("%.2f", feedbackCatererDetailsRecord.overallWeightage()), dataStyle);
                    }
                    XSSFRow row3 = sheet.createRow(rowCount.incrementAndGet());
                    row3.setHeightInPoints(35);
                    excelUtility.createCell(row3, 0, Strings.EMPTY, headerStyle2);
                    excelUtility.createCell(row3, 1,"Overall for a Score of 25", headerStyle2);
                    excelUtility.createCell(row3, 2, Strings.EMPTY, headerStyle2);
                    excelUtility.createCell(row3, 3, Strings.EMPTY, headerStyle2);
                    excelUtility.createCell(row3, 4,String.valueOf(k.overallWeightage()), headerStyle3);
                });
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Error generating Feedback Raw Score report", exception);
        }
        return workbook;
    }

    public FeedbackCatererDetailsRecord createFeedbackCatererRecord(Object[] o){
        return new FeedbackCatererDetailsRecord(
                utility.parseLong(o[0]),
                String.valueOf(o[1]),
                utility.parseInt(o[2]),
                String.valueOf(o[3]),
                utility.parseInt(o[4]),
                utility.parseLong(o[5]),
                utility.parseInt(o[6]),
                utility.parseDouble(o[7]),
                utility.convertToLocalDate(o[8]),
                utility.convertToLocalDate(o[9])

        );
    }

    public Workbook generateSummaryScoreReport(Long messPeriodId) throws Exception {
        MessMasterControllerEntity messMasterControllerEntity = messMasterControllerRepository.findByIdAndActiveFlag(messPeriodId, ModelConstants.STATUS_ACTIVE).orElse(null);
        String fromDate, toDate;
        if(Objects.nonNull(messMasterControllerEntity)){
            fromDate = utility.dateFormatter(messMasterControllerEntity.getDiningFromDate());
            toDate = utility.dateFormatter(messMasterControllerEntity.getDiningToDate());
        }
        else{
            fromDate = Strings.EMPTY;
            toDate = Strings.EMPTY;
        }

        List<FeedbackWeightageViewRecord> feedbackWeightageList = feedbackQuestionRepository.getFeedbackWeightageView(messPeriodId);

        XSSFWorkbook workbook;
        ExcelUtility excelUtility = new ExcelUtility();
        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(commonResponseUtil.getMessage("message.catered.feedback.summary.score.report"));

            // Create styles using ExcelUtility
            XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
            headerStyle.setWrapText(true);

            XSSFCellStyle headerStyle2 = excelUtility.setHeaderStyle(workbook);
            headerStyle2.setWrapText(true);
            headerStyle2.setAlignment(HorizontalAlignment.LEFT);

            XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
            dataStyle.setWrapText(true);
            dataStyle.setAlignment(HorizontalAlignment.RIGHT);

            // Create second header row
            XSSFRow rowheadFirst = sheet.createRow(1);
            excelUtility.createCell(rowheadFirst, 0, commonResponseUtil.getMessage("message.catered.feedback.summary.score.report"), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

            XSSFRow rowheadSecond = sheet.createRow(2);
            SimpleDateFormat sdf = new SimpleDateFormat(commonResponseUtil.getMessage("session.date.format"));
            String reportDate = commonResponseUtil.getMessage("message.label.report.date.colon") + sdf.format(new Date());
            excelUtility.createCell(rowheadSecond, 0, reportDate, headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));

            // Create column headers 1
            XSSFRow rowhead = sheet.createRow(3);
            excelUtility.createCell(rowhead, 0, "S.No", headerStyle2);
            sheet.setColumnWidth(0, 4000);

            excelUtility.createCell(rowhead, 1, "Caterer Name", headerStyle2);
            sheet.setColumnWidth(1, 10000);

            excelUtility.createCell(rowhead, 2, "Mess Period - ("+fromDate+")"+" to "+"("+toDate+")", headerStyle2);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 2, 3));
            sheet.setColumnWidth(2, 8000);
            sheet.setColumnWidth(3, 8000);

            //column header 2
            XSSFRow rowhead2 = sheet.createRow(4);
            excelUtility.createCell(rowhead2, 0, "", headerStyle2);
            sheet.setColumnWidth(0, 4000);
            excelUtility.createCell(rowhead2, 1, "", headerStyle2);
            sheet.setColumnWidth(0, 4000);

            excelUtility.createCell(rowhead2, 2, "Total No. of Students", headerStyle2);
            sheet.setColumnWidth(2, 8000);

            excelUtility.createCell(rowhead2, 3, "Overall for a score of 25", headerStyle2);
            sheet.setColumnWidth(3, 8000);

            // Populate data rows
            int rowCount = 4;
            if (CollectionUtils.isNotEmpty(feedbackWeightageList)) {
                int slNo = 0;
                for (FeedbackWeightageViewRecord r : feedbackWeightageList) {
                    XSSFRow row = sheet.createRow(++rowCount);
                    excelUtility.createCell(row, 0, ++slNo, dataStyle);
                    excelUtility.createCell(row, 1, r.messName(), dataStyle);
                    excelUtility.createCell(row, 2, r.studentCount(), dataStyle);
                    excelUtility.createCell(row, 3, String.valueOf(r.overallWeightage()), dataStyle);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Error generating Feedback Summary Score report", exception);
        }
        return workbook;
    }
}
