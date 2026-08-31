package com.iitm.hosteldine.controller.mess;

import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDropdownDto;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.form.common.MessDineSummaryForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.mess.MessMasterControllerEntity;
import com.iitm.hosteldine.model.mess.MessMasterEntity;
import com.iitm.hosteldine.repository.mess.MessMasterControllerRepository;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.mess.MessDineSummaryService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "${url.mess.dine.summary}")
@RequiredArgsConstructor
public class MessDineSummaryReportController {

    private final MessDineSummaryService messDineSummaryService;
    private final CommonResponseUtil commonResponseUtil;
    private final MessageSource messageSource;
    private final MessMasterControllerRepository messMasterControllerRepository;
    private final MessMasterRepository messMasterRepository;
    private final SimsConfigDataService simsConfigDataService;

    @Value("${url.mess.dine.summary}")
    private String baseUrl;

    @GetMapping
    public String getMessDineSummary( PaginationForm form, ModelMap model ,HttpServletRequest request) {
        List<MessMasterControllerEntity> allByActiveFlagOrderByModifiedAtDesc = messMasterControllerRepository.findAllByActiveFlagOrderByIdDesc(ModelConstants.STATUS_ACTIVE);
        List<MessMasterControllerDropdownDto> messPeriods = allByActiveFlagOrderByModifiedAtDesc.stream()
                .filter(d -> d.getDiningFromDate() != null && d.getDiningToDate() != null)
                .map(d -> new MessMasterControllerDropdownDto(
                        DateUtility.formatDate(d.getDiningFromDate()) + " to " + DateUtility.formatDate(d.getDiningToDate()), d.getId()))
                .collect(Collectors.toList());


        List<MessMasterEntity> foodCourtMessMasterList = messMasterRepository.findAllByActiveFlagOrderByMessNameAsc(ModelConstants.STATUS_ACTIVE);
        List<MessMasterDto> messNames = foodCourtMessMasterList.stream().map(d -> new MessMasterDto(d.getId(), d.getMessName()))
                .collect(Collectors.toList());

        model.addAttribute("messPeriods", messPeriods);
        model.addAttribute("messNames", messNames);
        model.addAttribute("searchForm", new MessDineSummaryForm());
        commonResponseUtil.updateCommonModelAttributes(model, request ,null , form);

        return HTMLPage.MESS_DINE_SUMMARY;
    }

    @PostMapping("${url.dean.approval.excel.report.download}")
    public void downloadRequestReport(@RequestBody MessDineSummaryForm searchForm, @RequestParam Map<String, String> allParams,@ModelAttribute PaginationForm pageForm, ModelMap map, HttpServletRequest request, HttpServletResponse response) {
        //Setting dynamic filter values
        HSSFWorkbook hSSFWorkbook = new HSSFWorkbook();
        pageForm.setPage(1);
        pageForm.setSize(Integer.MAX_VALUE);

        try {
            Workbook workbook =  messDineSummaryService.getMessDineSummaryData(pageForm, searchForm);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            byte[] excelBytes = bos.toByteArray();
            response.setContentType(FileUploadConstants.XLSX);
            String fileName = messageSource.getMessage("message.mess.dine.summary.report.filename", null, Locale.getDefault()) +"" + FileUploadConstants.XLSX_EXTENSION;
            response.setHeader(FileUploadConstants.CONTENT_DISPOSITION, messageSource.getMessage(
                    "message.attachment.filename", new Object[]{fileName}, Locale.getDefault()));
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
