package com.iitm.hosteldine.controller.reports;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.service.mess.MessAllottedListService;
import com.iitm.hosteldine.service.reports.StudentMessFeedbackReportService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

@Controller
@RequestMapping(value = "${url.student.mess.feedback.report}")
@RequiredArgsConstructor
public class StudentMessFeedbackReportController {

    private final CommonResponseUtil commonResponseUtil;
    private final MessAllottedListService messAllottedListService;
    private final StudentMessFeedbackReportService studentMessFeedbackReportService;
    private final MessageSource messageSource;

    @GetMapping
    public String getFeedbackReport(ModelMap model, HttpServletRequest request) {
        commonResponseUtil.updateCommonModelAttributes(model,request,null,null);
        model.addAttribute("messPeriodList", messAllottedListService.getMessPeriodList());
        return HTMLPage.STUDENT_MESS_FEEDBACK_REPORT;
    }

    @GetMapping(value ="${url.download}"+"${id}"+"${type}")
    public void downloadReport(@PathVariable("id") Long messPeriodId, @PathVariable("type") String reportType, ModelMap model, HttpServletResponse response) {
        try {
            Workbook workbook;
            String fileName;
            if(ModelConstants.Feed_Back_Raw_Report.equals(reportType)) {
                workbook = studentMessFeedbackReportService.generateRawScoreReport(messPeriodId);
                fileName = commonResponseUtil.getMessage("message.feedback.raw.report.filename") + Strings.EMPTY + FileUploadConstants.XLSX_EXTENSION;
            }
            else{
                workbook = studentMessFeedbackReportService.generateSummaryScoreReport(messPeriodId);
                fileName = commonResponseUtil.getMessage("message.feedback.summary.report.filename") + Strings.EMPTY + FileUploadConstants.XLSX_EXTENSION;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            byte[] excelBytes = bos.toByteArray();
            response.setContentType(FileUploadConstants.XLSX);
            response.setHeader(FileUploadConstants.CONTENT_DISPOSITION, messageSource.getMessage("message.attachment.filename", new Object[]{fileName}, Locale.getDefault()));
            response.setContentLength(excelBytes.length);
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                outputStream.write(excelBytes);
                outputStream.flush();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}