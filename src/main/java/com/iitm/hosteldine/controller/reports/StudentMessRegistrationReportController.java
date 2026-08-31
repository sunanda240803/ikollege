package com.iitm.hosteldine.controller.reports;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.service.mess.MessAllottedListService;
import com.iitm.hosteldine.service.reports.StudentMessRegistrationReportService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

@Controller
@RequestMapping(value = "${url.student.mess.registration.report}")
@RequiredArgsConstructor
public class StudentMessRegistrationReportController {

    private final CommonResponseUtil commonResponseUtil;
    private final MessAllottedListService messAllottedListService;
    private final StudentMessRegistrationReportService studentMessRegistrationReportService;
    private final MessageSource messageSource;

    @GetMapping
    public String getMessRegistrationReport(ModelMap model, HttpServletRequest request) {
        commonResponseUtil.updateCommonModelAttributes(model,request,null,null);
        model.addAttribute("messPeriodList", messAllottedListService.getMessPeriodList());
        return HTMLPage.STUDENT_MESS_REGISTRATION_REPORT;
    }

    @GetMapping(value ="${url.download}"+"${id}"+"${type}")
    public void downloadReport(@PathVariable("id") Integer messPeriodId, @PathVariable("type") String reportType, ModelMap model, HttpServletResponse response) {
        try {
            Workbook workbook;
            String fileName;
            if(ModelConstants.Mess_Priority_Registration_Report.equals(reportType)) {
                workbook = studentMessRegistrationReportService.getStudentPriorityReport(messPeriodId);
                fileName = commonResponseUtil.getMessage("message.mess.registration.priority.filename") + Strings.EMPTY + FileUploadConstants.XLSX_EXTENSION;
            }
            else if(ModelConstants.Mess_Student_Group_Registration_Report.equals(reportType)){
                workbook = studentMessRegistrationReportService.getStudentGroupReport(messPeriodId);
                fileName = commonResponseUtil.getMessage("message.mess.student.group.filename") + Strings.EMPTY + FileUploadConstants.XLSX_EXTENSION;
            }
            else{
                workbook = studentMessRegistrationReportService.getStudentLoginIssueReport(messPeriodId);
                fileName = commonResponseUtil.getMessage("message.mess.student.login.issue.filename") + Strings.EMPTY + FileUploadConstants.XLSX_EXTENSION;
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