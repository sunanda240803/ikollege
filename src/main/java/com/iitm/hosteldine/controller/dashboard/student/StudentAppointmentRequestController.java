package com.iitm.hosteldine.controller.dashboard.student;


import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.dto.dashboard.student.ScholarsHodDetailDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentAppointmentRequestDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentWorkflowDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.dashboard.student.StudentAppointmentRequestEntity;
import com.iitm.hosteldine.service.StudentBioDataService;
import com.iitm.hosteldine.service.dashboard.student.ScholarsHodDetailsService;
import com.iitm.hosteldine.service.dashboard.student.StudentAppointmentRequestService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.scholars.stay.extension}")
public class StudentAppointmentRequestController {
    private final StudentAppointmentRequestService scholarsStayExtensionService;
    private final ScholarsHodDetailsService scholarsHodDetailsService;
    private final CommonResponseUtil commonResponseUtil;
    private final MessageSource messageSource;
    private final StudentAppointmentRequestService studentAppointmentRequestService;
    private String studentName;
    private final StudentBioDataService studentBioDataService;

    @Value("${url.scholars.stay.extension}")
    private String baseUrl;

    @GetMapping
    public String getScholarsStayExtension(PaginationForm form,ModelMap map, HttpServletRequest request) {
        Page<StudentAppointmentRequestDto> scholarsStayExtensionList = scholarsStayExtensionService.getScholarsStayExtensionList(form);
        StudentBioDataFormDetailDto studentBioDataFormDetailDto = studentBioDataService.getStudentDetails(Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase());
        boolean isGuideNameEmailPresent = isGuideNameAndEmailEmptyOrNull(studentBioDataFormDetailDto);
        map.addAttribute(StudentConstants.IS_GUIDE_NAME_EMAIL_EMPTY.getStudentConstant(), isGuideNameEmailPresent);
        commonResponseUtil.updateCommonModelAttributes(map, request,scholarsStayExtensionList,form);
        return HTMLPage.SCHOLARS_STAY_EXTENSION;
    }

    @GetMapping(value = "${url.get.form}")
    public String getScholarStayExtensionModal(ModelMap model, HttpServletRequest request) {
        StudentAppointmentRequestDto scholarsStayExtensionDto = commonResponseUtil.handleModalFormError(request,model,messageSource.getMessage(StudentConstants.FORM_KEY.getStudentConstant(), null,Locale.getDefault()), StudentAppointmentRequestDto.class);
        ScholarsHodDetailDto hodDetails = scholarsHodDetailsService.getScholarsHodDetailsList();
        StudentBioDataFormDetailDto studentBioDataFormDetailDto = studentBioDataService.getStudentDetails(Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase());
        studentName = Objects.requireNonNullElse(SecurityCtxUtil.userName().toUpperCase().substring(4,5), ModelConstants.EMPTY_STRING);
        LocalDate today = LocalDate.now();
        String maxDate = today.format(DateTimeFormatter.ofPattern(Constants.BACKEND_DATE_FORMAT));
        setThesisStatusMethod(scholarsStayExtensionDto, studentName);
        scholarsStayExtensionDto.setHodName(hodDetails.getHodName());
        scholarsStayExtensionDto.setHodEmail(hodDetails.getHodEmail());
        scholarsStayExtensionDto.setValidatingAuthority(studentBioDataFormDetailDto.getFacultyName());
        scholarsStayExtensionDto.setValidatingAuthorityEmail(studentBioDataFormDetailDto.getFacultyEmail());
        model.addAttribute(messageSource.getMessage(StudentConstants.MAX_DATE.getStudentConstant(), null,Locale.getDefault()), maxDate);
        model.addAttribute(messageSource.getMessage(StudentConstants.FORM_KEY.getStudentConstant(), null,Locale.getDefault()), scholarsStayExtensionDto);
        return HTMLPage.ADD_SCHOLARS_STAY_EXTENSION;
    }

