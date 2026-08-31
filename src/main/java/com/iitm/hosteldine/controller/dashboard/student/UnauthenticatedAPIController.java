package com.iitm.hosteldine.controller.dashboard.student;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.OptionDto;
import com.iitm.hosteldine.dto.OtherCandidate.CandidateAppointmentRequestDto;
import com.iitm.hosteldine.dto.OtherCandidate.StayExtensionRequestDto;
import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentAppointmentRequestDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentBlackListDetailDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentHostelRoomVacatingRequestDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentWorkflowDto;
import com.iitm.hosteldine.dto.dean.*;
import com.iitm.hosteldine.dto.student.*;
import com.iitm.hosteldine.form.AccommodationRequestForm;
import com.iitm.hosteldine.form.StayExtensionRequestForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.dashboard.student.StudentWorkflowEntity;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.OtherCandidate.AccommodationRequestService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentBioDataService;
import com.iitm.hosteldine.service.dashboard.student.StudentAppointmentRequestService;
import com.iitm.hosteldine.service.dashboard.student.StudentHostelRoomVacatingRequestService;
import com.iitm.hosteldine.service.dashboard.student.StudentWorkflowService;
import com.iitm.hosteldine.service.dean.DeanDashboardService;
import com.iitm.hosteldine.service.dean.DeanMessRebateService;
import com.iitm.hosteldine.service.dean.StudentAccommodationRequestService;
import com.iitm.hosteldine.service.dean.StudentBulkAccommodationService;
import com.iitm.hosteldine.service.studentDashboard.GuestAccommodationRequestService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.UrlUtility;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.public.api}")
public class UnauthenticatedAPIController {
    private final GuestAccommodationRequestService guestAccommodationRequestService;
    private final SimsConfigDataService simsConfigDataService;
    private final MessageSource messageSource;
    private final Utility utility;
    private final AccommodationRequestService accommodationRequestService;

    private final StudentAppointmentRequestService studentAppointmentRequestService;
    private final StudentAccommodationRequestService studentAccommodationRequestService;
    private final StudentBioDataService studentBioDataService;
    private final CommonResponseUtil commonResponseUtil;
    private final StudentWorkflowService studentWorkflowService;
    private final DeanMessRebateService deanMessRebateService;
    private final StudentHostelRoomVacatingRequestService studentHostelRoomVacatingRequestService;
    private final FileService fileService;

    @Value("${url.dean.approval.mess.rebate}")
    private String messRebate;

    @Value("${url.student.vacating}")
    private String studentVacatingUrl;

    @GetMapping("${url.guest.student.view}" + "${id}")
    public String getRoomDetails(@PathVariable String id, PaginationForm form, ModelMap map, HttpServletRequest request) {
        StudentRoomDTO studentRoomDTO = new StudentRoomDTO();
        studentRoomDTO.setStudentDetailString(id);
        StudentGuestAccomDTO studentGuestAccomDTO = new StudentGuestAccomDTO();
        try {
            String paramString = new MCrypt().decryptToString(id);
            if (StringUtils.isNotEmpty(paramString)) {
                String[] split = paramString.split(Constants.BACKTICK);
                studentRoomDTO.setStudentId(split[0]);
                studentRoomDTO.setRequestId(Integer.parseInt(split[1]));
                studentRoomDTO.setWardenId(Long.parseLong(split[3]));
                studentRoomDTO.setParentRequestId(Integer.parseInt(split[5]));
//                studentRoomDTO.setPaymentStatus(split[4]);
//                studentRoomDTO.setWardenApprovalStatus(split[5]);
                studentRoomDTO.setStayType(split[6]);
                studentGuestAccomDTO = guestAccommodationRequestService.getGuestAccomForm(studentRoomDTO);
            }
        } catch (Exception e) {

        }
        studentGuestAccomDTO.setStudentDetailString(id);

        map.addAttribute("GUEST_MAILVIEW_MAX_TODATE", simsConfigDataService.getSimConfigValue(SimsConfigDataService.GUEST_MAILVIEW_MAX_TODATE));
        map.addAttribute("studentGuestAccomDTO", studentGuestAccomDTO);
        map.addAttribute("studentRoomDTO", studentRoomDTO);
        map.addAttribute("isUnauthenticatedView", true);
        map.addAttribute("wardenNotes", guestAccommodationRequestService.getWardenNote(studentGuestAccomDTO));
        commonResponseUtil.updateCommonModelAttributes(map, request);
        return HTMLPage.GUEST_STUDENT_ROOM_VIEW_DETAILS;
    }


