package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.hostel.RoomOccupancyDto;
import com.iitm.hosteldine.form.common.RoomOccupancyForm;
import com.iitm.hosteldine.repository.mess.MessMasterControllerRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.hostel.RoomOccupancyReportService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping(value = "${url.room.occupancy.report}")
@RequiredArgsConstructor
public class RoomOccupancyReportController {

    private final RoomOccupancyReportService roomOccupancyReportService;
    private final CommonResponseUtil commonResponseUtil;
    private final  MessageSource messageSource;
    private final MessMasterControllerRepository messMasterControllerRepository;
    private final HostelMasterService hostelMasterService;
    private final SimsConfigDataService simsConfigDataService;

    @Value("${url.room.occupancy.report}")
    private String baseUrl;

    @GetMapping
    public String getRoomOccupancyScreen( ModelMap model ,HttpServletRequest request) {

        model.addAttribute("hostels", hostelMasterService.getHostelList());
        model.addAttribute("searchForm", new RoomOccupancyForm());
        model.addAttribute("showTable", false);
        commonResponseUtil.updateCommonModelAttributes(model, request ,null , null);
        return HTMLPage.ROOM_OCCUPANCY_REPORT;
    }


    @PostMapping("${url.dean.approval.excel.report.download}")
    public void downloadRequestReport(@RequestBody RoomOccupancyForm searchForm, @RequestParam Map<String, String> allParams, ModelMap map, HttpServletRequest request, HttpServletResponse response) {
        List<RoomOccupancyDto> currentAllocationsByHostelId = roomOccupancyReportService.getCurrentAllocationsByHostelId(searchForm);
        Workbook workbook = null;
        String fileName;
        try {
            if(StringUtils.equalsIgnoreCase(searchForm.getReportType(), Constants.REPORT_TYPE_ROOM_OCCUPANCY)){
                workbook = roomOccupancyReportService.getRoomOccupancyReport(currentAllocationsByHostelId, searchForm);
                fileName = messageSource.getMessage("message.hostel.room.occupancy.report.filename", null, Locale.getDefault()) +"" + FileUploadConstants.XLSX_EXTENSION;
            } else {
                workbook = roomOccupancyReportService.getRoomVacancyReport(currentAllocationsByHostelId, searchForm);
                fileName = messageSource.getMessage("message.hostel.room.vacancy.report.filename", null, Locale.getDefault()) +"" + FileUploadConstants.XLSX_EXTENSION;
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            byte[] excelBytes = bos.toByteArray();
            response.setContentType(FileUploadConstants.XLSX);
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
