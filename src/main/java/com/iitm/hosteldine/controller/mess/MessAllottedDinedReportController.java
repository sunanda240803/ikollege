package com.iitm.hosteldine.controller.mess;

import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mess.MessAllottedDinedDto;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDropdownDto;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.form.common.MessDineSummaryForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.mess.MessMasterControllerEntity;
import com.iitm.hosteldine.model.mess.MessMasterEntity;
import com.iitm.hosteldine.repository.mess.MessMasterControllerRepository;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.mess.MessAllottedDinedReportService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "${url.mess.allotted.dined}")
@RequiredArgsConstructor
public class MessAllottedDinedReportController {

    private final MessAllottedDinedReportService messAllottedDinedReportService;
    private final CommonResponseUtil commonResponseUtil;
    private final MessageSource messageSource;
    private final MessMasterControllerRepository messMasterControllerRepository;
    private final MessMasterRepository messMasterRepository;
    private final SimsConfigDataService simsConfigDataService;

    @Value("${url.mess.allotted.dined}")
    private String baseUrl;

    @GetMapping
    public String getMessAllottedDined( PaginationForm form, ModelMap model ,HttpServletRequest request) {
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
        model.addAttribute("showTable", false);
        model.addAttribute("searchForm", new MessDineSummaryForm());
        commonResponseUtil.updateCommonModelAttributes(model, request ,null , form);

        return HTMLPage.MESS_ALLOTTED_DINED;
    }

    @PostMapping
    public String getMessAllottedDinedList(@ModelAttribute("searchForm") MessDineSummaryForm searchForm, PaginationForm form, ModelMap model, HttpServletRequest request) throws Exception {

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
        model.addAttribute("searchForm", searchForm);
        try {
            MessAllottedDinedDto messAllottedDined = messAllottedDinedReportService.getMessAllottedDined(searchForm);
            model.addAttribute("showTable", ObjectUtils.isNotEmpty(messAllottedDined));
            model.addAttribute("messAllottedDined", messAllottedDined);
            model.addAttribute("errorMessage",ObjectUtils.isEmpty(messAllottedDined) ?
                    messageSource.getMessage("message.label.no.record.found", null, Locale.getDefault())  :null);
        } catch (Exception e) {
            model.addAttribute("messAllottedDined", null);
            model.addAttribute("showTable", false);
            model.addAttribute("errorMessage", e.getMessage());
            e.printStackTrace();
        }

        commonResponseUtil.updateCommonModelAttributes(model, request,null,form);

        return HTMLPage.MESS_ALLOTTED_DINED;
    }


    @GetMapping("${url.pdf.download}" + "${periodId}" + "${messId}")
    public ResponseEntity<Resource> downloadPDF(@PathVariable Long periodId, @PathVariable Long messId) throws Exception {
        MessDineSummaryForm searchForm = new MessDineSummaryForm();
        searchForm.setMessId(messId);
        searchForm.setMessPeriodId(periodId);
        Resource resource = messAllottedDinedReportService.generatePdf(searchForm);
        return Utility.prepareDownloadFile(resource);
    }

}