    @PostMapping("${url.guest.student.action}")
    public @ResponseBody BaseResponse saveActionOnRequest(@ModelAttribute OverrideApproveGuestAccomDTO overrideApproveGuestAccomDTO, HttpServletRequest request,
                                                          HttpServletResponse response, RedirectAttributes redirectAttrs) throws IOException {
        String message = null, status = null;
        BaseResponse baseResponse = new BaseResponse();
        boolean isUpdated = false;
        try {
            isUpdated = guestAccommodationRequestService.updateWardenStatusOnRequest(overrideApproveGuestAccomDTO, request);
        } catch (Exception e) {
            e.printStackTrace();
            message = e.getMessage();
            status = messageSource.getMessage("response.status.error", null, Locale.getDefault());
            redirectAttrs.addFlashAttribute("response", new BaseResponse(message, status));
            redirectAttrs.addFlashAttribute(Constants.MODAL_ERROR, true);
            return baseResponse;
        }

        if (isUpdated) {
            message = messageSource.getMessage("response.student.room.update.success", null, Locale.getDefault());
            status = messageSource.getMessage("response.status.success", null, Locale.getDefault());
        } else {
            message = messageSource.getMessage("response.student.room.update.failed", null, Locale.getDefault());
            status = messageSource.getMessage("response.status.error", null, Locale.getDefault());
            redirectAttrs.addFlashAttribute(Constants.MODAL_ERROR, true);
        }

        baseResponse.setMessage(message);
        baseResponse.setStatus(status);
        redirectAttrs.addFlashAttribute(Constants.RESPONSE, baseResponse);
        return baseResponse;
    }

    @GetMapping(value = "${url.other.candidate.user}" + "${type}" + "${status}")
    public String getOtherCandidateApproval(@PathVariable("type") String type, @PathVariable("status") String status,
                                            ModelMap model, HttpServletRequest request) throws Exception {

        if (WorkflowStatus.VIEW.getStatus().equalsIgnoreCase(type)) {
            String[] split = MCrypt.getInstance().decryptToString(status).split(Constants.BACKTICK);
            Long candidateId = Long.parseLong(split[0]);
            Long requestId = Long.parseLong(split[1]);
            AccommodationRequestForm accommodationRequestForm;
            if (split.length > 5) {
                Long stayId = Long.parseLong(split[2]);
                accommodationRequestForm = accommodationRequestService.getStayExtensionDetails(requestId, candidateId, stayId, WorkflowStatus.APPROVE.getStatus(),
                        WorkflowStatus.FROM_EMAIL);
            } else {
                accommodationRequestForm = accommodationRequestService.requestView(candidateId,
                        requestId, WorkflowStatus.APPROVE.getStatus(), WorkflowStatus.FROM_EMAIL);
            }
            model.addAttribute("accommodationRequestForm", accommodationRequestForm);
            commonResponseUtil.updateCommonModelAttributes(model, request);
            return HTMLPage.OTHER_CANDIDATE_REQUEST_VIEW;
        } else if (WorkflowStatus.APPROVE.getStatus().equalsIgnoreCase(type) ||
                WorkflowStatus.APPROVED.getStatus().equalsIgnoreCase(type) ||
                WorkflowStatus.REJECTED.getStatus().equalsIgnoreCase(type)) {
            model.addAttribute("status", type);
            model.addAttribute("key", status);
            return HTMLPage.APPROVE_REJECT_REQUEST;
        } else {
            return null;
        }
    }

