package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.dto.hostel.SeatDTO;
import com.iitm.hosteldine.dto.hostel.ShowDTO;
import com.iitm.hosteldine.dto.hostel.ShowWiseReportDto;
import com.iitm.hosteldine.form.common.ShowWiseReportForm;
import com.iitm.hosteldine.repository.mess.MessMasterControllerRepository;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.hostel.ShowWiseReportService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

@Controller
@RequestMapping(value = "${url.show.wise.report}")
@RequiredArgsConstructor
public class ShowWiseReportController {

    private final ShowWiseReportService showWiseReportService;
    private final CommonResponseUtil commonResponseUtil;
    private final  MessageSource messageSource;
    private final MessMasterControllerRepository messMasterControllerRepository;
    private final MessMasterRepository messMasterRepository;
    private final SimsConfigDataService simsConfigDataService;

    @Value("${url.show.wise.report}")
    private String baseUrl;

    @GetMapping
    public String getShowWiseReport( ModelMap model ,HttpServletRequest request) {

        model.addAttribute("events", showWiseReportService.getActiveEvents());
        model.addAttribute("searchForm", new ShowWiseReportForm());
        model.addAttribute("showTable", false);
        commonResponseUtil.updateCommonModelAttributes(model, request ,null , null);
        return HTMLPage.SHOW_WISE_REPORT;
    }


    @GetMapping("/api/shows")
    @ResponseBody
    public List<ShowDTO> getShows(@RequestParam Long eventId) {
        return showWiseReportService.getActiveShowsByEvent(eventId);
    }

    @GetMapping("/api/seats")
    @ResponseBody
    public List<SeatDTO> getSeats(@RequestParam Long showId) {
        return showWiseReportService.getActiveSeatsByShow(showId);
    }

    @PostMapping
    public String getShowWiseReport(@ModelAttribute("searchForm") ShowWiseReportForm searchForm, ModelMap model, HttpServletRequest request) {

        model.addAttribute("events", showWiseReportService.getActiveEvents());
        model.addAttribute("searchForm", new ShowWiseReportForm());
        List<ShowWiseReportDto> purchaseData = showWiseReportService.getPurchaseData(searchForm);
        model.addAttribute("showWiseReportData", purchaseData);
        model.addAttribute("showTable", false);


        return HTMLPage.FOOD_COURT_PURCHASE;
    }

    @PostMapping("${url.dean.approval.excel.report.download}")
    public void downloadRequestReport(@RequestBody ShowWiseReportForm searchForm, @RequestParam Map<String, String> allParams, ModelMap map, HttpServletRequest request, HttpServletResponse response) {
        List<ShowWiseReportDto> purchaseData = showWiseReportService.getPurchaseData(searchForm);

        try {
            Workbook workbook = showWiseReportService.getShowWiseReport(purchaseData, searchForm);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            byte[] excelBytes = bos.toByteArray();
            response.setContentType(FileUploadConstants.XLSX);
            String fileName = messageSource.getMessage("message.hostel.show.wise.report.filename", null, Locale.getDefault()) +"" + FileUploadConstants.XLSX_EXTENSION;
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
