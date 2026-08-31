package com.iitm.hosteldine.controller.reports;

import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.service.CommonService;
import com.iitm.hosteldine.service.dashboard.student.HostelNightPaymentTransactionService;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping(value = "${url.hostel.night.coupon.report}")
@RequiredArgsConstructor
public class HostelNightCouponReportController {

    private final HostelNightPaymentTransactionService hostelNightPaymentTransactionService;
    private final CommonResponseUtil commonResponseUtil;
    private final MessageSource messageSource;
    private final CommonService commonService;

    @GetMapping
    String getHostelNightCouponReport(ModelMap model, HttpServletRequest request) {
        commonResponseUtil.updateCommonModelAttributes(model,request,null,null);
        return HTMLPage.HOSTEL_NIGHT_COUPON_REPORT;
    }

    @GetMapping("${url.download}"+"${type}")
    void downloadReport(@PathVariable("type") String type, HttpServletResponse response){
        try {
            Workbook workbook = hostelNightPaymentTransactionService.generateExcelReport(type);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            byte[] excelBytes = bos.toByteArray();
            response.setContentType(FileUploadConstants.XLSX);
            String fileName = commonResponseUtil.getMessage("message.night.coupon.filename") + Strings.EMPTY + FileUploadConstants.XLSX_EXTENSION;
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

    @GetMapping("${url.get.report}")
    String getHostelNightIndividualReport(ModelMap model, HttpServletRequest request) {
        commonResponseUtil.updateCommonModelAttributes(model,request,null,null);
        List<HostelMasterDto> hostelList = commonService.getHostelListForUser();
        model.addAttribute("hostelList", hostelList);
        return HTMLPage.HOSTEL_NIGHT_COUPON_INDIVIDUAL_REPORT;
    }

    @GetMapping("${url.download}")
    void downloadHostelNightIndividualReport(@RequestParam String type,
                                             @RequestParam String submittedFromDate,
                                             @RequestParam String submittedToDate,
                                             @RequestParam String hostelId,
                                             HttpServletResponse response){
        try {
            Workbook workbook = hostelNightPaymentTransactionService.generateExcelReport(type, submittedFromDate, submittedToDate, hostelId);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            byte[] excelBytes = bos.toByteArray();
            response.setContentType(FileUploadConstants.XLSX);
            String fileName = commonResponseUtil.getMessage("message.night.coupon.filename") + Strings.EMPTY + FileUploadConstants.XLSX_EXTENSION;
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