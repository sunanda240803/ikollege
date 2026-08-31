package com.iitm.hosteldine.controller.dashboard.dean;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.CategoryEnum;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.OtherCandidate.CandidateAppointmentRequestDto;
import com.iitm.hosteldine.dto.dean.BulkApprovalRejectDto;
import com.iitm.hosteldine.dto.dean.DeanApprovalDto;
import com.iitm.hosteldine.dto.dean.OtherCandidateRequestDto;
import com.iitm.hosteldine.form.AccommodationRequestForm;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.OtherCandidate.CandidateWorkflowEntity;
import com.iitm.hosteldine.repository.OtherCandidate.CandidateWorkflowRepository;
import com.iitm.hosteldine.service.CommonService;
import com.iitm.hosteldine.service.OtherCandidate.AccommodationRequestService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.dean.DeanDashboardService;
import com.iitm.hosteldine.service.dean.DeanOtherCandidateRequestService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.RoleEnum;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping(value = { "${url.dean.other.candidate.requests}", "${url.dean.other.candidate.requests}" + "${url.interview}"})
@RequiredArgsConstructor
public class DeanOtherCandidateRequestController {

    private final DeanDashboardService deanDashboardService;
    private final CommonResponseUtil commonResponseUtil;
    private final DeanOtherCandidateRequestService deanOtherCandidateRequestService;
    private final AccommodationRequestService accommodationRequestService;
    private final MessageSource messageSource;
    private final CandidateWorkflowRepository candidateWorkflowRepository;
    private final SimsConfigDataService simsConfigDataService;
    private final CommonService commonService;
    private final Utility utility;

    @Value("${url.dean.other.candidate.requests}")
    private String otherCandidateUrl;

    @Value("${url.interview}")
    private String interviewUrl;

    private String getOtherCandidateUrl(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        if (requestUri.contains(interviewUrl)) {
            return otherCandidateUrl + interviewUrl;
        } else {
            return otherCandidateUrl;
        }
    }

