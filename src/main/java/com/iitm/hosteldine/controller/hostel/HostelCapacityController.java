package com.iitm.hosteldine.controller.hostel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.iitm.hosteldine.dto.hostel.HostelCapacityDto;
import com.iitm.hosteldine.dto.hostel.HostelGuestTariffDto;
import com.iitm.hosteldine.dto.hostel.HostelStudentDistributionDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.HostelCapacityService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.hostel.capacity}")
public class HostelCapacityController {

    private final HostelCapacityService hostelCapacityService;
    private final CommonResponseUtil commonResponseUtil;

    @GetMapping
    public String getHostelCapacityList(
            @RequestParam(name = "hostelId", required = false) Long selectedHostelId,
            @RequestParam(name = "fromDate", required = false) String fromDate,
            @RequestParam(name = "toDate", required = false) String toDate,
            PaginationForm form, 
            ModelMap map, 
            HttpServletRequest request) throws Exception {
        
        LocalDate today = LocalDate.now();
        if (fromDate == null || fromDate.trim().isEmpty()) {
            fromDate = today.withDayOfMonth(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        if (toDate == null || toDate.trim().isEmpty()) {
            toDate = today.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        if (selectedHostelId == null) {
            selectedHostelId = 0L;
        }

        List<HostelCapacityDto> capacityList = hostelCapacityService.getHostelCapacityList(fromDate, toDate);
        List<HostelStudentDistributionDto> studentDistributionList = hostelCapacityService.getHostelStudentDistribution(selectedHostelId, fromDate, toDate);
        HostelGuestTariffDto guestTariffDto = hostelCapacityService.getLiveGuestRoomTariffDetails(selectedHostelId);

        map.addAttribute("capacityList", capacityList);
        map.addAttribute("selectedHostelId", selectedHostelId);
        map.addAttribute("selectedFromDate", fromDate);
        map.addAttribute("selectedToDate", toDate);
        map.addAttribute("studentDistributionList", studentDistributionList);
        map.addAttribute("guestTariffDto", guestTariffDto);
        map.addAttribute("todayDateStr", today.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));

        commonResponseUtil.updateCommonModelAttributes(map, request, null, form);
        
        return HTMLPage.HOSTEL_CAPACITY;
    }

    @GetMapping("/downloadReport")
    public ResponseEntity<byte[]> downloadExcelReport(
            @RequestParam(name = "hostelId", required = false) Long selectedHostelId,
            @RequestParam(name = "fromDate", required = false) String fromDate,
            @RequestParam(name = "toDate", required = false) String toDate) throws Exception {
        
        byte[] excelBytes = hostelCapacityService.downloadHostelCapacityExcelReport(selectedHostelId, fromDate, toDate);
        
        String fileName = "Hostel_Capacity_MultiSection_Report_" + (fromDate != null ? fromDate : "Start") + "_to_" + (toDate != null ? toDate : "End") + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}
