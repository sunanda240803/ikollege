package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.hostel.HostelUserMappingDto;
import com.iitm.hosteldine.dto.hostel.SummaryPageDto;
import com.iitm.hosteldine.service.hostel.HostelUserMappingService;
import com.iitm.hosteldine.service.hostel.SummaryPageService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.summary.page}")
public class SummaryPageController {

    private final CommonResponseUtil commonResponseUtil;
    private final HostelUserMappingService hostelUserMappingService;
    private final SummaryPageService summaryPageService;

    private final MessageSource messageSource;


    @GetMapping
    public String getSummaryPage(ModelMap map, HttpServletRequest request) {
        HostelUserMappingDto hostelUserMappingDto = hostelUserMappingService.getUserMapping(SecurityCtxUtil.userName());
        SummaryPageDto summaryPageDto = summaryPageService.getRoomDetails(hostelUserMappingDto.getId().getHostel().getId());
        List<SummaryPageDto> summaryPageDtoList = summaryPageService.getAccommodationCountList(SecurityCtxUtil.userName());
        Long vacantTotalCount = summaryPageService.getVacantCount(SecurityCtxUtil.userRole(), SecurityCtxUtil.userName(), null);
        Long approvedVacantCount = summaryPageService.getVacantCount(SecurityCtxUtil.userRole(), SecurityCtxUtil.userName(), WorkflowStatus.APPROVED.getStatus());
        Long pendingVacantCount = summaryPageService.getVacantCount(SecurityCtxUtil.userRole(), SecurityCtxUtil.userName(), WorkflowStatus.PENDING.getStatus());
        map.addAttribute("vacantTotalCount", vacantTotalCount);
        map.addAttribute("approvedVacantCount",approvedVacantCount );
        map.addAttribute("pendingVacantCount", pendingVacantCount);
        map.addAttribute("hostelEnrollmentTotalCount", summaryPageService.getHostelEnrollmentCount(null,SecurityCtxUtil.userRole(), SecurityCtxUtil.userName(), 0L));
        map.addAttribute("approvedHostelEnrollmentCount", summaryPageService.getHostelEnrollmentCount(WorkflowStatus.APPROVED.getStatus(), SecurityCtxUtil.userRole(), SecurityCtxUtil.userName(), 0L));
        map.addAttribute("pendingHostelEnrollmentCount", summaryPageService.getHostelEnrollmentCount(WorkflowStatus.PENDING.getStatus(), SecurityCtxUtil.userRole(), SecurityCtxUtil.userName(), 0L));
        map.addAttribute("summaryPageDto", summaryPageDto);
        map.addAttribute("candidateList", getCandidateList(summaryPageDtoList));
        map.addAttribute("studentList", getStudentList(summaryPageDtoList));
        commonResponseUtil.updateCommonModelAttributes(map, request);
        return HTMLPage.SUMMARY_PAGE;
    }

    private List<SummaryPageDto> getCandidateList(List<SummaryPageDto> summaryPageDtoList) {
        return summaryPageDtoList.stream()
                .filter(dto -> dto.getAccommodationType() != null && dto.getStudentType().contains(ModelConstants.CANDIDATE))
                .peek(dto -> dto.setAccommodationType(getAccommodationMessageKeyMap().getOrDefault(dto.getAccommodationType(), dto.getAccommodationType())))
                .toList();
    }

    private List<SummaryPageDto> getStudentList(List<SummaryPageDto> summaryPageDtoList) {
        return summaryPageDtoList.stream()
                .filter(dto -> dto.getAccommodationType() != null && dto.getStudentType().contains(ModelConstants.STUDENT))
                .peek(dto -> dto.setAccommodationType(getAccommodationMessageKeyMap().getOrDefault(dto.getAccommodationType(), dto.getAccommodationType())))
                .toList();
    }

    private Map<String, String> getAccommodationMessageKeyMap(){
        return Map.of(
                "StuScholar", messageSource.getMessage("message.label.student.scholar", null, Locale.getDefault()),
                "StuStayExtension", messageSource.getMessage("message.label.stay.extension", null, Locale.getDefault()),
                "vacationStudent", messageSource.getMessage("message.label.vacation.student", null, Locale.getDefault()),
                "CandIcsr", messageSource.getMessage("message.label.candidate.icsr", null, Locale.getDefault()),
                "CandInterview", messageSource.getMessage("message.label.candidate.interview", null, Locale.getDefault()),
                "OtherAccomm", messageSource.getMessage("message.label.other.accommodation", null, Locale.getDefault())
        );
    }
}
