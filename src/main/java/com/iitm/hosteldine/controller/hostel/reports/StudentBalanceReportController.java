package com.iitm.hosteldine.controller.hostel.reports;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.reports.StudentBalanceReportDto;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.hostel.reports.StudentBalanceReportService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value = "${url.hostel.student.balance.report}")
@RequiredArgsConstructor
public class StudentBalanceReportController {

    private final StudentBalanceReportService studentBalanceReportService;
    private final HostelMasterService hostelMasterService;
    private final CommonResponseUtil commonResponseUtil;
    private final MessageSource messageSource;

    @Value("${url.hostel.student.balance.report}")
    private String hostelStudentBalanceReport;

    @Value("${url.download}")
    private String hostelStudentBalanceReportDownload;

    @GetMapping
    public String getStudentBalanceReports(@RequestParam Map<String, String> allParams, PaginationForm form,
            ModelMap model, HttpServletRequest request) {

        List<HostelMasterDto> hostelList = hostelMasterService.getHostelList();
        commonResponseUtil.getAdditionalParams(allParams, form);

        if (form.getAdditionalParam().get("hostelId") == null) {
            form.getAdditionalParam().put("hostelId", 0L);
            form.getAdditionalParam().put("studentBalance", "all");
            System.out.println("Setting getAdditionalParam Hostel ID: " + form.getAdditionalParam().get("hostelId"));
        }

        Page<StudentBalanceReportDto> reportList = studentBalanceReportService.generateStudentBalanceReport(allParams,
                form, true);

        commonResponseUtil.updateCommonModelAttributes(model, request, reportList, form);

        HeaderForm headerForm = (HeaderForm) model.getAttribute(ModelConstants.HEADER_FORM);
        if (headerForm != null) {
            headerForm.setAdditionalButtonProperties(true, ModelConstants.BUTTON_PINK,
                    messageSource.getMessage("message.label.download.excel", null, Locale.getDefault()),
                    ModelConstants.FA_FILE_EXCEL);

            // Build excel URL with all filter parameters
            StringBuilder excelUrl = new StringBuilder(hostelStudentBalanceReport + hostelStudentBalanceReportDownload);
            boolean firstParam = true;

            // Add hostelId if present
            if (allParams.containsKey("hostelId") && allParams.get("hostelId") != null) {
                excelUrl.append("?hostelId=").append(allParams.get("hostelId"));
                firstParam = false;
            }

            // Add studentBalance if present
            if (allParams.containsKey("studentBalance") && allParams.get("studentBalance") != null) {
                excelUrl.append(firstParam ? "?" : "&").append("studentBalance=")
                        .append(allParams.get("studentBalance"));
            }

            model.addAttribute("excelUrl", excelUrl.toString());
        }

        model.addAttribute("hostelList", hostelList);
        model.addAttribute("allParams", allParams);
        return HTMLPage.STUDENT_BALANCE_REPORT;
    }

    @GetMapping("${url.download}")
    public void downloadStudentBalanceReport(@RequestParam Map<String, String> allParams, PaginationForm form,
            ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Throwable {

        try {
            // Ensure additionalParam map is populated from allParams
            commonResponseUtil.getAdditionalParams(allParams, form);

            // Ensure additionalParam map has default values to avoid NPE
            if (form.getAdditionalParam().get("hostelId") == null) {
                form.getAdditionalParam().put("hostelId", 0L);
            }
            if (form.getAdditionalParam().get("studentBalance") == null) {
                form.getAdditionalParam().put("studentBalance", "all");
            }

            if (allParams.containsKey("studentBalance")) {
                String studentBalance = allParams.get("studentBalance");
                int queryParamIndex = studentBalance.indexOf('?');
                if (queryParamIndex > 0) {
                    allParams.put("studentBalance", studentBalance.substring(0, queryParamIndex));
                }
            }

            if (!allParams.containsKey("hostelId") && request.getParameter("hostelId") != null) {
                allParams.put("hostelId", request.getParameter("hostelId"));
            }

            if (!allParams.containsKey("studentBalance") && request.getParameter("studentBalance") != null) {
                String studentBalance = request.getParameter("studentBalance");
                int queryParamIndex = studentBalance.indexOf('?');
                if (queryParamIndex > 0) {
                    allParams.put("studentBalance", studentBalance.substring(0, queryParamIndex));
                } else {
                    allParams.put("studentBalance", studentBalance);
                }
            }

            Page<StudentBalanceReportDto> reportList = studentBalanceReportService.generateStudentBalanceReport(allParams,
                    form, false);

            // Generate Excel file
            ByteArrayOutputStream bos;
            try (Workbook workbook = studentBalanceReportService
                    .generateExcelStudentBalanceReport(reportList.getContent())) {
                bos = new ByteArrayOutputStream();
                workbook.write(bos);
            }

            // Set response headers for file download
            byte[] excelBytes = bos.toByteArray();
            response.setContentType(FileUploadConstants.XLSX);
            String fileName = commonResponseUtil.getMessage("message.student.balance.report.filename") + Strings.EMPTY
                    + FileUploadConstants.XLSX_EXTENSION;
            response.setHeader(FileUploadConstants.CONTENT_DISPOSITION, messageSource
                    .getMessage("message.attachment.filename", new Object[]{fileName}, Locale.getDefault()));
            response.setContentLength(excelBytes.length);

            // Write to output stream
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                outputStream.write(excelBytes);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate Excel report: " + e.getMessage(), e);
        }
    }
}
