package com.iitm.hosteldine.controller.dashboard.student;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.dto.dashboard.student.HostelVacatingAllowedStudentDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentHostelRoomVacatingRequestDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.model.dashboard.student.StudentHostelRoomVacatingRequestEntity;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.dashboard.student.HostelVacatingAllowedStudentService;
import com.iitm.hosteldine.service.dashboard.student.StudentHostelRoomVacatingRequestService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.UrlUtility;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "${url.hostel.room.vacating.form}")
public class HostelRoomVacatingFormController {
    private final HostelVacatingAllowedStudentService hostelVacatingAllowedStudentService;
    private final HostelMasterService hostelMasterService;
    private final SimsConfigDataService simsConfigDataService;
    private final CommonResponseUtil commonResponseUtil;
    private final StudentHostelRoomVacatingRequestService studentHostelRoomVacatingRequestService;
    private final MessageSource messageSource;


    @Value("${url.hostel.room.vacating.form}")
    private String baseUrl;

    @Value("${url.student.vacating}")
    private String studentVacatingUrl;

    @GetMapping(value = "${url.widget}")
    public String getHostelRoomVacatingFormWidget(ModelMap model) {
        StudentHostelRoomVacatingRequestEntity studentHostelRoomVacatingRequestEntity =
                studentHostelRoomVacatingRequestService.getStudentInfoDetails();
        boolean isDateAndStatusEmpty = true;
        LocalDateTime createdAt = null;
        String status = null;
        if (studentHostelRoomVacatingRequestEntity != null) {
            createdAt = studentHostelRoomVacatingRequestEntity.getCreatedAt();
            status = studentHostelRoomVacatingRequestEntity.getHostelOrWardenApprovalStatus();
            isDateAndStatusEmpty = (createdAt == null && status == null);
        }
        model.addAttribute(StudentConstants.CREATED_AT.getStudentConstant(), createdAt);
        model.addAttribute(StudentConstants.STATUS.getStudentConstant(), status);
        model.addAttribute(
                messageSource.getMessage(StudentConstants.STUDENT_HOSTEL_VACATING_REQ_DTO.getStudentConstant(), null, Locale.getDefault()),
                studentHostelRoomVacatingRequestEntity
        );
        model.addAttribute(
                messageSource.getMessage(StudentConstants.IS_DATE_AND_STATUS_EMPTY.getStudentConstant(), null, Locale.getDefault()),
                isDateAndStatusEmpty
        );

        return HTMLPage.HOSTEL_ROOM_VACATING_FORM_WIDGET;
    }


