package com.iitm.hosteldine.service.OtherCandidate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.OtherCandidate.*;
import com.iitm.hosteldine.dto.dean.BulkApprovalRejectDto;
import com.iitm.hosteldine.entity.UserManagementOnlineEntity;
import com.iitm.hosteldine.mapper.OtherCandidate.*;
import com.iitm.hosteldine.service.OnlineUserDetailsService;
import com.itextpdf.layout.element.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.CategoryEnum;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.form.AccommodationRequestForm;
import com.iitm.hosteldine.form.StayExtensionRequestForm;
import com.iitm.hosteldine.form.common.FileForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.OtherCandidate.CandidateAppointmentRequestEntity;
import com.iitm.hosteldine.model.OtherCandidate.CandidateFilesInformationEntity;
import com.iitm.hosteldine.model.OtherCandidate.CandidateProfileEntity;
import com.iitm.hosteldine.model.OtherCandidate.CandidateStayRequestEntity;
import com.iitm.hosteldine.model.OtherCandidate.CandidateStayRequestWorkflowEntity;
import com.iitm.hosteldine.model.OtherCandidate.CandidateWorkflowEntity;
import com.iitm.hosteldine.model.OtherCandidate.StayExtensionRequestEntity;
import com.iitm.hosteldine.model.dashboard.student.WorkflowMasterEntity;
import com.iitm.hosteldine.model.dean.StudentAppointmentRequestHistory;
import com.iitm.hosteldine.model.hostel.HostelMasterEntity;
import com.iitm.hosteldine.model.hostel.HostelRoomInfoEntity;
import com.iitm.hosteldine.model.hostel.VacationHostelRoomAllotmentInfoEntity;
import com.iitm.hosteldine.repository.OtherCandidate.CandidateAppointmentRequestRepository;
import com.iitm.hosteldine.repository.OtherCandidate.CandidateFilesInformationRepository;
import com.iitm.hosteldine.repository.OtherCandidate.CandidateProfileRepository;
import com.iitm.hosteldine.repository.OtherCandidate.CandidateStayDateViewRepository;
import com.iitm.hosteldine.repository.OtherCandidate.CandidateStayRequestRepository;
import com.iitm.hosteldine.repository.OtherCandidate.CandidateStayRequestWorkflowRepository;
import com.iitm.hosteldine.repository.OtherCandidate.CandidateWorkflowRepository;
import com.iitm.hosteldine.repository.OtherCandidate.StayExtensionRequestRepository;
import com.iitm.hosteldine.repository.OtherCandidate.StayExtensionRequestWorkflowRepository;
import com.iitm.hosteldine.repository.dashboard.student.WorkflowMasterRepository;
import com.iitm.hosteldine.repository.dean.StudentAppointmentRequestHistoryRepository;
import com.iitm.hosteldine.repository.hostel.HostelMasterRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomInfoRepository;
import com.iitm.hosteldine.repository.hostel.VacationHostelRoomAllotmentInfoRepository;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.PdfActionService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.RoleEnum;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.CustomValidators;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccommodationRequestService {

    private final CandidateStayDateViewRepository candidateStayDateRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final CandidateAppointmentRequestRepository candidateAppointmentRequestRepository;
    private final CandidateFilesInformationRepository candidateFilesInformationRepository;
    private final CommonResponseUtil commonResponseUtil;
    private final CandidateWorkflowRepository candidateWorkflowRepository;
    private final StayExtensionRequestWorkflowRepository stayExtensionRequestWorkflowRepository;
    private final WorkflowMasterRepository workflowMasterRepository;
    private final FileService fileService;
    private final SimsConfigDataService simsConfigDataService;
    private final MailQueueService mailQueueService;
    private final CustomValidators customValidators;
    private final Utility utility;
    private final StayExtensionRequestRepository stayExtensionRequestRepository;

    private static final String SPACE = " ";
    private static final String COMMA = ", ";
    private static final Text COMMA_TEXT = new Text(COMMA);
    private static final String SLASH = "\\";
    private static final int DEFAULT_BULLET_INDENT = 10;
    private static final int DEFAULT_DECLARATION_INDENT = 10;
    private static final String DEFAULT_BULLET_SYMBOL_L1 = "•";
    private static final UnitValue VALUE_100_P = UnitValue.createPercentValue(100);
    private static final UnitValue VALUE_40_P = UnitValue.createPercentValue(40);
    private static final UnitValue VALUE_30_P = UnitValue.createPercentValue(30);
    private static final UnitValue VALUE_33_P = UnitValue.createPercentValue(100 / 3F);
    private final PdfActionService pdfActionService;
    private final StudentAppointmentRequestHistoryRepository studentAppointmentRequestHistoryRepository;
    private final MailTemplateRepository mailTemplateRepository;
    private final CandidateStayRequestRepository candidateStayRequestRepository;
    private final StayExtensionRequestService stayExtensionRequestService;
    private final CandidateStayRequestWorkflowRepository candidateStayRequestWorkflowRepository;
    private final HostelMasterRepository hostelMasterRepository;
    private final VacationHostelRoomAllotmentInfoRepository vacationHostelRoomAllotmentInfoRepository;
    private final HostelRoomInfoRepository hostelRoomInfoRepository;
    private final OnlineUserDetailsService onlineUserDetailsService;

    public Page<CandidateStayDateViewDto> getAccommodationRequestDetails(PaginationForm form) {
        var pageRequest = PageRequest.of(form.getPage() - 1, form.getSize());
        return candidateStayDateRepository.findAllById_CandidateId(SecurityCtxUtil.candidateId(), pageRequest)
                .map(CandidateStayDateMapper.INSTANCE::toDto);
    }

    public AccommodationRequestForm getAccommodationRequestDetailsById(Long candidateId, Long requestId,
                                                                       String encryptedKey, boolean checkProfileStatus) throws JsonProcessingException {
        if (checkProfileStatus && SecurityCtxUtil.candidateId().equals(0L)) {
            throw new IllegalArgumentException(commonResponseUtil.getMessage("message.exception.fill.profile.info"));
        }

        CandidateProfileDto candidateProfileDto = candidateProfileRepository.findByIdAndActiveFlag(candidateId, ModelConstants.STATUS_ACTIVE)
                .map(CandidateProfileMapper.INSTANCE::toDto)
                .orElse(new CandidateProfileDto());

        String postData = simsConfigDataService.getSimConfigValue(Constants.CANDIDATE_POST);
        if (postData != null && !postData.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            candidateProfileDto.setPostData(objectMapper.readValue(postData, Map.class));
        }

        CandidateAppointmentRequestDto candidateAppointmentRequestDto = candidateAppointmentRequestRepository.findByIdAndCandidateIdAndActiveFlag(requestId, candidateId,
                        ModelConstants.STATUS_ACTIVE)
                .map(CandidateAppointmentRequestMapper.INSTANCE::fromCandidateAppointmentRequestEntity)
                .orElse(CandidateAppointmentRequestDto.builder().build());


        List<CandidateFilesInformationDto> candidateFilesInformationList =
                candidateFilesInformationRepository.findAllByRequestIdAndCandidateIdAndActiveFlag(requestId, candidateId, ModelConstants.STATUS_ACTIVE)
                        .map(list -> list.stream().map(CandidateFilesInformationMapper.INSTANCE::toDto).toList())
                        .orElse(Collections.emptyList());


        List<CandidateWorkflowDto> workflowDtoList =
                candidateWorkflowRepository.findAllByCandidateIdAndApplicationIdAndActiveFlagAndStatusNot(candidateId, requestId,
                                ModelConstants.STATUS_ACTIVE, WorkflowStatus.DEFAULT.getStatus())
                        .map(list -> list.stream().map(CandidateWorkflowMapper.INSTANCE::toDto).toList())
                        .orElse(Collections.emptyList());

        return AccommodationRequestForm
                .builder()
                .candidateProfileDto(candidateProfileDto)
                .candidateAppointmentRequestDto(candidateAppointmentRequestDto)
                .candidateFilesInformationList(candidateFilesInformationList)
                .candidateWorkflowList(workflowDtoList)
                .encryptedKey(encryptedKey)
                .build();

    }

    @Transactional
    public String saveOrUpdateAccommodationRequest(AccommodationRequestForm form, String requestKey,
                                                   HttpServletRequest request) throws Exception {
        String[] split = MCrypt.getInstance().decryptToString(requestKey).split(Constants.BACKTICK);
        Long candidateId = SecurityCtxUtil.candidateId();
        Long requestId = Long.parseLong(split[2]);

        CandidateAppointmentRequestDto candidateAppointmentRequestDto = form.getCandidateAppointmentRequestDto();
        if (candidateAppointmentRequestDto.getCategory().equalsIgnoreCase(CategoryEnum.INTERVIEWS.getValue())) {
            candidateAppointmentRequestDto.setAppointmentFrom(candidateAppointmentRequestDto.getStayFrom());
            candidateAppointmentRequestDto.setAppointmentTo(candidateAppointmentRequestDto.getStayTo());
        }

        candidateAppointmentRequestDto.setOccupancy("multi");

        if (candidateAppointmentRequestDto.getMessOption() == null || candidateAppointmentRequestDto.getMessOption().isEmpty()) {
            candidateAppointmentRequestDto.setMessOption("accommodation");
        }

        String approvalStatus = WorkflowStatus.VALIDATING.getStatus();
        String validatingAuthorityEmail = null;

        CandidateAppointmentRequestEntity candidateAppointmentRequestEntity = candidateAppointmentRequestRepository.findByIdAndCandidateIdAndActiveFlag(requestId, candidateId,
                ModelConstants.STATUS_ACTIVE).orElse(null);

        if (Objects.nonNull(candidateAppointmentRequestEntity) && Objects.nonNull(candidateAppointmentRequestEntity.getApprovalStatus())
                && Objects.nonNull(candidateAppointmentRequestEntity.getValidatingAuthorityEmail())) {
            approvalStatus = candidateAppointmentRequestEntity.getApprovalStatus();
            validatingAuthorityEmail = candidateAppointmentRequestEntity.getValidatingAuthorityEmail();
        }

        int i = validateStayPeriod(candidateAppointmentRequestDto, requestKey);
        if (i == 1) {
            String statusNote = commonResponseUtil.getMessage("message.cancel.accommodation.request");

            candidateAppointmentRequestRepository.cancelOverlappingRequests(requestId, candidateId,
                    candidateAppointmentRequestDto.getAppointmentFrom().toString(),
                    candidateAppointmentRequestDto.getAppointmentTo().toString(), statusNote);

            candidateWorkflowRepository.cancelWorkflowDetails(requestId, candidateId,
                    candidateAppointmentRequestDto.getAppointmentFrom().toString(),
                    candidateAppointmentRequestDto.getAppointmentTo().toString());

            stayExtensionRequestRepository.getStayExtensionRequests(requestId, candidateId,
                            candidateAppointmentRequestDto.getAppointmentFrom().toString(), candidateAppointmentRequestDto.getAppointmentTo().toString())
                    .filter(list -> !list.isEmpty())
                    .ifPresent(list -> list.forEach(this::cancelStayExtension));
        }

//        String categoryCode = CategoryEnum.DOST.getValue();
        String categoryCode = CategoryEnum.CCW.getValue();
        List<String> categoryList = Arrays.asList(CategoryEnum.ICSR.getValue(), CategoryEnum.INTERVIEWS.getValue(), CategoryEnum.SASTHRA.getValue());
        if (categoryList.contains(candidateAppointmentRequestDto.getCategory().toUpperCase())) {
            categoryCode = candidateAppointmentRequestDto.getCategory().toUpperCase();
        }

        CandidateAppointmentRequestEntity saveOrUpdatedEntity;
        String status;
        if (requestId == 0) {
            if (approvalStatus.equals(WorkflowStatus.REJECTED.getStatus())) {
                candidateAppointmentRequestDto.setApprovalStatus(WorkflowStatus.VALIDATING.getStatus());
            }
            saveOrUpdatedEntity = saveAccommodationRequest(form, requestId, approvalStatus);
            status = Constants.SAVED;
        } else {
            if (approvalStatus.equals(WorkflowStatus.REJECTED.getStatus())) {
                candidateAppointmentRequestDto.setApprovalStatus(WorkflowStatus.REJECTED.getStatus());
            } else {
                candidateAppointmentRequestDto.setApprovalStatus(approvalStatus);
            }
            saveOrUpdatedEntity = updateAccommodationRequest(form, approvalStatus, requestId,
                    categoryCode, candidateAppointmentRequestEntity);
            status = Constants.UPDATED;
        }

        if (requestId == 0 || approvalStatus.equals(WorkflowStatus.REJECTED.getStatus())) {
            addWorkflowEntries(saveOrUpdatedEntity.getId(), categoryCode, form, approvalStatus, validatingAuthorityEmail);
        }

        if (Objects.nonNull(saveOrUpdatedEntity) && Objects.nonNull(form.getFiles())) {
            form.setFiles(
                    form.getFiles().stream()
                            .filter(file -> file.getFile() != null && !file.getFile().getOriginalFilename().isEmpty())
                            .toList()
            );
            uploadFiles(saveOrUpdatedEntity.getId(), form);
        }

        if (Objects.nonNull(saveOrUpdatedEntity) && (approvalStatus.equalsIgnoreCase(WorkflowStatus.VALIDATING.getStatus()) ||
                approvalStatus.equalsIgnoreCase(WorkflowStatus.REJECTED.getStatus()))) {
            sendMailToValidators(categoryCode, saveOrUpdatedEntity.getId(), request, SecurityCtxUtil.candidateId());
        }

        return status;
    }

    public void cancelStayExtension(Long stayId) {
        StayExtensionRequestEntity candidateStayRequestEntity = stayExtensionRequestRepository.findByStayIdAndActiveFlag(stayId, ModelConstants.STATUS_ACTIVE)
                .orElse(null);
        Long candidateId = SecurityCtxUtil.candidateId();
        if (Objects.nonNull(candidateStayRequestEntity)) {
            String statusNote = commonResponseUtil.getMessage("message.cancel.extension.request");
            candidateStayRequestEntity.setApprovalStatus(WorkflowStatus.CANCELLED.getStatus());
            candidateStayRequestEntity.setStatusNotes(statusNote);
            candidateStayRequestEntity.setModifiedBy(candidateId.toString());
            candidateStayRequestEntity.setModifiedAt(DateUtility.getNowTimeInstant());
            stayExtensionRequestRepository.save(candidateStayRequestEntity);
        }
        stayExtensionRequestWorkflowRepository.cancelStayRequestWorkflow(stayId, candidateId);
    }

    public int validateStayPeriod(CandidateAppointmentRequestDto dto, String requestKey) throws Exception {
        int status = 0;
        String[] split = MCrypt.getInstance().decryptToString(requestKey).split(Constants.BACKTICK);
        Long candidateId = SecurityCtxUtil.candidateId();
        Long requestId = Long.parseLong(split[2]);
        Long stayId = Objects.nonNull(split[3]) && !split[3].isEmpty() ? Long.parseLong(split[3]) : 0;
        List<Object[]> conflictingAppointments = candidateStayDateRepository.findConflictingAppointments(candidateId, requestId, stayId, dto.getAppointmentFrom()
                , dto.getAppointmentTo());
        if (Objects.nonNull(conflictingAppointments) && !conflictingAppointments.isEmpty()) {
            status = 1;
        }
        return status;
    }

    public CandidateAppointmentRequestEntity saveAccommodationRequest(AccommodationRequestForm form, Long requestId,
                                                                      String approvalStatus) {

        CandidateAppointmentRequestDto candidateAppointmentRequestDto = form.getCandidateAppointmentRequestDto();
        candidateAppointmentRequestDto.setCandidateId(SecurityCtxUtil.candidateId());

        if (candidateAppointmentRequestDto.getGrossPay() == null) {
            candidateAppointmentRequestDto.setGrossPay(0D);
        }
        candidateAppointmentRequestDto.setCategory(candidateAppointmentRequestDto.getCategory().toUpperCase());
        if (candidateAppointmentRequestDto.getValidatingAuthority() == null) {
            candidateAppointmentRequestDto.setValidatingAuthority(Strings.EMPTY);
        }
        if (candidateAppointmentRequestDto.getValidatingAuthorityEmail() == null) {
            candidateAppointmentRequestDto.setValidatingAuthorityEmail(Strings.EMPTY);
        }
        candidateAppointmentRequestDto.setHostelManagement(false);
        candidateAppointmentRequestDto.setDining(true);

        candidateAppointmentRequestDto.setStatusNotes(commonResponseUtil.getMessage("message.save.accommodation.request"));
        CandidateAppointmentRequestEntity candidateAppointmentRequestEntity = CandidateAppointmentRequestMapper.INSTANCE.toCandidateAppointmentRequestEntity(candidateAppointmentRequestDto);

        candidateAppointmentRequestEntity.setActiveFlag(ModelConstants.STATUS_ACTIVE);
        candidateAppointmentRequestEntity.setResendDate(LocalDateTime.now());

        if (Objects.equals(requestId, 0L) || approvalStatus.equalsIgnoreCase(WorkflowStatus.REJECTED.getStatus())) {
            candidateAppointmentRequestEntity.setApprovalStatus(WorkflowStatus.VALIDATING.getStatus());
        }

        candidateAppointmentRequestEntity.setCreatedBy(SecurityCtxUtil.candidateId().toString());
        candidateAppointmentRequestEntity.setCreatedAt(DateUtility.getNowTimeInstant());
        candidateAppointmentRequestEntity.setModifiedBy(SecurityCtxUtil.candidateId().toString());
        candidateAppointmentRequestEntity.setModifiedAt(DateUtility.getNowTimeInstant());
        return candidateAppointmentRequestRepository.save(candidateAppointmentRequestEntity);
    }

    public CandidateAppointmentRequestEntity updateAccommodationRequest(AccommodationRequestForm form, String approvalStatus, Long requestId,
                                                                        String category, CandidateAppointmentRequestEntity candidateAppointmentRequestEntity) {
        CandidateAppointmentRequestDto candidateAppointmentRequestDto = form.getCandidateAppointmentRequestDto();
        candidateAppointmentRequestDto.setStatusNotes(commonResponseUtil.getMessage("message.update.accommodation.request"));
        CandidateAppointmentRequestMapper.INSTANCE.updateEntity(candidateAppointmentRequestEntity, candidateAppointmentRequestDto);

        if (approvalStatus.equalsIgnoreCase(WorkflowStatus.REJECTED.getStatus())) {
            candidateAppointmentRequestEntity.setApprovalStatus(WorkflowStatus.VALIDATING.getStatus());
        } else {
            candidateAppointmentRequestEntity.setApprovalStatus(approvalStatus);
        }

        candidateWorkflowRepository.
                findAllByCandidateIdAndApplicationIdAndActiveFlag(SecurityCtxUtil.candidateId(), requestId,
                        ModelConstants.STATUS_ACTIVE)
                .ifPresent(list -> list.forEach(entity -> {
                    if (approvalStatus.equals(WorkflowStatus.REJECTED.getStatus())) {
                        deactivateWorkflow(entity);
                    } else if (candidateAppointmentRequestEntity.getApprovalStatus().equalsIgnoreCase(WorkflowStatus.VALIDATING.getStatus())) {
                        updateWorkflow(entity, category);
                    } else {
                        throw new IllegalArgumentException(commonResponseUtil.getMessage("message.exception.invalid.operation"));
                    }
                }));


        candidateAppointmentRequestEntity.setModifiedBy(SecurityCtxUtil.candidateId().toString());
        candidateAppointmentRequestEntity.setModifiedAt(DateUtility.getNowTimeInstant());
        return candidateAppointmentRequestRepository.save(candidateAppointmentRequestEntity);

    }

    public void deactivateWorkflow(CandidateWorkflowEntity workflowEntity) {
        workflowEntity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
        workflowEntity.setModifiedBy(SecurityCtxUtil.candidateId().toString());
        workflowEntity.setModifiedAt(DateUtility.getNowTimeInstant());
        candidateWorkflowRepository.save(workflowEntity);
    }

    public void updateWorkflow(CandidateWorkflowEntity workflowEntity, String category) {
        workflowEntity.setCategory(category);
        workflowEntity.setModifiedAt(DateUtility.getNowTimeInstant());
        workflowEntity.setModifiedBy(SecurityCtxUtil.candidateId().toString());
        candidateWorkflowRepository.save(workflowEntity);
    }

    public void addWorkflowEntries(Long requestId, String categoryCode, AccommodationRequestForm form,
                                   String approvalStatus, String validatorEmail) {
        List<WorkflowMasterEntity> workflowMasterEntities = workflowMasterRepository.findAllByCategoryAndActiveFlagOrderByAuthenticationTypeAscApprovalLevelAsc(categoryCode,
                ModelConstants.STATUS_ACTIVE);
        AtomicInteger firstApprovalLevel = new AtomicInteger(0);
        CandidateAppointmentRequestDto candidateAppointmentRequestDto = form.getCandidateAppointmentRequestDto();
        if (!workflowMasterEntities.isEmpty()) {
            workflowMasterEntities.forEach(workflowMasterEntity -> {
                if (validateWorkflowCategory(workflowMasterEntity, candidateAppointmentRequestDto.getCategory())) {
                    saveWorkflow(requestId, workflowMasterEntity, candidateAppointmentRequestDto, approvalStatus,
                            validatorEmail, firstApprovalLevel);
                }
            });
        }
    }

    public boolean validateWorkflowCategory(WorkflowMasterEntity workflowMasterEntity, String formCategory) {
        List<String> categoryList1 = Arrays.asList(CategoryEnum.SASTHRA.getValue(), CategoryEnum.INTERVIEWS.getValue());
        List<String> categoryList2 = Arrays.asList(CategoryEnum.ICSR.getValue(), CategoryEnum.INTERNSHIP.getValue(), CategoryEnum.OTHERS.getValue(), CategoryEnum.GIAN.getValue());
        String workflowCategory = workflowMasterEntity.getCategory().toUpperCase();
        if (formCategory.equalsIgnoreCase(workflowCategory) && categoryList1.contains(workflowCategory)) {
            return true;
        } else return workflowCategory.equals(CategoryEnum.CCW.getValue()) && categoryList2.contains(formCategory);
    }

    public void saveWorkflow(Long requestId, WorkflowMasterEntity workflowMasterEntity,
                             CandidateAppointmentRequestDto dto, String approvalStatus, String validatorEmail,
                             AtomicInteger firstApprovalLevel) {

        if (firstApprovalLevel.get() == 0) {
            firstApprovalLevel.set(workflowMasterEntity.getApprovalLevel());
        }

        CandidateWorkflowEntity candidateWorkflowEntity = new CandidateWorkflowEntity();
        candidateWorkflowEntity.setApplicationId(requestId);
        candidateWorkflowEntity.setCandidateId(SecurityCtxUtil.candidateId());
        candidateWorkflowEntity.setAuthorityType(workflowMasterEntity.getAuthorityType());
        if (workflowMasterEntity.getAuthorityType().equalsIgnoreCase(ModelConstants.VALIDATOR)) {
            candidateWorkflowEntity.setValidatorName(dto.getValidatingAuthority());
            if (approvalStatus.equals(WorkflowStatus.REJECTED.getStatus())) {
                candidateWorkflowEntity.setEmail(validatorEmail);
            } else {
                candidateWorkflowEntity.setEmail(dto.getValidatingAuthorityEmail());
            }
        } else {
            candidateWorkflowEntity.setValidatorName(workflowMasterEntity.getValidatorName());
            candidateWorkflowEntity.setEmail(workflowMasterEntity.getEmail());
        }
        candidateWorkflowEntity.setApprovalLevel(workflowMasterEntity.getApprovalLevel());
        candidateWorkflowEntity.setAuthenticationType(ModelConstants.A);
        candidateWorkflowEntity.setCreatedBy(SecurityCtxUtil.candidateId().toString());
        candidateWorkflowEntity.setCreatedAt(DateUtility.getNowTimeInstant());
        candidateWorkflowEntity.setModifiedBy(SecurityCtxUtil.candidateId().toString());
        candidateWorkflowEntity.setModifiedAt(DateUtility.getNowTimeInstant());
        candidateWorkflowEntity.setActiveFlag(ModelConstants.STATUS_ACTIVE);
        candidateWorkflowEntity.setCategory(workflowMasterEntity.getCategory());
        candidateWorkflowEntity.setStatus(Objects.equals(workflowMasterEntity.getApprovalLevel(), firstApprovalLevel.get()) ?
                WorkflowStatus.PENDING.getStatus() : WorkflowStatus.DEFAULT.getStatus());

        candidateWorkflowRepository.save(candidateWorkflowEntity);
    }

    public void uploadFiles(Long requestId, AccommodationRequestForm form) throws Exception {
        String fileName;
        for (FileForm file : form.getFiles()) {
            String unique = UUID.randomUUID().toString().substring(0, 8);
            fileName = SecurityCtxUtil.candidateId() + Constants.BACKTICK + requestId + Constants.BACKTICK + 0 + Constants.BACKTICK + ModelConstants.UNDERSCORE + unique;
            saveFileInformation(file, fileName, requestId);
        }
    }

    public boolean saveFileInformation(FileForm fileForm, String fileName, Long requestId) throws Exception {
        String extension = FilenameUtils.getExtension(fileForm.getFile().getOriginalFilename());
        String encryptedFileName = MCrypt.getInstance().encryptToText(fileName + Utility.getCurrentTimeStamp()) + "." + extension;
        CandidateFilesInformationEntity candidateFilesInformationEntity = new CandidateFilesInformationEntity();
        candidateFilesInformationEntity.setCandidateId(SecurityCtxUtil.candidateId().intValue());
        candidateFilesInformationEntity.setRequestId(requestId.intValue());
        candidateFilesInformationEntity.setFilename(encryptedFileName);
        candidateFilesInformationEntity.setDescription(fileForm.getDescription());
        candidateFilesInformationEntity.setStayId(0L);
        candidateFilesInformationEntity.setActiveFlag(ModelConstants.STATUS_ACTIVE);
        candidateFilesInformationRepository.save(candidateFilesInformationEntity);
        saveFile(fileForm.getFile(), encryptedFileName);
        return true;
    }

    public void saveFile(MultipartFile file, String fileName) throws Exception {
        fileService.encodeFile(ModelConstants.CANDIDATE_FILE, file.getBytes(), fileName);
    }


    public void sendMailToValidators(String category, Long requestId, HttpServletRequest request, Long candidateId) {
        candidateWorkflowRepository.findByApplicationIdAndCategoryAndStatusAndActiveFlag(requestId, category, WorkflowStatus.PENDING.getStatus(),
                        ModelConstants.STATUS_ACTIVE)
                .filter(list -> !list.isEmpty())
                .ifPresent(list -> list.forEach(entity -> {
                    try {
                        String mailTemplate = getMailTemplate(entity, requestId, candidateId, request);
                        sendMail(mailTemplate, entity.getEmail());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    public String getMailTemplate(CandidateWorkflowEntity workflowEntity, Long requestId, Long candidateId, HttpServletRequest request) throws Exception {
        CandidateProfileEntity candidateProfileEntity = candidateProfileRepository.findByIdAndActiveFlag(candidateId, ModelConstants.STATUS_ACTIVE)
                .orElse(null);
        CandidateAppointmentRequestEntity candidateAppointmentRequestEntity =
                candidateAppointmentRequestRepository.findByIdAndCandidateIdAndActiveFlag(requestId,
                                candidateId, ModelConstants.STATUS_ACTIVE)
                        .orElse(null);

        String workFlowStatus = workflowEntity.getId() + Constants.BACKTICK + workflowEntity.getModifiedAt();
        String requestStatus = candidateId + Constants.BACKTICK + requestId;
        String approvalStatusStr =
                MCrypt.getInstance().encryptToText(requestStatus + Constants.BACKTICK + workFlowStatus + Constants.BACKTICK + WorkflowStatus.APPROVED.getStatus());
        String rejectedStatusStr =
                MCrypt.getInstance().encryptToText(requestStatus + Constants.BACKTICK + workFlowStatus + Constants.BACKTICK + WorkflowStatus.REJECTED.getStatus());
        String statusStr =
                MCrypt.getInstance().encryptToText(requestStatus + Constants.BACKTICK + workFlowStatus + Constants.BACKTICK + workflowEntity.getAuthorityType());
        String address = Stream.of(
                        Objects.requireNonNull(candidateProfileEntity).getAddress(),
                        candidateProfileEntity.getAddress2(),
                        candidateProfileEntity.getCity(),
                        candidateProfileEntity.getState()
                )
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(ModelConstants.COMMA))
                + (candidateProfileEntity.getPin() != null ? Constants.HYPHEN + candidateProfileEntity.getPin() : ModelConstants.EMPTY_STRING);

        String previousValidators = Strings.EMPTY;
        String prioAccom =
                (candidateAppointmentRequestEntity != null &&
                        candidateAppointmentRequestEntity.getAccomPriority() != null &&
                        candidateAppointmentRequestEntity.getAccomPriority() > 0)
                        ? String.valueOf(candidateAppointmentRequestEntity.getAccomPriority())
                        : Constants.HYPHEN;


        String contactNum =
                Objects.nonNull(candidateProfileEntity.getPhoneNumber()) && !candidateProfileEntity.getPhoneNumber().isEmpty() ?
                        candidateProfileEntity.getPhoneNumber() : candidateProfileEntity.getMobileNumber();


        String messageTemplate = simsConfigDataService.getSimConfigValue(SimsConfigDataService.ACCOMMODATION_REQUEST_MAIL_TEMPLATE);

        if (candidateAppointmentRequestEntity.getCategory().equalsIgnoreCase(CategoryEnum.ICSR.getValue())) {
            String empId =
                    Objects.nonNull(candidateProfileEntity.getEmployeeId()) && !candidateProfileEntity.getEmployeeId().isEmpty()
                            ? candidateProfileEntity.getEmployeeId() : Constants.NA;
            String designation =
                    Objects.nonNull(candidateProfileEntity.getDesignation()) && !candidateProfileEntity.getDesignation().isEmpty()
                            ? candidateProfileEntity.getDesignation() : Constants.NA;

            messageTemplate = messageTemplate.replace("#%dynamicTr%#", commonResponseUtil.getMessage("message.dynamic.tr"))
                    .replace("#%designation%#", designation)
                    .replace("#%empId%#", empId);
        } else {
            messageTemplate = messageTemplate.replace("#%dynamicTr%#", Strings.EMPTY);
        }

        messageTemplate = messageTemplate
                .replace("#%candidateName%#", candidateProfileEntity.getFirstName() + candidateProfileEntity.getLastName())
                .replace("#%address%#", address)
                .replace("#%contactNum%#", contactNum)
                .replace("#%email%#", candidateProfileEntity.getEmail())
                .replace("#%appFromDate%#", String.valueOf(candidateAppointmentRequestEntity.getAppointmentFrom()))
                .replace("#%appToDate%#", String.valueOf(candidateAppointmentRequestEntity.getAppointmentTo()))
                .replace("#%stayFromDate%#", String.valueOf(candidateAppointmentRequestEntity.getStayFrom()))
                .replace("#%stayToDate%#", String.valueOf(candidateAppointmentRequestEntity.getStayTo()))
                .replace("#%internship%#", candidateAppointmentRequestEntity.getCategory())
                .replace("#%purpose%#", candidateAppointmentRequestEntity.getPurpose())
                .replace("#%prioAccom%#", prioAccom);

        String subSubject;

        String domainUrl = Utility.getDomainUrl(request);

        if (workflowEntity.getAuthorityType().equalsIgnoreCase(ModelConstants.CCW_OFFICE)) {
            subSubject = commonResponseUtil.getMessage("message.mail.particulars");

            String viewButton = commonResponseUtil.getMessage("message.view.button")
                    .replace("#%domainUrl%#", domainUrl);
            String plainUrl = commonResponseUtil.getMessage("message.plain.url_2");
            messageTemplate = messageTemplate
                    .replace("#%subSubject%#", subSubject)
                    .replace("#%approvalButton%#", Strings.EMPTY)
                    .replace("#%rejectedButton%#", Strings.EMPTY)
                    .replace("#%viewButton%#", viewButton)
                    .replace("#%urlText%#", plainUrl);

        } else {
            subSubject = commonResponseUtil.getMessage("message.mail.particulars") + " by " + candidateProfileEntity.getFirstName() +
                    " " + candidateProfileEntity.getLastName() + ".";

            if (!previousValidators.isEmpty()) {
                subSubject = subSubject + commonResponseUtil.getMessage("message.approved.by") + previousValidators + ".";
            }

            String url = commonResponseUtil.getMessage("url.domain") + commonResponseUtil.getMessage("url.public.api") +
                    commonResponseUtil.getMessage("url.other.candidate.user");
            url = url.replace("#%domainUrl%#", domainUrl);
            String approvalUrl = url + ModelConstants.SLASH + WorkflowStatus.APPROVE.getStatus() + ModelConstants.SLASH + approvalStatusStr;
            String rejectUrl = url + ModelConstants.SLASH + WorkflowStatus.REJECTED.getStatus() + ModelConstants.SLASH + rejectedStatusStr;
            String viewUrl = url + ModelConstants.SLASH + WorkflowStatus.VIEW.getStatus() + ModelConstants.SLASH + statusStr;

            String plainUrl = commonResponseUtil.getMessage("message.plain.url_1")
                    .replace("#%approvalUrl%#", approvalUrl)
                    .replace("#%rejectUrl%#", rejectUrl)
                    .replace("#%viewUrl%#", viewUrl);

            String approveButton = commonResponseUtil.getMessage("message.approve.button")
                    .replace("#%url%#", approvalUrl);
            String rejectButton = commonResponseUtil.getMessage("message.reject.button")
                    .replace("#%url%#", rejectUrl);
            String viewButton = commonResponseUtil.getMessage("message.view.button")
                    .replace("#%url%#", viewUrl);
            messageTemplate = messageTemplate
                    .replace("#%subSubject%#", subSubject)
                    .replace("#%approvalButton%#", approveButton)
                    .replace("#%rejectButton%#", rejectButton)
                    .replace("#%viewButton%#", viewButton)
                    .replace("#%urlText%#", plainUrl);
        }

        return messageTemplate;
    }

    public void sendMail(String mailTemplate, String email) {
        List<String> emails;
        if (Objects.nonNull(email) && !email.isEmpty() && email.contains(",")) {
            emails = Stream.of(email.split(","))
                    .toList();
        } else {
            emails = List.of(email);
        }

        String subject = commonResponseUtil.getMessage("message.validate.accommodation.request");

        if (Objects.nonNull(email) && !emails.isEmpty()) {
            emails.forEach(e -> {
                try {
                    mailQueueService.saveMailQueue(subject, commonResponseUtil.getMessage("message.mail.greetings.for"), mailTemplate, e, commonResponseUtil.getMessage("message.accommodation.request"), null, null, null,
                            null, ModelConstants.REGARDS, ModelConstants.CCW_OFFICE);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    public String cancelAccommodationRequest(Long candidateId, Long requestId) throws Exception {
        Boolean cancelStatus = candidateAppointmentRequestRepository.findByIdAndCandidateIdAndActiveFlag(requestId,
                        candidateId, ModelConstants.STATUS_ACTIVE)
                .map(this::accommodationRequestCancel)
                .orElse(false);

        if (cancelStatus) {
            cancelCandidateWorkflow(requestId);
            return Constants.SAVED;
        }
        return Constants.FAILURE;
    }

    public boolean accommodationRequestCancel(CandidateAppointmentRequestEntity appointmentRequestEntity) {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(commonResponseUtil.getMessage("message.mail.date.format"));
        String formattedDateTime = now.format(formatter);

        String statusNotes = commonResponseUtil.getMessage("message.cancel.accommodation.request.status")
                .replace("#%currentTimeStamp%#", formattedDateTime);
        appointmentRequestEntity.setApprovalStatus(WorkflowStatus.CANCELLED.getStatus());
        appointmentRequestEntity.setStatusNotes(statusNotes);
        appointmentRequestEntity.setModifiedBy(SecurityCtxUtil.candidateId().toString());
        appointmentRequestEntity.setModifiedAt(DateUtility.getNowTimeInstant());
        candidateAppointmentRequestRepository.save(appointmentRequestEntity);
        return true;
    }

    public void cancelCandidateWorkflow(Long requestId) {
        candidateWorkflowRepository.findAllByCandidateIdAndApplicationIdAndActiveFlag(SecurityCtxUtil.candidateId(),
                        requestId, ModelConstants.STATUS_ACTIVE)
                .filter(list -> !list.isEmpty())
                .ifPresent(list -> list.forEach(this::cancelWorkflow));
    }

    public void cancelWorkflow(CandidateWorkflowEntity workflowEntity) {
        workflowEntity.setStatus(WorkflowStatus.CANCELLED.getStatus());
        workflowEntity.setModifiedBy(SecurityCtxUtil.candidateId().toString());
        workflowEntity.setModifiedAt(DateUtility.getNowTimeInstant());
        candidateWorkflowRepository.save(workflowEntity);
    }

    public boolean resendMail(HttpServletRequest request, Long candidateId, Long requestId, Long stayId) throws Exception {
        if (stayId <= 0){
            CandidateAppointmentRequestEntity candidateAppointmentRequestEntity =
                    candidateAppointmentRequestRepository.findByIdAndCandidateIdAndActiveFlag(requestId, candidateId,
                                    ModelConstants.STATUS_ACTIVE)
                            .orElse(null);

            if (Objects.nonNull(candidateAppointmentRequestEntity)) {
                String categoryCode = CategoryEnum.CCW.getValue();
                List<String> categoryList = Arrays.asList(CategoryEnum.ICSR.getValue(), CategoryEnum.INTERVIEWS.getValue(), CategoryEnum.SASTHRA.getValue());
                if (categoryList.contains(candidateAppointmentRequestEntity.getCategory().toUpperCase())) {
                    categoryCode = candidateAppointmentRequestEntity.getCategory().toUpperCase();
                }
                sendMailToValidators(categoryCode, requestId, request, candidateId);
                candidateAppointmentRequestEntity.setResendDate(LocalDateTime.now());
                candidateAppointmentRequestRepository.save(candidateAppointmentRequestEntity);
                return true;
            } else {
                return false;
            }
        } else {
            return resendStayExtensionMail(request, candidateId, requestId, stayId);
        }
    }

    public boolean resendStayExtensionMail(HttpServletRequest request, Long candidateId, Long requestId, Long stayId) throws Exception {
        CandidateStayRequestEntity candidateStayRequestEntity = candidateStayRequestRepository.findByStayIdAndActiveFlag(stayId, ModelConstants.STATUS_ACTIVE).orElse(null);

        if (Objects.nonNull(candidateStayRequestEntity)) {
            List<String> approvalStatuses = List.of(WorkflowStatus.PENDING.getStatus(), WorkflowStatus.REJECTED.getStatus());
            stayExtensionRequestWorkflowRepository
                    .findAllByCandidateIdAndAppointmentIdAndStayIdAndApprovalStatusInAndActiveFlag(candidateId, requestId, stayId, approvalStatuses,
                            ModelConstants.STATUS_ACTIVE)
                    .stream()
                    .map(StayExtensionRequestWorkflowMapper.INSTANCE::fromEntity)
                    .toList()
                    .forEach(w-> {
                        try {
                            stayExtensionRequestService.stayExtensionRequestMail(w, candidateId, requestId, stayId, CategoryEnum.CCW.getValue(),request);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
            candidateStayRequestEntity.setResendDate(LocalDateTime.now());
            candidateStayRequestRepository.save(candidateStayRequestEntity);
            return true;
        } else {
            return false;
        }
    }

    public void validateAccommodationRequestForm(CandidateAppointmentRequestDto dto, BindingResult result) {

        result.pushNestedPath("candidateAppointmentRequestDto");

        customValidators.validateField(dto.getStayFrom(), "stayFrom", "message.validation.period.of.stay.from.required", result);

        customValidators.validateField(dto.getStayTo(), "stayTo", "message.validation.period.of.stay.to.required", result);

        customValidators.validateField(dto.getPurpose(), "purpose", "message.validation.stay.request.purpose.required", result);

        if (dto.getDocumentsUploaded() == null || !dto.getDocumentsUploaded()) {
            customValidators.rejectField(result, "documentsUploaded", "message.validation.terms.condition.required");
        }

        if (dto.getApplicableCharges() == null || !dto.getApplicableCharges()) {
            customValidators.rejectField(result, "applicableCharges", "message.validation.terms.condition.required");
        }

        customValidators.validateDateOrder(dto.getStayFrom(), dto.getStayTo(),
                "stayTo", "message.validation.stay.end.date", result);


        if (dto.getCategory().equalsIgnoreCase(CategoryEnum.INTERVIEWS.getValue())) {
            customValidators.validateField(dto.getApplicationNo(), "applicationNo", "message.validation.applicationNo", result);
            customValidators.validateField(dto.getProgramDept(), "programDept", "messsage.validation.program.department.required", result);
        } else {
            customValidators.validateField(dto.getAppointmentFrom(), "appointmentFrom", "message.validation.period.of.appointment.from.required", result);

            customValidators.validateField(dto.getAppointmentTo(), "appointmentTo", "message.validation.period.of.appointment.to.required", result);

            customValidators.validateField(dto.getValidatingAuthority(), "validatingAuthority", "message.validation.authority.name.required", result);

            customValidators.validateField(dto.getValidatingAuthorityEmail(), "validatingAuthorityEmail", "message.validation.authority.email.required", result);

            customValidators.validateDateOrder(dto.getAppointmentFrom(), dto.getAppointmentTo(),
                    "appointmentTo", "message.validation.appointment.end.date", result);
        }

        result.popNestedPath();
    }


    public Resource generateAccommodationRequestDetailsPdf(Long candidateId, Long requestId, String encryptedKey, boolean checkProfileStatus, Long stayId) throws Exception {

        AccommodationRequestForm accommodationRequestDetailsById = getAccommodationRequestDetailsById(candidateId,
                requestId, encryptedKey, checkProfileStatus);
        String tempFileLocation = simsConfigDataService.getSimConfigValue(SimsConfigDataService.TEMP_FILE_LOCATION);

        if (Objects.nonNull(accommodationRequestDetailsById) && Objects.nonNull(accommodationRequestDetailsById.getCandidateProfileDto())
                && Objects.nonNull(accommodationRequestDetailsById.getCandidateAppointmentRequestDto())) {
            CandidateProfileDto candidateProfileDto = accommodationRequestDetailsById.getCandidateProfileDto();
            CandidateAppointmentRequestDto candidateAppointmentRequestDto = accommodationRequestDetailsById.getCandidateAppointmentRequestDto();
            List<CandidateWorkflowDto> candidateWorkflowList = accommodationRequestDetailsById.getCandidateWorkflowList();
            var approvalNotes = candidateWorkflowList.stream()
                    .filter(dto -> Objects.nonNull(dto.getValidatorName()) && Objects.nonNull(dto.getApprovalNotes()))
                    .map(dto -> dto.getAuthorityType() + ModelConstants.SPACE + ModelConstants.COLAN + ModelConstants.SPACE + dto.getApprovalNotes())
                    .collect(Collectors.joining(ModelConstants.COMMA + ModelConstants.SPACE));
            byte[] candidateProfileImage = fileService.getDecodedFile(SimsConfigDataService.CANDIDATE_FILE, candidateProfileDto.getImageName());
            String fileName = "AppointmentRequestDetails_" + Utility.getCurrentTimeStamp();
            File file = new File(tempFileLocation + fileName + ".pdf");

            boolean created;
            if (!file.getParentFile().exists()) {
                created = file.getParentFile().mkdirs();
            } else {
                created = true;
            }
            if (created) {
                created = file.createNewFile();
            }

            if (created) {
                ClassLoader classLoader = getClass().getClassLoader();
                InputStream imageStream = classLoader.getResourceAsStream(simsConfigDataService.getSimConfigValue(SimsConfigDataService.LOGO));

                Image headerLogo = null;

                if (imageStream != null) {
                    headerLogo = new Image(ImageDataFactory.create(imageStream.readAllBytes()));
                    headerLogo.setHeight(50);
                    headerLogo.setWidth(50);
                }

                Image candidateProfile = null;
                if (candidateProfileImage != null) {
                    candidateProfile = new Image(ImageDataFactory.create(candidateProfileImage));
                    candidateProfile.setWidth(60);
                    candidateProfile.setHeight(80);
                }


                LineSeparator lineSeparator = new LineSeparator(new SolidLine());
                lineSeparator.setWidth(VALUE_100_P);

                Cell emptySpace = new Cell();
                emptySpace.setHeight(10F);

                PdfWriter writer = new PdfWriter(tempFileLocation + fileName + ".pdf");
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc);

                Table table = new Table(new float[]{1, 3, 1}); // 3 columns
                table.setWidth(VALUE_100_P);

                Cell logoCell = new Cell();
                logoCell.add(headerLogo);
                logoCell.setHorizontalAlignment(HorizontalAlignment.LEFT);
                logoCell.setBorder(null);

                Cell profileCell = new Cell();
                if (candidateProfile != null) {
                    profileCell.add(candidateProfile);
                } else {
                    profileCell.add(new Paragraph("No Profile Image"));
                }
                profileCell.setHorizontalAlignment(HorizontalAlignment.RIGHT);
                profileCell.setBorder(null);

                Paragraph title = new Paragraph(commonResponseUtil.getMessage("message.pdf.header"))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                        .setFontSize(14); // Adjust as needed
                Cell titleCell = new Cell();
                titleCell.add(title);
                titleCell.setHorizontalAlignment(HorizontalAlignment.CENTER);

                Paragraph subtitle = new Paragraph(commonResponseUtil.getMessage("message.appointment.request.details"))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                        .setFontSize(12); // Adjust as needed
                Cell subtitleCell = new Cell();
                subtitleCell.add(subtitle);
                subtitleCell.setHorizontalAlignment(HorizontalAlignment.CENTER);

                Cell headingTextCell = new Cell();
                headingTextCell.add(titleCell);
                headingTextCell.add(subtitleCell);
                headingTextCell.setBorder(null);

                table.addCell(logoCell);
                table.addCell(headingTextCell);
                table.addCell(profileCell);

                Table personalDetailsTable = new Table(new float[]{2, 3, 2, 3});
                personalDetailsTable.setWidth(VALUE_100_P);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
                addTableTextValue("Request ID: ", candidateAppointmentRequestDto.getId(), personalDetailsTable, new int[]{1, 3});
                addTableTextValue("First Name: ", candidateProfileDto.getFirstName(), personalDetailsTable);
                addTableTextValue("Last Name: ", candidateProfileDto.getLastName(), personalDetailsTable, new int[]{1, 3});
                addTableTextValue("Date of Birth: ", candidateProfileDto.getDob() != null ?
                        candidateProfileDto.getDob().format(formatter) : Constants.NA, personalDetailsTable);
                addTableTextValue("Gender: ", candidateProfileDto.getGender().toUpperCase(), personalDetailsTable);
                addTableTextValue("Address1: ", candidateProfileDto.getAddress() != null
                        ? candidateProfileDto.getAddress() : Constants.HYPHEN, personalDetailsTable);
                addTableTextValue("Address2: ", candidateProfileDto.getAddress2() != null
                        ? candidateProfileDto.getAddress2() : Constants.HYPHEN, personalDetailsTable);
                addTableTextValue("City / Town: ", candidateProfileDto.getCity(), personalDetailsTable);
                addTableTextValue("State: ", candidateProfileDto.getState(), personalDetailsTable);
                addTableTextValue("PIN Code: ", candidateProfileDto.getPin(), personalDetailsTable);
                addTableTextValue("Phone Number: ", candidateProfileDto.getPhoneNumber(), personalDetailsTable);
                addTableTextValue("Mobile Number: ", candidateProfileDto.getMobileNumber(), personalDetailsTable);
                addTableTextValue("Email Address: ", candidateProfileDto.getEmail(), personalDetailsTable);
                addTableTextValue("Purpose: ", candidateProfileDto.getPostSelect(), personalDetailsTable, new int[]{1, 3});

                Table accommodationDetailsTable = new Table(new float[]{2, 3, 2, 3});
                accommodationDetailsTable.setWidth(VALUE_100_P);
                addTableTextValue("Appointment From:", Objects.nonNull(candidateAppointmentRequestDto.getAppointmentFrom()) ?
                        candidateAppointmentRequestDto.getAppointmentFrom().format(formatter) : Constants.NA, accommodationDetailsTable);
                addTableTextValue("Appointment To:", Objects.nonNull(candidateAppointmentRequestDto.getAppointmentTo()) ?
                        candidateAppointmentRequestDto.getAppointmentTo().format(formatter) : Constants.NA, accommodationDetailsTable);
                addTableTextValue("Stay Request From:", Objects.nonNull(candidateAppointmentRequestDto.getStayFrom()) ?
                        candidateAppointmentRequestDto.getStayFrom().format(formatter) : Constants.NA, accommodationDetailsTable);
                addTableTextValue("Stay Request To:", Objects.nonNull(candidateAppointmentRequestDto.getStayTo()) ?
                        candidateAppointmentRequestDto.getStayTo().format(formatter) : Constants.NA, accommodationDetailsTable);
                addTableTextValue("Category:", Objects.nonNull(candidateAppointmentRequestDto.getCategory()) ?
                        candidateAppointmentRequestDto.getCategory() : Constants.NA, accommodationDetailsTable);
                addTableTextValue("Purpose:", Objects.nonNull(candidateAppointmentRequestDto.getPurpose()) ?
                        candidateAppointmentRequestDto.getPurpose() : Constants.NA, accommodationDetailsTable);


                Table approvalStatusTable = new Table(new float[]{2, 3, 2, 3});
                approvalStatusTable.setWidth(VALUE_100_P);
                approvalStatusTable.setBorder(new SolidBorder(1));
                addTableHeader(approvalStatusTable, "Approver Name", "Approver Email", "Approval Status", new int[]{1, 3});
                candidateWorkflowList.forEach(workflow ->
                        addTableTextValueWithBorder(workflow.getValidatorName(), workflow.getEmail(), workflow.getStatus(),
                                approvalStatusTable, new int[]{1, 3}));

                Table recommodationNotesTable = new Table(new float[]{2, 3, 2, 3});
                accommodationDetailsTable.setWidth(VALUE_100_P);
                addTableTextValueWithBold("Recommended Occupancy:",
                        Objects.nonNull(candidateAppointmentRequestDto.getOccupancy()) ?
                                candidateAppointmentRequestDto.getOccupancy() : Constants.NA, recommodationNotesTable, new int[]{1, 3});
                addTableTextValueWithBold("Notes:", approvalNotes, recommodationNotesTable, new int[]{1, 3});

                Table emptyTable = new Table(new float[]{2, 3, 2, 3});
                emptyTable.setWidth(VALUE_100_P);
                Table vehicleDeclarationSignTable = new Table(new float[]{2, 3, 2, 3});
                vehicleDeclarationSignTable.setWidth(VALUE_100_P).setMarginTop(30);
                addTableTextValueWithEmpty("Authorized Signatory", "", vehicleDeclarationSignTable, new int[]{1, 3});
                addTableTextValueWithEmpty("CCW Office", "", vehicleDeclarationSignTable, new int[]{1, 3});

                document.add(table);
                document.add(lineSeparator);
                document.add(addFullWidthTitle("Personal Details", TextAlignment.LEFT));
                document.add(personalDetailsTable);
                document.add(lineSeparator);
                document.add(addFullWidthTitle("Appointment Details", TextAlignment.LEFT));
                document.add(accommodationDetailsTable);
                document.add(lineSeparator);
                if (stayId > 0) {
                    addStayExtensionSection(document, requestId, candidateId, stayId, formatter);
                }
                Div approvalBlock = new Div();
                approvalBlock.setKeepTogether(true);
                approvalBlock.add(addFullWidthTitle("Approval Status Details", TextAlignment.LEFT));
                approvalBlock.add(approvalStatusTable);
                document.add(approvalBlock);
//                document.add(new AreaBreak());
                if (pdfDoc.getLastPage().getPageSize().getHeight() - document.getBottomMargin() < 200) {
                    document.add(new AreaBreak());
                }
                addVerticalSpace(document, 4);
                document.add(lineSeparator);
                document.add(recommodationNotesTable);
                document.add(lineSeparator);
                document.add(new Paragraph("").setMarginBottom(8));
                vehicleDeclarationSignTable.setKeepTogether(true);
                document.add(vehicleDeclarationSignTable);
                document.close();

                String fileInputName = tempFileLocation + fileName + ".pdf";

                String outputFileName = tempFileLocation + fileName + "_watermarked_" + ".pdf";

                try (PdfDocument finalPdfDoc = new PdfDocument(new PdfReader(fileInputName), new PdfWriter(outputFileName))) {
                    pdfActionService.addWatermarkImage(finalPdfDoc);
                    finalPdfDoc.close();
                    Path outputPath = Paths.get(outputFileName);
                    return new UrlResource(outputPath.toUri());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    private void addStayExtensionSection(Document document, Long requestId, Long candidateId, Long stayId, DateTimeFormatter formatter) {

        AccommodationRequestForm stayForm = getStayExtensionDetails(requestId, candidateId, stayId, WorkflowStatus.APPROVED.getStatus(), WorkflowStatus.FROM_LOGIN);
        document.add(new LineSeparator(new SolidLine()));
//        addVerticalSpace(document, 8);
//        document.add(addFullWidthTitle("Stay Extension Details", TextAlignment.LEFT));
//        addVerticalSpace(document, 10);

        document.add(addFullWidthTitle("Previous Stay Extension Details", TextAlignment.LEFT));
        Table previousTable = createStayDetailsTable();
        previousTable.setKeepTogether(false);
        addStayTableHeader(previousTable);
        addPreviousStayRows(previousTable, stayForm.getPreviousStayExtensionDetailsList(), formatter);
        document.add(previousTable);
        addVerticalSpace(document, 4);

        Div currentStayBlock = new Div();
        currentStayBlock.setKeepTogether(true);
        currentStayBlock.add(addFullWidthTitle("Current Stay Extension Details", TextAlignment.LEFT));
        Table currentTable = createStayDetailsTable();
        currentTable.setKeepTogether(true);
        addStayTableHeader(currentTable);
        addCurrentStayRow(currentTable, stayForm.getCurrentStayExtensionDetails(), formatter);
        currentStayBlock.add(currentTable);
        document.add(currentStayBlock);

        addVerticalSpace(document, 4);
        document.add(new LineSeparator(new SolidLine()));
    }

    private void addVerticalSpace(Document document, float space) {
        document.add(new Paragraph("").setMarginBottom(space));
    }

    private void addVerticalSpace(Div div, float space) {
        div.add(new Paragraph("").setMarginBottom(space));
    }

    private Table createStayDetailsTable() {
        Table table = new Table(new float[]{2, 2, 2, 2, 2, 2});
        table.setWidth(UnitValue.createPercentValue(100));
        return table;
    }

    private void addStayTableHeader(Table table) {
        DeviceRgb headerColor = new DeviceRgb(227, 230, 240);
        addHeaderCell(table, "Stay From", headerColor);
        addHeaderCell(table, "Stay To", headerColor);
        addHeaderCell(table, "Approval Date", headerColor);
        addHeaderCell(table, "Mess Option", headerColor);
        addHeaderCell(table, "Hostel", headerColor);
        addHeaderCell(table, "Room No", headerColor);
    }

    private void addHeaderCell(Table table, String text, DeviceRgb bg) {
        Cell cell = new Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(bg)
                .setFontSize(10)
                .setBorder(new SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(cell);
    }

    private void addPreviousStayRows(Table table, List<PreviousStayExtensionDetails> list, DateTimeFormatter formatter) {
        if (list == null || list.isEmpty()) {
            addEmptyStayRow(table);
            return;
        }
        for (PreviousStayExtensionDetails d : list) {
            table.addCell(textCell(d.stayFrom(), formatter));
            table.addCell(textCell(d.stayTo(), formatter));
            table.addCell(textCell(d.approvalDate(), formatter));
            table.addCell(textCell(d.messOption()));
            table.addCell(textCell(d.hostelName()));
            table.addCell(textCell(d.roomNo()));
        }
    }

    private void addCurrentStayRow(Table table, CurrentStayExtensionDetails d, DateTimeFormatter formatter) {
        if (d == null) {
            addEmptyStayRow(table);
            return;
        }
        table.addCell(textCell(d.stayFrom(), formatter));
        table.addCell(textCell(d.stayTo(), formatter));
        table.addCell(textCell(d.modifiedAt(), formatter));
        table.addCell(textCell(d.messOption()));
        table.addCell(textCell(d.hostelName()));
        table.addCell(textCell(d.roomNo()));
    }

    private Cell textCell(Object value) {
        return new Cell()
                .add(new Paragraph(value != null ? value.toString() : Constants.NA))
                .setBorder(new SolidBorder(1))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private Cell textCell(LocalDateTime dt, DateTimeFormatter f) {
        return textCell(dt != null ? dt.format(f) : Constants.NA);
    }

    private Cell textCell(LocalDate d, DateTimeFormatter f) {
        return textCell(d != null ? d.format(f) : Constants.NA);
    }

    private void addEmptyStayRow(Table table) {
        for (int i = 0; i < 6; i++) {
            table.addCell(textCell(Constants.NA));
        }
    }

    private void addTableTextValue(String text, Object value, Table table) {
        addTableTextValue(text, value, table, new int[]{1, 1});
    }

    private void addTableTextValue(String text, Object value, Table table, int[] colSpan) {
        Cell textCell = new Cell(1, colSpan[0]).add(new Paragraph(text));
        textCell.setWidth(UnitValue.createPercentValue(20));
        textCell.setFontSize(10);
        textCell.setBorder(null);
        textCell.setPaddingBottom(5F);
        table.addCell(textCell);
        Cell valueCell = new Cell(1, colSpan[1]).add(new Paragraph(value != null && !value.equals("") ? value.toString() : "N/A"));
        valueCell.setWidth(UnitValue.createPercentValue(30));
        valueCell.setBorder(null);
        valueCell.setPaddingBottom(5F);
        valueCell.setFontSize(10);
        table.addCell(valueCell);
    }

    private void addTableTextValueWithBold(String text, Object value, Table table, int[] colSpan) {
        Cell textCell = new Cell(1, colSpan[0]).add(new Paragraph(text).setBold().setFontSize(12));
        textCell.setWidth(UnitValue.createPercentValue(20));
        textCell.setBorder(null);
        textCell.setPaddingBottom(10F);
        table.addCell(textCell);
        Cell valueCell = new Cell(1, colSpan[1]).add(new Paragraph(value != null && !value.equals("") ? value.toString() : "N/A"));
        valueCell.setWidth(UnitValue.createPercentValue(30));
        valueCell.setBorder(null);
        valueCell.setPaddingBottom(10F);
        valueCell.setFontSize(10);
        table.addCell(valueCell);
    }

    private void addTableTextValueWithEmpty(String text, Object value, Table table, int[] colSpan) {
        Cell textCell = new Cell(1, colSpan[0]).add(new Paragraph(text));
        textCell.setWidth(UnitValue.createPercentValue(20));
        textCell.setBorder(null);
        textCell.setPaddingBottom(3F);
        textCell.setFontSize(10);
        table.addCell(textCell);
        Cell valueCell = new Cell(1, colSpan[1]).add(new Paragraph(value != null && !value.equals("") ?
                value.toString() : Strings.EMPTY));
        valueCell.setWidth(UnitValue.createPercentValue(30));
        valueCell.setBorder(null);
        valueCell.setPaddingBottom(3F);
        valueCell.setFontSize(10);
        table.addCell(valueCell);
    }

    private void addTableTextValueWithBorder(String text, String email, Object value, Table table, int[] colSpan) {
        Cell textCell = new Cell(1, colSpan[0]).add(new Paragraph(text));
        textCell.setWidth(UnitValue.createPercentValue(20));
        textCell.setBorder(new SolidBorder(1));
        textCell.setPaddingBottom(5f);
        textCell.setPaddingLeft(5f);
        textCell.setFontSize(10);
        table.addCell(textCell);

        Cell emailCell = new Cell(1, colSpan[0]).add(new Paragraph(email));
        emailCell.setWidth(UnitValue.createPercentValue(20));
        emailCell.setBorder(new SolidBorder(1));
        emailCell.setPaddingLeft(5f);
        emailCell.setPaddingBottom(5f);
        emailCell.setFontSize(10);
        table.addCell(emailCell);

        Cell valueCell = new Cell(1, colSpan[1]).add(new Paragraph(value != null && !value.equals("") ? value.toString() : "N/A"));
        valueCell.setWidth(UnitValue.createPercentValue(30));
        valueCell.setBorder(new SolidBorder(1));
        valueCell.setPaddingBottom(5f);
        valueCell.setPaddingLeft(5f);
        valueCell.setTextAlignment(TextAlignment.CENTER);
        valueCell.setFontSize(10);
        table.addCell(valueCell);
    }

    private IBlockElement addFullWidthTitle(String title, TextAlignment alignment) {
        Paragraph titlePara = new Paragraph(title)
                .setBold()
                .setFontSize(12);
        Cell titleCell = new Cell();
        titleCell.add(titlePara);
        titleCell.setTextAlignment(alignment);
        titleCell.setVerticalAlignment(VerticalAlignment.BOTTOM);
        titleCell.setMinHeight(30);
        titleCell.setBorder(null);
        titleCell.setPaddingBottom(10F);
        Table titleTable = new Table(1);
        titleTable.setWidth(VALUE_100_P);
        titleTable.addCell(titleCell);
        return titleTable;
    }

    private void addTableHeader(Table table, String col1Value, String col2Value, String col3Value, int[] colSpan) {

        DeviceRgb tableHeaderColor = new DeviceRgb(227, 230, 240);

        Cell col1Header = new Cell(1, colSpan[0]).add(new Paragraph(col1Value));
        col1Header.setWidth(UnitValue.createPercentValue(20));
        col1Header.setBorder(new SolidBorder(1));
        col1Header.setPaddingBottom(5f);
        col1Header.setPaddingLeft(5f);
        col1Header.setBackgroundColor(tableHeaderColor);
        col1Header.setFontSize(10);
        table.addCell(col1Header);

        Cell col2Header = new Cell(1, colSpan[0]).add(new Paragraph(col2Value));
        col2Header.setWidth(UnitValue.createPercentValue(20));
        col2Header.setBorder(new SolidBorder(1));
        col2Header.setPaddingBottom(5f);
        col2Header.setPaddingLeft(5f);
        col2Header.setBackgroundColor(tableHeaderColor);
        col2Header.setFontSize(10);
        table.addCell(col2Header);

        Cell col3Header = new Cell(1, colSpan[1]).add(new Paragraph(col3Value));
        col3Header.setWidth(UnitValue.createPercentValue(30));
        col3Header.setBorder(new SolidBorder(1));
        col3Header.setPaddingBottom(5f);
        col3Header.setPaddingLeft(5f);
        col3Header.setBackgroundColor(tableHeaderColor);
        col3Header.setTextAlignment(TextAlignment.CENTER);
        col3Header.setFontSize(10);
        table.addCell(col3Header);
    }

    public CandidateAppointmentRequestDto getCandidateAppointmentRequestDetails(Long candidateId) {
        return candidateAppointmentRequestRepository.getCandidateAppointmentRequestDetails(candidateId,
                        ModelConstants.STATUS_ACTIVE)
                .map(CandidateAppointmentRequestMapper.INSTANCE::fromCandidateAppointmentRequestEntity)
                .orElse(CandidateAppointmentRequestDto.builder().build());
    }

    public AccommodationRequestForm viewAccommodationRequestDetails(Long candidateId, Long requestId) throws JsonProcessingException {
        CandidateProfileDto candidateProfileDto = candidateProfileRepository.findByIdAndActiveFlag(candidateId, ModelConstants.STATUS_ACTIVE)
                .map(CandidateProfileMapper.INSTANCE::toDto)
                .orElse(new CandidateProfileDto());

        String postData = simsConfigDataService.getSimConfigValue(Constants.CANDIDATE_POST);
        if (postData != null && !postData.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            candidateProfileDto.setPostData(objectMapper.readValue(postData, Map.class));
        }

        CandidateAppointmentRequestDto candidateAppointmentRequestDto = candidateAppointmentRequestRepository.findByIdAndCandidateIdAndActiveFlag(requestId, candidateId,
                        ModelConstants.STATUS_ACTIVE)
                .map(CandidateAppointmentRequestMapper.INSTANCE::fromCandidateAppointmentRequestEntity)
                .orElse(CandidateAppointmentRequestDto.builder().build());


        List<CandidateFilesInformationDto> candidateFilesInformationList =
                candidateFilesInformationRepository.findAllByRequestIdAndCandidateIdAndActiveFlag(requestId, candidateId, ModelConstants.STATUS_ACTIVE)
                        .map(list -> list.stream().map(CandidateFilesInformationMapper.INSTANCE::toDto).toList())
                        .orElse(Collections.emptyList());


        List<CandidateWorkflowDto> workflowDtoList =
                candidateWorkflowRepository.findAllByCandidateIdAndApplicationIdAndActiveFlagAndStatusNot(candidateId, requestId,
                                ModelConstants.STATUS_ACTIVE, WorkflowStatus.DEFAULT.getStatus())
                        .map(list -> list.stream().map(CandidateWorkflowMapper.INSTANCE::toDto).toList())
                        .orElse(Collections.emptyList());
        String approvalNotes = workflowDtoList.stream().map(CandidateWorkflowDto::getApprovalNotes)
                .filter(notes -> notes != null && !notes.isEmpty())
                .collect(Collectors.joining(", "));
        String rejectDescription = candidateAppointmentRequestDto.getRejectionDescription();
        if (rejectDescription != null && !rejectDescription.isEmpty()) {
            candidateAppointmentRequestDto.setNotes(rejectDescription);
        } else {
            candidateAppointmentRequestDto.setNotes(approvalNotes);
        }

        return AccommodationRequestForm
                .builder()
                .candidateProfileDto(candidateProfileDto)
                .candidateAppointmentRequestDto(candidateAppointmentRequestDto)
                .candidateFilesInformationList(candidateFilesInformationList)
                .candidateWorkflowList(workflowDtoList)
                .build();
    }

    public String approveOrRejectRequest(Long candidateId, Long requestId, Long workflowId, String status,
                                         String reason, HttpServletRequest request) {
        processCandidateAppointmentRequest(candidateId, requestId, status, reason);
        if (WorkflowStatus.APPROVED.getStatus().equals(status)) {
            approveRequest(workflowId, reason, status, requestId, candidateId, request);
        } else {
            rejectRequest(workflowId, reason, status, requestId, candidateId);
        }
        return Constants.SAVED;
    }

    public boolean approveRequest(Long workflowId, String reason, String status, Long requestId, Long candidateId,
                                  HttpServletRequest request) {
        Integer i = processWorkflow(workflowId, reason, status);
        if (i > 0) {
            AtomicBoolean s = new AtomicBoolean(false);
            candidateWorkflowRepository.getNextLevelApprovalList(candidateId, requestId, i)
                    .forEach(e -> this.updateNextLevelValidators(e, s));

            if (!s.get()) {
                sendMailToOtherCandidate(status, candidateId, requestId, reason);
            } else {
                String category = "";
                mailToNextLevelApprovers(category, requestId, candidateId, request);
            }
        }
        return true;
    }

    public boolean rejectRequest(Long workflowId, String reason, String status, Long requestId, Long candidateId) {
        Integer i = processWorkflow(workflowId, reason, status);
        sendMailToOtherCandidate(status, candidateId, requestId, reason);
        return true;
    }

    public boolean processCandidateAppointmentRequest(Long candidateId, Long requestId, String status, String reason) {
        return candidateAppointmentRequestRepository.findByIdAndCandidateIdAndActiveFlag(requestId, candidateId, ModelConstants.STATUS_ACTIVE)
                .map(e -> updateCandidateAppointmentRequest(e, status, reason))
                .orElse(false);
    }

    public boolean updateCandidateAppointmentRequest(CandidateAppointmentRequestEntity entity, String status, String reason) {
        entity.setApprovalStatus(status);
        entity.setStatusNotes(reason);
        candidateAppointmentRequestRepository.save(entity);
        return true;
    }

    public void updateNextLevelValidators(CandidateWorkflowEntity entity, AtomicBoolean status) {
        entity.setStatus(WorkflowStatus.PENDING.getStatus());
        candidateWorkflowRepository.save(entity);
        status.set(true);
    }


    public Integer processWorkflow(Long workflowId, String reason, String status) {
        List<CandidateWorkflowEntity> workflowEntityList =
                candidateWorkflowRepository.findAllByIdAndActiveFlag(workflowId, ModelConstants.STATUS_ACTIVE)
                        .map(list -> list.stream()
                                .map(e -> updateCandidateWorkflow(e, reason, status))
                                .toList())
                        .orElseGet(Collections::emptyList);

        if (!workflowEntityList.isEmpty()) {
            candidateWorkflowRepository.saveAllAndFlush(workflowEntityList);
            return workflowEntityList.getFirst().getApprovalLevel();
        }

        return 0;
    }

    public CandidateWorkflowEntity updateCandidateWorkflow(CandidateWorkflowEntity entity, String reason, String status) {
        entity.setStatus(status);
        entity.setApprovalNotes(reason);
        return entity;
    }

    public void sendMailToOtherCandidate(String status, Long candidateId, Long requestId, String reason) {
        CandidateProfileEntity candidateProfileEntity = candidateProfileRepository.findByIdAndActiveFlag(candidateId, ModelConstants.STATUS_ACTIVE)
                .orElse(null);
        CandidateAppointmentRequestEntity candidateAppointmentRequestEntity =
                candidateAppointmentRequestRepository.findByIdAndCandidateIdAndActiveFlag(requestId,
                                candidateId, ModelConstants.STATUS_ACTIVE)
                        .orElse(null);

        String subject = commonResponseUtil.getMessage("message.validate.accommodation.request");

        String subSubject = "";
        if (WorkflowStatus.APPROVED.getStatus().equalsIgnoreCase(status)) {
            subSubject = commonResponseUtil.getMessage("message.other.candidate.approved.request");
        } else {
            subSubject = commonResponseUtil.getMessage("message.other.candidate.rejected.request")
                    .replace("#%reason%#", reason);

        }

        String messageTemplate = simsConfigDataService.getSimConfigValue(SimsConfigDataService.ACCOMMODATION_REQUEST_MAIL_TEMPLATE);

        String address = Stream.of(
                        Objects.requireNonNull(candidateProfileEntity).getAddress(),
                        candidateProfileEntity.getAddress2(),
                        candidateProfileEntity.getCity(),
                        candidateProfileEntity.getState()
                )
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(ModelConstants.COMMA))
                + (candidateProfileEntity.getPin() != null ? Constants.HYPHEN + candidateProfileEntity.getPin() : ModelConstants.EMPTY_STRING);

        String prioAccom =
                Objects.nonNull(candidateAppointmentRequestEntity.getAccomPriority()) && candidateAppointmentRequestEntity.getAccomPriority() > 0L ?
                        String.valueOf(candidateAppointmentRequestEntity.getAccomPriority()) : Constants.HYPHEN;
        String contactNum =
                Objects.nonNull(candidateProfileEntity.getPhoneNumber()) && !candidateProfileEntity.getPhoneNumber().isEmpty() ?
                        candidateProfileEntity.getPhoneNumber() : candidateProfileEntity.getMobileNumber();

        messageTemplate = messageTemplate
                .replace("#%candidateName%#", candidateProfileEntity.getFirstName() + candidateProfileEntity.getLastName())
                .replace("#%address%#", address)
                .replace("#%contactNum%#", contactNum)
                .replace("#%email%#", candidateProfileEntity.getEmail())
                .replace("#%appFromDate%#", String.valueOf(candidateAppointmentRequestEntity.getAppointmentFrom()))
                .replace("#%appToDate%#", String.valueOf(candidateAppointmentRequestEntity.getAppointmentTo()))
                .replace("#%stayFromDate%#", String.valueOf(candidateAppointmentRequestEntity.getStayFrom()))
                .replace("#%stayToDate%#", String.valueOf(candidateAppointmentRequestEntity.getStayTo()))
                .replace("#%internship%#", candidateAppointmentRequestEntity.getCategory())
                .replace("#%purpose%#", candidateAppointmentRequestEntity.getPurpose())
                .replace("#%prioAccom%#", prioAccom);

        messageTemplate = messageTemplate
                .replace("#%subSubject%#", subSubject)
                .replace("#%approvalButton%#", Strings.EMPTY)
                .replace("#%rejectButton%#", Strings.EMPTY)
                .replace("#%viewButton%#", Strings.EMPTY)
                .replace("#%urlText%#", Strings.EMPTY);

        try {
            mailQueueService.saveMailQueue(subject, commonResponseUtil.getMessage("message.mail.greetings.for"),
                    messageTemplate, candidateProfileEntity.getEmail(),
                    commonResponseUtil.getMessage("message.accommodation.request"), null, null, null,
                    null, ModelConstants.REGARDS, ModelConstants.CCW_OFFICE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void mailToNextLevelApprovers(String category, Long requestId, Long candidateId, HttpServletRequest request) {
        sendMailToValidators(category, requestId, request, candidateId);
    }

    public String deleteAccommodationRequest(Long candidateId, Long requestId) {
        String notes = commonResponseUtil.getMessage("message.acc.delete.notes") + " " + SecurityCtxUtil.userName() + ".";

        int workflowDeleted = candidateWorkflowRepository
                .deleteWorkflowDetails(requestId, candidateId, ModelConstants.STATUS_ACTIVE,Constants.CCW,RoleEnum.DEAN.getValue());

        int appointmentDeleted = candidateAppointmentRequestRepository
                .deleteAppointmentRequests(requestId, candidateId, notes);

        return (workflowDeleted > 0 && appointmentDeleted > 0) ? Constants.SAVED : Constants.FAILURE;
    }

    public AccommodationRequestForm requestView(Long candidateId, Long requestId, String approvalStatus, WorkflowStatus emailStatus) {
        try {
            AccommodationRequestForm accommodationRequestForm = viewAccommodationRequestDetails(candidateId, requestId);
            accommodationRequestForm.setApprovalStatus(approvalStatus);
            accommodationRequestForm.setEmailStatus(emailStatus.getStatus());
            return accommodationRequestForm;
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public String updateStatus(AccommodationRequestForm accommodationRequestForm, String status, Long candidateId,
                               Long requestId, Long workflowId, String modifiedAt, HttpServletRequest request) throws Exception {

        LocalDateTime instant = LocalDateTime.parse(modifiedAt);

        String rejectReason = Objects.nonNull(accommodationRequestForm.getRejectReason())
                && accommodationRequestForm.getRejectReason().length() > 3 ? accommodationRequestForm.getRejectReason()
                : null;
        String approvalStatus = accommodationRequestForm.getCandidateAppointmentRequestDto().getNotes();
        String occupancy = accommodationRequestForm.getCandidateAppointmentRequestDto().getOccupancy();
        Long accomPriority = accommodationRequestForm.getCandidateAppointmentRequestDto().getAccomPriority();
        CandidateAppointmentRequestDto requestDto = accommodationRequestForm.getCandidateAppointmentRequestDto();
        if (!checkRequestRejected(workflowId)) {
            if (checkApprovalStatus(workflowId, instant)) {
                CandidateWorkflowEntity candidateWorkflowEntity;
                if(WorkflowStatus.APPROVED.getStatus().equalsIgnoreCase(status)){
                    candidateWorkflowEntity = isValidRequest(workflowId);
                    if(Objects.nonNull(candidateWorkflowEntity)){
                        return updateWorkflowProcess(requestId, candidateId, workflowId, instant, status, rejectReason, approvalStatus,
                                occupancy, accomPriority, candidateWorkflowEntity, requestDto, request);
                    }
                    else{
                        return commonResponseUtil.getMessage("message.invalid.request");
                    }
                }
                else {
                    candidateWorkflowEntity = checkWorkflowExists(workflowId);
                    if (Objects.nonNull(candidateWorkflowEntity)) {
                        return updateWorkflowProcess(requestId, candidateId, workflowId, instant, status, rejectReason,
                                approvalStatus, occupancy, accomPriority, candidateWorkflowEntity, requestDto, request);
                    } else {
                        return commonResponseUtil.getMessage("message.invalid.request");
                    }
                }
            } else {
                return commonResponseUtil.getMessage("message.invalid.request");
            }
        } else {
            return commonResponseUtil.getMessage("message.request.already.rejected");
        }
    }

    public boolean checkRequestRejected(Long workflowId) {
        return candidateAppointmentRequestRepository.getCandidateRequestByStatus(workflowId, WorkflowStatus.REJECTED.getStatus(),
                        Constants.CCW,RoleEnum.DEAN.getValue())
                .isPresent();
    }

    public boolean checkApprovalStatus(Long workflowId, LocalDateTime modifiedAt) {
        return candidateAppointmentRequestRepository.getApprovalStatus(workflowId, modifiedAt, ModelConstants.STATUS_ACTIVE)
                .isPresent();
    }

    public CandidateWorkflowEntity isValidRequest(Long workflowId) {
        return candidateWorkflowRepository.getByStatus(workflowId, ModelConstants.STATUS_ACTIVE).orElse(null);
    }

    public CandidateWorkflowEntity checkWorkflowExists(Long workflowId) {
        return candidateWorkflowRepository.findByIdAndActiveFlag(workflowId, ModelConstants.STATUS_ACTIVE).orElse(null);
    }

    public String updateWorkflowProcess(Long requestId, Long candidateId, Long workflowId, LocalDateTime modifiedAt, String status, String rejectReason,
                                        String approvalNotes, String occupancy, Long accomPriority, CandidateWorkflowEntity workflowEntity,
                                        CandidateAppointmentRequestDto requestDto, HttpServletRequest request) {
        String modifiedBy = Objects.nonNull(SecurityCtxUtil.userName()) ? SecurityCtxUtil.userName() : workflowEntity.getValidatorName();
        List<Object[]> objects = candidateWorkflowRepository.processWorkflow(workflowId, modifiedAt, status, rejectReason, approvalNotes, occupancy,
                accomPriority, modifiedBy);

        String currentValidatorName = prepareCurrentLevelValidator(workflowEntity);
        List<CandidateWorkflowDto> nextLevelValidatorList =
                objects.stream()
                        .map(this::prepareNextLevelValidatorList)
                        .toList();
        String nextLevelValidatorNameList;
        CandidateWorkflowDto candidateWorkflowDto;

        if (Objects.nonNull(objects) && !objects.isEmpty() && WorkflowStatus.APPROVED.getStatus().equalsIgnoreCase(status)) {
            nextLevelValidatorNameList = objects.stream()
                    .map(this::createValidatorName)
                    .collect(Collectors.joining(ModelConstants.COMMA));

            String category = nextLevelValidatorList.getFirst().getCategory();
            Long applicationId = nextLevelValidatorList.getFirst().getApplicationId();
            sendMailToValidators(category, applicationId, request, nextLevelValidatorList.getFirst().getCandidateId());
            boolean b = intermediateMailForCandidate(workflowEntity.getApprovalLevel(),
                    currentValidatorName, nextLevelValidatorNameList, candidateId);
            if (b) {
                return Constants.SAVED;
            } else {
                return Constants.FAILURE;
            }
        }

        if (nextLevelValidatorList.isEmpty() && (WorkflowStatus.APPROVED.getStatus().equalsIgnoreCase(status) ||
                WorkflowStatus.OVERRIDE_APPROVED.getStatus().equalsIgnoreCase(status))) {
            candidateWorkflowDto = prepareNextLevelValidatorList(workflowEntity);
            updateAppointmentRequest(requestId, candidateId, requestDto);
            try {
                boolean s1 = finalMailForCcwConfig(candidateWorkflowDto, request);
                boolean s2 = finalApprovalMailToCandidate(requestId, workflowEntity.getCategory(), workflowEntity.getApprovalLevel(),
                        currentValidatorName, candidateId,0L);
                return Constants.SAVED;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (WorkflowStatus.REJECTED.getStatus().equalsIgnoreCase(status)) {
            candidateWorkflowDto = CandidateWorkflowDto.builder()
                    .email(workflowEntity.getEmail())
                    .applicationId(workflowEntity.getApplicationId())
                    .candidateId(workflowEntity.getCandidateId())
                    .modifiedAt(workflowEntity.getModifiedAt())
                    .validatorName(workflowEntity.getValidatorName())
                    .authorityType(workflowEntity.getAuthorityType())
                    .id(workflowEntity.getId())
                    .build();
            sendRejectionMailToCandidate(candidateWorkflowDto, rejectReason);
            return Constants.SAVED;
        }
        return Constants.FAILURE;
    }

    public String createValidatorName(Object[] o) {
        String validatorName = utility.parseString(o[6]);
        String authorityType = utility.parseString(o[3]);
        if (!authorityType.isEmpty() && authorityType.equalsIgnoreCase(CategoryEnum.OTHERS.getValue())) {
            return validatorName;
        } else {
            if (!validatorName.isEmpty()) {
                return authorityType + "(" + validatorName + ")";
            } else {
                return authorityType;
            }
        }
    }

    public String createStayExtValidatorName(Object[] o) {
        String validatorName = utility.parseString(o[7]);
        String authorityType = utility.parseString(o[4]);
        if (!authorityType.isEmpty() && authorityType.equalsIgnoreCase(CategoryEnum.OTHERS.getValue())) {
            return validatorName;
        } else {
            if (!validatorName.isEmpty()) {
                return authorityType + "(" + validatorName + ")";
            } else {
                return authorityType;
            }
        }
    }

    public String prepareCurrentLevelValidator(CandidateWorkflowEntity entity) {
        String validatorName;
        if (Objects.nonNull(entity.getAuthorityType()) &&
                entity.getAuthorityType().equalsIgnoreCase(CategoryEnum.OTHERS.getValue())) {
            validatorName = entity.getValidatorName();
        } else {
            if (Objects.nonNull(entity.getValidatorName()) && entity.getValidatorName().length() > 3) {
                validatorName = entity.getAuthorityType() + '(' + entity.getValidatorName() + ')';
            } else {
                validatorName = entity.getAuthorityType();
            }
        }
        return validatorName;
    }

    public CandidateWorkflowDto prepareNextLevelValidatorList(Object[] o) {
        return CandidateWorkflowDto.builder()
                .applicationId(utility.parseLong(o[1]))
                .candidateId(utility.parseLong(o[2]))
                .category(String.valueOf(o[14]))
                .build();
    }

    public CandidateWorkflowDto prepareNextLevelValidatorList(CandidateWorkflowEntity candidateWorkflowEntity) {
        String validatorEmail = Optional.ofNullable(simsConfigDataService.getSimConfigValue(SimsConfigDataService.APPROVAL_MAIL_TO))
                .orElse("support@triesten.com");

        return CandidateWorkflowDto.builder()
                .email(validatorEmail)
                .applicationId(candidateWorkflowEntity.getApplicationId())
                .candidateId(candidateWorkflowEntity.getCandidateId())
                .modifiedAt(candidateWorkflowEntity.getModifiedAt())
                .validatorName(candidateWorkflowEntity.getValidatorName())
                .authorityType(candidateWorkflowEntity.getAuthorityType())
                .id(candidateWorkflowEntity.getId())
                .build();
    }

    public void updateAppointmentRequest(Long requestId, Long candidateId, CandidateAppointmentRequestDto requestDto) {
        CandidateAppointmentRequestEntity requestEntity = candidateAppointmentRequestRepository
                .findByIdAndActiveFlag(requestId, ModelConstants.STATUS_ACTIVE)
                .orElse(null);

        boolean status = false;
        if (Objects.nonNull(requestEntity)) {
            if (Objects.nonNull(requestDto.getStayFrom()) && Objects.nonNull(requestDto.getStayTo())) {
                if (!requestEntity.getStayFrom().equals(requestDto.getStayFrom())) {
                    requestEntity.setStayFrom(requestDto.getStayFrom());
                    status = true;
                }

                if (!requestEntity.getStayTo().equals(requestDto.getStayTo())) {
                    requestEntity.setStayTo(requestDto.getStayTo());
                    status = true;
                }

                if (StringUtils.hasText(requestDto.getNewCategory()) 
                        && !requestEntity.getCategory().equals(requestDto.getNewCategory())) {
                    requestEntity.setCategory(requestDto.getNewCategory());
                    status = true;
                }

                if (StringUtils.hasText(requestEntity.getCategory())
                        && requestEntity.getCategory().equalsIgnoreCase(CategoryEnum.OTHERS.getValue())
                        && StringUtils.hasText(requestEntity.getCategoryOthers())
                        && StringUtils.hasText(requestDto.getNewCategory())
                        && StringUtils.hasText(requestDto.getNewCategoryOthers())
                        && !requestEntity.getCategoryOthers().equalsIgnoreCase(requestDto.getNewCategoryOthers())) {
                    requestEntity.setCategoryOthers(requestDto.getNewCategoryOthers());
                    requestDto.setNewCategory(requestDto.getNewCategory() + " - " + requestDto.getNewCategoryOthers());
                    status = true;
                } else if (StringUtils.hasText(requestDto.getNewCategory())) {
                    if (requestDto.getNewCategory().equalsIgnoreCase(CategoryEnum.OTHERS.getValue())
                            && StringUtils.hasText(requestDto.getNewCategoryOthers())) {
                        requestEntity.setCategoryOthers(requestDto.getNewCategoryOthers());
                        requestDto.setNewCategory(requestDto.getNewCategory() + " - " + requestDto.getNewCategoryOthers());
                    } else {
                        requestEntity.setCategory(requestDto.getNewCategory());
                        requestDto.setNewCategory(requestDto.getNewCategory());
                    }
                    status = true;
                }

                if (status) {
                    updateHistoryTable(candidateId, requestId, requestEntity.getStayFrom(), requestEntity.getStayTo(),
                            requestDto.getStayFrom(), requestDto.getStayTo(), requestEntity.getCategory(), requestDto.getNewCategory());
                    requestEntity.setModifiedBy(SecurityCtxUtil.userName());
                    requestEntity.setModifiedAt(LocalDateTime.now());
                    candidateAppointmentRequestRepository.save(requestEntity);
                }
            }
        }
    }

    public void updateHistoryTable(Long candidateId, Long requestId, LocalDate originalStayFrom, LocalDate originalStayTo,
                                   LocalDate modifiedStayFrom, LocalDate modifiedStayTo, String originalCategory,
                                   String modifiedCategory) {
        StudentAppointmentRequestHistory history = new StudentAppointmentRequestHistory();
        history.setRequestType("Candidate");
        history.setCandidateId(candidateId);
        history.setRequestId(requestId);
        history.setStudentId(Strings.EMPTY);
        history.setOriginalStayFrom(originalStayFrom);
        history.setOriginalStayTo(originalStayTo);
        history.setModifiedStayFrom(modifiedStayFrom);
        history.setModifiedStayTo(modifiedStayTo);
        history.setOriginalCategory(originalCategory);
        history.setModifiedCategory(modifiedCategory);
        studentAppointmentRequestHistoryRepository.save(history);
    }

    public void saveForm(Long requestId, Long candidateId, AccommodationRequestForm accommodationRequestForm, Long stayId) {
        updateAppointmentRequest(requestId, candidateId, accommodationRequestForm.getCandidateAppointmentRequestDto());
        if (stayId > 0) {
            updateStayRequest(stayId, candidateId, accommodationRequestForm.getStayExtensionRequestForm().getStayExtensionRequestDto());
        }
    }

    public boolean intermediateMailForCandidate(long currentApprovalLevel, String currentLevelValidator,
                                                String nextLevelValidator, Long candidateId) {
        CandidateProfileEntity candidateProfileEntity = candidateProfileRepository.findByIdAndActiveFlag(candidateId,
                ModelConstants.STATUS_ACTIVE).orElse(null);

        if (Objects.nonNull(candidateProfileEntity)) {
            String greetingMessage = candidateProfileEntity.getFirstName() + " " + candidateProfileEntity.getLastName();
            MailTemplateEntity mailTemplate = mailTemplateRepository.findByCategoryIgnoreCaseAndApprovalLevelAndAuthorityTypeAndActiveFlag(
                            candidateProfileEntity.getPostSelect(), currentApprovalLevel, "Candidate", ModelConstants.STATUS_ACTIVE)
                    .orElse(null);
            if (Objects.nonNull(mailTemplate)) {
                String messageTemplate = Optional.ofNullable(mailTemplate)
                        .map(MailTemplateEntity::getMailTemplate)
                        .map(t -> t.replace("#%prevValidators%#", currentLevelValidator)
                                .replace("#%currentValidators%#", nextLevelValidator))
                        .orElse(null);
                if (Objects.nonNull(messageTemplate)) {
                    try {
                        mailQueueService.saveMailQueue(mailTemplate.getMailSubject(), greetingMessage, messageTemplate,
                                candidateProfileEntity.getEmail(), commonResponseUtil.getMessage("message.accommodation.request"),
                                null, null, null, null, ModelConstants.REGARDS, ModelConstants.CCW_OFFICE);
                        return true;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return false;
    }

    public boolean finalMailForCcwConfig(CandidateWorkflowDto dto, HttpServletRequest request) throws Exception {
        CandidateProfileEntity candidateProfileEntity = candidateProfileRepository.findByIdAndActiveFlag(dto.getCandidateId(),
                ModelConstants.STATUS_ACTIVE).orElse(null);
        CandidateAppointmentRequestEntity candidateAppointmentRequestEntity =
                candidateAppointmentRequestRepository.findByIdAndCandidateIdAndActiveFlag(dto.getApplicationId(),
                                dto.getCandidateId(), ModelConstants.STATUS_ACTIVE)
                        .orElse(null);

        String workFlowStatus = dto.getId() + Constants.BACKTICK + dto.getModifiedAt();
        String requestStatus = dto.getCandidateId() + Constants.BACKTICK + dto.getApplicationId();
        LocalDate stayFrom=candidateAppointmentRequestEntity.getStayFrom();
        LocalDate stayTo=candidateAppointmentRequestEntity.getStayTo();

        CandidateStayRequestEntity stayReqEntity=new CandidateStayRequestEntity();
        if (Objects.nonNull(dto.getStayId()) && dto.getStayId() > 0) {
            requestStatus = requestStatus + Constants.BACKTICK + dto.getStayId();
            stayReqEntity = candidateStayRequestRepository.findByStayIdAndActiveFlag(dto.getStayId(), ModelConstants.STATUS_ACTIVE).orElse(null);
            stayFrom = stayReqEntity!=null ? stayReqEntity.getStayFrom() : stayFrom;
            stayTo = stayReqEntity!=null ? stayReqEntity.getStayTo() : stayTo;
        }
        String statusStr =
                MCrypt.getInstance().encryptToText(requestStatus + Constants.BACKTICK + workFlowStatus + Constants.BACKTICK + dto.getAuthorityType());
        String address = Stream.of(
                        Objects.requireNonNull(candidateProfileEntity).getAddress(),
                        candidateProfileEntity.getAddress2(),
                        candidateProfileEntity.getCity(),
                        candidateProfileEntity.getState()
                )
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(ModelConstants.COMMA))
                + (candidateProfileEntity.getPin() != null ? Constants.HYPHEN + candidateProfileEntity.getPin() : ModelConstants.EMPTY_STRING);
        String previousValidators = Strings.EMPTY;
        String prioAccom =
                Objects.nonNull(candidateAppointmentRequestEntity.getAccomPriority()) && candidateAppointmentRequestEntity.getAccomPriority() > 0L ?
                        String.valueOf(candidateAppointmentRequestEntity.getAccomPriority()) : Constants.HYPHEN;

        String contactNum =
                Objects.nonNull(candidateProfileEntity.getPhoneNumber()) && !candidateProfileEntity.getPhoneNumber().isEmpty() ?
                        candidateProfileEntity.getPhoneNumber() : candidateProfileEntity.getMobileNumber();


        String messageTemplate = simsConfigDataService.getSimConfigValue(SimsConfigDataService.ACCOMMODATION_REQUEST_MAIL_TEMPLATE);

        if (candidateAppointmentRequestEntity.getCategory().equalsIgnoreCase(CategoryEnum.ICSR.getValue())) {
            String empId =
                    Objects.nonNull(candidateProfileEntity.getEmployeeId()) && !candidateProfileEntity.getEmployeeId().isEmpty()
                            ? candidateProfileEntity.getEmployeeId() : Constants.NA;
            String designation =
                    Objects.nonNull(candidateProfileEntity.getDesignation()) && !candidateProfileEntity.getDesignation().isEmpty()
                            ? candidateProfileEntity.getDesignation() : Constants.NA;

            messageTemplate = messageTemplate.replace("#%dynamicTr%#", commonResponseUtil.getMessage("message.dynamic.tr"))
                    .replace("#%designation%#", designation)
                    .replace("#%empId%#", empId);
        } else {
            messageTemplate = messageTemplate.replace("#%dynamicTr%#", Strings.EMPTY);
        }

        messageTemplate = messageTemplate
                .replace("#%candidateName%#", candidateProfileEntity.getFirstName() + candidateProfileEntity.getLastName())
                .replace("#%address%#", address)
                .replace("#%contactNum%#", contactNum)
                .replace("#%email%#", candidateProfileEntity.getEmail())
                .replace("#%appFromDate%#", String.valueOf(candidateAppointmentRequestEntity.getAppointmentFrom()))
                .replace("#%appToDate%#", String.valueOf(candidateAppointmentRequestEntity.getAppointmentTo()))
                .replace("#%stayFromDate%#", String.valueOf(stayFrom))
                .replace("#%stayToDate%#", String.valueOf(stayTo))
                .replace("#%internship%#", candidateAppointmentRequestEntity.getCategory())
                .replace("#%purpose%#", Objects.nonNull(candidateAppointmentRequestEntity.getPurpose())? candidateAppointmentRequestEntity.getPurpose() : ModelConstants.NOT_APPLICABLE)
                .replace("#%prioAccom%#", prioAccom);

        String subSubject;

        String domainUrl = Utility.getDomainUrl(request);

        if (dto.getAuthorityType().equalsIgnoreCase(ModelConstants.CCW_OFFICE)) {
            subSubject = commonResponseUtil.getMessage("message.mail.particulars.final");

            String viewButton = commonResponseUtil.getMessage("message.view.button")
                    .replace("#%domainUrl%#", domainUrl);
            String plainUrl = commonResponseUtil.getMessage("message.plain.url_2");
            messageTemplate = messageTemplate
                    .replace("#%subSubject%#", subSubject)
                    .replace("#%approvalButton%#", Strings.EMPTY)
                    .replace("#%rejectedButton%#", Strings.EMPTY)
                    .replace("#%viewButton%#", viewButton)
                    .replace("#%urlText%#", plainUrl);

        } else {
            subSubject = commonResponseUtil.getMessage("message.mail.particulars.final") + " by " + candidateProfileEntity.getFirstName() +
                    " " + candidateProfileEntity.getLastName() + ".";

            String url = commonResponseUtil.getMessage("url.domain") + commonResponseUtil.getMessage("url.public.api") +
                    commonResponseUtil.getMessage("url.other.candidate.user");
            url = url.replace("#%domainUrl%#", domainUrl);

            String viewUrl = url + ModelConstants.SLASH + WorkflowStatus.VIEW.getStatus() + ModelConstants.SLASH + statusStr;

            String plainUrl = commonResponseUtil.getMessage("message.plain.url_2")
                    .replace("#%approvalUrl%#", Strings.EMPTY)
                    .replace("#%rejectUrl%#", Strings.EMPTY)
                    .replace("#%viewUrl%#", viewUrl);

            /*String approveButton = commonResponseUtil.getMessage("message.approve.button")
                    .replace("#%url%#", Strings.EMPTY);
            String rejectButton = commonResponseUtil.getMessage("message.reject.button")
                    .replace("#%url%#", Strings.EMPTY);*/
            String viewButton = commonResponseUtil.getMessage("message.view.button")
                    .replace("#%url%#", viewUrl);
            messageTemplate = messageTemplate
                    .replace("#%subSubject%#", subSubject)
                    .replace("#%approvalButton%#", Strings.EMPTY)
                    .replace("#%rejectButton%#", Strings.EMPTY)
                    .replace("#%viewButton%#", viewButton)
                    .replace("#%urlText%#", plainUrl);
        }
        sendMail(messageTemplate, dto.getEmail());
        return true;
    }

    public boolean finalApprovalMailToCandidate(Long requestId, String category, long currentApprovalLevel, String currentLevelValidator,
                                                Long candidateId,Long stayId) {

        CandidateProfileEntity candidateProfileEntity = candidateProfileRepository.findByIdAndActiveFlag(candidateId,
                ModelConstants.STATUS_ACTIVE).orElse(null);

        CandidateAppointmentRequestEntity candidateAppointmentRequestEntity = candidateAppointmentRequestRepository
                .findByIdAndActiveFlag(requestId, ModelConstants.STATUS_ACTIVE).orElse(null);


        if (Objects.nonNull(candidateProfileEntity) && Objects.nonNull(candidateAppointmentRequestEntity)) {
            String greetingMessage = candidateProfileEntity.getFirstName() + " " + candidateProfileEntity.getLastName();
            MailTemplateEntity mailTemplate = mailTemplateRepository.findByCategoryIgnoreCaseAndApprovalLevelAndAuthorityTypeAndActiveFlag(candidateProfileEntity.getPostSelect(), currentApprovalLevel,
                    "Candidate", ModelConstants.STATUS_ACTIVE).orElse(null);
            if (Objects.nonNull(mailTemplate)) {
                String submittedDate = null;
                String stayFrom = null;
                String stayTo=null;
                CandidateStayRequestEntity stayReqEntity=new CandidateStayRequestEntity();
                if (Objects.nonNull(stayId) && stayId > 0) {
                    stayReqEntity = candidateStayRequestRepository.findByStayIdAndActiveFlag(stayId, ModelConstants.STATUS_ACTIVE).orElse(null);
                    LocalDate localDate = stayReqEntity.getCreatedAt()
                            .atZone(ZoneId.of("Asia/Kolkata"))
                            .toLocalDate();
                    submittedDate = utility.dateFormatter(localDate);
                    stayFrom = utility.dateFormatter(stayReqEntity.getStayFrom());
                    stayTo = utility.dateFormatter(stayReqEntity.getStayTo());
                }else{
                    LocalDate localDate = candidateAppointmentRequestEntity.getCreatedAt()
                            .atZone(ZoneId.of("Asia/Kolkata"))
                            .toLocalDate();
                    submittedDate = utility.dateFormatter(localDate);
                    stayFrom = utility.dateFormatter(candidateAppointmentRequestEntity.getStayFrom());
                    stayTo = utility.dateFormatter(candidateAppointmentRequestEntity.getStayTo());
                }


                String messageTemplate = Optional.of(mailTemplate)
                        .map(MailTemplateEntity::getMailTemplate)
                        .orElse(null);

                if (Objects.nonNull(messageTemplate)) {
                    if (messageTemplate.contains("#%submittedDate%#")) {
                        messageTemplate = messageTemplate.replace("#%submittedDate%#", submittedDate);
                    }

                    if (messageTemplate.contains("#%prevValidators%#")) {
                        messageTemplate = messageTemplate.replace("#%prevValidators%#", currentLevelValidator);
                    }

                    if (messageTemplate.contains("#%stayFromDate%#") || messageTemplate.contains("#%stayToDate%#")) {
                        messageTemplate = messageTemplate.replace("#%stayFromDate%#", stayFrom)
                                .replace("#%stayToDate%#", stayTo);
                    }
                }

                if (Objects.nonNull(messageTemplate)) {
                    try {
                        mailQueueService.saveMailQueue(mailTemplate.getMailSubject(), greetingMessage, messageTemplate,
                                candidateProfileEntity.getEmail(), commonResponseUtil.getMessage("message.accommodation.request"),
                                null, null, null, null, ModelConstants.REGARDS, ModelConstants.CCW_OFFICE);
                        return true;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return false;
    }

    public void sendRejectionMailToCandidate(CandidateWorkflowDto candidateWorkflowDto, String rejectReason) {
        CandidateProfileEntity candidateProfileEntity = candidateProfileRepository.findByIdAndActiveFlag(candidateWorkflowDto.getCandidateId(),
                ModelConstants.STATUS_ACTIVE).orElse(null);

        CandidateAppointmentRequestEntity candidateAppointmentRequestEntity = candidateAppointmentRequestRepository
                .findByIdAndActiveFlag(candidateWorkflowDto.getApplicationId(), ModelConstants.STATUS_ACTIVE).orElse(null);

        if (Objects.nonNull(candidateProfileEntity) && Objects.nonNull(candidateAppointmentRequestEntity)) {
            MailTemplateEntity mailTemplate = mailTemplateRepository.findByCategoryIgnoreCaseAndApprovalLevelAndAuthorityTypeAndActiveFlag(WorkflowStatus.REJECT.getStatus(), 1L,
                    "Candidate", ModelConstants.STATUS_ACTIVE).orElse(null);
            if (Objects.nonNull(mailTemplate)) {
                String greetingMessage = candidateProfileEntity.getFirstName() + " " + candidateProfileEntity.getLastName();
                String submittedDate = null;
                CandidateStayRequestEntity stayReqEntity=new CandidateStayRequestEntity();
                if (Objects.nonNull(candidateWorkflowDto.getStayId()) && candidateWorkflowDto.getStayId() > 0) {
                    stayReqEntity = candidateStayRequestRepository.findByStayIdAndActiveFlag(candidateWorkflowDto.getStayId(), ModelConstants.STATUS_ACTIVE).orElse(null);
                    LocalDate localDate = stayReqEntity.getCreatedAt()
                            .atZone(ZoneId.of("Asia/Kolkata"))
                            .toLocalDate();
                    submittedDate = utility.dateFormatter(localDate);
                }else {
                    LocalDate localDate = candidateAppointmentRequestEntity.getCreatedAt()
                            .atZone(ZoneId.of("Asia/Kolkata"))
                            .toLocalDate();
                    submittedDate = utility.dateFormatter(localDate);
                }
                String messageTemplate = Optional.of(mailTemplate)
                        .map(MailTemplateEntity::getMailTemplate)
                        .orElse(null);

                if (Objects.nonNull(messageTemplate)) {
                    if (messageTemplate.contains("#%submittedDate%#")) {
                        messageTemplate = messageTemplate.replace("#%submittedDate%#", submittedDate);
                    }

                    if (messageTemplate.contains("#%authorityType%#")) {
                        messageTemplate = messageTemplate.replace("#%authorityType%#", candidateWorkflowDto.getAuthorityType());
                    }

                    if (messageTemplate.contains("#%rejectReason%#")) {
                        messageTemplate = messageTemplate.replace("#%rejectReason%#", rejectReason);
                    }

                    try {
                        mailQueueService.saveMailQueue(mailTemplate.getMailSubject(), greetingMessage, messageTemplate,
                                candidateProfileEntity.getEmail(), commonResponseUtil.getMessage("message.accommodation.request"),
                                null, null, null, null, ModelConstants.REGARDS, ModelConstants.CCW_OFFICE);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public AccommodationRequestForm getStayExtensionDetails(Long requestId, Long candidateId, Long stayId, String approvalStatus, WorkflowStatus emailStatus) {
        AccommodationRequestForm accommodationRequestForm;
        try {
            accommodationRequestForm = viewAccommodationRequestDetails(candidateId, requestId);
        } catch (Exception e) {
            accommodationRequestForm = AccommodationRequestForm.builder().build();
        }

        StayExtensionRequestForm stayExtensionDetails = stayExtensionRequestService.getStayExtensionDetailsById(candidateId, requestId, stayId, null, false);

        List<String> status = List.of(WorkflowStatus.APPROVED.getStatus(), WorkflowStatus.ALLOTED.getStatus(), WorkflowStatus.CHECKED_IN.getStatus(),
                WorkflowStatus.CHECKED_OUT.getStatus());

        List<PreviousStayExtensionDetails> previousStayExtensionDetails =
                candidateStayDateRepository.getPreviousStayExtensionDetails(requestId, candidateId, stayId, status,Constants.CCW,RoleEnum.DEAN.getValue());

        CurrentStayExtensionDetails currentStayExtensionDetails = candidateStayRequestRepository.getCurrentStayExtensionDetails(stayId,
                ModelConstants.STATUS_ACTIVE, RoleEnum.DEAN.getValue()).orElse(null);
        String approvalNotes = Objects.nonNull(stayExtensionDetails.getWorkflowList()) ?(stayExtensionDetails.getWorkflowList().stream().map(StayExtensionRequestWorkflowDto::getApprovalNotes)
                .filter(notes -> notes != null && !notes.isEmpty())
                .collect(Collectors.joining(", "))) : "";
        String rejectReason = stayExtensionDetails.getStayExtensionRequestDto().getRejectionDescription();
        if (rejectReason != null &&  !rejectReason.isEmpty()) {
            accommodationRequestForm.getCandidateAppointmentRequestDto().setNotes(rejectReason);
        } else {
            accommodationRequestForm.getCandidateAppointmentRequestDto().setNotes(approvalNotes);
        }
        accommodationRequestForm.setPreviousStayExtensionDetailsList(previousStayExtensionDetails);
        accommodationRequestForm.setCurrentStayExtensionDetails(currentStayExtensionDetails);
        accommodationRequestForm.setApprovalStatus(approvalStatus);
        accommodationRequestForm.setEmailStatus(emailStatus.getStatus());
        accommodationRequestForm.setStayExtensionRequestForm(stayExtensionDetails);
        accommodationRequestForm.setCandidateWorkflowList(stayExtensionDetails.getCandidateWorkflowList());
        accommodationRequestForm.setCandidateFilesInformationList(stayExtensionDetails.getCandidateFilesInformationDto());
        return accommodationRequestForm;
    }

    public String processStayRequest(AccommodationRequestForm accommodationRequestForm, String status, Long candidateId,
                                     Long stayId, Long requestId, Long workflowId, String modifiedAt, HttpServletRequest request) {

//        LocalDateTime instant;
//        try {
//            instant = OffsetDateTime.parse(modifiedAt).toLocalDateTime();
//        } catch (DateTimeParseException e) {
//            instant = LocalDateTime.parse(modifiedAt);
//        }

        String rejectReason = Objects.nonNull(accommodationRequestForm.getRejectReason())
                && accommodationRequestForm.getRejectReason().length() > 3 ? accommodationRequestForm.getRejectReason()
                : null;
        String approvalStatus = accommodationRequestForm.getCandidateAppointmentRequestDto().getNotes();
        String occupancy = accommodationRequestForm.getCandidateAppointmentRequestDto().getOccupancy();
        Long accomPriority = accommodationRequestForm.getCandidateAppointmentRequestDto().getAccomPriority();
        StayExtensionRequestDto requestDto;
        requestDto = accommodationRequestForm.getStayExtensionRequestForm().getStayExtensionRequestDto();

        if (!checkStayRequestRejected(workflowId)) {
            if (checkStayApprovalStatus(workflowId)) {
                CandidateStayRequestWorkflowEntity workflowEntity;
                if (WorkflowStatus.APPROVED.getStatus().equalsIgnoreCase(status)) {
                    workflowEntity = isValidStayRequest(workflowId);
                    if (Objects.nonNull(workflowEntity)) {
                        return updateStayWorkflowProcess(stayId, requestId, candidateId, workflowId, status, rejectReason, approvalStatus,
                                occupancy, accomPriority, workflowEntity, requestDto, request);
                    } else {
                        return Constants.INVALID_REQUEST;
                    }
                } else {
                    workflowEntity = checkStayWorkflowExists(workflowId);
                    if (Objects.nonNull(workflowEntity)) {
                        return updateStayWorkflowProcess(stayId, requestId, candidateId, workflowId, status, rejectReason, approvalStatus,
                                occupancy, accomPriority, workflowEntity, requestDto, request);
                    } else {
                        return Constants.INVALID_REQUEST;
                    }
                }
            } else {
                return commonResponseUtil.getMessage("message.invalid.request");
            }
        } else {
            return commonResponseUtil.getMessage("message.request.already.rejected");
        }
    }

    public boolean checkStayRequestRejected(Long workflowId) {
        return stayExtensionRequestRepository.getCandidateStayRequestByStatus(workflowId, WorkflowStatus.REJECTED.getStatus(),Constants.CCW,RoleEnum.DEAN.getValue())
                .isPresent();
    }

    public boolean checkStayApprovalStatus(Long workflowId) {
        return stayExtensionRequestRepository.getStayApprovalStatus(workflowId, ModelConstants.STATUS_ACTIVE)
                .isPresent();
    }

    public CandidateStayRequestWorkflowEntity isValidStayRequest(Long workflowId) {
        return candidateStayRequestWorkflowRepository.getWorkflowByStatus(workflowId, ModelConstants.STATUS_ACTIVE).orElse(null);
    }

    public CandidateStayRequestWorkflowEntity checkStayWorkflowExists(Long workflowId) {
        return candidateStayRequestWorkflowRepository.findByIdAndActiveFlag(workflowId, ModelConstants.STATUS_ACTIVE).orElse(null);
    }

    public String updateStayWorkflowProcess(Long stayId, Long requestId, Long candidateId, Long workflowId, String status, String rejectReason,
                                            String approvalNotes, String occupancy, Long accomPriority, CandidateStayRequestWorkflowEntity workflowEntity,
                                            StayExtensionRequestDto requestDto, HttpServletRequest request) {
        String modifiedBy = Objects.nonNull(SecurityCtxUtil.userName()) ? SecurityCtxUtil.userName() : workflowEntity.getValidatorName();
        List<Object[]> objects = candidateStayRequestWorkflowRepository
                .processStayWorkflow(workflowId, status, rejectReason, approvalNotes, modifiedBy);

        String currentValidatorName = prepareStayCurrentLevelValidator(workflowEntity);
        List<CandidateWorkflowDto> nextLevelValidatorList =
                objects.stream()
                        .map(this::prepareNextLevelValidatorList)
                        .toList();
        String nextLevelValidatorNameList;
        CandidateWorkflowDto workflowDto;

        if (!objects.isEmpty() && WorkflowStatus.APPROVED.getStatus().equalsIgnoreCase(status)) {
            nextLevelValidatorNameList = objects.stream()
                    .map(this::createStayExtValidatorName)
                    .collect(Collectors.joining(ModelConstants.COMMA));

            String category = nextLevelValidatorList.getFirst().getCategory();
            List<String> approvalStatuses = List.of(WorkflowStatus.PENDING.getStatus(), WorkflowStatus.REJECTED.getStatus());
            //mail to next level validators
            stayExtensionRequestWorkflowRepository
                    .findAllByCandidateIdAndAppointmentIdAndStayIdAndApprovalStatusInAndActiveFlag(candidateId, requestId, stayId, approvalStatuses,
                            ModelConstants.STATUS_ACTIVE)
                    .stream()
                    .map(StayExtensionRequestWorkflowMapper.INSTANCE::fromEntity)
                    .toList()
                    .forEach(w -> {
                        try {
                            stayExtensionRequestService.stayExtensionRequestMail(w, candidateId, requestId, stayId, category, request);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

            boolean b = intermediateMailForCandidate(workflowEntity.getApprovalLevel(),
                    currentValidatorName, nextLevelValidatorNameList, candidateId);
            if (b) {
                return Constants.SAVED;
            } else {
                return Constants.FAILURE;
            }
        }

        if (nextLevelValidatorList.isEmpty() && (WorkflowStatus.APPROVED.getStatus().equalsIgnoreCase(status) ||
                WorkflowStatus.OVERRIDE_APPROVED.getStatus().equalsIgnoreCase(status))) {
            workflowDto = prepareNextLevelStayValidatorList(workflowEntity);
            workflowDto.setStayId(stayId);
            updateStayRequest(stayId, candidateId, requestDto);
            try {
                finalMailForCcwConfig(workflowDto, request);
                finalApprovalMailToCandidate(requestId, workflowEntity.getCategory(), workflowEntity.getApprovalLevel(), currentValidatorName, candidateId,stayId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return Constants.SAVED;
        }

        if (WorkflowStatus.REJECTED.getStatus().equalsIgnoreCase(status)) {
            workflowDto = CandidateWorkflowDto.builder()
                    .email(workflowEntity.getValidatorEmail())
                    .applicationId(workflowEntity.getAppointmentId())
                    .candidateId(workflowEntity.getCandidateId())
                    .modifiedAt(workflowEntity.getModifiedAt())
                    .validatorName(workflowEntity.getValidatorName())
                    .authorityType(workflowEntity.getAuthorityType())
                    .id(workflowEntity.getId())
                    .stayId(stayId)
                    .build();
            sendRejectionMailToCandidate(workflowDto, rejectReason);
            return Constants.SAVED;
        }
        return Constants.FAILURE;
    }

    public String prepareStayCurrentLevelValidator(CandidateStayRequestWorkflowEntity entity) {
        String validatorName;
        if (Objects.nonNull(entity.getAuthorityType()) &&
                entity.getAuthorityType().equalsIgnoreCase(CategoryEnum.OTHERS.getValue())) {
            validatorName = entity.getValidatorName();
        } else {
            if (Objects.nonNull(entity.getValidatorName()) && entity.getValidatorName().length() > 3) {
                validatorName = entity.getAuthorityType() + '(' + entity.getValidatorName() + ')';
            } else {
                validatorName = entity.getAuthorityType();
            }
        }
        return validatorName;
    }

    public CandidateWorkflowDto prepareNextLevelStayValidatorList(CandidateStayRequestWorkflowEntity workflowEntity) {
        String validatorEmail = Optional.ofNullable(simsConfigDataService.getSimConfigValue(SimsConfigDataService.APPROVAL_MAIL_TO))
                .orElse(ModelConstants.DEFAULT_MAIL_TO);

        return CandidateWorkflowDto.builder()
                .email(validatorEmail)
                .applicationId(workflowEntity.getAppointmentId())
                .candidateId(workflowEntity.getCandidateId())
                .validatorName(workflowEntity.getValidatorName())
                .authorityType(workflowEntity.getAuthorityType())
                .id(workflowEntity.getId())
                .build();
    }

    public void updateStayRequest(Long stayId, Long candidateId, StayExtensionRequestDto requestDto) {

        CandidateStayRequestEntity requestEntity = candidateStayRequestRepository
                .findByStayIdAndActiveFlag(stayId, ModelConstants.STATUS_ACTIVE).orElse(null);

        boolean status = false;
        if (Objects.nonNull(requestEntity)) {
            if (Objects.nonNull(requestDto.getStayFrom()) && Objects.nonNull(requestDto.getStayTo())) {
                if (!requestEntity.getStayFrom().equals(requestDto.getStayFrom())) {
                    requestEntity.setStayFrom(requestDto.getStayFrom());
                    status = true;
                }

                if (!requestEntity.getStayTo().equals(requestDto.getStayTo())) {
                    requestEntity.setStayTo(requestDto.getStayTo());
                    status = true;
                }

                if (status) {
                    updateStayHistoryTable(candidateId, stayId, requestEntity.getStayFrom(), requestEntity.getStayTo(),
                            requestDto.getStayFrom(), requestDto.getStayTo());
                    requestEntity.setModifiedBy(SecurityCtxUtil.userName());
                    requestEntity.setModifiedAt(LocalDateTime.now());
                    candidateStayRequestRepository.save(requestEntity);
                }
            }
        }
    }

    public void updateStayHistoryTable(Long candidateId, Long stayId, LocalDate originalStayFrom, LocalDate originalStayTo,
                                       LocalDate modifiedStayFrom, LocalDate modifiedStayTo) {
        StudentAppointmentRequestHistory history = new StudentAppointmentRequestHistory();
        history.setRequestType(ModelConstants.STAY_EXTENSION);
        history.setCandidateId(candidateId);
        history.setRequestId(stayId);
        history.setStudentId(Strings.EMPTY);
        history.setOriginalStayFrom(originalStayFrom);
        history.setOriginalStayTo(originalStayTo);
        history.setModifiedStayFrom(modifiedStayFrom);
        history.setModifiedStayTo(modifiedStayTo);
        history.setOriginalCategory(null);
        history.setModifiedCategory(null);
        studentAppointmentRequestHistoryRepository.save(history);
    }

    @Transactional
    public String processAllocation(String type, Long requestId, Long stayId, Long hostelId, String roomNo, String seatName) {
        String approvalStatus;
        if (WorkflowStatus.REALLOCATE.getStatus().equalsIgnoreCase(type)) {
            approvalStatus = WorkflowStatus.ALLOTTED.getStatus();
        } else {
            approvalStatus = WorkflowStatus.APPROVED.getStatus();
        }

        RequestDetails requestDetails = candidateAppointmentRequestRepository
                .getDetailsByRequestIdAndApprovalStatus(requestId, stayId, hostelId, roomNo, approvalStatus, ModelConstants.STATUS_ACTIVE)
                .orElse(null);

        LocalDate stayFrom;
        LocalDate stayTo;
        if (Objects.nonNull(requestDetails)) {
            if (stayId > 0) {
                stayFrom = requestDetails.stayFrom();
                stayTo = requestDetails.stayTo();
            } else {
                stayFrom = requestDetails.accStayFrom();
                stayTo = requestDetails.accStayTo();
            }

            HostelMasterEntity hostelMasterEntity = hostelMasterRepository.findByIdAndActiveFlag(hostelId, ModelConstants.STATUS_ACTIVE).orElse(null);
            String hostelGenderType = hostelMasterEntity.getHostelGenderType();

            if (Objects.nonNull(hostelGenderType)) {
                if (Objects.nonNull(requestDetails.gender()) &&
                        hostelGenderType.equalsIgnoreCase(requestDetails.gender())) {
                    VacationHostelRoomAllotmentInfoEntity vacationHostelRoomAllotmentInfoEntity = vacationHostelRoomAllotmentInfoRepository
                            .getAllocatedDetailsByRequestId(stayFrom, stayTo, requestDetails.email(),
                                    requestDetails.requestId(), ModelConstants.STATUS_ACTIVE)
                            .orElse(null);

                    HostelRoomInfoEntity roomByHostelIdAndRoomNo = hostelRoomInfoRepository.findRoomByHostelIdAndRoomNo(ModelConstants.STATUS_ACTIVE,
                            hostelId, roomNo);
                    if (Objects.isNull(roomByHostelIdAndRoomNo)) {
                        return Constants.FAILURE;
                    }
                    updateCandidateInfo(requestDetails, requestId, roomNo, seatName, hostelMasterEntity.getHostelName(), stayId);
                    if (Objects.nonNull(vacationHostelRoomAllotmentInfoEntity)) {
                        vacationHostelRoomAllotmentInfoEntity.setBuilding(roomByHostelIdAndRoomNo.getBuilding());
                        vacationHostelRoomAllotmentInfoEntity.setRoomid(roomByHostelIdAndRoomNo);
                        vacationHostelRoomAllotmentInfoEntity.setSubRoomid(seatName);
                        vacationHostelRoomAllotmentInfoRepository.save(vacationHostelRoomAllotmentInfoEntity);
                        return Constants.SAVED;
                    } else {
                        return saveVacationInfo(requestDetails, roomByHostelIdAndRoomNo, seatName);
                    }
                } else {
                    return commonResponseUtil.getMessage("message.hostel.gender.failure")
                            .replace("<gender>", hostelGenderType.equalsIgnoreCase("M") ? Constants.MALE_FULL_FORM
                                    : Constants.FEMALE_FULL_FORM);
                }
            } else {
                return Constants.HOSTEL_NOT_FOUND;
            }
        } else {
            return Constants.INVALID_REQUEST;
        }
    }

    private void updateCandidateInfo(RequestDetails requestDetails, Long requestId, String roomNo, String seatName, String hostelName, Long stayId) {
        String statusNote = HostelConstants.YOU_HAVE_BEEN_ALLOTTED.getConstants() + ModelConstants.SPACE + hostelName +
                ModelConstants.SPACE + ModelConstants.HYPHEN + ModelConstants.SPACE + HostelConstants.ROOM_NO.getConstants() + ModelConstants.SPACE +
                roomNo + ModelConstants.SPACE + ModelConstants.HYPHEN + ModelConstants.SPACE + seatName;
        if (stayId > 0) {
            Optional<CandidateStayRequestEntity> candidateStayRequestEntity = candidateStayRequestRepository.findByStayIdAndActiveFlag(stayId, ModelConstants.STATUS_ACTIVE);
            candidateStayRequestEntity.ifPresent(
                    o->{
                        o.setApprovalStatus(WorkflowStatus.ALLOTTED.getStatus());
                        o.setStatusNotes(statusNote);
                        candidateStayRequestRepository.save(o);
                    }
            );
        } else {
            UserManagementOnlineEntity userManagementOnlineEntity = onlineUserDetailsService.getUserByUserName(requestDetails.email());
            Optional<CandidateAppointmentRequestEntity> candidateAppointmentRequestEntity = candidateAppointmentRequestRepository.findByIdAndCandidateIdAndActiveFlag(
                    requestId, Long.valueOf(userManagementOnlineEntity.getApplicationId()), ModelConstants.STATUS_ACTIVE);
            candidateAppointmentRequestEntity.ifPresent(
                    o->{
                        o.setApprovalStatus(WorkflowStatus.ALLOTTED.getStatus());
                        o.setStatusNotes(statusNote);
                        candidateAppointmentRequestRepository.save(o);
                    }
            );
        }
    }

    public String saveVacationInfo(RequestDetails requestDetails, HostelRoomInfoEntity hostelRoomInfoEntity,
                                   String seatName) {
        VacationHostelRoomAllotmentInfoEntity entity = new VacationHostelRoomAllotmentInfoEntity();
        entity.setRequestid(requestDetails.requestId().toString());
        entity.setStayId(Objects.nonNull(requestDetails.stayId()) ? requestDetails.stayId().toString() : "0");
        entity.setStayFromDate(Objects.nonNull(requestDetails.stayFrom()) ? requestDetails.stayFrom() : requestDetails.accStayFrom());
        entity.setStayToDate(Objects.nonNull(requestDetails.stayTo()) ? requestDetails.stayTo() : requestDetails.accStayTo());
        entity.setGender(requestDetails.gender());
        entity.setEmail(requestDetails.email());
        entity.setBuilding(hostelRoomInfoEntity.getBuilding());
        entity.setRoomid(hostelRoomInfoEntity);
        entity.setSubRoomid(seatName);
        vacationHostelRoomAllotmentInfoRepository.save(entity);
        return Constants.SAVED;
    }

    @Transactional
    public String updateCheckInCheckOut(long requestId, long stayId, String status) {
        if (status.equalsIgnoreCase(WorkflowStatus.CHECK_IN.getStatus())) {
            status = WorkflowStatus.CHECKED_IN.getStatus();
        } else {
            status = WorkflowStatus.CHECKED_OUT.getStatus();
        }
        String statusNotes = "You have been " + status + " to the allotted hostel";
        if (stayId > 0) {
            return updateStayRequest(requestId, statusNotes, status);
        } else {
            return updateAccommodationRequest(requestId, statusNotes, status);
        }
    }

    private String updateAccommodationRequest(long requestId, String statusNotes, String status) {
        CandidateAppointmentRequestEntity candidateAppointmentRequestEntity = candidateAppointmentRequestRepository
                .findByIdAndActiveFlag(requestId, ModelConstants.STATUS_ACTIVE).orElse(null);

        if (Objects.nonNull(candidateAppointmentRequestEntity)) {
            candidateAppointmentRequestEntity.setApprovalStatus(status);
            candidateAppointmentRequestEntity.setStatusNotes(statusNotes);
            candidateAppointmentRequestRepository.save(candidateAppointmentRequestEntity);
            return status;
        } else {
            return Constants.FAILURE;
        }
    }

    private String updateStayRequest(long stayId, String statusNotes, String status) {
        CandidateStayRequestEntity candidateStayRequestEntity = candidateStayRequestRepository
                .findByStayIdAndActiveFlag(stayId, ModelConstants.STATUS_ACTIVE).orElse(null);

        if (Objects.nonNull(candidateStayRequestEntity)) {
            candidateStayRequestEntity.setApprovalStatus(status);
            candidateStayRequestEntity.setStatusNotes(statusNotes);
            candidateStayRequestRepository.save(candidateStayRequestEntity);
            return status;
        } else {
            return Constants.FAILURE;
        }
    }

    public String bulkApproveReject(BulkApprovalRejectDto dto, HttpServletRequest request) {
        AccommodationRequestForm accommodationRequestForm = AccommodationRequestForm.builder().build();
        AtomicReference<String> saveStatus = new AtomicReference<>(ModelConstants.EMPTY_STRING);
        CandidateAppointmentRequestDto candidateAppointmentRequestDto = CandidateAppointmentRequestDto
                .builder()
                .stayFrom(dto.getStayFromDate())
                .stayTo(dto.getStayToDate())
                .notes(dto.getApprovalNote()).build();
        StayExtensionRequestDto requestDto = StayExtensionRequestDto.builder().stayFrom(dto.getStayFromDate()).stayTo(dto.getStayToDate()).build();
        accommodationRequestForm.setCandidateAppointmentRequestDto(candidateAppointmentRequestDto);
        accommodationRequestForm.setStayExtensionRequestForm(StayExtensionRequestForm.builder().stayExtensionRequestDto(requestDto).build());
        accommodationRequestForm.setRejectReason(Objects.nonNull(dto.getRejectionReason()) ? dto.getRejectionReason() : null);
        try {
            dto.getEncryptedIds().forEach(s-> {
                try {
                    processBulkApproval(s, accommodationRequestForm, saveStatus, dto, request);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            return Constants.SAVED;
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.ERROR;
        }
    }

    private void processBulkApproval(String s, AccommodationRequestForm accommodationRequestForm, AtomicReference<String> saveStatus, BulkApprovalRejectDto dto, HttpServletRequest request) throws Exception {
        String[] split;
        try {
            split = MCrypt.getInstance().decryptToString(s).split(Constants.BACKTICK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        saveStatus.set(processApprovalBySplit(split, accommodationRequestForm, dto.getApprovalStatus(), request));
    }

    private LocalDateTime normalizeModifiedAt(String modifiedAt) {
        if (modifiedAt == null || modifiedAt.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            Instant instant = Instant.parse(modifiedAt);
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (DateTimeParseException e) {
            return LocalDateTime.parse(modifiedAt);
        }
    }

    public String processApprovalBySplit(String[] split, AccommodationRequestForm form, String status, HttpServletRequest request) throws Exception {
        Long candidateId = Long.parseLong(split[0]);
        Long requestId = Long.parseLong(split[1]);
        Long workflowId = Long.parseLong(split[2]);
        boolean isBulk = split.length >= 5;
        int modifiedAtIndex = isBulk ? 4 : 3;
        String modifiedAt = isBulk ? String.valueOf(normalizeModifiedAt(split[modifiedAtIndex])) : split[modifiedAtIndex] ;
        boolean hasStayId = split.length > modifiedAtIndex + 1;
        if (hasStayId) {
            modifiedAt = String.valueOf(normalizeModifiedAt(split[4]));
            Long stayId = Long.parseLong(split[5]);
            return processStayRequest(form, status, candidateId, stayId, requestId, workflowId, modifiedAt, request);
        }
        return updateStatus(form, status, candidateId, requestId, workflowId, modifiedAt, request);
    }

    public List<CandidateAppointmentRequestDto> getApprovedCandidateDetails(Long candidateId) {
        return candidateAppointmentRequestRepository.findAllByCandidateIdAndApprovalStatusAndActiveFlag(candidateId, WorkflowStatus.APPROVED.getStatus(), ModelConstants.STATUS_ACTIVE)
                .stream()
                .map(CandidateAppointmentRequestMapper.INSTANCE::fromCandidateAppointmentRequestEntity)
                .toList();
    }
}
