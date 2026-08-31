package com.iitm.hosteldine.controller.hostel.reports;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.dean.DeanMessRebateDto;
import com.iitm.hosteldine.dto.dean.FilterCriteriaDto;
import com.iitm.hosteldine.dto.dean.OtherCandidateRequestDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.dean.DeanDashboardService;
import com.iitm.hosteldine.service.dean.DeanMessRebateService;
import com.iitm.hosteldine.service.dean.DeanOtherCandidateRequestService;
import com.iitm.hosteldine.service.dean.StudentAccommodationRequestService;
import com.iitm.hosteldine.service.hostel.HostelAccommodationService;
import com.iitm.hosteldine.util.FilterEnum;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.RoleEnum;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.hostel.accommo.rebate.request}")
public class HostelAccommodationAndRebateCommonReportController {

    private final CommonResponseUtil commonResponseUtil;
    private final SimsConfigDataService simsConfigDataService;
    private final DeanDashboardService deanDashboardService;
    private final DeanMessRebateService deanMessRebateService;
    private final StudentAccommodationRequestService studentAccommodationRequestService;
    private final DeanOtherCandidateRequestService deanOtherCandidateRequestService;

    @Value("${url.dean.other.candidate.requests}")
    private String otherCandidateRequestUrl;

    @Value("${url.dean.student.accommodation.request}")
    private String studentAccommodationUrl;

    @Value("${url.dean.approval.mess.rebate}")
    private String messRebateUrl;

    @GetMapping
    String getHostelAccommodationRebateRequestReport(ModelMap map, HttpServletRequest request, PaginationForm form) {
        commonResponseUtil.updateCommonModelAttributes(map, request,null, form);
        map.addAttribute(FilterEnum.APPLICATION_TYPE.getValue(), simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.APPLICATION_TYPE));
        map.addAttribute("validationStatusList", deanDashboardService.getValidationStatusList(SimsConfigDataService.VALIDATION_STATUS));
        return HTMLPage.HOSTEL_ACCOMMODATION_REBATE_REQUEST_REPORT;
    }

    @GetMapping("${url.download}")
    void downloadExcelReport(@RequestParam Map<String, String> allParams, PaginationForm form, HttpServletResponse response, HttpServletRequest request) {
        commonResponseUtil.getAdditionalParams(allParams, form);
        try {
            Workbook workbook = getWorkbook(form, request);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            byte[] excelBytes = bos.toByteArray();
            response.setContentType(ExcelConstants.CONTENT_TYPE);
            response.setHeader(ExcelConstants.CONTENT_DISPOSITION, getFileName(form));
            response.setContentLength(excelBytes.length);
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                outputStream.write(excelBytes);
                outputStream.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Workbook getWorkbook(PaginationForm form, HttpServletRequest request) throws Exception {
        String applicationType = Optional.ofNullable(form.getAdditionalParam().get(FilterEnum.APPLICATION_TYPE.getValue()))
                .map(Object::toString)
                .orElse(ModelConstants.EMPTY_STRING);

        return switch (applicationType) {
            case "candidateAccommodation" -> {
                form.getAdditionalParam().put(FilterEnum.STAY_TYPE.getValue(), ModelConstants.APP_AUTH.toLowerCase());
                var list = deanOtherCandidateRequestService.getOtherCandidateRequestList(
                        form,
                        otherCandidateRequestUrl.replace(ModelConstants.SLASH, ModelConstants.EMPTY_STRING),
                        false,
                        Collections.emptyList(),
                        ModelConstants.EMPTY_STRING,
                        request
                );
                yield deanOtherCandidateRequestService.generateOtherCandidateListExcelReport(list, otherCandidateRequestUrl);
            }

            case "candidateStayAccommodation" -> {
                form.getAdditionalParam().put(FilterEnum.STAY_TYPE.getValue(), ModelConstants.STAY.toLowerCase());
                var list = deanOtherCandidateRequestService.getOtherCandidateRequestList(
                        form,
                        otherCandidateRequestUrl.replace(ModelConstants.SLASH, ModelConstants.EMPTY_STRING),
                        false,
                        Collections.emptyList(),
                        ModelConstants.EMPTY_STRING,
                        request
                );
                yield deanOtherCandidateRequestService.generateOtherCandidateListExcelReport(list, otherCandidateRequestUrl);
            }

            case "studentAccommodation" -> {
                FilterCriteriaDto filterCriteria = studentAccommodationRequestService.getFilterData(form, studentAccommodationUrl);
                yield studentAccommodationRequestService.downloadStudentAccommodationRequestReport(form, studentAccommodationUrl, filterCriteria);
            }

            default -> {
                List<DeanMessRebateDto> rebateList = deanMessRebateService.getMessRebateList(form, messRebateUrl.replace(ModelConstants.SLASH, ModelConstants.EMPTY_STRING), true);
                yield deanMessRebateService.getMessRebateReport(rebateList);
            }
        };
    }

    private String getFileName(PaginationForm form) {
        var applicationType = form.getAdditionalParam().get(FilterEnum.APPLICATION_TYPE.getValue()).toString();
        return "candidateAccommodation".equalsIgnoreCase(applicationType) ||
                "candidateStayAccommodation".equalsIgnoreCase(applicationType) ? ExcelConstants.OTHER_CANDIDATE : (
                "studentAccommodation".equalsIgnoreCase(applicationType) ? ExcelConstants.LIST_OF_STUDENTS : ExcelConstants.MESS_REBATE_LIST);
    }
}
