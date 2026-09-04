package com.iitm.hosteldine.controller.hostel;

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
            @RequestParam(name = "academicYear", required = false, defaultValue = "2026-2027") String selectedAcademicYear,
            PaginationForm form, 
            ModelMap map, 
            HttpServletRequest request) throws Exception {
        
        List<String> academicYearList = hostelCapacityService.getAcademicYearList();
        List<HostelCapacityDto> capacityList = hostelCapacityService.getHostelCapacityList(selectedAcademicYear);
        
        // Default to ALL Hostels (0L) if none selected
        if (selectedHostelId == null) {
            selectedHostelId = 0L;
        }
        
        List<HostelStudentDistributionDto> studentDistributionList = hostelCapacityService.getHostelStudentDistribution(selectedHostelId, selectedAcademicYear);

        map.addAttribute("capacityList", capacityList);
        map.addAttribute("selectedHostelId", selectedHostelId);
        map.addAttribute("academicYearList", academicYearList);
        map.addAttribute("selectedAcademicYear", selectedAcademicYear);
        map.addAttribute("studentDistributionList", studentDistributionList);

        commonResponseUtil.updateCommonModelAttributes(map, request, null, form);
        
        return HTMLPage.HOSTEL_CAPACITY;
    }

    @GetMapping("/downloadReport")
    public ResponseEntity<byte[]> downloadExcelReport(
            @RequestParam(name = "hostelId", required = false) Long selectedHostelId,
            @RequestParam(name = "academicYear", required = false, defaultValue = "2026-2027") String academicYear) throws Exception {
        
        byte[] excelBytes = hostelCapacityService.downloadHostelCapacityExcelReport(selectedHostelId, academicYear);
        
        String fileName = "Hostel_Capacity_Report_" + (academicYear != null ? academicYear : "ALL") + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}
