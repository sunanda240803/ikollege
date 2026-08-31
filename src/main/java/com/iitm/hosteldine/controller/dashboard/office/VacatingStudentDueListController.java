package com.iitm.hosteldine.controller.dashboard.office;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.constant.dean.DeanConstants;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.dean.DeanApprovalDto;
import com.iitm.hosteldine.dto.dean.MessInspectionReportDto;
import com.iitm.hosteldine.dto.dean.OtherCandidateRequestDto;
import com.iitm.hosteldine.form.AccommodationRequestForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.CommonService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.dashboard.student.StudentHostelRoomVacatingRequestService;
import com.iitm.hosteldine.service.dean.DeanDashboardService;
import com.iitm.hosteldine.service.dean.DeanOtherCandidateRequestService;
import com.iitm.hosteldine.service.office.VacatingStudentDueListRecord;
import com.iitm.hosteldine.service.office.VacatingStudentDueListService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${url.vacating.student.due.list}")
@RequiredArgsConstructor
public class VacatingStudentDueListController {

    private final DeanDashboardService deanDashboardService;
    private final CommonResponseUtil commonResponseUtil;
    private final DeanOtherCandidateRequestService deanOtherCandidateRequestService;
    private final VacatingStudentDueListService vacatingStudentDueListService;
    private final CommonService commonService;
    private final SimsConfigDataService simsConfigDataService;
    private final StudentHostelRoomVacatingRequestService studentHostelRoomVacatingRequestService;

    @Value("${url.vacating.student.due.list}")
    private String baseUrl;

    @GetMapping
    public String getVacatingStudentDueList(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request) throws JsonProcessingException {
        DeanApprovalDto columnList = deanDashboardService.getDeanMenuListByRoleAndUserIdAndValue(SecurityCtxUtil.userRole()
                , SecurityCtxUtil.userName(), baseUrl.replace(ModelConstants.SLASH, ModelConstants.EMPTY_STRING));
        commonResponseUtil.getAdditionalParams(allParams, form);
        List<String> filterList = List.of(DeanConstants.SEARCH_KEY.getConstants());
        if (!form.isSearchFilter()) {
            filterList.forEach(filter -> form.getAdditionalParam().put(filter, ModelConstants.EMPTY_STRING));
        }
        String[] vacatingList = studentHostelRoomVacatingRequestService.getDropdownList(
                simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.REASON_FOR_VACATION)
        );
        List<VacatingStudentDueListRecord> otherCandidateRequestList = vacatingStudentDueListService.getVacatingStudentDueList(form, columnList, baseUrl);
        commonResponseUtil.updateCommonModelAttributes2(map, request, otherCandidateRequestList, form);
        map.addAttribute(DeanConstants.VALIDATION_STATUS_LIST.getConstants(), deanDashboardService.getValidationStatusList(SimsConfigDataService.VALIDATION_STATUS));
        map.addAttribute(DeanConstants.COLUMN_DTO.getConstants(), columnList);
        map.addAttribute(DeanConstants.REASON_FOR_VACATING_LIST.getConstants(), vacatingList);
        map.addAttribute(HostelConstants.HOSTEL_OR_WARDEN_LIST.getConstants(), commonService.getHostelListForUser());
        return HTMLPage.DEAN_DASHBOARD_MENU_LIST;
    }

    @GetMapping("${url.view}"+"${id}")
    public String getVacatingDueStudentViw(@PathVariable String id, PaginationForm form, ModelMap map, HttpServletRequest request) throws Exception {
        List<String> splitEncryptData = List.of(id.split(Constants.BACKTICK));
        String studentId = MCrypt.getInstance().decryptToString(splitEncryptData.getFirst());
        VacatingStudentDueListRecord vacatingStudentDueListRecord = vacatingStudentDueListService.getVacatingStudentDueDetails(studentId);
        map.addAttribute("vacatingStudentDueListRecord", vacatingStudentDueListRecord);
        map.addAttribute("baseUrl", baseUrl);
        map.addAttribute("encryptedDetails", splitEncryptData.getLast());
        return HTMLPage.VACATING_DUE_LIST;
    }

    @PostMapping("${id}")
    public @ResponseBody BaseResponse approveOrRejectRequest(@PathVariable String id, HttpServletRequest request) throws Exception {
        String requestId = MCrypt.getInstance().decryptToString(id);
        String saveStatus = vacatingStudentDueListService.approveVacatingStudentDueDetails(Long.parseLong(requestId));
        String message;
        String status = Constants.ERROR;
        if (Constants.SAVED.equals(saveStatus)) {
            status = Constants.SUCCESS;
            message = commonResponseUtil.getMessage("message.request.approved.successfully");
        } else {
            message = commonResponseUtil.getMessage("message.request.approved.failed");
        }
        return new BaseResponse(message,status);
    }
}
