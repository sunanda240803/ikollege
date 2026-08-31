package com.iitm.hosteldine.controller.reports;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.HostelBiometricTerminalDto;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.HostelBiometricTerminalService;
import com.iitm.hosteldine.service.reports.GeneralLogReportService;
import com.iitm.hosteldine.service.reports.LateNightEntriesService;
import com.iitm.hosteldine.service.reports.LateNightEntryRecord;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping(value = "${url.general.log.report}")
@RequiredArgsConstructor
public class GeneralLogReportController {

    private final CommonResponseUtil commonResponseUtil;
    private final HostelBiometricTerminalService hostelBiometricTerminalService;
    private final GeneralLogReportService generalLogReportService;
    private final LateNightEntriesService lateNightEntriesService;
    private final MessageSource messageSource;
    private final Utility utility;

    @GetMapping
    String getGeneralLogReport(@RequestParam Map<String, String> allParams, PaginationForm form, HttpServletRequest request, ModelMap model) {
        List<HostelBiometricTerminalDto> hostelBiometricTerminals = hostelBiometricTerminalService.getHostelBiometricTerminals(ModelConstants.STATUS_ACTIVE);
        model.addAttribute("hostelBiometricTerminals", hostelBiometricTerminals);
        if (!form.isSearchFilter()) {
            List<String> filterList = List.of("hostelName", "fromDate", "toDate", "genderType");
            filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
        }
        model.addAttribute("filters", form.getAdditionalParam());
        commonResponseUtil.getAdditionalParams(allParams, form);
        Page<LateNightEntryRecord> generalLogReport = generalLogReportService.getGeneralLogReport(form);
        commonResponseUtil.updateCommonModelAttributes(model, request, generalLogReport, form);
        setExportButton(model, Strings.EMPTY);
        return HTMLPage.GENERAL_LOG_REPORT;
    }

    private void setExportButton(ModelMap map, String splitBaseUrl) {
        HeaderForm headerForm = (HeaderForm) map.getAttribute(ModelConstants.HEADER_FORM);
        if (headerForm != null) {
            headerForm.setAdditionalButtonProperties(true, ModelConstants.BUTTON_PINK, commonResponseUtil.getMessage("message.label.download.excel"),
                    ModelConstants.FA_FILE_EXCEL);
            map.addAttribute("excelUrl", splitBaseUrl + commonResponseUtil.getMessage("url.download"));
        }
    }

    @GetMapping(value = "${url.download}")
    void downloadGeneralLogReport(@RequestParam Map<String, String> allParams, PaginationForm form, HttpServletResponse response) {
        try {
            commonResponseUtil.getAdditionalParams(allParams, form);
            form.setSize(Integer.MAX_VALUE);
            Page<LateNightEntryRecord> lateNightEntries = generalLogReportService.getGeneralLogReport(form);
            List<LateNightEntryRecord> content = Objects.nonNull(lateNightEntries) ? lateNightEntries.getContent() : List.of();
            Workbook workbook = lateNightEntriesService.generateExcelReport(content, "message.general.log.report");
            String fileName;
            fileName = commonResponseUtil.getMessage("message.general.log.report.filename") + Strings.EMPTY + FileUploadConstants.XLSX_EXTENSION;

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