    @PostMapping(value = "${url.other.candidate.user}" + "${status}")
    public @ResponseBody BaseResponse approveOrRejectAccommodationRequest(@PathVariable("status") String key,
                                                                          @RequestParam("reason") String reason, HttpServletRequest request) throws Exception {
        String[] split = MCrypt.getInstance().decryptToString(key).split(Constants.BACKTICK);
        Long candidateId = Long.parseLong(split[0]);
        Long requestId = Long.parseLong(split[1]);
        Long workflowId ;
        String modifiedAt;
        String status;
        Long stayId;
        if(split.length > 5) {
            stayId = Long.parseLong(split[2]);
            workflowId = Long.parseLong(split[3]);
            modifiedAt = split[4];
            status = split[5];
        }
        else{
            workflowId = Long.parseLong(split[2]);
            modifiedAt = split[3];
            status = split[4];
            stayId = 0L;
        }
        String rejectedReason;
        String approvalNotes;
        if (WorkflowStatus.APPROVED.getStatus().equalsIgnoreCase(status)) {
            approvalNotes = reason;
            rejectedReason = null;
        } else {
            rejectedReason = reason;
            approvalNotes = null;
        }
        AccommodationRequestForm form = AccommodationRequestForm.builder()
                .candidateAppointmentRequestDto(CandidateAppointmentRequestDto.builder().notes(approvalNotes).build())
                .rejectReason(rejectedReason)
                .build();
        String result = "";
        if(stayId > 0){
            StayExtensionRequestForm stayExtensionRequestForm =
                    StayExtensionRequestForm.builder()
                            .stayExtensionRequestDto(StayExtensionRequestDto.builder().build())
                            .build();
            form.setStayExtensionRequestForm(stayExtensionRequestForm);
            result = accommodationRequestService.processStayRequest(form,status,candidateId,stayId,requestId,workflowId,modifiedAt,request);
        }
        else{
            result = accommodationRequestService.updateStatus(form, status, candidateId, requestId, workflowId, modifiedAt, request);
        }
        String message;
        String statusType = Constants.ERROR;
        if (result.equalsIgnoreCase(Constants.SAVED)) {
            statusType = Constants.SUCCESS;
            if (status.equalsIgnoreCase(WorkflowStatus.APPROVED.getStatus())) {
                message = commonResponseUtil.getMessage("message.request.approved.successfully");
            } else {
                message = commonResponseUtil.getMessage("message.request.rejected.successfully");
            }
        } else {
            message = result;
        }
        return new BaseResponse(message, statusType);
    }

    @GetMapping("/" + "${url.dean.student.accommodation.request}" + "${url.approve.reject}")
    public String getApprovalConfirmationPage(@RequestParam String data, @RequestParam String status, ModelMap map, HttpServletRequest request) throws Exception {
        List<String> split = List.of(MCrypt.getInstance().decryptToString(data).split(Constants.BACKTICK));
        StudentAppointmentRequestDto studentAppointmentRequestDto = studentAppointmentRequestService.getStudentAppointmentRequestById(Long.valueOf(split.get(2)));
        studentAppointmentRequestDto.setEncryptedString(
                studentAppointmentRequestService.encryptWorkflowIdAndModifiedAt(LocalDateTime.parse(split.get(1)), Long.valueOf(split.getFirst())));
        StudentWorkflowDto studentWorkflowDto = studentWorkflowService.getWorkflowById(Long.valueOf(split.getFirst()));
        map.addAttribute("studentAppointmentRequestDto", studentAppointmentRequestDto);
        map.addAttribute("studentWorkflowDto", studentWorkflowDto);
        map.addAttribute("status", status);
        return HTMLPage.STUDENT_APPOINTMENT_APPROVAL_CONFIRMATION;
    }

    @PostMapping(value = "/" + "${url.dean.student.accommodation.request}" + "${url.update}")
    public ResponseEntity<String> updateApprovalStatus(@ModelAttribute StudentAppointmentRequestDto studentAppointmentRequestDto, HttpServletRequest request) throws Exception {
        List<String> split = List.of(appendAndDecryptString(studentAppointmentRequestDto).split(Constants.BACKTICK));
        String status = studentAccommodationRequestService.updateApprovalStatus(split, studentAppointmentRequestDto, request);
        return ResponseEntity.ok(status);
    }

    private String appendAndDecryptString(StudentAppointmentRequestDto studentAppointmentRequestDto) throws Exception {
        String decryptString = MCrypt.getInstance().decryptToString(studentAppointmentRequestDto.getEncryptedString());
        List<String> split = List.of(decryptString.split(Constants.BACKTICK));
        return split.getFirst() + Constants.BACKTICK + split.get(1) + Constants.BACKTICK +
                (WorkflowStatus.REJECTED.getStatus().equals(studentAppointmentRequestDto.getStatus()) ?
                        studentAppointmentRequestDto.getStatus() + Constants.BACKTICK + studentAppointmentRequestDto.getRejectionReason() :
                        studentAppointmentRequestDto.getStatus()
                );
    }

