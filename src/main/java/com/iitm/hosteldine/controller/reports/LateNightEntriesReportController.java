package com.iitm.hosteldine.controller.reports;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.SimsConfigDataJsonArrayDto;
import com.iitm.hosteldine.dto.hostel.HostelBiometricTerminalDto;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.hostel.HostelBiometricTerminalService;
import com.iitm.hosteldine.service.reports.LateNightEntriesService;
import com.iitm.hosteldine.service.reports.LateNightEntryRecord;
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
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Controller
@RequestMapping(value = "${url.late.night.entries.report}")
@RequiredArgsConstructor
class LateNightEntriesReportController {

    private final HostelBiometricTerminalService hostelBiometricTerminalService;
    private final CommonResponseUtil commonResponseUtil;
    private final LateNightEntriesService lateNightEntriesService;
    private final MessageSource messageSource;
    private final SimsConfigDataService simsConfigDataService;

    @GetMapping
    String getLateNightEntriesReport(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap model, HttpServletRequest request) {
        List<HostelBiometricTerminalDto> hostelBiometricTerminals = hostelBiometricTerminalService.getHostelBiometricTerminals(ModelConstants.STATUS_ACTIVE);
        model.addAttribute("hostelBiometricTerminals", hostelBiometricTerminals);
        ArrayList<SimsConfigDataJsonArrayDto> reportTimings = simsConfigDataService
                .getSimConfigValueFromJsonArray(SimsConfigDataService.LATE_NIGHT_TIMINGS);
        model.addAttribute("reportTimings",reportTimings);

        if (!form.isSearchFilter()) {
            List<String> filterList = List.of("hostelName", "timing");
            filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
        }
        model.addAttribute("filters", form.getAdditionalParam());
        commonResponseUtil.getAdditionalParams(allParams, form);
        Page<LateNightEntryRecord> lateNightEntries = lateNightEntriesService.getLateNightEntries(form);
        commonResponseUtil.updateCommonModelAttributes(model, request, lateNightEntries, form);
        if(Objects.nonNull(lateNightEntries) && !lateNightEntries.getContent().isEmpty()){
            setExportButton(model, Strings.EMPTY);
        }
        return HTMLPage.LATE_NIGHT_ENTRIES_REPORT;
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
    void downloadReport(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap model, HttpServletResponse response) {
        try {
            commonResponseUtil.getAdditionalParams(allParams, form);
            form.setSize(Integer.MAX_VALUE);
            Page<LateNightEntryRecord> lateNightEntries = lateNightEntriesService.getLateNightEntries(form);
            Workbook workbook  = lateNightEntriesService.generateExcelReport(lateNightEntries.getContent(), "message.late.night.entries.report");
            String fileName;
            fileName = commonResponseUtil.getMessage("message.late.night.entries.filename") + Strings.EMPTY + FileUploadConstants.XLSX_EXTENSION;

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