package com.iitm.hosteldine.controller.hostel;

import java.time.LocalDate;
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
        if (toDate == null || toDate.trim().isEmpty()) {
            toDate = today.toString();
        }
        if (fromDate == null || fromDate.trim().isEmpty()) {
            fromDate = today.minusDays(30).toString();
        }

        String selectedAcademicYear = fromDate.length() >= 4 ? fromDate.substring(0, 4) + "-" + (Integer.parseInt(fromDate.substring(0, 4)) + 1) : "2026-2027";

        List<HostelCapacityDto> capacityList = hostelCapacityService.getHostelCapacityList(selectedAcademicYear);
        
        // Default to ALL Hostels (0L) if none selected
        if (selectedHostelId == null) {
            selectedHostelId = 0L;
        }
        
        List<HostelStudentDistributionDto> studentDistributionList = hostelCapacityService.getHostelStudentDistribution(selectedHostelId, selectedAcademicYear);
        List<HostelGuestTariffDto> guestTariffList = hostelCapacityService.getGuestRoomTariffReport(selectedHostelId);
        List<HostelCapacityDto> yearWiseCapacityList = hostelCapacityService.getYearWiseHostelCapacityList(selectedHostelId);

        map.addAttribute("capacityList", capacityList);
        map.addAttribute("selectedHostelId", selectedHostelId);
        map.addAttribute("fromDate", fromDate);
        map.addAttribute("toDate", toDate);
        map.addAttribute("selectedAcademicYear", selectedAcademicYear);
        map.addAttribute("studentDistributionList", studentDistributionList);
        map.addAttribute("guestTariffList", guestTariffList);
        map.addAttribute("yearWiseCapacityList", yearWiseCapacityList);

        commonResponseUtil.updateCommonModelAttributes(map, request, null, form);
        
        return HTMLPage.HOSTEL_CAPACITY;
    }

    @GetMapping("/downloadReport")
    public ResponseEntity<byte[]> downloadExcelReport(
            @RequestParam(name = "hostelId", required = false) Long selectedHostelId,
            @RequestParam(name = "fromDate", required = false) String fromDate,
            @RequestParam(name = "toDate", required = false) String toDate) throws Exception {
        
        LocalDate today = LocalDate.now();
        if (toDate == null || toDate.trim().isEmpty()) toDate = today.toString();
        if (fromDate == null || fromDate.trim().isEmpty()) fromDate = today.minusDays(30).toString();

        byte[] excelBytes = hostelCapacityService.downloadHostelCapacityExcelReport(selectedHostelId, fromDate, toDate);
        
        String fileName = "Comprehensive_Hostel_Capacity_Report_" + fromDate + "_to_" + toDate + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}
