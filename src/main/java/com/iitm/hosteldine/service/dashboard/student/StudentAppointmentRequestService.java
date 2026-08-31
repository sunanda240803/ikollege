package com.iitm.hosteldine.service.dashboard.student;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.AppointmentStatusNote;
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.SimsConfigDataJsonArrayDto;
import com.iitm.hosteldine.dto.dashboard.student.*;
import com.iitm.hosteldine.dto.student.AllStudentsDetailsViewDto;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.dashboard.student.StudentAppointmentRequestMapper;
import com.iitm.hosteldine.mapper.dashboard.student.StudentFilesInfoMapper;
import com.iitm.hosteldine.mapper.dashboard.student.StudentWorkflowMapper;
import com.iitm.hosteldine.model.dashboard.student.StudentAppointmentRequestEntity;
import com.iitm.hosteldine.model.dashboard.student.StudentWorkflowEntity;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.repository.dashboard.student.StudentAppointmentRequestRepository;
import com.iitm.hosteldine.repository.dashboard.student.StudentFilesInfoRepository;
import com.iitm.hosteldine.repository.dashboard.student.StudentWorkflowRepository;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.service.AllStudentsDetailsViewService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.RoleEnum;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentAppointmentRequestService {

    private final MessageSource messageSource;
    private final StudentAppointmentRequestRepository studentAppointmentRequestRepository;
    private final WorkflowMasterService workflowMasterService;
    private final StudentWorkflowRepository studentWorkflowRepository;
    private final StudentWorkflowService studentWorkflowService;
    private final MailQueueService mailQueueService;
    private final MailTemplateRepository mailTemplateRepository;
    private final StudentFilesInfoRepository studentFilesInfoRepository;
    private final Utility utility;
    private final SimsConfigDataService simsConfigDataService;

    private final List<String> excludedStatuses1 = List.of(
            WorkflowStatus.DELETED.getStatus(),
            WorkflowStatus.CANCELLED.getStatus(),
            WorkflowStatus.REJECTED.getStatus(),
            WorkflowStatus.APPROVED.getStatus(),
            WorkflowStatus.CANCELLED_AFTER_APPROVED.getStatus()
    );
    private final List<String> excludedStatuses2 = List.of(
            WorkflowStatus.DELETED.getStatus(),
            WorkflowStatus.CANCELLED.getStatus(),
            WorkflowStatus.REJECTED.getStatus(),
            WorkflowStatus.CANCELLED_AFTER_APPROVED.getStatus()
    );
    private final List<String> includedStatuses = List.of(
            WorkflowStatus.PENDING.getStatus(),
            WorkflowStatus.DEFAULT.getStatus(),
            WorkflowStatus.APPROVED.getStatus(),
            WorkflowStatus.REJECTED.getStatus()
    );
    private final AllStudentsDetailsViewService allStudentsDetailsViewService;
    private final CommonResponseUtil commonResponseUtil;

    public Page<StudentAppointmentRequestDto> getScholarsStayExtensionList(PaginationForm form) {
        var pageRequest = PageRequest.of(form.getPage() - 1, form.getSize(), Sort.by(ModelConstants.ID).descending());
        Page<StudentAppointmentRequestEntity> result = Optional.ofNullable(form.getSearch())
                .filter(search -> !search.isEmpty())
                .map(search -> studentAppointmentRequestRepository.getScholarStayExtensionWithSearchOption(SecurityCtxUtil.userId().toUpperCase(), ModelConstants.STATUS_ACTIVE, pageRequest, search))
                .orElseGet(() -> studentAppointmentRequestRepository.getScholarStayExtension(SecurityCtxUtil.userId().toUpperCase(), ModelConstants.STATUS_ACTIVE, pageRequest));
        return result.map(StudentAppointmentRequestMapper.INSTANCE::toDto);
    }

    public StudentAppointmentRequestDto getStudentAppointmentRequestById(Long id) {
        return studentAppointmentRequestRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
                .map(StudentAppointmentRequestMapper.INSTANCE::toDto)
                .orElse(new StudentAppointmentRequestDto());
    }

    public StudentAppointmentRequestEntity getRecentStatus(){
        return studentAppointmentRequestRepository.getRecentStatus(SecurityCtxUtil.userId().toUpperCase(), ModelConstants.STATUS_ACTIVE)
                .orElse(new StudentAppointmentRequestEntity());
    }

    @Transactional
    public String saveScholarsStayExtension(StudentAppointmentRequestDto scholarsStayExtensionDto, HttpServletRequest request) {
        final Long[] requestIdContainer = {0L};
        final String[] categoryStdContainer = {ModelConstants.EMPTY_STRING};
        final String[] studentId = {ModelConstants.EMPTY_STRING};
        StudentAppointmentRequestEntity studentAppointmentRequestEntity = saveAppointmentRequestDetails(scholarsStayExtensionDto);
        List<WorkflowMasterDto> workflowMasterDtoList = workflowMasterService.getAllWorkflowMastersByCategory(studentAppointmentRequestEntity.getCategory());

        workflowMasterDtoList.stream()
                .map(workflowMasterDto -> saveStudentWorkflowDetails(
                        workflowMasterDto,
                        studentAppointmentRequestEntity,
                        requestIdContainer,
                        categoryStdContainer,
                        studentId))
                .forEach(studentWorkflowRepository::saveAndFlush);
        mailToValidator(studentId[0], requestIdContainer[0], categoryStdContainer[0], request);
        return Constants.SAVED;
    }

    private StudentWorkflowEntity saveStudentWorkflowDetails(WorkflowMasterDto workflowMasterDto, StudentAppointmentRequestEntity studentAppointmentRequestEntity, Long[] requestIdContainer, String[] categoryStdContainer, String[] studentId) {
        StudentWorkflowEntity studentWorkflowEntity = new StudentWorkflowEntity();
        if (messageSource.getMessage(StudentConstants.VALIDATOR.getStudentConstant(), null, Locale.getDefault())
                .equalsIgnoreCase(workflowMasterDto.getAuthorityType())) {
            studentWorkflowEntity.setValidatorEmail(studentAppointmentRequestEntity.getValidatingAuthorityEmail());
            studentWorkflowEntity.setValidatorName(studentAppointmentRequestEntity.getValidatingAuthority());
        } else if (messageSource.getMessage(StudentConstants.HOD.getStudentConstant(), null, Locale.getDefault())
                .equalsIgnoreCase(workflowMasterDto.getAuthorityType())) {
            studentWorkflowEntity.setValidatorEmail(studentAppointmentRequestEntity.getHodEmail());
            studentWorkflowEntity.setValidatorName(studentAppointmentRequestEntity.getHodName());
        } else {
            studentWorkflowEntity.setValidatorEmail(workflowMasterDto.getEmail());
            studentWorkflowEntity.setValidatorName(workflowMasterDto.getValidatorName());
        }
        studentWorkflowEntity.setAuthenticationType(workflowMasterDto.getAuthenticationType());
        studentWorkflowEntity.setCategory(workflowMasterDto.getCategory());
        studentWorkflowEntity.setStatus(workflowMasterDto.getApprovalLevel() == 1 ?
                WorkflowStatus.PENDING.getStatus() :
                WorkflowStatus.DEFAULT.getStatus());
        studentWorkflowEntity.setStudentId(studentAppointmentRequestEntity.getStudentId());
        studentWorkflowEntity.setRequestId(studentAppointmentRequestEntity.getId());
        studentWorkflowEntity.setAuthorityType(workflowMasterDto.getAuthorityType());
        studentWorkflowEntity.setApprovalLevel(workflowMasterDto.getApprovalLevel());
        requestIdContainer[0] = studentWorkflowEntity.getRequestId();
        categoryStdContainer[0] = studentWorkflowEntity.getCategory();
        studentId[0] = studentWorkflowEntity.getStudentId();
        return studentWorkflowEntity;
    }

    private StudentAppointmentRequestEntity saveAppointmentRequestDetails(StudentAppointmentRequestDto scholarsStayExtensionDto) {
        StudentAppointmentRequestEntity studentAppointmentRequestEntity = StudentAppointmentRequestMapper.INSTANCE.toEntity(scholarsStayExtensionDto);
        int exist = ajaxAccommodationDateValidator(scholarsStayExtensionDto);
        if (exist == 1) {
            cancelOverlappingRequests(scholarsStayExtensionDto);
        }
        studentAppointmentRequestEntity.setStudentId(SecurityCtxUtil.userId().toUpperCase());
        studentAppointmentRequestEntity.setAppointmentFrom(scholarsStayExtensionDto.getStayFrom());
        studentAppointmentRequestEntity.setAppointmentTo(scholarsStayExtensionDto.getStayTo());
        studentAppointmentRequestEntity.setStatus(WorkflowStatus.VALIDATING.getStatus());
        studentAppointmentRequestEntity.setStatusNotes(messageSource.getMessage(AppointmentStatusNote.SCHOLARS_STAY_EXTENSION_VALIDATING_NOTE.getNoteKey(), null, Locale.getDefault()));
        studentAppointmentRequestEntity.setOccupancy(messageSource
                .getMessage(StudentConstants.OCCUPANCY
                        .getStudentConstant(),null,Locale.getDefault()));
        studentAppointmentRequestEntity.setThesisSubmittedDate(LocalDate.now());
        if (studentAppointmentRequestEntity.getCategory() != null
                && studentAppointmentRequestEntity.getCategory()
                .equalsIgnoreCase(StudentConstants.CATEGORY_SCHOLAR.getStudentConstant())){
            studentAppointmentRequestEntity.setPurpose(messageSource
                    .getMessage(StudentConstants.PURPOSE
                            .getStudentConstant(),null,Locale.getDefault()));
            studentAppointmentRequestEntity.setDining(messageSource
                    .getMessage(StudentConstants.Y
                            .getStudentConstant(), null, Locale.getDefault()));
        }else{
            studentAppointmentRequestEntity.setPurpose(messageSource
                    .getMessage(StudentConstants.SCHOLAR_STAY_EXTENSION_STUDENT_APPOINTMENT
                            .getStudentConstant(),null, Locale.getDefault()));
        }
        studentAppointmentRequestEntity.onCreate();
        studentAppointmentRequestRepository.save(studentAppointmentRequestEntity);
        return studentAppointmentRequestEntity;
    }

    public void mailToValidator(String studentId, Long requestId, String categoryStd, HttpServletRequest request) {
        StudentAppointmentRequestDto studentAppointmentRequestDto = studentWorkflowService.getValidatorList(studentId, requestId, categoryStd);
        List<StudentWorkflowDto> validatorList = studentAppointmentRequestDto.getStudentWorkflowDto();
        if (CollectionUtils.isNotEmpty(validatorList)) {
            validatorList.forEach(studentWorkflowDtos -> {
                try {
                    studentAppointmentRequestDto.setRequestId(String.valueOf(studentAppointmentRequestDto.getId()));
                    saveAppointmentMail(studentWorkflowDtos, studentAppointmentRequestDto, request);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private int ajaxAccommodationDateValidator(StudentAppointmentRequestDto scholarsStayExtensionDto) {
        List<StudentAppointmentRequestEntity> overlappingRequests = studentAppointmentRequestRepository.getScholarStayExtensionOverlappingRequests(
                scholarsStayExtensionDto.getStudentId(),
                scholarsStayExtensionDto.getAppointmentFrom(),
                scholarsStayExtensionDto.getAppointmentTo(),
                scholarsStayExtensionDto.getId(),
                excludedStatuses2
        );
        if (overlappingRequests.isEmpty())
            return 0;
        return overlappingRequests.stream()
                .anyMatch(request -> WorkflowStatus.APPROVED.getStatus().equals(request.getStatus())
                        || WorkflowStatus.ALLOTTED.getStatus().equals(request.getStatus())
                        || WorkflowStatus.CHECKED_IN.getStatus().equals(request.getStatus())) ? 2 : 1;
    }

    public void cancelOverlappingRequests(StudentAppointmentRequestDto scholarsStayExtensionDto) {
        studentAppointmentRequestRepository.updateStatusToCancelled(
                scholarsStayExtensionDto.getStudentId(),
                scholarsStayExtensionDto.getAppointmentFrom(),
                scholarsStayExtensionDto.getAppointmentTo(),
                scholarsStayExtensionDto.getId(),
                messageSource.getMessage(AppointmentStatusNote.SCHOLARS_STAY_EXTENSION_PRE_CANCEL_NOTE.getNoteKey(),null,Locale.getDefault()),
                excludedStatuses1,
                WorkflowStatus.CANCELLED.getStatus()
        );

        studentWorkflowRepository.updateWorkflowToCancelled(
                scholarsStayExtensionDto.getStudentId(),
                scholarsStayExtensionDto.getAppointmentFrom(),
                scholarsStayExtensionDto.getAppointmentTo(),
                scholarsStayExtensionDto.getId(),
                excludedStatuses1,
                WorkflowStatus.CANCELLED.getStatus()
        );
    }

    @Transactional
    public String cancelScholarsStayExtension(StudentAppointmentRequestDto scholarsStayExtensionDto) {
        String cancelledStatusNotes = messageSource
                .getMessage(AppointmentStatusNote.SCHOLARS_STAY_EXTENSION_CANCEL_NOTE.getNoteKey(), null, Locale.getDefault());
        String cancelledAfterApprovalStatusNotes = messageSource
                .getMessage(AppointmentStatusNote.SCHOLARS_STAY_EXTENSION_APPROVED_CANCEL_NOTE.getNoteKey(), null, Locale.getDefault());
        return Optional.ofNullable(scholarsStayExtensionDto.getId())
                .filter(id -> id > 0L)
                .flatMap(studentAppointmentRequestRepository::findById)
                .map(existingEntity -> {
                    String cancelStatus = WorkflowStatus.CANCELLED.getStatus();
                    String cancelAfterApprovalStatus = WorkflowStatus.CANCELLED_AFTER_APPROVED.getStatus();
                    if (WorkflowStatus.APPROVED.getStatus().equals(existingEntity.getStatus())) {
                        getFormattedDate(cancelledAfterApprovalStatusNotes, existingEntity, cancelAfterApprovalStatus);
                    } else {
                        getFormattedDate(cancelledStatusNotes, existingEntity, cancelStatus);
                    }
                    studentAppointmentRequestRepository.save(existingEntity);
                    return Constants.UPDATED;
                })
                .orElse(Constants.UPDATED);
    }

    private void getFormattedDate(String cancelledStatusNotes, StudentAppointmentRequestEntity existingEntity, String cancelStatus) {
        String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern(
                messageSource.getMessage(StudentConstants.DATE_FORMAT.getStudentConstant(), null, Locale.getDefault())
                , Locale.ENGLISH
        ));
        existingEntity.setStatus(cancelStatus);
        existingEntity.setStatusNotes(cancelledStatusNotes + ModelConstants.SPACE + formattedDate);
        updateStudentWorkflowStatus(existingEntity.getStudentId(),existingEntity.getId(), cancelStatus);
    }

    @Transactional
    public String resendEmailScholarsStayExtension(StudentAppointmentRequestDto scholarsStayExtensionDto, HttpServletRequest request) {
        LocalDate newDate = LocalDate.parse(DateUtility.formatDateInd(new Date()), DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT));
        return Optional.ofNullable(scholarsStayExtensionDto.getId())
                .filter(id -> id > 0L)
                .flatMap(studentAppointmentRequestRepository::findById)
                .map(existingEntity -> {
                    mailToValidator(
                            existingEntity.getStudentId(),
                            existingEntity.getId(),
                            existingEntity.getCategory(),
                            request
                    );
                    existingEntity.setResendDate(newDate);
                    studentAppointmentRequestRepository.save(existingEntity);
                    return Constants.UPDATED;
                })
                .orElse(Constants.UPDATED);
    }

    private void updateStudentWorkflowStatus(String studentId, Long requestId, String status) {
        studentWorkflowRepository.getStudentWorkflowStatus(studentId, requestId, ModelConstants.STATUS_ACTIVE)
                .stream()
                .peek(entity -> entity.setStatus(status))
                .forEach(studentWorkflowRepository::save);
    }

    public void saveAppointmentMail(StudentWorkflowDto studentWorkflowDto, StudentAppointmentRequestDto studentAppointmentRequestDto, HttpServletRequest request) throws Exception {
        StudentWorkflowDto studentWorkflowDto1 = studentWorkflowService.getWorkflowById(studentWorkflowDto.getId());
        setAuthType(studentWorkflowDto, studentWorkflowDto1);
        Optional<MailTemplateEntity> commonMailTemplateOpt = mailTemplateRepository.findByMailType(StudentConstants.MAIL_TEMPLATE_SCHOLAR_LEVEL.getStudentConstant());
        Optional<MailTemplateEntity> studentMailTemplateOpt = mailTemplateRepository.findByMailType(StudentConstants.STUDENT_MAIL_TEMPLATE_BODY.getStudentConstant());
        Optional<MailTemplateEntity> othersMailTemplateOpt = mailTemplateRepository.findByMailType(StudentConstants.OTHERS_MAIL_TEMPLATE_BODY.getStudentConstant());
        if (commonMailTemplateOpt.isPresent()) {
            MailTemplateEntity commonTemplate = commonMailTemplateOpt.get();
            mailSave(commonTemplate, studentMailTemplateOpt, othersMailTemplateOpt, studentWorkflowDto1, studentAppointmentRequestDto, request);
        }
    }

    private void mailSave(MailTemplateEntity commonTemplate, Optional<MailTemplateEntity> studentMailTemplateOpt,
                          Optional<MailTemplateEntity> othersMailTemplateOpt, StudentWorkflowDto studentWorkflowDto1,
                          StudentAppointmentRequestDto studentAppointmentRequestDto, HttpServletRequest request) throws Exception {
        String studentTemplate = studentMailTemplateOpt.isPresent() ? studentMailTemplateOpt.get().getMailTemplate() : ModelConstants.EMPTY_STRING;
        String othersTemplate = othersMailTemplateOpt.isPresent() ? othersMailTemplateOpt.get().getMailTemplate() : ModelConstants.EMPTY_STRING;
        AllStudentsDetailsViewDto allStudentsDetailsViewDto = allStudentsDetailsViewService.getCompleteStudentDetails(studentAppointmentRequestDto.getStudentId());
        String messageContent = getMessageContent(studentTemplate, othersTemplate, studentWorkflowDto1, studentAppointmentRequestDto, allStudentsDetailsViewDto, request);


        mailQueueService.saveMailQueue(ModelConstants.STUDENT.equalsIgnoreCase(studentWorkflowDto1.getAuthorityType()) ?
                        studentAppointmentRequestDto.getMailSubject() :
                        (othersMailTemplateOpt.isPresent() ?
                                othersMailTemplateOpt.get().getMailSubject() : ModelConstants.EMPTY_STRING),
                getNameForMail(studentWorkflowDto1, allStudentsDetailsViewDto),
                messageContent,
                getEmailAddressForMail(studentWorkflowDto1, allStudentsDetailsViewDto),
                messageSource.getMessage(StudentConstants.HOSTEL_ACCOMM_SCHOLAR_STAY_EXTENSION
                        .getStudentConstant(), null, Locale.getDefault()),
                SecurityCtxUtil.userId(), 1, null, null, null, null);
    }

    private void setAuthType(StudentWorkflowDto studentWorkflowDto, StudentWorkflowDto studentWorkflowDto1) {
        if (ModelConstants.STUDENT.equalsIgnoreCase(studentWorkflowDto.getAuthorityType())) {
            studentWorkflowDto1.setAuthorityType(ModelConstants.STUDENT);
        } else if (ModelConstants.CCW_OFFICE.equalsIgnoreCase(studentWorkflowDto.getAuthorityType())) {
            studentWorkflowDto1.setAuthorityType(ModelConstants.CCW_OFFICE);
        }
    }

    private String getEmailAddressForMail(StudentWorkflowDto studentWorkflowDto, AllStudentsDetailsViewDto allStudentsDetailsViewDto) {
        if (allStudentsDetailsViewDto.getEmailId() != null && ModelConstants.STUDENT.equalsIgnoreCase(studentWorkflowDto.getAuthorityType())) {
            return allStudentsDetailsViewDto.getEmailId();
        } else if (isValidatorEmailNotNullCCWOfficeNotStudent(studentWorkflowDto)) {
            return studentWorkflowDto.getValidatorEmail();
        } else {
            return messageSource.getMessage(StudentConstants.DEFAULT_MAIL.getStudentConstant(), null, Locale.getDefault());
        }
    }

    private boolean isValidatorEmailNotNullCCWOfficeNotStudent(StudentWorkflowDto studentWorkflowDto) {
        return (studentWorkflowDto.getValidatorEmail() != null ||
                ModelConstants.CCW_OFFICE.equalsIgnoreCase(studentWorkflowDto.getAuthorityType())) &&
                !ModelConstants.STUDENT.equalsIgnoreCase(studentWorkflowDto.getAuthorityType());
    }

    private String getNameForMail(StudentWorkflowDto studentWorkflowDto, AllStudentsDetailsViewDto allStudentsDetailsViewDto) {
        if (allStudentsDetailsViewDto.getStudentName() != null && ModelConstants.STUDENT.equalsIgnoreCase(studentWorkflowDto.getAuthorityType())) {
            return allStudentsDetailsViewDto.getStudentName();
        } else {
            return commonResponseUtil.getMessage("message.mail.greetings.for");
        }
    }

    private boolean isValidatorNameNotNullCCWOfficeNotStudent(StudentWorkflowDto studentWorkflowDto) {
        return (studentWorkflowDto.getValidatorName() != null ||
                ModelConstants.CCW_OFFICE.equalsIgnoreCase(studentWorkflowDto.getAuthorityType())) &&
                !ModelConstants.STUDENT.equalsIgnoreCase(studentWorkflowDto.getAuthorityType());
    }

    private String getMessageContent(String studentsTemplate, String othersTemplate, StudentWorkflowDto studentWorkflowDto,
                                     StudentAppointmentRequestDto studentAppointmentRequestDto,
                                     AllStudentsDetailsViewDto allStudentsDetailsViewDto, HttpServletRequest request) throws Exception {
        boolean isAuthorityTypeStudent = ModelConstants.STUDENT.equalsIgnoreCase(studentWorkflowDto.getAuthorityType());
        boolean isAuthorityTypeCCWOffice = ModelConstants.CCW_OFFICE.equalsIgnoreCase(studentWorkflowDto.getAuthorityType());
        StudentAppointmentRequestDto studentAppointmentRequestDto1 = studentAppointmentRequestRepository
                .getRequestIdStatus(studentAppointmentRequestDto.getStudentId(), studentWorkflowDto.getRequestId(), ModelConstants.STATUS_ACTIVE)
                .map(StudentAppointmentRequestMapper.INSTANCE::toDto)
                .orElse(new StudentAppointmentRequestDto());
        String approveUrl = getApproveUrl(request, studentWorkflowDto, studentAppointmentRequestDto);
        String rejectUrl = getRejectUrl(request, studentWorkflowDto, studentAppointmentRequestDto);
        String viewUrl = getViewUrl(request, studentWorkflowDto, studentAppointmentRequestDto);

        if (!isAuthorityTypeStudent && !isAuthorityTypeCCWOffice) {
            return getOthersMailTemplateBody(othersTemplate, allStudentsDetailsViewDto, studentWorkflowDto, studentAppointmentRequestDto1, approveUrl, rejectUrl, viewUrl);
        } else if (isAuthorityTypeCCWOffice) {
            return getCCWOfficeMailTemplateBody(othersTemplate, allStudentsDetailsViewDto, studentWorkflowDto, studentAppointmentRequestDto1, viewUrl);
        } else {
            return getStudentMailTemplateBody(studentsTemplate, studentAppointmentRequestDto);
        }
    }

    private String getOthersMailTemplateBody(String template, AllStudentsDetailsViewDto allStudentsDetailsViewDto,
                                             StudentWorkflowDto studentWorkflowDto, StudentAppointmentRequestDto studentAppointmentRequestDto1,
                                             String approveUrl, String rejectUrl, String viewUrl) {
        Optional<MailTemplateEntity> approveRejectButtons = mailTemplateRepository.findByMailType(StudentConstants.STUDENT_ACCOMM_APPROVE_REJECT_BUTTON.getStudentConstant());
        String buttonTemplate = ModelConstants.EMPTY_STRING;
        if(approveRejectButtons.isPresent()) {
            MailTemplateEntity mailTemplateEntity = approveRejectButtons.get();
            buttonTemplate = mailTemplateEntity.getMailTemplate();
            buttonTemplate = buttonTemplate.replace("#%approveUrl%#", approveUrl);
            buttonTemplate = buttonTemplate.replace("#%rejectUrl%#", rejectUrl);
        }
        template = mailBody(template, allStudentsDetailsViewDto, studentWorkflowDto, studentAppointmentRequestDto1);
        template = replacePlaceholder(template, StudentConstants.MAIL_BODY_HEADER_OTHERS, StudentConstants.MAIL_BODY_OTHERS_HEADER.getStudentConstant());
        template = replacePlaceholder(template, StudentConstants.MAIL_BODY_APPROVAL_BUTTON_TEMPLATE, buttonTemplate);
        template = replacePlaceholder(template, StudentConstants.MAIL_BODY_VIEW_URL, viewUrl);
        return template;
    }

    private String getCCWOfficeMailTemplateBody(String template, AllStudentsDetailsViewDto allStudentsDetailsViewDto,
                                                StudentWorkflowDto studentWorkflowDto, StudentAppointmentRequestDto studentAppointmentRequestDto,
                                                String viewUrl) {
        template = mailBody(template, allStudentsDetailsViewDto, studentWorkflowDto, studentAppointmentRequestDto);
        template = template.replace(commonResponseUtil.getMessage("message.label.mail.template.header.other"), StudentConstants.MAIL_BODY_OTHERS_HEADER_CCW_OFFICE.getStudentConstant());
        template = template.replace(commonResponseUtil.getMessage("message.label.mail.template.approval.button.template"), ModelConstants.EMPTY_STRING);
        template = template.replace(commonResponseUtil.getMessage("message.label.mail.template.view.url"), viewUrl);
        return template;
    }

    private String getStudentMailTemplateBody(String template, StudentAppointmentRequestDto studentAppointmentRequestDto) {
        template = replacePlaceholder(template, StudentConstants.MAIL_BODY_DATE,
                String.valueOf(LocalDate.parse(DateUtility.formatDateInd(new Date()), DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT, Locale.ENGLISH))));
        template = replacePlaceholder(template, StudentConstants.MAIL_BODY_APPROVE_REJECT_DESC, studentAppointmentRequestDto.getStudentMailContent());
        return template;
    }

    private String getViewUrl(HttpServletRequest request, StudentWorkflowDto studentWorkflowDto, StudentAppointmentRequestDto studentAppointmentRequestDto) throws Exception {
        return Utility.getDomainUrl(request) +
                messageSource.getMessage("url.public.api", null, Locale.getDefault()) + ModelConstants.SLASH +
                messageSource.getMessage("url.dean.student.accommodation.request", null, Locale.getDefault()) +
                messageSource.getMessage("url.view", null, Locale.getDefault()) + "?data="
                + MCrypt.getInstance().encryptToText(encryptString(studentWorkflowDto, studentAppointmentRequestDto)) + "&status=v";
    }

    private String getApproveUrl(HttpServletRequest request, StudentWorkflowDto studentWorkflowDto, StudentAppointmentRequestDto studentAppointmentRequestDto) throws Exception {
        return Utility.getDomainUrl(request) +
                messageSource.getMessage("url.public.api", null, Locale.getDefault()) + ModelConstants.SLASH +
                messageSource.getMessage("url.dean.student.accommodation.request", null, Locale.getDefault()) +
                messageSource.getMessage("url.approve.reject", null, Locale.getDefault()) +
                "?data=" + MCrypt.getInstance().encryptToText(encryptString(studentWorkflowDto, studentAppointmentRequestDto)) + "&status=a";
    }

    private String getRejectUrl(HttpServletRequest request, StudentWorkflowDto studentWorkflowDto, StudentAppointmentRequestDto studentAppointmentRequestDto) throws Exception {
        return Utility.getDomainUrl(request) +
                messageSource.getMessage("url.public.api", null, Locale.getDefault()) + ModelConstants.SLASH +
                messageSource.getMessage("url.dean.student.accommodation.request", null, Locale.getDefault()) +
                messageSource.getMessage("url.approve.reject", null, Locale.getDefault()) +
                "?data=" + MCrypt.getInstance().encryptToText(encryptString(studentWorkflowDto, studentAppointmentRequestDto)) + "&status=r";
    }

    private String encryptString(StudentWorkflowDto studentWorkflowDto, StudentAppointmentRequestDto studentAppointmentRequestDto) {
        return studentWorkflowDto.getId() + Constants.BACKTICK +
                studentWorkflowDto.getModifiedAt() + Constants.BACKTICK +
                studentWorkflowDto.getRequestId() + Constants.BACKTICK +
                studentAppointmentRequestDto.getStudentId();
    }

    private String mailBody(String template, AllStudentsDetailsViewDto allStudentsDetailsViewDto,
                          StudentWorkflowDto studentWorkflowDto, StudentAppointmentRequestDto studentAppointmentRequestDto) {
        template = replacePlaceholder(template, StudentConstants.MAIL_BODY_STUDENT_NAME, allStudentsDetailsViewDto.getStudentName());
        template = replacePlaceholder(template, StudentConstants.MAIL_BODY_STUDENT_ID, studentWorkflowDto.getStudentId());
        template = replacePlaceholder(template, StudentConstants.MAIL_BODY_EMAIL_ID, allStudentsDetailsViewDto.getEmailId());
        template = replacePlaceholder(template, StudentConstants.MAIL_BODY_ADDRESS, allStudentsDetailsViewDto.getStudentAddress());
        template = replacePlaceholder(template, StudentConstants.MAIL_BODY_CONTACT_NUMBER, String.valueOf(allStudentsDetailsViewDto.getStudentMobile()));
        template = replacePlaceholder(template, StudentConstants.MAIL_BODY_FROM_DATE, getFrontEndDate(studentAppointmentRequestDto.getStayFrom()));
        template = replacePlaceholder(template, StudentConstants.MAIL_BODY_TO_DATE, getFrontEndDate(studentAppointmentRequestDto.getStayTo()));
        template = replacePlaceholder(template, StudentConstants.MAIL_BODY_CATEGORY, studentAppointmentRequestDto.getCategory());
        template = replacePlaceholder(template, StudentConstants.MAIL_BODY_PURPOSE, studentAppointmentRequestDto.getPurpose());
        return template;
    }

    private String getFrontEndDate(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT, Locale.ENGLISH)) : null;
    }

    private String replacePlaceholder(String template, StudentConstants key, String value) {
        String replacementValue = (value != null && !value.trim().isEmpty()) ? value : ModelConstants.NOT_APPLICABLE;
        return template.replace(
                messageSource.getMessage(key.getStudentConstant(), null, Locale.getDefault()), replacementValue);
    }

    public String encryptWorkflowIdAndModifiedAt(LocalDateTime modifiedAt, Long workflowId) throws Exception {
        return MCrypt.getInstance().encryptToText(workflowId + Constants.BACKTICK + modifiedAt);
    }

    public StudentAppointmentRequestDto getStudentAppointmentRequest(String studentId, String authorityType, Long requestId) {
        return studentAppointmentRequestRepository
                .getStudentAppointmentRequest(authorityType.trim(), studentId, requestId, ModelConstants.STATUS_ACTIVE, includedStatuses, RoleEnum.DEAN.getValue())
                .map(this::convertObjectToDto)
                .orElse(null);
    }

    private StudentAppointmentRequestDto convertObjectToDto(List<Object[]> resultList) {
        if (resultList == null || resultList.isEmpty()) {
            return null;
        }
        Object[] result = resultList.getFirst();
        if (result.length < 3) {
            return null;
        }
        StudentAppointmentRequestEntity studentAppointment = (StudentAppointmentRequestEntity) result[0];
        StudentWorkflowEntity workflowEntity = (StudentWorkflowEntity) result[1];
//        LocalDateTime modifiedAt = (result[2] instanceof LocalDateTime) ? (LocalDateTime) result[2] : null;
        LocalDateTime modifiedAt = result[2] != null ? DateUtility.toLocalDateTime(result[2]) : null;

        StudentAppointmentRequestDto dto = StudentAppointmentRequestMapper.INSTANCE.toDto(studentAppointment);
        dto.setStudentWorkflow(StudentWorkflowMapper.INSTANCE.toDto(workflowEntity));
        dto.setModifiedAt(modifiedAt);
        return dto;
    }

    public List<StudentWorkflowDto> getByStudentIdAndRequestIdAndActiveFlag(Long requestId, String studentId, String statusDefault) {
        return studentWorkflowRepository.getStudentWorkflowList(studentId, requestId, ModelConstants.STATUS_ACTIVE, statusDefault)
                .stream()
                .map(StudentWorkflowMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    public List<StudentFilesInfoDto> getStudentFileInfoList(long encryptedRequestId, String studentId) {
        return studentFilesInfoRepository.getStudentFilesInfo(
                        studentId,
                        (int) encryptedRequestId,
                        ModelConstants.STATUS_ACTIVE
                )
                .stream()
                .map(StudentFilesInfoMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    public StudentAppointmentRequestEntity getRequestIdStatus(Long requestId){
        return studentAppointmentRequestRepository.getRequestIdStatus(SecurityCtxUtil.userId().toUpperCase(), requestId, ModelConstants.STATUS_ACTIVE)
                .orElse(new StudentAppointmentRequestEntity());
    }

    public StudentDetailsPdfDto getStudentDetailsPdf(Long requestId) {
        Optional<List<Object[]>> result = studentAppointmentRequestRepository.getInformationForPdf(requestId,
                ModelConstants.STATUS_ACTIVE);

        if (result.isEmpty() || result.get().isEmpty()) {
            throw new EntityNotFoundException(messageSource.getMessage("message.label.no.data.found", null, Locale.getDefault()) + requestId);
        }

        Object[] entities = result.get().getFirst();
        AllStudentsDetailsViewEntity studentEntity = (AllStudentsDetailsViewEntity) entities[1];
        StudentAppointmentRequestDto studentAppointmentRequestDto = getStudentAppointmentRequestById(requestId);
        StudentWorkflowDto studentWorkflowDto = studentWorkflowService.getStudentWorkflowAuthorityTypeDean(requestId);
        StudentDetailsPdfDto dto = StudentAppointmentRequestMapper.INSTANCE.toDtos(studentAppointmentRequestDto);

        if (studentEntity != null) {
            dto.setGender(studentEntity.getGender());
            dto.setStudentPersonalEmail(Objects.nonNull(studentEntity.getStudentPersonalEmail()) && !studentEntity.getStudentPersonalEmail().isEmpty() ? studentEntity.getStudentPersonalEmail() : studentEntity.getEmailId());
            dto.setStudentMobile(studentEntity.getStudentMobile());
            dto.setDob(studentEntity.getDob());
            dto.setStudentAddress(studentEntity.getStudentAddress());
            dto.setStudentName(studentEntity.getStudentName());
            dto.setApprovalNotes(studentWorkflowDto.getApprovalNotes());
        }
        return dto;
    }

    public StudentAppointmentRequestDto getStudentAccommodationRequestDetails(String studentId, Long requestId, String authorityType) {
   	 // return getStudentAppointmentRequest(studentId, authorityType, requestId);
		StudentAppointmentRequestDto dto = getStudentAppointmentRequest(studentId, authorityType, requestId);

		ArrayList<SimsConfigDataJsonArrayDto> configList = simsConfigDataService
				.getSimConfigValueFromJsonArray(SimsConfigDataService.NATURE_OF_APPOINTMENT);
		String displayValue = resolveCategoryDisplayValue(configList, dto.getCategory());
		dto.setCategory(displayValue);
		return dto;
     
   }

	private String resolveCategoryDisplayValue(ArrayList<SimsConfigDataJsonArrayDto> configList, String category) {
		if (configList == null || category == null) {
	        return "";
	    }
	    for (SimsConfigDataJsonArrayDto dto : configList) {
	        if (dto.getId() != null &&
	            dto.getId().equalsIgnoreCase(category)) {

	            return dto.getValue(); 
	        }
	    }
	    return category;
	}
}