    @PostMapping(value = "${url.save}")
    public String saveScholarsStayExtension(@Valid @ModelAttribute StudentAppointmentRequestDto scholarsStayExtensionDto, BindingResult bindingResult,
                                            RedirectAttributes redirectAttributes, HttpServletRequest request) {
        if(bindingResult.hasErrors()){
            commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes,bindingResult, scholarsStayExtensionDto);
            return "redirect:" + baseUrl;
        }
        String status = scholarsStayExtensionService.saveScholarsStayExtension(scholarsStayExtensionDto, request);
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, StudentConstants.SCHOLAR_STAY_EXTENSION_SAVE.getStudentConstant());
        return Constants.REDIRECT + baseUrl;
    }

    @GetMapping(value = "${url.get.view}" + ModelConstants.URL_ID)
    public String getScholarStayExtensionViewModal(@PathVariable(ModelConstants.ID) Long id, ModelMap model) {
//        StudentAppointmentRequestDto scholarsStayExtensionDto1 = scholarsStayExtensionService.getAccommodationRequest(id);
        String studentId = SecurityCtxUtil.userId().toUpperCase();
        StudentAppointmentRequestDto studentAppointmentRequestDto = studentAppointmentRequestService
                .getStudentAccommodationRequestDetails(studentId, id, Constants.CCW);
        List<StudentWorkflowDto> studentWorkflowDtoList = scholarsStayExtensionService.getByStudentIdAndRequestIdAndActiveFlag(id, studentId, WorkflowStatus.DEFAULT.getStatus());
        studentName = Objects.requireNonNullElse(SecurityCtxUtil.userName().toUpperCase().substring(4,5), ModelConstants.EMPTY_STRING);
        setThesisStatusMethod(studentAppointmentRequestDto, studentName);
        LocalDate maxDate = LocalDate.now().minusDays(1);
        studentAppointmentRequestDto.setStudentWorkflowDto(studentWorkflowDtoList);
        model.addAttribute(messageSource.getMessage(StudentConstants.STD_APPOINTMENT_DTO.getStudentConstant(), null,Locale.getDefault()), studentAppointmentRequestDto);
        model.addAttribute(messageSource.getMessage(StudentConstants.MAX_DATE.getStudentConstant(), null, Locale.getDefault()), maxDate);
        return HTMLPage.VIEW_SCHOLARS_STAY_EXTENSION;
    }

    @GetMapping(value = "${url.get.cancel}" + ModelConstants.URL_ID)
    public String getScholarStayExtensionCancelModal(@PathVariable(ModelConstants.ID) Long id, ModelMap model,HttpServletRequest request) {
        getScholarStayExtensionDto(id, model, request);
        return HTMLPage.CANCEL_SCHOLARS_STAY_EXTENSION;
    }

    @PostMapping(value = "${url.cancel}")
    public String cancelScholarStayExtension(@ModelAttribute StudentAppointmentRequestDto scholarsStayExtensionDto, RedirectAttributes redirectAttributes) {
        String status = scholarsStayExtensionService.cancelScholarsStayExtension(scholarsStayExtensionDto);
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, StudentConstants.SCHOLAR_STAY_EXTENSION_CANCEL.getStudentConstant());
        return Constants.REDIRECT + baseUrl;
    }

    @GetMapping(value = "${url.get.resend.mail}" + ModelConstants.URL_ID)
    public String getScholarStayExtensionResendEmailModal(@PathVariable(ModelConstants.ID) Long id, ModelMap model,HttpServletRequest request) {
        getScholarStayExtensionDto(id, model, request);
        return HTMLPage.RESEND_EMAIL_SCHOLARS_STAY_EXTENSION;
    }

    @PostMapping(value = "${url.resend.email}")
    public String resendEmailScholarStayExtension(@ModelAttribute StudentAppointmentRequestDto scholarsStayExtensionDto, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String status = scholarsStayExtensionService.resendEmailScholarsStayExtension(scholarsStayExtensionDto, request);
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, StudentConstants.SCHOLAR_STAY_EXTENSION_RESEND_EMAIL.getStudentConstant());
        return Constants.REDIRECT + baseUrl;
    }

    @GetMapping(value = "${url.widget}")
    public String getScholarsStayExtensionWidget(ModelMap model) {
        StudentAppointmentRequestEntity scholarsStayExtensionEntity = scholarsStayExtensionService.getRecentStatus();
        studentName = Objects.requireNonNullElse(SecurityCtxUtil.userName().toUpperCase().substring(4,5), ModelConstants.EMPTY_STRING);
        boolean msPhdStayExtension = studentName.equalsIgnoreCase(messageSource.getMessage(StudentConstants.D.getStudentConstant(), null, Locale.getDefault())) ||
                studentName.equalsIgnoreCase(messageSource.getMessage(StudentConstants.S.getStudentConstant(), null, Locale.getDefault()));
        model.addAttribute(messageSource.getMessage(StudentConstants.FORM_KEY.getStudentConstant(), null,Locale.getDefault()), scholarsStayExtensionEntity);
        model.addAttribute(messageSource.getMessage(StudentConstants.MS_PHD_STAY_EXTENSION_FLAG.getStudentConstant(), null,Locale.getDefault()), msPhdStayExtension);
        return HTMLPage.SCHOLAR_STAY_EXTENSION_WIDGET;
    }

    private void setThesisStatusMethod(StudentAppointmentRequestDto scholarsStayExtensionDto1, String studentName){
        if(studentName.equalsIgnoreCase(messageSource.getMessage(StudentConstants.S.getStudentConstant(), null,Locale.getDefault()))) {
            scholarsStayExtensionDto1.setMsStudent(ModelConstants.TRUE);
            scholarsStayExtensionDto1.setThesisStatus(messageSource.getMessage(StudentConstants.MS_THESIS_STATUS.getStudentConstant(),null, Locale.getDefault()));
        }
        else if(studentName.equalsIgnoreCase(messageSource.getMessage(StudentConstants.D.getStudentConstant(), null,Locale.getDefault()))) {
            scholarsStayExtensionDto1.setPhdStudent(ModelConstants.TRUE);
            scholarsStayExtensionDto1.setThesisStatus(messageSource.getMessage(StudentConstants.PHD_THESIS_STATUS.getStudentConstant(),null, Locale.getDefault()));
        }
    }

    private void getScholarStayExtensionDto(@PathVariable(ModelConstants.ID) Long id, ModelMap model, HttpServletRequest request) {
        StudentAppointmentRequestDto scholarsStayExtensionDto = commonResponseUtil.handleModalFormError(request,model,messageSource.getMessage(StudentConstants.FORM_KEY.getStudentConstant(), null, Locale.getDefault()), StudentAppointmentRequestDto.class);
        if (Objects.nonNull(id) && id > 0) {
            scholarsStayExtensionDto = scholarsStayExtensionService.getStudentAppointmentRequestById(id);
        }
        model.addAttribute(messageSource.getMessage(StudentConstants.FORM_KEY.getStudentConstant(), null,Locale.getDefault()), scholarsStayExtensionDto);
    }

    private boolean isGuideNameAndEmailEmptyOrNull(StudentBioDataFormDetailDto studentBioDataFormDetailDto) {
        return !(studentBioDataFormDetailDto.getFacultyName() == null ||
                studentBioDataFormDetailDto.getFacultyName().trim().isEmpty() ||
                "0".equals(studentBioDataFormDetailDto.getFacultyName()) ||
                studentBioDataFormDetailDto.getFacultyEmail() == null ||
                studentBioDataFormDetailDto.getFacultyEmail().trim().isEmpty() ||
                "0".equals(studentBioDataFormDetailDto.getFacultyEmail()));
    }
}