    @GetMapping("/" + "${url.dean.student.accommodation.request}" + "${url.view}")
    public String getStudentAccommodationRequestView(@RequestParam String data, @RequestParam String status, ModelMap map, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        List<String> split = List.of(MCrypt.getInstance().decryptToString(data).split(Constants.BACKTICK));
        String studentId = studentAccommodationRequestService.getNonNullValue(split.get(3));
        Long requestId = Long.parseLong(split.get(2));
        StudentAppointmentRequestDto studentAppointmentRequestDto = studentAppointmentRequestService.getStudentAppointmentRequestById(requestId);
        StudentDetailsDto studentDetails = studentBioDataService.getFullStudentDetails(studentId);
        AllStudentsDetailsViewDto allStudentsDetailsViewDto = studentAccommodationRequestService.getAllStudentDetails(studentId);
        StudentBlackListDetailDto studentBlackListDetailDto = studentAccommodationRequestService.getBlackListDetails(studentId);
        List<StudentWorkflowDto> studentWorkflowDtoList = studentAppointmentRequestService.getByStudentIdAndRequestIdAndActiveFlag(requestId, studentId, WorkflowStatus.DEFAULT.getStatus());
        StudentWorkflowEntity studentWorkflowEntity = studentWorkflowService.getStudentWorkflowDetails(requestId, studentId);
        studentAccommodationRequestService.setDtoValues(studentAppointmentRequestDto, split, studentWorkflowEntity, studentWorkflowDtoList);
        studentAppointmentRequestDto.setStudentWorkflowDto(studentWorkflowDtoList);
        commonResponseUtil.updateCommonModelAttributes(map, request);
        map.addAttribute("studentAppointmentRequestDto", studentAppointmentRequestDto);
        map.addAttribute("studentBlackListDetailDto", studentBlackListDetailDto);
        map.addAttribute("allStudentsDetailsViewDto", allStudentsDetailsViewDto);
        map.addAttribute("studentDetails", studentDetails);
        map.addAttribute("status", status);
        return HTMLPage.STUDENT_APPOINTMENT_REQUEST_VIEW;
    }

    private final DeanDashboardService deanDashboardService;
    private final StudentBulkAccommodationService studentBulkAccommodationService;
    @Value("${url.dean.student.bulk.accommodation}")
    private String bulkUploadBaseUrl;

    @GetMapping("${url.dean.student.bulk.accommodation.edit}" + "${id}")
    public String editStudentBulkUpload(@PathVariable String id, @RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request) throws Exception {
        //Getting dynamic tabs
        DeanApprovalDto deanApprovalDto = deanDashboardService.getDeanMenuList();
        map.addAttribute("deanApprovalDto", deanApprovalDto);
        DeanApprovalDto columnList = deanDashboardService.getDeanMenuListById(bulkUploadBaseUrl.replace("/", ""));

        map.addAttribute("columnDto", columnList);
        //Setting dynamic filter values
        if (!form.isSearchFilter()) {
            List<String> filterList = List.of("validationStatus", "approvalFromDate", "approvalToDate", "submittedFromDate",
                    "fileName", "eventName");
            filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
        }
        commonResponseUtil.getAdditionalParams(allParams, form);

        map.addAttribute("showUploadCard", true);
        map.addAttribute("isDeanLogin", false);
        map.addAttribute("isFacultyLogin", true);
        map.addAttribute("diningStatusList", Stream.of("Required", "Not Required").map(d -> new OptionDto(d.toLowerCase(), d)).collect(Collectors.toList()));
        map.addAttribute("sessionList", Stream.of("All", "Limited Session").map(d -> new OptionDto(d.toLowerCase(), d)).collect(Collectors.toList()));
        StudentBulkAccommodationDto dto = studentBulkAccommodationService.getStudentBulkAccommodation(id);
        map.addAttribute("studentBulkAccommodationDto", dto);
        map.addAttribute("validationStatusList", deanDashboardService.getValidationStatusList(SimsConfigDataService.VALIDATION_STATUS));

        //fetch method
        List<StudentAccomBulkRequestDto> studentAccomBulkRequestDto = studentBulkAccommodationService.getBulkUploadList(form, bulkUploadBaseUrl.replace("/", ""), true, columnList);
        commonResponseUtil.updateCommonModelAttributes2(map, request, studentAccomBulkRequestDto, form);
        return HTMLPage.DEAN_DASHBOARD_MENU_LIST;
    }