    @GetMapping
    public String getHostelRoomVacatingForm(ModelMap model, HttpServletRequest request) throws Exception {
        HostelVacatingAllowedStudentDto hostelVacatingAllowedStudentDto = hostelVacatingAllowedStudentService.getHostelVacatingAllowedStudentByStudentId();
        commonResponseUtil.updateCommonModelAttributes(model, request);
        StudentBioDataFormDetailDto studentDetailsDto = hostelVacatingAllowedStudentService.getStudentDetails();
        StudentHostelRoomVacatingRequestDto studentHostelRoomVacatingRequestDto = studentHostelRoomVacatingRequestService.getStudentDetails();
        List<HostelMasterDto> hostelList = hostelMasterService.getHostelList();
        String donationType = simsConfigDataService.getSimConfigValue(SimsConfigDataService.DONATION_TYPE);
        List<String> donationTypeList = Arrays.stream(donationType.split(ModelConstants.COMMA))
                .map(String::trim)
                .map(value -> value.replaceAll(ModelConstants.SPLIT_SPECIAL_CHARS, ModelConstants.EMPTY_STRING))
                .collect(Collectors.toList());
        HostelVacatingAllowedStudentDto studentBalance = studentHostelRoomVacatingRequestService.getStudentNegativeBalance();
        String successNote = ModelConstants.EMPTY_STRING;
        String cancelOrRejectedNote = ModelConstants.EMPTY_STRING;
        commonResponseUtil.updateCommonModelAttributes(model, request);
        model.addAttribute(
                messageSource.getMessage(StudentConstants.HOSTEL_ROOM_VACATING_ALLOWED_STUDENT_DTO.getStudentConstant(), null, Locale.getDefault())
                , hostelVacatingAllowedStudentDto);
        model.addAttribute(
                messageSource.getMessage(StudentConstants.STUDENT_HOSTEL_VACATING_REQ_DTO.getStudentConstant(), null, Locale.getDefault())
                , studentHostelRoomVacatingRequestDto);
        model.addAttribute(
                messageSource.getMessage(StudentConstants.STUDENT_DETAILS_DTO.getStudentConstant(), null, Locale.getDefault())
                , studentDetailsDto);
        model.addAttribute(
                messageSource.getMessage(StudentConstants.HOSTEL_LIST_DTO.getStudentConstant(), null, Locale.getDefault())
                , hostelList);
        model.addAttribute(messageSource.getMessage(StudentConstants.REASON_FOR_VACATING_LIST.getStudentConstant(), null, Locale.getDefault())
                , simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.REASON_FOR_VACATION));
        model.addAttribute(
                messageSource.getMessage(StudentConstants.DONATION_TYPE_LIST.getStudentConstant(), null, Locale.getDefault())
                , donationTypeList);
        if (studentHostelRoomVacatingRequestDto.getStudent() != null) {
            boolean negBal = false;
            String status = studentHostelRoomVacatingRequestDto.getHostelOrWardenApprovalStatus();
            if (status.equals(WorkflowStatus.APPROVED.getStatus())){
                successNote = messageSource.getMessage(StudentConstants.FORM_SUBMITTING_APPROVED_NOTE.getStudentConstant(), null, Locale.getDefault());
            }else if(studentBalance.getStudentBalance() < 0){
                successNote = messageSource.getMessage(StudentConstants.FORM_SUBMITTING_DUE_NOTE.getStudentConstant(), null, Locale.getDefault());
                negBal = true;
            }else if(status.equals(WorkflowStatus.VALIDATING.getStatus()) || status.equals(WorkflowStatus.PENDING.getStatus())){
                successNote = messageSource.getMessage(StudentConstants.FORM_SUBMITTING_PENDING_NOTE.getStudentConstant(), null, Locale.getDefault());
            }else if(status.equals(WorkflowStatus.CANCELLED.getStatus())){
                cancelOrRejectedNote = messageSource.getMessage(StudentConstants.FORM_SUBMITTING_CANCELLED_NOTE.getStudentConstant(), null, Locale.getDefault());
            }else if(status.equals(WorkflowStatus.REJECTED.getStatus())){
                cancelOrRejectedNote = messageSource.getMessage(StudentConstants.FORM_SUBMITTING_REJECTED_NOTE.getStudentConstant(), null, Locale.getDefault());
            }
            if (studentHostelRoomVacatingRequestDto.getHostelOrWardenApprovalStatus()!=null && studentHostelRoomVacatingRequestDto.getHostelOrWardenApprovalStatus().equals(WorkflowStatus.APPROVED.getStatus())){
				HeaderForm headerForm = (HeaderForm) model.getAttribute(ModelConstants.HEADER_FORM);
				if (headerForm != null) {
					headerForm.setAdditionalButtonProperties(true, ModelConstants.BUTTON_PINK,
							messageSource.getMessage("message.button.download.pdf", null, Locale.getDefault()),
							ModelConstants.FA_FA_DOWNLOAD);
				}
            }
            String formattedDonationAmount = studentHostelRoomVacatingRequestDto.getDonationAmount() != null
                    ? String.format(ModelConstants.TWO_DECIMAL_POINT, (double)studentHostelRoomVacatingRequestDto.getDonationAmount()) : "0.00";
            model.addAttribute(messageSource.getMessage(StudentConstants.STD_HOSTEL_ROOM_VACATING_DTO.getStudentConstant(), null, Locale.getDefault())
                    , studentHostelRoomVacatingRequestDto);
            model.addAttribute(messageSource.getMessage(StudentConstants.SUCCESS_NOTE.getStudentConstant(), null, Locale.getDefault())
                    , successNote);
            model.addAttribute("encryptedId", MCrypt.getInstance().encryptToText(String.valueOf(studentHostelRoomVacatingRequestDto.getId())));
            model.addAttribute(StudentConstants.CANCELLED_REJECTED_NOTE.getStudentConstant(), cancelOrRejectedNote);
            model.addAttribute(StudentConstants.STATUS.getStudentConstant(), status);
            model.addAttribute(messageSource.getMessage(StudentConstants.NEG_BAL.getStudentConstant(), null, Locale.getDefault()), negBal);
            model.addAttribute(StudentConstants.FORMATTED_DONATION_AMOUNT.getStudentConstant(), formattedDonationAmount);
            return HTMLPage.HOSTEL_ROOM_VACATING_VIEW;
        }
        return HTMLPage.HOSTEL_ROOM_VACATING_FORM;
    }

    @PostMapping(value = "${url.save}")
    public String saveStudentHostelRoomVacatingRequest(@Valid @ModelAttribute StudentHostelRoomVacatingRequestDto studentHostelRoomVacatingRequestDto, BindingResult bindingResult,
                                                       RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String url = UrlUtility.getBaseURL(request) + studentVacatingUrl;
        String status = studentHostelRoomVacatingRequestService.saveStudentHostelRoomVacatingForm(studentHostelRoomVacatingRequestDto, url);
        String vacatingReason = studentHostelRoomVacatingRequestDto.getVacatingReason();
        if (vacatingReason != null) {
            vacatingReason = vacatingReason.trim().replaceAll(ModelConstants.SPLIT_SPECIAL_CHARS, ModelConstants.EMPTY_STRING);
            studentHostelRoomVacatingRequestDto.setVacatingReason(vacatingReason);
        }
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, StudentConstants.HOSTEL_ROOM_VACATING_FORM_SAVE.getStudentConstant());
        return Constants.REDIRECT + baseUrl;
    }

    @GetMapping("${url.pdf.download}")
    public ResponseEntity<Resource> downloadStudentVacatingFormPDF(@RequestParam String studentId, @RequestParam String id) throws Exception {
        Long requestId = MCrypt.getInstance().decryptToLong(id);
        StudentHostelRoomVacatingRequestEntity studentHostelRoomVacatingRequestEntity = studentHostelRoomVacatingRequestService.getStudentHostelRoomVacatingById(requestId);
        if (!WorkflowStatus.APPROVED.getStatus().equalsIgnoreCase(studentHostelRoomVacatingRequestEntity.getHostelOrWardenApprovalStatus())) {
            return ResponseEntity.ok().body(null);
        }
        Resource resource = studentHostelRoomVacatingRequestService.generatePdf(studentId);
        return Utility.prepareDownloadFile(resource);
    }
}