    @GetMapping
    public String getOtherCandidateRequests(@RequestParam Map<String, String> allParams, PaginationForm form,
                                            ModelMap map, HttpServletRequest request) {
        DeanApprovalDto columnList = deanDashboardService.getDeanMenuListByRoleAndUserIdAndValue(SecurityCtxUtil.userRole()
                , SecurityCtxUtil.userName(), otherCandidateUrl.replace("/", "") + (request.getRequestURI().contains(interviewUrl) ? interviewUrl : ModelConstants.EMPTY_STRING));
        map.addAttribute("columnDto", columnList);

        commonResponseUtil.getAdditionalParams(allParams, form);
        List<String> filterList = List.of("validationStatus", "category", "approvalFromDate", "approvalToDate",
                "submittedFromDate", "submittedToDate", "appointmentFromDate", "appointmentToDate", "stayFromDate",
                "stayToDate", "candidateName", "candidateEmail", "hostelName", "stayType","currentDayStayFlag");
        if (!form.isSearchFilter()) {
            filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
        }
        else if (Constants.VACATING_LINK.equals(form.getAdditionalParam().get("currentDayStayFlag"))) {
            List<String> excludedFilters = List.of("validationStatus", "stayToDate", "currentDayStayFlag");
            filterList.stream()
                    .filter(filter -> !excludedFilters.contains(filter))
                    .forEach(filter -> form.getAdditionalParam().put(filter, ""));
        }

        map.addAttribute("validationStatusList", deanDashboardService.getValidationStatusList(SimsConfigDataService.VALIDATION_STATUS));
        List<OtherCandidateRequestDto> otherCandidateRequestList = deanOtherCandidateRequestService.getOtherCandidateRequestList(form,
                otherCandidateUrl.replace("/", "") + (request.getRequestURI().contains(interviewUrl) ? interviewUrl : ModelConstants.EMPTY_STRING)
                , true, columnList.getActionUrlList(),
                request.getRequestURI().contains(interviewUrl) ? CategoryEnum.INTERVIEWS.getValue().toLowerCase() : ModelConstants.EMPTY_STRING, request);
        commonResponseUtil.updateCommonModelAttributes2(map, request, otherCandidateRequestList, form);
        map.addAttribute("categoryList", simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.CANDIDATE_POST));
        map.addAttribute(HostelConstants.HOSTEL_OR_WARDEN_LIST.getConstants(), commonService.getHostelListForUser());
        map.addAttribute("isBulkApproveRejectNeeded", true);
        map.addAttribute("isRoleDean", RoleEnum.DEAN.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole()));
        setExportButton(map, otherCandidateUrl.replace("/", "") + (request.getRequestURI().contains(interviewUrl) ? interviewUrl : ModelConstants.EMPTY_STRING));
        if(RoleEnum.HOSTEL_CHECK_IN.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole())){
            HeaderForm headerForm = (HeaderForm) map.getAttribute(ModelConstants.HEADER_FORM);
            if (headerForm != null) {
                headerForm.setAdditionalButton2Properties(true, ModelConstants.BUTTON_PURPLE,
                        messageSource.getMessage("message.label.today.vacating.list", null, Locale.getDefault()),null);
            }
        }
        return RoleEnum.CCW_OFFICE.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole()) ? HTMLPage.DEAN_ALLOTMENT_LIST
                : HTMLPage.DEAN_DASHBOARD_MENU_LIST;
    }

    @GetMapping(value = "${url.view}${id}")
    public String viewCandidateRequest(@PathVariable("id") String key, HttpServletRequest request, ModelMap model) throws Exception {
        String[] split = MCrypt.getInstance().decryptToString(key).split(Constants.BACKTICK);
        Long candidateId = Long.parseLong(split[0]);
        Long requestId = Long.parseLong(split[1]);
        Long workflowId = Long.parseLong(split[2]);
        String validationStatus = split[3];
        AccommodationRequestForm accommodationRequestForm = accommodationRequestService.requestView(candidateId,
                requestId, validationStatus, WorkflowStatus.FROM_LOGIN);
        accommodationRequestForm.setOtherCandidateRequestDto(OtherCandidateRequestDto.builder().build());
        model.addAttribute("accommodationRequestForm", accommodationRequestForm);

        CandidateWorkflowEntity candidateWorkflowEntity = candidateWorkflowRepository.
                findByAuthorityTypeCCWOrDean( Constants.CCW,  RoleEnum.DEAN.getValue(),requestId,  ModelConstants.STATUS_ACTIVE)
                .orElse(null);
        if (Objects.nonNull(candidateWorkflowEntity)) {
            key = Optional.ofNullable(regenerateKey(candidateId, requestId, candidateWorkflowEntity.getId(), candidateWorkflowEntity.getModifiedAt()))
                    .orElse(key);
        }
        request.getSession().setAttribute("requestKey", key);
        commonResponseUtil.updateCommonModelAttributes(model, request);
        return HTMLPage.OTHER_CANDIDATE_REQUEST_VIEW;
    }

    private String regenerateKey(Long candidateId, Long requestId, Long workflowId, LocalDateTime modifiedAt) {
        String ids = candidateId + Constants.BACKTICK + requestId + Constants.BACKTICK + workflowId +
                Constants.BACKTICK + modifiedAt;
        try {
            return MCrypt.getInstance().encryptToText(ids);
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping(value = "${type}")
    public @ResponseBody BaseResponse approveOrRejectRequest(@ModelAttribute AccommodationRequestForm form, @PathVariable("type") String type,
                                                           HttpServletRequest request) throws Exception {
        String key = String.valueOf(request.getSession().getAttribute("requestKey"));
        String[] split = MCrypt.getInstance().decryptToString(key).split(Constants.BACKTICK);
        String saveStatus= accommodationRequestService.processApprovalBySplit(split, form, type, request);
        String message;
        String status = Constants.ERROR;
        if (WorkflowStatus.APPROVED.getStatus().equals(type) || WorkflowStatus.OVERRIDE_APPROVED.getStatus().equals(type)) {
            if (Constants.SAVED.equals(saveStatus)) {
                status = Constants.SUCCESS;
                message = commonResponseUtil.getMessage("message.request.approved.successfully");
            } else {
                message = commonResponseUtil.getMessage("message.request.approved.failed");
            }
        } else {
            if (Constants.SAVED.equals(saveStatus)) {
                status = Constants.SUCCESS;
                message = commonResponseUtil.getMessage("message.request.rejected.successfully");
            } else {
                message = commonResponseUtil.getMessage("message.request.reject.failed");
            }
        }
        return new BaseResponse(message,status);
    }

    @DeleteMapping(value = "${id}")
    @ResponseBody
    public BaseResponse cancelRequest(@PathVariable("id") String key) throws Exception {
        String[] split = MCrypt.getInstance().decryptToString(key).split(Constants.BACKTICK);
        Long candidateId = Long.parseLong(split[0]);
        Long requestId = Long.parseLong(split[1]);

        BaseResponse response = new BaseResponse();
        String cancelStatus = accommodationRequestService.deleteAccommodationRequest(candidateId, requestId);
        if (cancelStatus.equals(Constants.SAVED)) {
            response.setStatus(ModelConstants.SUCCESS);
            response.setMessage(commonResponseUtil.getMessage("message.cancelled.accommodation.request"));
        } else {
            response.setStatus(commonResponseUtil.getMessage("message.failure.cancelling.accommodation.request"));
        }
        return response;
    }

    private void setExportButton(ModelMap map, String splitBaseUrl) {
        HeaderForm headerForm = (HeaderForm) map.getAttribute(ModelConstants.HEADER_FORM);
        if (headerForm != null) {
            headerForm.setAdditionalButtonProperties(true, ModelConstants.BUTTON_PINK, commonResponseUtil.getMessage("message.label.other.candidate.report"),
                    ModelConstants.FA_FILE_EXCEL);
            map.addAttribute("excelUrl", splitBaseUrl + commonResponseUtil.getMessage("url.dean.approval.excel.report.download"));
        }
    }


    @GetMapping("${url.dean.approval.excel.report.download}")
    public void downloadRequestExcelReport(@RequestParam Map<String, String> allParams, PaginationForm form, HttpServletResponse response, HttpServletRequest request) throws Exception {

        commonResponseUtil.getAdditionalParams(allParams, form);
        List<OtherCandidateRequestDto> otherCandidateRequestList = deanOtherCandidateRequestService.getOtherCandidateRequestList(form,
                otherCandidateUrl.replace("/", "") + (request.getRequestURI().contains(interviewUrl) ? interviewUrl : ModelConstants.EMPTY_STRING)
                , false, Collections.emptyList(),
                request.getRequestURI().contains(interviewUrl) ? CategoryEnum.INTERVIEWS.getValue().toLowerCase() : ModelConstants.EMPTY_STRING, request);
        try {
            Workbook workbook = deanOtherCandidateRequestService.generateOtherCandidateListExcelReport(otherCandidateRequestList, otherCandidateUrl);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            byte[] excelBytes = bos.toByteArray();
            response.setContentType(FileUploadConstants.XLSX);
            String fileName = commonResponseUtil.getMessage("message.other.candidate.list.filename") + Strings.EMPTY + FileUploadConstants.XLSX_EXTENSION;
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


    @GetMapping({"${url.pdf.download}" + "${id}", "${url.pdf.download}" + "${url.stay.extension}"  + "${id}"})
    public ResponseEntity<Resource> downloadAccommodationRequestDetailsPDF(@PathVariable("id") String encryptedkey) throws Exception {
        String[] split = MCrypt.getInstance().decryptToString(encryptedkey).split(Constants.BACKTICK);
        Long candidateId = Long.parseLong(split[0]);
        Long requestId = Long.parseLong(split[1]);
        Long stayId = split.length > 5 ? Long.parseLong(split[5]) : 0L;
        Resource resource = accommodationRequestService.generateAccommodationRequestDetailsPdf(candidateId,
                requestId, encryptedkey, false, stayId);
        return Utility.prepareDownloadFile(resource);
    }


    @GetMapping(value = "${url.view}" + "${url.stay.extension}" + "${id}")
    public String viewStayExtensionRequest(@PathVariable("id") String key, HttpServletRequest request, ModelMap model) throws Exception {
        String[] split = MCrypt.getInstance().decryptToString(key).split(Constants.BACKTICK);
        Long candidateId = Long.parseLong(split[0]);
        Long requestId = Long.parseLong(split[1]);
        Long workflowId = Long.parseLong(split[2]);
        String validationStatus = split[3];
        Long stayId = Long.parseLong(split[5]);
        AccommodationRequestForm accommodationRequestForm = accommodationRequestService.getStayExtensionDetails(requestId,
                candidateId, stayId, validationStatus, WorkflowStatus.FROM_LOGIN);
        accommodationRequestForm.setOtherCandidateRequestDto(OtherCandidateRequestDto.builder().build());
        model.addAttribute("accommodationRequestForm", accommodationRequestForm);
        commonResponseUtil.updateCommonModelAttributes(model, request);
        /*CandidateWorkflowEntity candidateWorkflowEntity = candidateWorkflowRepository.findByAuthorityTypeContainingIgnoreCaseAndApplicationIdAndActiveFlag("Dean",
                requestId, ModelConstants.STATUS_ACTIVE).orElse(null);

        if(Objects.nonNull(candidateWorkflowEntity)){
            key = Optional.ofNullable(regenerateKey(candidateId,requestId,candidateWorkflowEntity.getId(),candidateWorkflowEntity.getModifiedAt()))
                    .orElse(key);
        }*/

        request.getSession().setAttribute("requestKey", key);
        return HTMLPage.OTHER_CANDIDATE_REQUEST_VIEW;
    }

    @PostMapping(value = "${url.allocate}" + "${id}")
    public ResponseEntity<String> allocateCandidate(@PathVariable("id") String key,
                                               @RequestParam Long hostelId,
                                               @RequestParam String roomNo,
                                               @RequestParam String status,
                                               @RequestParam String seatName) throws Exception {
        List<String> split = List.of(MCrypt.getInstance().decryptToString(key).split(Constants.BACKTICK));
        long requestId = Long.parseLong(split.get(1));
        long stayId = Long.parseLong(split.getLast());
        String saveStatus = accommodationRequestService.processAllocation(status, requestId, stayId, hostelId, roomNo, seatName);
        return ResponseEntity.ok(saveStatus);
    }

    @PostMapping(value = "${url.checkIn.checkOut}" + "${type}" + "${id}")
    public ResponseEntity<String> updateCheckInOrCheckOut(@PathVariable("id") String id, @PathVariable("type") String status) throws Exception {
        List<String> split = List.of(MCrypt.getInstance().decryptToString(id).split(Constants.BACKTICK));
        long requestId = Long.parseLong(split.get(1));
        long stayId = Long.parseLong(split.getLast());
        String saveStatus = accommodationRequestService.updateCheckInCheckOut(requestId, stayId, status);
        return ResponseEntity.ok(saveStatus);
    }

    @PostMapping(value = "${url.save}")
    public ResponseEntity<String> saveDetails(
            @ModelAttribute AccommodationRequestForm accommodationRequestForm,
            HttpServletRequest request) {
        try {
            String key = (String) request.getSession().getAttribute("requestKey");
            if (key == null) {
                return ResponseEntity.badRequest().body("Session expired or invalid request");
            } else {
                String[] split = MCrypt.getInstance().decryptToString(key).split(Constants.BACKTICK);
                Long candidateId = Long.parseLong(split[0]);
                Long requestId = Long.parseLong(split[1]);
                Long workflowId = Long.parseLong(split[2]);
                Long stayId = split.length > 5 ? Long.parseLong(split[5]) : 0;
                CandidateAppointmentRequestDto dto = accommodationRequestForm.getCandidateAppointmentRequestDto();
                if (dto == null) {
                    dto = CandidateAppointmentRequestDto.builder().build();
                    accommodationRequestForm.setCandidateAppointmentRequestDto(dto);
                }
                accommodationRequestService.saveForm(requestId, candidateId, accommodationRequestForm, stayId);
                return ResponseEntity.ok(Constants.UPDATED);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Constants.ERROR);
        }
    }

    @PostMapping({"${url.resend.mail}" + "${id}",  "${url.resend.mail}" + "${url.stay.extension}" + "${id}"})
    @ResponseBody
    public BaseResponse reSendMail(@PathVariable("id") String key, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        String[] split = MCrypt.getInstance().decryptToString(key).split(Constants.BACKTICK);
        long stayId = 0L;
        if (split.length > 5) {
            stayId = Long.parseLong(split[5]);
        }
        return CommonResponseUtil.updateResponseByStatus(accommodationRequestService.resendMail(request, Long.valueOf(split[0]), Long.valueOf(split[1]), stayId) ?
                true : false, "response.mail.success", "response.mail.error");
    }

    @PostMapping({"${url.bulk.approve.reject}", "${url.bulk.approve.reject}" + "${url.stay.extension}"})
    @ResponseBody
    public BaseResponse bulkApproveReject(@RequestBody BulkApprovalRejectDto dto, HttpServletRequest request) {
        String saveStatus = accommodationRequestService.bulkApproveReject(dto, request);
        String status = Constants.ERROR, message;
        if (WorkflowStatus.APPROVED.getStatus().equals(dto.getApprovalStatus())) {
            if (Constants.SAVED.equals(saveStatus)) {
                status = Constants.SUCCESS;
                message = commonResponseUtil.getMessage("message.request.approved.successfully");
            } else {
                message = commonResponseUtil.getMessage("message.request.approved.failed");
            }
        } else {
            if (Constants.SAVED.equals(saveStatus)) {
                status = Constants.SUCCESS;
                message = commonResponseUtil.getMessage("message.request.rejected.successfully");
            } else {
                message = commonResponseUtil.getMessage("message.request.reject.failed");
            }
        }
        return new BaseResponse(message, status);
    }
}