    @GetMapping("${url.dean.student.bulk.accommodation.view}" + "${id}")
    public String viewStudentBulkRequest(@PathVariable String id, PaginationForm form, ModelMap map, HttpServletRequest request) {

        map.addAttribute("isDeanLogin", false);
        map.addAttribute("isFacultyLogin", false);

        StudentBulkAccommodationDto dto = studentBulkAccommodationService.getStudentBulkAccommodation(id);
        map.addAttribute("studentBulkAccommodationDto", dto);
        commonResponseUtil.updateCommonModelAttributes(map, request);

        return HTMLPage.STUDENT_BULK_UPLOAD_VIEW;
    }

    @GetMapping("${url.dean.student.bulk.accommodation.view.and.update.status}" + "${id}")
    public String viewAndUpdateStatusStudentBulkRequest(@PathVariable String id, PaginationForm form, ModelMap map, HttpServletRequest request) {

        map.addAttribute("isDeanLogin", true);
        map.addAttribute("isFacultyLogin", false);

        StudentBulkAccommodationDto dto = studentBulkAccommodationService.getStudentBulkAccommodation(id);
        map.addAttribute("studentBulkAccommodationDto", dto);
        map.addAttribute("isMail", true);
        commonResponseUtil.updateCommonModelAttributes(map, request);
        return HTMLPage.STUDENT_BULK_UPLOAD_VIEW;
    }


    @PostMapping("${url.dean.student.bulk.accommodation.update.and.approve}" + "${status}")
    @ResponseBody
    public BaseResponse updateAndApprove(@PathVariable String status, @ModelAttribute StudentBulkAccommodationDto studentBulkAccommodationDto, HttpServletRequest request,
                                   HttpServletResponse response, RedirectAttributes redirectAttrs) throws IOException {
        String message;
        String status1;
        String s = "redirect:" + bulkUploadBaseUrl;
        BaseResponse baseResponse = new BaseResponse();
        Boolean updateAndApprove = studentBulkAccommodationService.updateAndApprove(studentBulkAccommodationDto, status, request);

        if (!updateAndApprove) {
            redirectAttrs.addFlashAttribute("Error",
                    messageSource.getMessage("response.student.bulk.upload.status.failed", null, Locale.getDefault()));
            message = messageSource.getMessage("response.student.bulk.upload.status.failed", null, Locale.getDefault());
            status1 = messageSource.getMessage("response.status.failure", null, Locale.getDefault());
        } else {
            message = messageSource.getMessage("response.student.bulk.upload.status.updated", null, Locale.getDefault());
            status1 = messageSource.getMessage("response.status.success", null, Locale.getDefault());
            redirectAttrs.addFlashAttribute("response", new BaseResponse(message, status1));
        }
        baseResponse.setStatus(status1);
        baseResponse.setMessage(message);
        return baseResponse;
    }

    @GetMapping("${url.dean.student.bulk.accommodation}" + "${url.download}" + "${id}")
    public void downloadExcel(@PathVariable String id, @ModelAttribute PaginationForm pageForm,
                              HttpServletResponse response, HttpServletRequest request) {
        pageForm.setPage(1);
        pageForm.setSize(500);
        try {
            String fileName = ExcelConstants.STUDENT_BULK_UPLOAD_SHEET_NAME + FileUploadConstants.XLSX_EXTENSION;
            Workbook workbook = studentBulkAccommodationService.downloadFile(id);
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

    @GetMapping(value = "${url.dean.approval.mess.rebate}" + "${url.view}")
    public String getMessRebateDetails(@RequestParam String data, ModelMap map, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        Boolean status = Utility.checkRequestType(data, 4, 3);
        try {
            DeanMessRebateDto messRebateDto = new DeanMessRebateDto();
            String[] split = Utility.decryptData(data);
            String studentId = Utility.getValueOrDefault(split, 0, Strings.EMPTY);
            Long id = Utility.getLongValueOrDefault(split, 1, null);
            String authorityType = Utility.getValueOrDefault(split, 2, Strings.EMPTY);
            messRebateDto = deanMessRebateService.getMessRebateDetails(studentId, id, authorityType);
            map.addAttribute("deanMessRebateDto", messRebateDto);
            map.addAttribute("mail", true);
            commonResponseUtil.updateCommonModelAttributes(map, request);
            return HTMLPage.DEAN_DASHBOARD_MESS_REBATE_DETAILS;
        } catch (Exception e) {
            e.printStackTrace();
            commonResponseUtil.exceptionMessageHandling(e, redirectAttributes);
        }
//        if (status) {
//        } else {
//            commonResponseUtil.invalidAccess(redirectAttributes);
//        }
        return null;
    }

    @PutMapping("${url.dean.approval.mess.rebate.update.status}")
    public @ResponseBody BaseResponse updateMessRebateStatus(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        String action = (String) requestBody.get("action");
        DeanMessRebateDto deanMessRebateDto = new ObjectMapper().convertValue(requestBody.get("deanMessRebateDto"), DeanMessRebateDto.class);
        boolean updateStatus = false;
        String url = UrlUtility.getBaseURL(request) + messRebate;
        // Handle the form submission based on the action
        if (WorkflowStatus.OVERRIDE_AND_APPROVED.getStatus().equals(action)) {
            updateStatus = deanMessRebateService.updateMessRebateStatusFromView(deanMessRebateDto, WorkflowStatus.APPROVED.getStatus(), url, request);
        } else if (WorkflowStatus.REJECTED.getStatus().equals(action)) {
            updateStatus = deanMessRebateService.updateMessRebateStatusFromView(deanMessRebateDto, WorkflowStatus.REJECTED.getStatus(), url, request);
        }
        return CommonResponseUtil.updateResponseByStatus(updateStatus, "response.update.success", "response.update.error");
    }

    @GetMapping(value = "${url.student.vacating}"+"${url.view}")
    public String getVacatingStudentDetails(@RequestParam String data, ModelMap map, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        Boolean status = Utility.checkRequestType(data, 4, 3);
        try {
            StudentHostelRoomVacatingRequestDto studentHostelRoomVacatingRequestDto = new StudentHostelRoomVacatingRequestDto();
            String[] split = Utility.decryptData(data);
            String studentId = Utility.getValueOrDefault(split, 0, Strings.EMPTY);
            Long id = Utility.getLongValueOrDefault(split, 1, null);
            String authorityType = Utility.getValueOrDefault(split, 2, Strings.EMPTY);
            if(Constants.USER_ROLE_DEAN.equalsIgnoreCase(authorityType)) {  //Need to remove
                authorityType = Constants.USER_ROLE_WARDEN;
            }
            studentHostelRoomVacatingRequestDto = studentHostelRoomVacatingRequestService.getVacatingStudentDetails(studentId, id, authorityType);
            Map<String, String> paintingTypeMap = simsConfigDataService.getSimConfigValueAsMap(SimsConfigDataService.PAINTING_TYPE);
//				VacatingHostelStudentWorkflowDto vacatingHostelStudentWorkflowDto = studentHostelRoomVacatingRequestService.getVacatingHostelStudentWorkflow(Long.valueOf(workflowId));
            map.addAttribute("paintingTypeMap", paintingTypeMap);
            map.addAttribute("studentHostelRoomVacatingRequestDto", studentHostelRoomVacatingRequestDto);
            commonResponseUtil.updateCommonModelAttributes(map, request);
            return HTMLPage.DEAN_DASHBOARD_VACATING_STUDENT_DETAILS;
        } catch (Exception e) {
            e.printStackTrace();
            commonResponseUtil.exceptionMessageHandling(e, redirectAttributes);
        }
//        if (status) {
//        } else {
//            commonResponseUtil.invalidAccess(redirectAttributes);
//        }
        return null;
    }

    @PostMapping("${url.student.vacating}"+"${url.student.vacating.update.status}")
    public @ResponseBody BaseResponse updateStudentVacatingApprovalStatus(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        StudentHostelRoomVacatingRequestDto dto = new ObjectMapper().convertValue(requestBody.get("dto"), StudentHostelRoomVacatingRequestDto.class);
        boolean updateStatus = false;
        String url = UrlUtility.getBaseURL(request) + studentVacatingUrl;
        updateStatus = studentHostelRoomVacatingRequestService.updateStudentVacatingApprovalStatus(dto, WorkflowStatus.APPROVED.getStatus(), url);
        return CommonResponseUtil.updateResponseByStatus(updateStatus, "response.update.success", "response.update.error");
    }
}
