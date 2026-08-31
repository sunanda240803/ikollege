package com.iitm.hosteldine.service.dashboard.student;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.iitm.hosteldine.constant.*;
import com.iitm.hosteldine.dto.student.AllStudentsDetailsViewDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.service.AllStudentsDetailsViewService;
import com.iitm.hosteldine.service.reports.VacatingStudentReportRecord;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.dashboard.student.CategoryCode;
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.constant.dean.AssetCategory;
import com.iitm.hosteldine.constant.dean.DeanConstants;
import com.iitm.hosteldine.dto.SimsConfigDataJsonArrayDto;
import com.iitm.hosteldine.dto.dashboard.student.HostelVacatingAllowedStudentDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentHostelRoomVacatingRequestDto;
import com.iitm.hosteldine.dto.dashboard.student.WorkflowMasterDto;
import com.iitm.hosteldine.dto.dean.PropertyDto;
import com.iitm.hosteldine.dto.dean.VacatingStudentInventoryDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.staff.StaffDetailsDto;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.dashboard.student.StudentHostelRoomVacatingRequestMapper;
import com.iitm.hosteldine.mapper.staff.StaffDetailsMapper;
import com.iitm.hosteldine.model.asset.AssetCategoryInfoEntity;
import com.iitm.hosteldine.model.dashboard.student.StudentHostelRoomVacatingRequestEntity;
import com.iitm.hosteldine.model.dashboard.student.StudentRoomAssetDetailsEntity;
import com.iitm.hosteldine.model.dashboard.student.VacatingHostelStudentWorkflowEntity;
import com.iitm.hosteldine.repository.asset.AssetCategoryInfoRepository;
import com.iitm.hosteldine.repository.dashboard.student.StudentHostelRoomVacatingRequestRepository;
import com.iitm.hosteldine.repository.dashboard.student.StudentRoomAssetDetailsRepository;
import com.iitm.hosteldine.repository.dashboard.student.VacatingHostelStudentWorkflowRepository;
import com.iitm.hosteldine.repository.dean.DynamicUserTabRepository;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.repository.staff.StaffDetailsRepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.service.PdfActionService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.helper.VacatingStudentsServiceHelper;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.validator.common.ValidationCommon;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentHostelRoomVacatingRequestService {
	
	private final DynamicUserTabRepository dynamicUserTabRepository;
    private final StudentHostelRoomVacatingRequestRepository studentHostelRoomVacatingRequestRepository;
    private final StudentDetailsInfoRepository studentDetailsInfoRepository;
    private final WorkflowMasterService workflowMasterService;
    private final MessageSource messageSource;
    private final SimsConfigDataService simsConfigDataService;
    private final HostelVacatingAllowedStudentService hostelVacatingAllowedStudentService;
    private final VacatingHostelStudentWorkflowRepository vacatingHostelStudentWorkflowRepository;
    private final StaffDetailsRepository staffDetailsRepository;
    private final MailTemplateRepository mailTemplateRepository;
    private final MailQueueService mailQueueService;
    private final PdfActionService pdfActiveService;
    private final Utility utility;
    private final AssetCategoryInfoRepository assetCategoryInfoRepository;
    private final StudentRoomAssetDetailsRepository studentRoomAssetDetailsRepository;
    private final VacatingStudentsServiceHelper vacatingStudentsServiceHelper;
    private final AllStudentsDetailsViewService allStudentsDetailsViewService;
    private final ExcelUtility excelUtility;
    private final CommonResponseUtil commonResponseUtil;

    public StudentHostelRoomVacatingRequestDto getStudentDetails(){
        StudentDetailsInfoEntity studentEntity = studentDetailsInfoRepository
                .findById(Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase())
                .orElse(null);
        return studentHostelRoomVacatingRequestRepository
                .findFirstByStudentAndActiveFlagAndRejoiningDateIsNullOrderByCreatedAtDesc(studentEntity, ModelConstants.STATUS_ACTIVE)
                .map(StudentHostelRoomVacatingRequestMapper.INSTANCE::toDto)
                .orElse(new StudentHostelRoomVacatingRequestDto());
    }

    @Transactional
    public String saveStudentHostelRoomVacatingForm(StudentHostelRoomVacatingRequestDto studentHostelRoomVacatingRequestDto, String url) {
        String vacatingReason = studentHostelRoomVacatingRequestDto.getVacatingReason();
        HostelVacatingAllowedStudentDto studentBalance = getStudentNegativeBalance();
        double balance = studentBalance.getStudentBalance();
        if (vacatingReason != null) {
            vacatingReason = vacatingReason.trim().replaceAll(ModelConstants.SPLIT_SPECIAL_CHARS, ModelConstants.EMPTY_STRING);
        }
        StudentHostelRoomVacatingRequestEntity studentHostelRoomVacatingRequestEntity = StudentHostelRoomVacatingRequestMapper.INSTANCE.toEntity(studentHostelRoomVacatingRequestDto);
        StudentDetailsInfoEntity studentEntity = studentDetailsInfoRepository
                .findById(Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase())
                .orElse(null);
        final String[] authType = {ModelConstants.EMPTY_STRING};
        studentHostelRoomVacatingRequestEntity.setStudent(studentEntity);
        studentHostelRoomVacatingRequestEntity.setHostelOrWardenApprovalStatus(WorkflowStatus.PENDING.getStatus());
        if(balance < 0) {
        studentHostelRoomVacatingRequestEntity.setDuesPermissionRequired(ModelConstants.Y);
        }else {
        	studentHostelRoomVacatingRequestEntity.setDuesPermissionRequired(ModelConstants.N);
        }
        studentHostelRoomVacatingRequestEntity.onCreate();
        studentHostelRoomVacatingRequestEntity.setVacatingReason(vacatingReason);
        studentHostelRoomVacatingRequestRepository.save(studentHostelRoomVacatingRequestEntity);
        if(balance >= 0 ) {
        hostelRoomVacatingWorkflow(authType,studentHostelRoomVacatingRequestEntity);
        }
        //updateDayScholarStatus();
        List<HostelVacatingAllowedStudentDto> studentApprovalStatusList = getStudentApprovalStatusDetails();
        boolean conditionCheckPassed = studentApprovalStatusList.stream()
                .allMatch(student ->
                        !WorkflowStatus.APPROVED.getStatus()
                                .equalsIgnoreCase(student.getHostelOrWardenApprovalStatus()) &&
                                !ModelConstants.Y.equalsIgnoreCase(student.getDuesPermissionRequired())
                );
        mailToValidator(conditionCheckPassed, studentHostelRoomVacatingRequestEntity, authType[0], url);
        return Constants.SAVED;
    }

    public void hostelRoomVacatingWorkflow(String[] authType,StudentHostelRoomVacatingRequestEntity studentHostelRoomVacatingRequestEntity) {
    	String categoryCode = CategoryCode.VACATING_HOSTEL.getCategoryCode();
        String simsConfigValue = simsConfigDataService.getSimConfigValue(messageSource.getMessage(StudentConstants.VACATING_FORM_APPROVAL_MAIL.getStudentConstant(), null, Locale.getDefault()));
        List<WorkflowMasterDto> workflowMasterDtos = workflowMasterService.getAllWorkflowMastersByCategory(categoryCode);
        //List<HostelVacatingAllowedStudentDto> studentDtoList = getHostelVacatingStudentDetails();
        List<HostelVacatingAllowedStudentDto> studentDtoList = getHostelVacatingStudentDetailsByStudentId(studentHostelRoomVacatingRequestEntity.getStudent().getStudentId().toUpperCase());
        Map<Integer, String[]> authorityData = parseSimsConfigAuthorityData(simsConfigValue);
        workflowMasterDtos.forEach(workflowMasterDto -> {
            VacatingHostelStudentWorkflowEntity vacatingWorkflowEntity = new VacatingHostelStudentWorkflowEntity();
            HostelVacatingAllowedStudentDto studentDto = studentDtoList != null && !studentDtoList.isEmpty() ? studentDtoList.getFirst() : null;
            if (!workflowMasterDto.getAuthorityType().equalsIgnoreCase(StudentConstants.HOSTEL_CHECK_IN.getStudentConstant()) &&
                    !workflowMasterDto.getAuthorityType().equalsIgnoreCase(StudentConstants.WARDEN.getStudentConstant())) {
                if (workflowMasterDto.getApprovalLevel() == 1) {
                    vacatingWorkflowEntity.setApprovalName(authorityData.get(1)[0]);
                    vacatingWorkflowEntity.setApprovalEmail(authorityData.get(1)[1]);
                    vacatingWorkflowEntity.setApprovalLevel(workflowMasterDto.getApprovalLevel());
                } else if (workflowMasterDto.getApprovalLevel() == 2) {
                    vacatingWorkflowEntity.setApprovalName(authorityData.get(2)[0]);
                    vacatingWorkflowEntity.setApprovalEmail(authorityData.get(2)[1]);
                    vacatingWorkflowEntity.setApprovalLevel(workflowMasterDto.getApprovalLevel());
                }
            } else if (workflowMasterDto.getAuthorityType().equalsIgnoreCase(StudentConstants.HOSTEL_CHECK_IN.getStudentConstant())) {
                vacatingWorkflowEntity.setApprovalName(studentDto != null ? studentDto.getHostelOfficeName() : ModelConstants.EMPTY_STRING);
                vacatingWorkflowEntity.setApprovalEmail(studentDto != null ? studentDto.getEmailAddress() : ModelConstants.EMPTY_STRING);
                vacatingWorkflowEntity.setApprovalLevel(workflowMasterDto.getApprovalLevel());
            } else if (workflowMasterDto.getAuthorityType().equalsIgnoreCase(StudentConstants.WARDEN.getStudentConstant())) {
                vacatingWorkflowEntity.setApprovalName(studentDto != null ? studentDto.getWardenName() : ModelConstants.EMPTY_STRING);
                vacatingWorkflowEntity.setApprovalEmail(studentDto != null ? studentDto.getWardenEmail() : ModelConstants.EMPTY_STRING);
                vacatingWorkflowEntity.setApprovalLevel(workflowMasterDto.getApprovalLevel());
            }
            if(vacatingWorkflowEntity.getApprovalName().isEmpty() || vacatingWorkflowEntity.getApprovalEmail().isEmpty()){
                vacatingWorkflowEntity.setApprovalName(workflowMasterDto.getValidatorName());
                vacatingWorkflowEntity.setApprovalEmail(workflowMasterDto.getEmail());
                vacatingWorkflowEntity.setApprovalLevel(workflowMasterDto.getApprovalLevel());
            }
            if (workflowMasterDto.getApprovalLevel() == 1 && workflowMasterDto.getAuthenticationType().equalsIgnoreCase(ModelConstants.AUTH_TYPE_I)) {
                vacatingWorkflowEntity.setStatus(WorkflowStatus.APPROVED.getStatus());
                vacatingWorkflowEntity.setAuthenticationType(ModelConstants.AUTH_TYPE_I);
            } if (workflowMasterDto.getApprovalLevel() == 2 && workflowMasterDto.getAuthenticationType().equalsIgnoreCase(ModelConstants.AUTH_TYPE_I)) {
                vacatingWorkflowEntity.setStatus(WorkflowStatus.AUTO_APPROVED.getStatus());
                vacatingWorkflowEntity.setAuthenticationType(ModelConstants.AUTH_TYPE_I);
            } else if (workflowMasterDto.getApprovalLevel() == 1 && workflowMasterDto.getAuthenticationType().equalsIgnoreCase(ModelConstants.AUTH_TYPE_A)){
                vacatingWorkflowEntity.setStatus(WorkflowStatus.PENDING.getStatus());
                vacatingWorkflowEntity.setAuthenticationType(ModelConstants.AUTH_TYPE_A);
            } else {
                vacatingWorkflowEntity.setStatus(WorkflowStatus.DEFAULT.getStatus());
            }
            authType[0] = vacatingWorkflowEntity.getAuthenticationType();
            vacatingWorkflowEntity.setCategory(categoryCode);
            vacatingWorkflowEntity.setStudentId(studentHostelRoomVacatingRequestEntity.getStudent().getStudentId());
            vacatingWorkflowEntity.setRequestId(studentHostelRoomVacatingRequestEntity.getId());
            vacatingWorkflowEntity.setAuthorityType(workflowMasterDto.getAuthorityType());
            vacatingHostelStudentWorkflowRepository.save(vacatingWorkflowEntity);
        });
    }

	private void mailToValidator(boolean conditionCheckPassed, StudentHostelRoomVacatingRequestEntity studentHostelRoomVacatingRequestEntity, String authType, String url) {
        if (conditionCheckPassed) {
            StudentHostelRoomVacatingRequestDto studentHostelRoomVacatingRequestDto = StudentHostelRoomVacatingRequestMapper.INSTANCE.toDto(studentHostelRoomVacatingRequestEntity);
            studentHostelRoomVacatingRequestDto.setStudentId(studentHostelRoomVacatingRequestEntity.getStudent().getStudentId());
            studentHostelRoomVacatingRequestDto.setStudentName(studentHostelRoomVacatingRequestEntity.getStudent().getFirstName() + ModelConstants.EMPTY_STRING + studentHostelRoomVacatingRequestEntity.getStudent().getLastName());
            studentHostelRoomVacatingRequestDto.setVacatingDateStr(
                    studentHostelRoomVacatingRequestEntity.getVacatingDate()
                            .format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT, Locale.ENGLISH))
            );
            List<VacatingHostelStudentWorkflowEntity> pendingWorkflows = vacatingHostelStudentWorkflowRepository
                    .getStudentWorkflowList( Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase(),
                            WorkflowStatus.PENDING.getStatus(),
                            ModelConstants.STATUS_ACTIVE);
            AllStudentsDetailsViewDto allStudentsDetailsViewDto = allStudentsDetailsViewService.getCompleteStudentDetails(studentHostelRoomVacatingRequestEntity.getStudent().getStudentId());
            studentHostelRoomVacatingRequestDto.setHostelName(allStudentsDetailsViewDto.getHostelName());
            studentHostelRoomVacatingRequestDto.setRoomNo(Objects.nonNull(allStudentsDetailsViewDto.getRoomNumber()) ? Integer.parseInt(allStudentsDetailsViewDto.getRoomNumber()) : 0);
            studentHostelRoomVacatingRequestDto.setHostelName(allStudentsDetailsViewDto.getHostelName());
            studentHostelRoomVacatingRequestDto.setSeat(allStudentsDetailsViewDto.getSeat());
            HostelVacatingAllowedStudentDto studentBalance = getStudentNegativeBalance();
            double balance = studentBalance.getStudentBalance();
            pendingWorkflows.forEach(workflow -> {
                String email = workflow.getApprovalEmail();
                LocalDateTime createdAt = LocalDateTime.now();
                ZonedDateTime zonedDateTime = createdAt.atZone(ZoneId.systemDefault());
                String formattedDate = zonedDateTime.format(
                        DateTimeFormatter.ofPattern(
                                messageSource.getMessage(StudentConstants.DATE_FORMAT.getStudentConstant(), null, Locale.getDefault()), Locale.getDefault())
                );
                String name = workflow.getApprovalName();
                String emailHeading;
                String vacatingReason = studentHostelRoomVacatingRequestEntity.getVacatingReason();
                boolean isExchangeProg = vacatingReason != null && vacatingReason.trim().equalsIgnoreCase(StudentConstants.EXCHANGE_VACATING_REASON.getStudentConstant());
                String vacatingExchangePeriod = isExchangeProg
                        ? studentHostelRoomVacatingRequestEntity.getExchangeProgPeriodFromDate().format(
                                DateTimeFormatter.ofPattern(messageSource.getMessage(StudentConstants.DATE_FORMAT.getStudentConstant(), null, Locale.getDefault()), Locale.getDefault())
                ) + StudentConstants.TO.getStudentConstant() +
                        studentHostelRoomVacatingRequestEntity.getExchangeProgPeriodToDate().format(
                                DateTimeFormatter.ofPattern(messageSource.getMessage(StudentConstants.DATE_FORMAT.getStudentConstant(), null, Locale.getDefault()), Locale.getDefault())
                        )
                        : ModelConstants.NOT_APPLICABLE;
                String hostelName = studentHostelRoomVacatingRequestEntity.getHostelOrWardenName();
                if (StudentConstants.HOSTEL_CHECK_IN.getStudentConstant().equalsIgnoreCase(workflow.getAuthorityType()) ||
                        StudentConstants.HM_OFFICE.getStudentConstant().equalsIgnoreCase(workflow.getAuthorityType())) {
                    emailHeading = messageSource.getMessage(StudentConstants.FORM_SUBMITTED_EMAIL_HEADING.getStudentConstant(), null, Locale.getDefault());
                } else if (balance < 0) {
                    emailHeading = String.format(
                            messageSource.getMessage(StudentConstants.FORM_SUBMITTED_WITH_DUES_EMAIL_HEADING.getStudentConstant(), null, Locale.getDefault()),
                            LocalDate.now().format(DateTimeFormatter.ofPattern(
                                    messageSource.getMessage(StudentConstants.DATE_FORMAT.getStudentConstant(), null, Locale.getDefault())
                            ))
                    );
                } else {
                    emailHeading = String.format(
                            messageSource.getMessage(StudentConstants.FORM_SUBMITTED_APPROVED_EMAIL_HEADING.getStudentConstant(), null, Locale.getDefault()),
                            name == null ? ModelConstants.NOT_APPLICABLE : name);
                }
                try {
                    String urlData = url.replace("/studentVacating", "/public/studentVacating") + "/view?data=" + Utility.encryptAccommodationRequestUrl(studentHostelRoomVacatingRequestDto.getStudent().getStudentId(), studentHostelRoomVacatingRequestDto.getId(), workflow.getAuthorityType());
                    vacatingStudentsServiceHelper.sendMail(studentHostelRoomVacatingRequestDto, name, email, authType, urlData, name);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void updateDayScholarStatus() {
        studentHostelRoomVacatingRequestRepository.updateStudentInfoDetails(Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase(), ModelConstants.STATUS_ACTIVE, ModelConstants.YES);
    }

    private List<HostelVacatingAllowedStudentDto> getHostelVacatingStudentDetails() {
        return hostelVacatingAllowedStudentService.getStudentAllottedRoomStatus(Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase())
                .stream().map(this::getHostelVacatingAllowedStudentDto)
                .collect(Collectors.toList());
    }

    public List<HostelVacatingAllowedStudentDto> getStudentApprovalStatusDetails() {
        return hostelVacatingAllowedStudentService.getDueApprovalStatus()
                .stream().map(this::getStudentApprovalStatusDetailsDto)
                .collect(Collectors.toList());
    }

    public HostelVacatingAllowedStudentDto getStudentNegativeBalance() {
        return hostelVacatingAllowedStudentService.getStudentBalanceDetails();
    }

    public StudentHostelRoomVacatingRequestEntity getStudentInfoDetails(){
        StudentDetailsInfoEntity studentEntity = studentDetailsInfoRepository
                .findById(Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase())
                .orElse(null);
        return studentHostelRoomVacatingRequestRepository
                .getStudentVacatingDetails(studentEntity, ModelConstants.STATUS_ACTIVE)
                .orElse(null);
    }

    private HostelVacatingAllowedStudentDto getStudentApprovalStatusDetailsDto(Object[] row) {
        return HostelVacatingAllowedStudentDto.builder()
                .duesPermissionRequired(Objects.nonNull(row[0]) ? row[0].toString() : ModelConstants.EMPTY_STRING)
                .studentId(Objects.nonNull(row[1]) ? row[1].toString() : ModelConstants.EMPTY_STRING)
                .hostelOrWardenApprovalStatus(Objects.nonNull(row[2]) ? row[2].toString() : ModelConstants.EMPTY_STRING)
                .build();
    }

    private HostelVacatingAllowedStudentDto getHostelVacatingAllowedStudentDto(Object[] row) {
        return HostelVacatingAllowedStudentDto.builder()
                .wardenEmail(Objects.nonNull(row[0]) ? row[0].toString() : ModelConstants.EMPTY_STRING)
                .emailAddress(Objects.nonNull(row[1]) ? row[1].toString() : ModelConstants.EMPTY_STRING)
                .facilityMasterName(Objects.nonNull(row[2]) ? row[2].toString() : ModelConstants.EMPTY_STRING)
                .studentId(Objects.nonNull(row[3]) ? row[3].toString() : ModelConstants.EMPTY_STRING)
                .wardenName(Objects.nonNull(row[4]) ? row[4].toString() : ModelConstants.EMPTY_STRING)
                .hostelOfficeName(Objects.nonNull(row[5]) ? row[5].toString() : ModelConstants.EMPTY_STRING)
                .build();
    }

    private Map<Integer, String[]> parseSimsConfigAuthorityData(String authorityData) {
        return Arrays.stream(authorityData.split(ModelConstants.COMMA))
                .map(entry -> entry.split(ModelConstants.COLAN))
                .filter(parts -> parts.length == 3)
                .collect(Collectors.toMap(
                        parts -> Integer.parseInt(parts[0].trim()),
                        parts -> new String[]{parts[1].trim(), parts[2].trim()},
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    public Resource generatePdf(String studentID) throws Exception {
        String tempFileLocation = pdfActiveService.getTempFileLocation();
        String studentId = Objects.requireNonNull(studentID).toUpperCase();
        String fileName =  commonResponseUtil.getMessage(StudentConstants.HOSTEL_ROOM_VACATING_FORM.getStudentConstant()) + ModelConstants.UNDERSCORE + System.currentTimeMillis() + PdfActionService.PDF_EXTENSION;
        String outputFilePath = tempFileLocation + fileName;
        StudentHostelRoomVacatingRequestDto dto = getStudentDetailsByStudentId(studentID);
        try (PdfWriter writer = new PdfWriter(outputFilePath);
             PdfDocument pdfDocument = new PdfDocument(writer);
             Document document = new Document(pdfDocument)) {
            setPdfDocumentHeader(document, dto);//Pdf Header Section
//            document.add(new Paragraph(PdfActionService.NEXT_LINE));
            setPdfPersonalDetails(document, dto, studentID);//PDF Personal Details Section
            setPdfHostelVacatingDetails(document, dto);//PDF Hostel Details section
            setPdfBankDetails(document, dto);
            document.add(new LineSeparator(new SolidLine()).setMarginTop(5F).setMarginBottom(5F));
            setPdfDeclaration(document);//Pdf Declaration Section
            setPdfDateStudentSignature(document);//Pdf Date and Student Signature Section
//            pdfActiveService.addPageBreaker(document);
            setPdfHostelOfficeUseBox(document);//Pdf Hostel Office Use Section
            setPdfClearanceSection(document);//Pdf Clearance Section
            document.setProperty(Property.LEADING, new Leading(Leading.MULTIPLIED, 0.95f));
            pdfActiveService.addWatermarkImage(pdfDocument);
            document.close();
            return new FileSystemResource(outputFilePath);
        } catch (IOException e) {
            throw new Exception(messageSource.getMessage("message.label.error.generate.pdf", null, Locale.getDefault()), e);
        }
    }

	public StudentHostelRoomVacatingRequestDto getStudentDetailsByStudentId(String studentId){
        StudentDetailsInfoEntity studentEntity = studentDetailsInfoRepository
                .findById(Objects.requireNonNull(studentId).toUpperCase())
                .orElse(null);
        return studentHostelRoomVacatingRequestRepository
                .getStudentVacatingDetails(studentEntity, ModelConstants.STATUS_ACTIVE)
                .map(StudentHostelRoomVacatingRequestMapper.INSTANCE::toDto)
                .orElse(new StudentHostelRoomVacatingRequestDto());
    }

    private void setPdfDocumentHeader(Document document, StudentHostelRoomVacatingRequestDto dto) throws IOException {
        Table headerTable = new Table(new float[]{1.5f, 6f, 1.5f});
        headerTable.setWidth(UnitValue.createPercentValue(100));
        headerTable.setBorder(Border.NO_BORDER);
        headerTable.setMarginBottom(5);

        //Logo
        Cell logoCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.MIDDLE);
        String logoPath = simsConfigDataService.getSimConfigValue(SimsConfigDataService.LOGO);
        InputStream imageStream = getClass().getClassLoader().getResourceAsStream(logoPath);
        if (imageStream != null) {
            Image logo = new Image(ImageDataFactory.create(imageStream.readAllBytes()));
            logo.setHeight(45);
            logo.setAutoScaleWidth(true);
            logoCell.add(logo);
        }
        headerTable.addCell(logoCell);

        //Title
        Cell titleCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE);
        Paragraph officeHeader = new Paragraph(commonResponseUtil.getMessage("message.label.heading")).setBold().setFontSize(11).setMarginBottom(2).setMultipliedLeading(1.0f);
        Paragraph formHeader = new Paragraph(commonResponseUtil.getMessage("message.label.hostel.room.vacating.form.pdf.heading")).setBold().setFontSize(12).setMarginTop(0).setMultipliedLeading(1.0f);
        titleCell.add(officeHeader);
        titleCell.add(formHeader);
        headerTable.addCell(titleCell);

        //Sl. No
        Cell slNoCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT).setVerticalAlignment(VerticalAlignment.TOP);
        slNoCell.add(new Paragraph(commonResponseUtil.getMessage("message.label.sl.no.pdf") + ModelConstants.SPACE + dto.getId()).setFontSize(9).setBold());
        headerTable.addCell(slNoCell);
        document.add(headerTable);
    }

    private void setPdfPersonalDetails(Document document, StudentHostelRoomVacatingRequestDto dto, String studentID) throws IOException {
        String[] messageKeys = {
                "message.label.student.details", "message.label.std.name.pdf", "message.label.student.id.pdf", "message.label.mobile.number",
                "message.label.email.pdf", "message.label.address", "message.label.vacating.hostel.date.pdf", "message.label.reason.for.vacating.pdf",
                "message.label.approval.status.pdf", "message.label.donation.amount.pdf", "message.label.hostel.name.pdf", "message.label.from.date.pdf",
                "message.label.to.date.pdf", "message.label.place.of.visit.pdf", "message.label.bank.account.no.pdf", "message.label.ifsc.code.pdf",
                "message.label.bank.name.pdf", "message.label.branch.name.pdf", "message.label.student.note.pdf", "message.label.bank.note.pdf"
                ,"message.label.for.pdf" ,"message.label.room.no"
        };
        String[] messages = Arrays.stream(messageKeys).map(commonResponseUtil::getMessage).toArray(String[]::new);
      //  document.add(pdfActiveService.addFullWidthTitle(messages[0], TextAlignment.LEFT));
        AllStudentsDetailsViewDto studentsDetailsViewDto = allStudentsDetailsViewService.getCompleteStudentDetails(studentID);
       
     Table outerTable = new Table(1);
     outerTable.setWidth(PdfActionService.VALUE_100_P);
     outerTable.setBorder(new SolidBorder(1));
     outerTable.setMarginBottom(5);

		// Section title INSIDE the box
		outerTable.addCell(new Cell().add(new Paragraph(messages[0]).setBold()).setBorder(Border.NO_BORDER).setPaddingBottom(5));
        Table personalDetailsTable = new Table(new float[]{3, 5, 3, 5});
        personalDetailsTable.setWidth(PdfActionService.VALUE_100_P);
        personalDetailsTable.setMarginBottom(5);
        personalDetailsTable.setFontSize(9);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
        String formattedVacatingDate = Objects.nonNull(dto.getVacatingDate()) ? dto.getVacatingDate().format(dateFormatter) : ModelConstants.NOT_APPLICABLE;
        pdfActiveService.addTableTextValue(messages[1], dto.getAcountName(), personalDetailsTable);
        pdfActiveService.addTableTextValue(messages[2], studentID, personalDetailsTable);
        Cell noteCell1 = new Cell(1, 4)
                .add(new Paragraph(messages[18])
                        .setItalic()
                        .setFontSize(7)
                        .setMarginBottom(5))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT);
        personalDetailsTable.addCell(noteCell1);
        pdfActiveService.addTableTextValue(messages[3], dto.getMobileNo(), personalDetailsTable);
        pdfActiveService.addTableTextValue(messages[4], dto.getEmailId(), personalDetailsTable);
        pdfActiveService.addTableTextValue(messages[10], Objects.nonNull(studentsDetailsViewDto.getHostelName()) ? studentsDetailsViewDto.getHostelName() : ModelConstants.NOT_APPLICABLE, personalDetailsTable);
        pdfActiveService.addTableTextValue(messages[21], Objects.nonNull(studentsDetailsViewDto.getRoomNumber()) ? studentsDetailsViewDto.getRoomNumber() : ModelConstants.NOT_APPLICABLE, personalDetailsTable);
        pdfActiveService.addTableTextValue(messages[5], dto.getStudentAddress(), personalDetailsTable, new int[]{1, 3});
        reAlignPdfContent(personalDetailsTable);
        // Add INNER table into OUTER box
        outerTable.addCell(
                new Cell().add(personalDetailsTable).setBorder(Border.NO_BORDER)
        );
        document.add(outerTable);
    }

    private void setPdfDeclaration(Document document) {
        document.add(pdfActiveService.addFullWidthTitle(
                messageSource.getMessage("message.label.declaration", null, Locale.getDefault()),
                TextAlignment.LEFT));
        Table declarationSection = new Table(new float[]{0.3f, 9.7f})
                .setWidth(UnitValue.createPercentValue(100))
                .setMargin(0)
                .setPadding(0);
        String[] texts = {
                messageSource.getMessage("message.label.handed.over", null, Locale.getDefault()),
                messageSource.getMessage("message.label.belongings", null, Locale.getDefault()),
                messageSource.getMessage("message.label.dues", null, Locale.getDefault()),
                messageSource.getMessage("message.label.bicycle", null, Locale.getDefault())
        };
        IntStream.range(0, texts.length).forEach(i ->
                declarationSection.addCell(new Cell()
                                .add(new Paragraph((i + 1) + ModelConstants.DOT)
                                .setFontSize(8))
                                .setTextAlignment(TextAlignment.LEFT)
                                .setPadding(0)
                                .setBorder(Border.NO_BORDER))
                        .addCell(new Cell()
                                .add(new Paragraph(texts[i])
                                .setFontSize(8))
                                .setTextAlignment(TextAlignment.LEFT)
                                .setPadding(0)
                                .setBorder(Border.NO_BORDER))
        );
        document.add(declarationSection);
    }

    private void setPdfDateStudentSignature(Document document) {
        Table signatureSection = new Table(new float[]{1, 2, 1})
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(5)
                .setFontSize(7)
                .addCell(new Cell()
                        .add(new Paragraph(messageSource.getMessage("message.label.date.pdf", null, Locale.getDefault())))
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.LEFT))
                .addCell(new Cell().setBorder(Border.NO_BORDER))
                .addCell(new Cell()
                        .add(new Paragraph(messageSource.getMessage("message.label.student.signature.pdf", null, Locale.getDefault())))
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.RIGHT));
        document.add(signatureSection);
    }


    private void setPdfHostelOfficeUseBox(Document document) {
        Table hostelOfficeUseSection = new Table(1)
                .setWidth(UnitValue.createPercentValue(30))
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBorder(new SolidBorder(1))
                .setMarginTop(5)
                .setPadding(3)
                .setPaddingTop(4)
//                .setKeepTogether(true)
                .addCell(new Cell()
                        .add(new Paragraph(messageSource.getMessage("message.label.hostel.office.use.pdf", null, Locale.getDefault()))
                                .setBold().setFontSize(7.5f)
                                .setMultipliedLeading(1.0f))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(Border.NO_BORDER))
                .addCell(new Cell()
                        .add(new Paragraph(messageSource.getMessage("message.label.office.text.pdf", null, Locale.getDefault()))
                                .setFontSize(7.5f)
                                .setMultipliedLeading(1.0f))
                        .setTextAlignment(TextAlignment.LEFT)
                        .setBorder(Border.NO_BORDER)
                        .setPadding(10))
                .addCell(new Cell()
                        .add(new Table(new float[]{3, 3})
                                .setWidth(UnitValue.createPercentValue(100))
                                .setBorder(Border.NO_BORDER)
                                .addCell(new Cell()
                                        .add(new Paragraph(messageSource.getMessage("message.label.hostel.office.seal.signature.date.pdf", null, Locale.getDefault()))
                                                .setFontSize(7.5f)
                                                .setMultipliedLeading(1.0f))
                                        .setTextAlignment(TextAlignment.CENTER)
                                        .setBorder(Border.NO_BORDER)
                                        .setPaddingTop(25)
                                        .setMarginLeft(10)))
                        .setBorder(Border.NO_BORDER));
        document.add(hostelOfficeUseSection);
    }


    private void setPdfClearanceSection(Document document) {
        Table clearanceSection = new Table(new float[]{3, 2})
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(3)
                .setFontSize(7)
                .addCell(new Cell()
                        .add(new Paragraph(messageSource.getMessage("message.label.clearance.certificate.pdf", null, Locale.getDefault())))
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.LEFT))
                .addCell(new Cell()
                        .add(new Paragraph(messageSource.getMessage("message.label.student.signature.pdf", null, Locale.getDefault())))
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.RIGHT));
        document.add(clearanceSection);
    }
    
    public List<StudentHostelRoomVacatingRequestDto> getStudentVacatingHostelList(PaginationForm form, String url, boolean isExport) {
		Object[] result = fetchStudentVacatingHostelList(form, isExport);
        List<Object[]> subMenuList = dynamicUserTabRepository
                .getDeanSubMenuListByRoleAndUserIdAndUrl(SecurityCtxUtil.userRole(), SecurityCtxUtil.userName(), url);
		return setStudentVacatingHostelValues(url, result, subMenuList, form);
	}

	private Object[] fetchStudentVacatingHostelList(PaginationForm form, boolean isExport) {
		String approvalStatus = ValidationCommon.toString(form.getAdditionalParam().get(DeanConstants.APPROVAL_STATUS.getConstants()));
		String vacatingReason = ValidationCommon.toString(form.getAdditionalParam().get(DeanConstants.VACATING_REASON.getConstants()));
		LocalDate submittedFromDate = ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get(DeanConstants.SUBMITTED_FROM_DATE.getConstants()));
		LocalDate submittedToDate = ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get(DeanConstants.SUBMITTED_TO_DATE.getConstants()));
		LocalDate vacatingFromDate = ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get(DeanConstants.VACATING_FROM_DATE.getConstants()));
		LocalDate vacatingToDate = ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get(DeanConstants.VACATING_TO_DATE.getConstants()));		
		String studentName = ValidationCommon.toString(form.getAdditionalParam().get(DeanConstants.STUDENT_NAME.getConstants()));
		String studentId = ValidationCommon.toString(form.getAdditionalParam().get(DeanConstants.STUDENT_ID.getConstants()));
		int hostelId = getHostelId(form);
		String userRole = Objects.requireNonNull(SecurityCtxUtil.userRole());
		String userName = Objects.requireNonNull(SecurityCtxUtil.userName());
		
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize());
		Object[] result;

		int approvalLevel;
        if (DeanConstants.HM_OFFICE.getConstants().equals(userRole) || DeanConstants.HOSTEL_CHECK_IN.getConstants().equals(userRole)) {
            approvalLevel = 1;
        } else {
            approvalLevel = 2;
        } 
        
        StaffDetailsDto staffDetailsDto = staffDetailsRepository
                .getFacultyEmailAndName(DeanConstants.CCW_IITM.getConstants())
                .orElse(new StaffDetailsDto(null, null, null, null));
        
        if(isExport) {
        	pageable = Pageable.unpaged();
        }
        
        result = studentHostelRoomVacatingRequestRepository.getStudentVacatingHostelList(submittedFromDate, submittedToDate, vacatingReason, vacatingFromDate, vacatingToDate, 
        		hostelId, studentName, studentId, approvalStatus, userRole, approvalLevel,  staffDetailsDto.getEmailAddress() , userName);
        
		return result;
	}
	
	private List<StudentHostelRoomVacatingRequestDto> setStudentVacatingHostelValues(String url, Object[] result, List<Object[]> subMenuList, PaginationForm form) {
		return Arrays.stream(result).map(data -> {
            Object[] record = (Object[]) data;
			StudentHostelRoomVacatingRequestDto dto = new StudentHostelRoomVacatingRequestDto();
    		List<PropertyDto> actionList = new ArrayList<PropertyDto>();
    		createActionList(url, subMenuList, record, actionList);
    		dto.setStatus(record[0] != null ? record[0].toString() : null);
    		dto.setApprovalStatus(record[1] != null ? record[1].toString() : null);
    		dto.setId(record[2] != null ? ((Number) record[2]).longValue() : null);
            dto.setAuthorityType(record[3] != null ? record[3].toString() : null);
            dto.setStudentId(record[4] != null ? record[4].toString() : null);
            dto.setApprovalEmail(Objects.requireNonNull(SecurityCtxUtil.userRole()));
            dto.setEmailId(record[6] != null ? record[6].toString() : null);
            dto.setRequestId(record[7] != null ? ((Number) record[7]).longValue() : null);
            dto.setStudentName(record[8] != null ? record[8].toString() : null);
            dto.setHostelName(record[9] != null ? record[9].toString() : null);
            dto.setRoomNo(record[10] != null ? ((Number) record[10]).intValue() : null);
            dto.setVacatingDate(record[11] != null ? LocalDate.parse(record[11].toString()) : null);
            dto.setVacatingDateStr(ValidationCommon.formatDate(record[11], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
            dto.setVacatingReason(record[12] != null ? record[12].toString() : null);
            dto.setPenaltyAmount(record[13] != null ? Long.parseLong(record[13].toString()) : null);
            dto.setDonationAmount(record[14] != null ? Long.parseLong(record[14].toString()) : null);
            dto.setActionList(actionList);
            return dto;
		}).toList();
	}
	
	private void createActionList(String url, List<Object[]> subMenuList, Object[] objects, List<PropertyDto> actionList) {
		for (Object[] action : subMenuList) {
			if (action[2].toString().equals(Constants.COL_LINK) || action[2].toString().equals(Constants.COL_ACTION)) {
				PropertyDto actionDto = createPropertyDto(action);
				String authorityType = Objects.requireNonNull(SecurityCtxUtil.userRole());
				String studentIdVal = objects[4] != null ? objects[4].toString() : null;
				Long requestId = objects[7] != null ? ((Number) objects[7]).longValue() : null;
//                List<VacatingHostelStudentWorkflowEntity> pendingWorkflows = vacatingHostelStudentWorkflowRepository
//                        .findByRequestIdAndStudentIdAndAuthorityTypeAndActiveFlag( requestId, Objects.requireNonNull(studentIdVal),
//                                authorityType,
//                                ModelConstants.STATUS_ACTIVE);
				try {
					if (Constants.SOFTWARE_ADMIN.equals(SecurityCtxUtil.userRole())) {
						if (WorkflowStatus.VIEW.getStatus().equals(action[8].toString())) {
							actionDto.setDisplayName("View NOC");
							actionDto.setUrl(messageSource.getMessage("url.noc.vacation.payment.report", null,
									Locale.getDefault()) + ModelConstants.SLASH + studentIdVal);
							actionList.add(actionDto);
						} else if (WorkflowStatus.DELETE.getStatus().equals(action[8].toString())) {
                            actionDto.setUrl(url + "/delete?data=" + Utility.encryptAccommodationRequestUrl(studentIdVal, requestId, authorityType));
                            actionList.add(actionDto);
                        } else if (WorkflowStatus.PDF.getStatus().equals(action[8].toString()) && WorkflowStatus.APPROVED.getStatus().equals(objects[1])) {
                            actionDto.setUrl(url + "/pdfDownload?data=" + Utility.encryptAccommodationRequestUrl(studentIdVal, requestId, authorityType));
                            actionList.add(actionDto);
                        }
					} else {
						if (WorkflowStatus.VIEW.getStatus().equals(action[8].toString())) {
							actionDto.setUrl(url + "/view?data=" + Utility.encryptAccommodationRequestUrl(studentIdVal, requestId, authorityType));
							actionList.add(actionDto);
						} else if (WorkflowStatus.DELETE.getStatus().equals(action[8].toString())) {
	                        actionDto.setUrl(url + "/delete?data=" + Utility.encryptAccommodationRequestUrl(studentIdVal, requestId, authorityType));
	                        actionList.add(actionDto);
	                    } else if (WorkflowStatus.PDF.getStatus().equals(action[8].toString()) && WorkflowStatus.APPROVED.getStatus().equals(objects[1])) {
	                        actionDto.setUrl(url + "/pdfDownload?data=" + Utility.encryptAccommodationRequestUrl(studentIdVal, requestId, authorityType));
	                        actionList.add(actionDto);
	                    }
                    }
                } catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private PropertyDto createPropertyDto(Object[] action) {
		PropertyDto actionDto = new PropertyDto();
		actionDto.setActionIcon(action[5] != null ? action[5].toString() : null);
		actionDto.setActionStyle(action[6] != null ? action[6].toString() : null);
		actionDto.setDisplayName(action[8] != null ? action[8].toString() : null);
		return actionDto;
	}
	
	public StudentHostelRoomVacatingRequestDto getVacatingStudentDetails(String studentId, Long requestId, String authorityType) throws Exception {
		StudentHostelRoomVacatingRequestDto dto = new StudentHostelRoomVacatingRequestDto();
		vacatingStudentsServiceHelper.fetchStudentVacatingDetails(studentId, requestId, authorityType, dto);
		fetchVacatingStudentWorkflowDtoList(dto, requestId, studentId);
		fetchVacatingStudentWorkFlowDetailsByAuthority(studentId, requestId, authorityType, dto);
		fetchInventoryDetails(studentId, requestId, dto);		
		return dto;
	}
	
	private void fetchVacatingStudentWorkflowDtoList(StudentHostelRoomVacatingRequestDto dto, Long requestId, String studentId) {
		List<VacatingHostelStudentWorkflowEntity> vacatingHostelStudentWorkflowEntityList = vacatingHostelStudentWorkflowRepository.findByRequestIdAndStudentIdAndActiveFlag(requestId, studentId, Constants.ACTIVE_FLAG);
		StringBuilder approvalStatusDetails = new StringBuilder();
		dto.setTotalApprovalCount(vacatingHostelStudentWorkflowEntityList != null ? vacatingHostelStudentWorkflowEntityList.size() : 0);
		for (VacatingHostelStudentWorkflowEntity workflowEntity : vacatingHostelStudentWorkflowEntityList) {
			String authType = workflowEntity.getAuthenticationType();
			String approvalStatus = workflowEntity.getStatus();
			// int approvalLevel = (int) messRebateWorkFlow[1];
			approvalStatusDetails.append(workflowEntity.getApprovalName() + "   " + workflowEntity.getApprovalEmail() + " - ");
			if (WorkflowStatus.APPROVED.getStatus().equals(approvalStatus)) {
				String modifiedDate = workflowEntity.getModifiedAt() != null ? (utility.convertToLocalDate(workflowEntity.getModifiedAt()).format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT))) : "";
				approvalStatusDetails.append(approvalStatus + " " + Constants.ON + " " + modifiedDate);
			} else if (ModelConstants.AUTHENTICATION_TYPE_INFORMATION.equalsIgnoreCase(authType) && WorkflowStatus.PENDING.getStatus().equals(approvalStatus)) {
				approvalStatusDetails.append(Constants.MESS_REBATE_INFORMATION);
			} else if (WorkflowStatus.DEFAULT.getStatus().equals(approvalStatus) || WorkflowStatus.PENDING.getStatus().equals(approvalStatus)) {
				approvalStatusDetails.append(WorkflowStatus.PENDING.getStatus());
			}
			approvalStatusDetails.append("<br>");
		}
		dto.setApprovalStatusDetails(approvalStatusDetails.toString());
	}
	
	private void fetchVacatingStudentWorkFlowDetailsByAuthority(String studentId, Long id, String authorityType, StudentHostelRoomVacatingRequestDto dto) {
		List<VacatingHostelStudentWorkflowEntity> vacatingHostelStudentWorkflowEntityList = vacatingHostelStudentWorkflowRepository.findByRequestIdAndStudentIdAndAuthorityTypeAndActiveFlag(id, studentId, authorityType, Constants.ACTIVE_FLAG);
		for (VacatingHostelStudentWorkflowEntity workflowEntity : vacatingHostelStudentWorkflowEntityList) {
			dto.setApprovalLevel(workflowEntity.getApprovalLevel());
			dto.setAuthorityType(workflowEntity.getAuthorityType());
			dto.setStatus(workflowEntity.getStatus());
		}
	}
	
	private void fetchInventoryDetails(String studentId, Long requestId, StudentHostelRoomVacatingRequestDto dto) {
		Object[] inventoryDetails = studentHostelRoomVacatingRequestRepository.getVacatingStudentInventoryDetails(studentId, requestId, dto.getRoomId());
		if(inventoryDetails != null && inventoryDetails.length > 0) {
			List<VacatingStudentInventoryDto> inventoryDtoList = new ArrayList<>();
			for (Object record : inventoryDetails) {
				Object[] recordArr = (Object[]) record;
				VacatingStudentInventoryDto inventoryDto = new VacatingStudentInventoryDto();
				inventoryDto.setAssetName(ValidationCommon.toString(recordArr[0]));
				inventoryDto.setAssetCategory(ValidationCommon.toString(recordArr[1]));
				inventoryDto.setAssetId(utility.parseLong(recordArr[2]));
				inventoryDto.setAssetCode(ValidationCommon.toString(recordArr[3]));
				inventoryDto.setAssetCondition(ValidationCommon.toString(recordArr[6]));
				inventoryDto.setPenaltyAmount(utility.parseLong(recordArr[7]));
				inventoryDto.setPenaltyReason(ValidationCommon.toString(recordArr[8]));				
				inventoryDtoList.add(inventoryDto);
			}
			dto.setInventoryDtoList(inventoryDtoList);
		}		
	}
	
	public double getCost(String assetCategory, String assetCondition) {
		double cost_amount = 0.0;
		List<AssetCategoryInfoEntity> assetCategoryList = assetCategoryInfoRepository.findByAssetCategoryIgnoreCaseAndActiveFlag(assetCategory, Constants.ACTIVE_FLAG);
		for(AssetCategoryInfoEntity assetCategoryInfoEntity : assetCategoryList) {
			if (AssetCategory.REPLACEMENT.getValue().equals(assetCondition)) {
				cost_amount = assetCategoryInfoEntity.getReplacementCost() != null ? assetCategoryInfoEntity.getReplacementCost() : 0.0 ;
			} else if (AssetCategory.MINOR_REPAIR.getValue().equals(assetCondition) || AssetCategory.PAINTING_PARTIALLY.getValue().equals(assetCondition)) {
				cost_amount = assetCategoryInfoEntity.getMinorRepairCost() != null ? assetCategoryInfoEntity.getMinorRepairCost() : 0.0 ;
			} else if (AssetCategory.MAJOR_REPAIR.getValue().equals(assetCondition) || AssetCategory.PAINTING_FULLY.getValue().equals(assetCondition)) {// minor
				cost_amount = assetCategoryInfoEntity.getMajorRepairCost() != null ? assetCategoryInfoEntity.getMajorRepairCost() : 0.0;
			}
			break;
		}
		return cost_amount;
	}
	
	public boolean isFacultyAvailable(String facultyId) {
		StaffDetailsDto staffDetailsDto = staffDetailsRepository.findByFacultyIdAndActiveFlag(facultyId, ModelConstants.STATUS_ACTIVE).map(StaffDetailsMapper.INSTANCE::fromStaffDetailsEntity)
				.orElse(null);
		return staffDetailsDto != null;
	}
	
    public String[] getDropdownList(ArrayList<SimsConfigDataJsonArrayDto> list) throws JsonMappingException, JsonProcessingException {
        List<String> values = new ArrayList<>();
        
		for (SimsConfigDataJsonArrayDto json : list) {
            values.add(json.getValue());
        }
		
        String[] resultArray = values.toArray(new String[0]);

		return resultArray;
	}

    public List<Map<String, Object>> getHostelName(List<HostelMasterDto> hostelList) {
    	List<Map<String, Object>> dropdownList = new ArrayList<>();        
        for (HostelMasterDto hostelMasterDto : hostelList) {
            if (hostelMasterDto != null) {
                Map<String, Object> dropdownItem = new HashMap<>();
                dropdownItem.put("id", hostelMasterDto.getId());
                dropdownItem.put("hostelName", hostelMasterDto.getHostelName());
                dropdownList.add(dropdownItem);
            }
        }        
        return dropdownList;
    }
    
    public Workbook getVacatingStudentReport(List<StudentHostelRoomVacatingRequestDto> studentHostelRoomVacatingRequestDtoList) throws Exception {
		XSSFWorkbook workbook = null;
		int colCount = 0;
		ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

		try {
			workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(messageSource.getMessage("message.vacating.student.list", null, Locale.getDefault()));

			// Create styles using ExcelUtility
			XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
			XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
			
			// Add new row with title "Office of the Hostel Management - IITMADRAS CAMPUS"
			XSSFRow rowheadFirst = sheet.createRow(0);
			excelUtility.createCell(rowheadFirst, 0, messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null, Locale.getDefault()), headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));
			
			// Create second header row
			XSSFRow rowheadSecond = sheet.createRow(1);
			excelUtility.createCell(rowheadSecond, 0, messageSource.getMessage("message.vacating.student.list", null, Locale.getDefault()), headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));

			// Create third header row for report date
			XSSFRow rowheadThird = sheet.createRow(2);
			SimpleDateFormat sdf = new SimpleDateFormat(messageSource.getMessage("session.date.format", null, Locale.getDefault()));
			excelUtility.createCell(rowheadThird, 0, messageSource.getMessage("message.label.report.date.colon", null, Locale.getDefault()) + sdf.format(new Date()), headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 9));

			// Create column headers
			XSSFRow rowhead = sheet.createRow(3);
			String[] headers = { messageSource.getMessage("message.label.studentID", null, Locale.getDefault()), messageSource.getMessage("message.label.studentName", null, Locale.getDefault()),
					messageSource.getMessage("message.label.hostelName", null, Locale.getDefault()), messageSource.getMessage("message.label.roomNo", null, Locale.getDefault()),
					messageSource.getMessage("message.label.vacating.date", null, Locale.getDefault()), messageSource.getMessage("message.label.reason.for.vacating", null, Locale.getDefault()),
					messageSource.getMessage("message.label.warden.approval.status", null, Locale.getDefault()), messageSource.getMessage("message.label.penalty.amount", null, Locale.getDefault()),
					messageSource.getMessage("message.label.donation.amount", null, Locale.getDefault()) };

			// Add headers to the sheet
			for (String header : headers) {
				excelUtility.createCell(rowhead, colCount, header, headerStyle);
				sheet.setColumnWidth(colCount, 4000); // Set column width
				colCount++;
			}

			// Populate data rows
			int rowcount = 3;
			if (CollectionUtils.isNotEmpty(studentHostelRoomVacatingRequestDtoList)) {
				for (StudentHostelRoomVacatingRequestDto studentHostelRoomVacatingRequestDto : studentHostelRoomVacatingRequestDtoList) {
					rowcount++;
					XSSFRow row = sheet.createRow(rowcount);
					excelUtility.createCell(row, 0, studentHostelRoomVacatingRequestDto.getStudentId(), dataStyle);
					excelUtility.createCell(row, 1, studentHostelRoomVacatingRequestDto.getStudentName(), dataStyle);
					excelUtility.createCell(row, 2, studentHostelRoomVacatingRequestDto.getHostelName(), dataStyle);
					excelUtility.createCell(row, 3, studentHostelRoomVacatingRequestDto.getRoomNo(), dataStyle);
					excelUtility.createCell(row, 4, studentHostelRoomVacatingRequestDto.getVacatingDateStr(), dataStyle);
					excelUtility.createCell(row, 5, studentHostelRoomVacatingRequestDto.getVacatingReason(), dataStyle);
					excelUtility.createCell(row, 6, studentHostelRoomVacatingRequestDto.getApprovalStatus(), dataStyle);
					excelUtility.createCell(row, 7, studentHostelRoomVacatingRequestDto.getPenaltyAmount(), dataStyle);
					excelUtility.createCell(row, 8, studentHostelRoomVacatingRequestDto.getDonationAmount(), dataStyle);
				}
			}

		} catch (Exception exception) {
			exception.printStackTrace();
			throw new Exception("Error generating Mess Rebate List report", exception);
		}
		return workbook;
	}
    
    @Transactional
	public boolean updateStudentVacatingApprovalStatus(StudentHostelRoomVacatingRequestDto dto, String status, String url) {
		try {
			updateVacatingStudentStatus(dto, status);
			updateVacatingStudentWorkflowStatus(dto, status);
			updateVacatingStudentAssetInventory(dto);
			if (dto.getApprovalLevel() != null && dto.getApprovalLevel() != dto.getTotalApprovalCount()) {
				vacatingStudentsServiceHelper.sendMailToValidators(dto.getStudentId(), dto.getId(), dto.getApproverName(), url);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	

	private void updateVacatingStudentStatus(StudentHostelRoomVacatingRequestDto dto, String status) throws Exception {
		try {
			StudentHostelRoomVacatingRequestEntity vacatingRequestEntity = studentHostelRoomVacatingRequestRepository.findByIdAndActiveFlag(dto.getId(), Constants.ACTIVE_FLAG);
			// changed warden authentication type to i
			/*if (!Constants.USER_ROLE_WARDEN.equalsIgnoreCase(dto.getAuthorityType()) && WorkflowStatus.APPROVED.getStatus().equals(status)) {
				vacatingRequestEntity.setHostelOrWardenApprovalStatus(WorkflowStatus.PENDING.getStatus());
			} else {
				vacatingRequestEntity.setHostelOrWardenApprovalStatus(status);
			}*/ 
			vacatingRequestEntity.setHostelOrWardenApprovalStatus(status);
			vacatingRequestEntity.setPenaltyReason(dto.getPenaltyReason());
			vacatingRequestEntity.setPenalityAmount(dto.getTotalAmount());
			vacatingRequestEntity.setCheckedBy(dto.getCheckedBy());
			vacatingRequestEntity.setRecommendedBy(dto.getRecommendedBy());
			vacatingRequestEntity.setEmployeeId(dto.getEmployeeId());
			vacatingRequestEntity.setRoomPaintingType(dto.getRoomPaintingType());
			if(WorkflowStatus.APPROVED.getStatus().equalsIgnoreCase(status)) {
				vacatingRequestEntity.setApprovalDate(LocalDate.now());
			}
			vacatingRequestEntity.onUpdate();
			studentHostelRoomVacatingRequestRepository.save(vacatingRequestEntity);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void updateVacatingStudentWorkflowStatus(StudentHostelRoomVacatingRequestDto dto, String status) throws Exception {
		try {
			List<VacatingHostelStudentWorkflowEntity> vacatingHostelStudentWorkflowEntityList = vacatingHostelStudentWorkflowRepository
					.findByRequestIdAndStudentIdAndActiveFlag(dto.getId(), dto.getStudentId(), Constants.ACTIVE_FLAG);

			// Sort the list based on the approval level
			vacatingHostelStudentWorkflowEntityList.sort(Comparator.comparingInt(VacatingHostelStudentWorkflowEntity::getApprovalLevel));

			for (VacatingHostelStudentWorkflowEntity vacatingHostelStudentWorkflowEntity : vacatingHostelStudentWorkflowEntityList) {
				if (dto.getApprovalLevel() == vacatingHostelStudentWorkflowEntity.getApprovalLevel()) {
					if (WorkflowStatus.APPROVED.getStatus().equals(status)) {
						dto.setApproverName(vacatingHostelStudentWorkflowEntity.getApprovalName());
						vacatingHostelStudentWorkflowEntity.setStatus(WorkflowStatus.APPROVED.getStatus());
						vacatingHostelStudentWorkflowEntity.onUpdate();
						vacatingHostelStudentWorkflowRepository.save(vacatingHostelStudentWorkflowEntity);
						
						Optional<Integer> nextApprovalLevel = vacatingHostelStudentWorkflowEntityList.stream()
								.filter(entity -> entity.getApprovalLevel() > dto.getApprovalLevel())
								.filter(entity -> !ModelConstants.AUTHENTICATION_TYPE_INFORMATION.equalsIgnoreCase(entity.getAuthenticationType()))
								.map(VacatingHostelStudentWorkflowEntity::getApprovalLevel).findFirst();
						
						if (nextApprovalLevel.isPresent()) {
							int nextLevel = nextApprovalLevel.get();
							vacatingHostelStudentWorkflowEntityList.stream().filter(nextDto -> nextDto.getApprovalLevel() == nextLevel).forEach(nextEntity -> {
								nextEntity.setStatus(WorkflowStatus.PENDING.getStatus());
								nextEntity.onUpdate();
								vacatingHostelStudentWorkflowRepository.save(nextEntity);
							});
						}                   
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private void updateVacatingStudentAssetInventory(StudentHostelRoomVacatingRequestDto dto) {
		List<StudentRoomAssetDetailsEntity> studentRoomAssetDetailsEntityList = studentRoomAssetDetailsRepository
		        .findByStudentIdAndVacatingRequestIdAndActiveFlag(dto.getStudentId(), dto.getId(), Constants.ACTIVE_FLAG);
		List<VacatingStudentInventoryDto> inventoryDtoList = dto.getInventoryDtoList();
        inventoryDtoList.forEach(inventoryDto -> {
            Optional<StudentRoomAssetDetailsEntity> matchingEntityOpt = studentRoomAssetDetailsEntityList.stream()
                    .filter(entity -> entity.getAssetId().equals(inventoryDto.getAssetId()))
                    .findFirst();
            matchingEntityOpt.ifPresentOrElse(
                    entity -> updateRoomAssetDetails(inventoryDto, entity),
                    () -> saveRoomAssetDetails(dto, inventoryDto)
            );
        });
	}

    private void saveRoomAssetDetails(StudentHostelRoomVacatingRequestDto dto, VacatingStudentInventoryDto inventoryDto) {
        StudentRoomAssetDetailsEntity studentRoomAssetDetailsEntity = new StudentRoomAssetDetailsEntity();
        studentRoomAssetDetailsEntity.setVacatingRequestId(dto.getId());
        studentRoomAssetDetailsEntity.setStudentId(dto.getStudentId());
        studentRoomAssetDetailsEntity.setAssetId(inventoryDto.getAssetId());
        studentRoomAssetDetailsEntity.setAssetCategory(inventoryDto.getAssetCategory());
        studentRoomAssetDetailsEntity.setAssetName(inventoryDto.getAssetName());
        studentRoomAssetDetailsEntity.setAssetCode(inventoryDto.getAssetCode());
        studentRoomAssetDetailsEntity.setAssetCondition(inventoryDto.getAssetCondition());
        studentRoomAssetDetailsEntity.setPenaltyAmount(inventoryDto.getPenaltyAmount());
        studentRoomAssetDetailsEntity.setPenaltyReason(inventoryDto.getPenaltyReason());
        studentRoomAssetDetailsEntity.onCreate();
        studentRoomAssetDetailsRepository.save(studentRoomAssetDetailsEntity);
    }

    private void updateRoomAssetDetails(VacatingStudentInventoryDto inventoryDto, StudentRoomAssetDetailsEntity studentRoomAssetDetailsEntity) {
        studentRoomAssetDetailsEntity.setAssetCondition(inventoryDto.getAssetCondition());
        studentRoomAssetDetailsEntity.setPenaltyAmount(inventoryDto.getPenaltyAmount());
        studentRoomAssetDetailsEntity.setPenaltyReason(inventoryDto.getPenaltyReason());
        studentRoomAssetDetailsEntity.onUpdate();
        studentRoomAssetDetailsRepository.save(studentRoomAssetDetailsEntity);
    }

    public int getHostelId(PaginationForm form) {
		return ValidationCommon.toIntegerOrZero(form.getAdditionalParam().get(DeanConstants.HOSTEL_NAME.getConstants()));
	}

    @Transactional
    public boolean deleteStudentVacatingRequest(String data) throws Exception {
        List<String> split = List.of(MCrypt.getInstance().decryptToString(data).split(Constants.BACKTICK));
        StudentDetailsInfoEntity studentEntity = studentDetailsInfoRepository
                .findById(Objects.requireNonNull(split.getFirst()))
                .orElse(null);
        return studentHostelRoomVacatingRequestRepository.getStudentVacatingDetails(studentEntity, ModelConstants.STATUS_ACTIVE)
                .map(entity->{
                    updateVacatingStudent(entity, split);
                    updateVacatingStudentWorkflow(entity, split);
                    return true;
                })
                .orElseThrow(() -> new RecordNotExistsException(
                        messageSource.getMessage("validation.error.id.not.found", null, Locale.getDefault()
                        )));
    }

    private void updateVacatingStudentWorkflow(StudentHostelRoomVacatingRequestEntity entity, List<String> split) {
        List<VacatingHostelStudentWorkflowEntity> vacatingHostelStudentWorkflowEntityList = vacatingHostelStudentWorkflowRepository.findByRequestIdAndStudentIdAndActiveFlag(entity.getId(), entity.getStudent().getStudentId(), ModelConstants.STATUS_ACTIVE);
        vacatingHostelStudentWorkflowEntityList.forEach(
                vacatingHostelStudentWorkflowEntity -> {
                    vacatingHostelStudentWorkflowEntity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
                    vacatingHostelStudentWorkflowEntity.setModifiedAt(DateUtility.getNowTimeInstant());
                    vacatingHostelStudentWorkflowEntity.setModifiedBy(split.get(2));
                    vacatingHostelStudentWorkflowRepository.save(vacatingHostelStudentWorkflowEntity);
                }
        );
    }

    private void updateVacatingStudent(StudentHostelRoomVacatingRequestEntity entity, List<String> split) {
        entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
        entity.setModifiedAt(DateUtility.getNowTimeInstant());
        entity.setModifiedBy(split.get(2));
        studentHostelRoomVacatingRequestRepository.save(entity);
    }

    public Workbook getWorkbook(List<String> split){
        var reportType = split.getFirst();
        var fromDate = ModelConstants.HYPHEN.equals(split.get(1)) ? null : LocalDate.parse(split.get(1));
        var toDate = ModelConstants.HYPHEN.equals(split.getLast()) ? null : LocalDate.parse(split.getLast());
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet(ExcelConstants.VACATING_STUDENTS_REPORT);
        var headerStyle = excelUtility.setHeaderStyle(workbook);
        var dataStyle = excelUtility.setDataStyle(workbook);
        var row = sheet.createRow(0);
        var cellHeader = row.createCell(0);
        var wrappedHeaderStyle = workbook.createCellStyle();
        var columnWidths = getColumnWidths(reportType);
        var rowIndex = new AtomicInteger(3);
        var slNo = new AtomicInteger(0);
        var headers = getExcelHeader(reportType);
        var headerRow = sheet.createRow(2);
        var vacatingStudentReportList = getVacatingStudentReportList(reportType, fromDate, toDate);

        wrappedHeaderStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        wrappedHeaderStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        wrappedHeaderStyle.setWrapText(true);

        createMessLedgerHeader(cellHeader, wrappedHeaderStyle, row, sheet, headerStyle, headers.size());
//        performRowOneCellCreation(sheet, wrappedHeaderStyle);
        headerStyle.setWrapText(true);
        headerRow.setHeightInPoints(60);
        IntStream.range(0, headers.size()).forEach(i -> {
            var cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
        });

        performExcelDetailsMapping(vacatingStudentReportList, rowIndex, sheet, dataStyle, reportType, slNo);
        IntStream.range(0, columnWidths.size()).forEach(i -> sheet.setColumnWidth(i, columnWidths.get(i)));
        return workbook;
    }

    private List<String> getExcelHeader(String reportType) {
        return switch (reportType) {
            case ExcelConstants.TYPE_VACATING_STUDENTS_REPORT -> ExcelConstants.VACATING_STUDENTS_REPORT_HEADER;
            case ExcelConstants.TYPE_PENALTY_REPORT -> ExcelConstants.PENALTY_REPORT_HEADER;
            case ExcelConstants.TYPE_DONATION_REPORT -> ExcelConstants.DONATION_REPORT_HEADER;
            case ExcelConstants.TYPE_VACATING_STUDENTS_SUMMARY_REPORT -> ExcelConstants.VACATING_STUDENTS_SUMMARY_REPORT_HEADER;
            default -> throw new IllegalArgumentException("Unsupported report type: " + reportType);
        };
    }

    private List<Integer> getColumnWidths(String reportType) {
        return switch (reportType) {
            case ExcelConstants.TYPE_VACATING_STUDENTS_REPORT -> ExcelConstants.VACATING_STUDENTS_REPORT_DATA_WIDTH;
            case ExcelConstants.TYPE_PENALTY_REPORT -> ExcelConstants.PENALTY_REPORT_DATA_WIDTH;
            case ExcelConstants.TYPE_DONATION_REPORT -> ExcelConstants.DONATION_REPORT_DATA_WIDTH;
            case ExcelConstants.TYPE_VACATING_STUDENTS_SUMMARY_REPORT -> ExcelConstants.VACATING_STUDENTS_SUMMARY_REPORT_DATA_WIDTH;
            default -> throw new IllegalArgumentException("Unsupported report type: " + reportType);
        };
    }

    private List<VacatingStudentReportRecord> getVacatingStudentReportList(String reportType, LocalDate fromDate, LocalDate toDate) {
        return switch (reportType) {
            case ExcelConstants.TYPE_VACATING_STUDENTS_REPORT -> studentHostelRoomVacatingRequestRepository
                    .getVacatingStudentReport(fromDate, toDate, ModelConstants.STATUS_ACTIVE);

            case ExcelConstants.TYPE_PENALTY_REPORT -> studentHostelRoomVacatingRequestRepository
                    .getPenaltyReport(fromDate, toDate, ModelConstants.STATUS_ACTIVE, WorkflowStatus.APPROVE.getStatus());

            case ExcelConstants.TYPE_DONATION_REPORT -> studentHostelRoomVacatingRequestRepository
                    .getDonationReport(fromDate, toDate, ModelConstants.STATUS_ACTIVE, WorkflowStatus.APPROVED.getStatus(), true);

            case ExcelConstants.TYPE_VACATING_STUDENTS_SUMMARY_REPORT -> studentHostelRoomVacatingRequestRepository
                    .getVacatingStudentSummaryReport(fromDate, toDate, ModelConstants.STATUS_ACTIVE);

            default -> throw new IllegalArgumentException("Unsupported report type: " + reportType);
        };
    }

    private void createMessLedgerHeader(XSSFCell cellHeader, XSSFCellStyle wrappedHeaderStyle, XSSFRow row, XSSFSheet sheet, XSSFCellStyle headerStyle, int lastColSize) {
        SimpleDateFormat sdf = new SimpleDateFormat(commonResponseUtil.getMessage("session.date.format"));
        String reportDateField = ModelConstants.NEW_LINE + commonResponseUtil.getMessage("message.label.report.date.colon") + sdf.format(new Date());
        String titleText = String.join(ModelConstants.NEW_LINE, ExcelConstants.VACATING_STUDENTS_REPORT_EXCEL_HEADER);
        cellHeader.setCellValue(titleText + reportDateField);
        wrappedHeaderStyle.cloneStyleFrom(headerStyle);
        wrappedHeaderStyle.setWrapText(true);
        cellHeader.setCellStyle(wrappedHeaderStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, lastColSize - 1));
        row.setHeightInPoints(ExcelConstants.VACATING_STUDENTS_REPORT_EXCEL_HEADER.size() * sheet.getDefaultRowHeightInPoints());
    }

    private void performRowOneCellCreation(XSSFSheet sheet, XSSFCellStyle wrappedHeaderStyle) {
        var row = sheet.createRow(1);
        var reportDate = row.createCell(0);
        SimpleDateFormat sdf = new SimpleDateFormat(commonResponseUtil.getMessage("session.date.format"));
        String reportDateField = commonResponseUtil.getMessage("message.label.report.date.colon") + sdf.format(new Date());
        reportDate.setCellValue(reportDateField);
        reportDate.setCellStyle(wrappedHeaderStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
    }

    private void performExcelDetailsMapping(List<VacatingStudentReportRecord> vacatingStudentReportRecords,
                                            AtomicInteger rowIndex,
                                            XSSFSheet sheet,
                                            XSSFCellStyle dataStyle,
                                            String reportType,
                                            AtomicInteger slNo) {
        switch (reportType) {
            case ExcelConstants.TYPE_VACATING_STUDENTS_REPORT -> vacatingStudentExcelReport(vacatingStudentReportRecords, rowIndex, sheet, dataStyle, slNo);
            case ExcelConstants.TYPE_PENALTY_REPORT -> penaltyExcelReport(vacatingStudentReportRecords, rowIndex, sheet, dataStyle, slNo);
            case ExcelConstants.TYPE_DONATION_REPORT -> donationExcelReport(vacatingStudentReportRecords, rowIndex, sheet, dataStyle, slNo);
            case ExcelConstants.TYPE_VACATING_STUDENTS_SUMMARY_REPORT -> vacatingStudentSummaryExcelReport(vacatingStudentReportRecords, rowIndex, sheet, dataStyle, slNo);
        }
    }

    private void vacatingStudentExcelReport(List<VacatingStudentReportRecord> vacatingStudentReportRecords, AtomicInteger rowIndex, XSSFSheet sheet, XSSFCellStyle dataStyle, AtomicInteger slNo) {
        vacatingStudentReportRecords.forEach(record -> {
            var row = sheet.createRow(rowIndex.getAndIncrement());
            int col = 0;
            int colWidth = -1;
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, String.valueOf(slNo.incrementAndGet()), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.ofNullable(Objects.nonNull(record.studentName()) ? record.studentName() : record.acountName())
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.ofNullable(record.studentId())
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    String.format(ModelConstants.TWO_DECIMAL_POINT, Optional.ofNullable(record.creditAmt()).orElse(0.00d)), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    String.format(ModelConstants.TWO_DECIMAL_POINT, Optional.ofNullable(record.debitAmt()).orElse(0.00d)), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    String.format(ModelConstants.TWO_DECIMAL_POINT, Optional.ofNullable(record.netBal()).orElse(0.00d)), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.ofNullable(record.bankNameOne())
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.ofNullable(record.bankAccountNoOne())
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.ofNullable(record.acountName())
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.ofNullable(record.ifsCodeOne())
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.ofNullable(record.branchNameOne())
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, Optional.ofNullable(record.vacatingDate()).map(DateUtility::formatDate)
                    .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.ofNullable(CategoryEnum.OTHERS.getValue().equalsIgnoreCase(record.vacatingReason()) ?
                                    record.othersVacatingReason() : record.vacatingReason())
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, Optional.ofNullable(record.exchangeProgPeriodFromDate()).map(DateUtility::formatDate)
                    .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, Optional.ofNullable(record.exchangeProgPeriodToDate()).map(DateUtility::formatDate)
                    .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.ofNullable(record.placeOfVisit())
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.of(record.donationStatus() ? PdfActionService.YES : PdfActionService.NO)
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.ofNullable(CategoryEnum.OTHERS.getValue().equalsIgnoreCase(record.donatorType()) ? record.donatorType() + "(" + record.othersDescription() + ")" :
                                    record.donatorType())
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    String.valueOf(Optional.ofNullable(record.donationAmount())
                            .orElse(0L)), dataStyle);
        });
    }

    private void penaltyExcelReport(List<VacatingStudentReportRecord> vacatingStudentReportRecords, AtomicInteger rowIndex, XSSFSheet sheet, XSSFCellStyle dataStyle, AtomicInteger slNo) {
        vacatingStudentReportRecords.forEach(record -> {
            var row = sheet.createRow(rowIndex.getAndIncrement());
            int col = 0;
            int colWidth = -1;
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, String.valueOf(slNo.incrementAndGet()), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.ofNullable(record.studentId())
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    String.valueOf(Optional.ofNullable(record.penalityAmount())
                            .orElse(Long.valueOf(String.format(ModelConstants.TWO_DECIMAL_POINT, Optional.ofNullable(record.creditAmt()).orElse(0.00d))))), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.ofNullable(record.penaltyReason())
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.ofNullable(record.hostelName())
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
        });
    }

    private void donationExcelReport(List<VacatingStudentReportRecord> vacatingStudentReportRecords, AtomicInteger rowIndex, XSSFSheet sheet, XSSFCellStyle dataStyle, AtomicInteger slNo) {
        vacatingStudentReportRecords.forEach(record -> {
            var row = sheet.createRow(rowIndex.getAndIncrement());
            int col = 0;
            int colWidth = -1;
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, String.valueOf(slNo.incrementAndGet()), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.ofNullable(Objects.nonNull(record.studentName()) ? record.studentName() : record.acountName())
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.ofNullable(record.studentId())
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    String.valueOf(Optional.ofNullable(record.donationAmount())
                            .orElse(Long.valueOf(String.format(ModelConstants.TWO_DECIMAL_POINT, Optional.ofNullable(record.creditAmt()).orElse(0.00d))))), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.ofNullable(ModelConstants.HOSTEL.equalsIgnoreCase(record.donatorType()) ?
                                    record.donatorType() + ModelConstants.HYPHEN + ModelConstants.SPACE + record.donationHostel() : record.donatorType())
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.ofNullable(record.hostelName())
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
        });
    }

    private void vacatingStudentSummaryExcelReport(List<VacatingStudentReportRecord> vacatingStudentReportRecords, AtomicInteger rowIndex, XSSFSheet sheet, XSSFCellStyle dataStyle, AtomicInteger slNo) {
        vacatingStudentReportRecords.forEach(record -> {
            var row = sheet.createRow(rowIndex.getAndIncrement());
            int col = 0;
            int colWidth = -1;
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, String.valueOf(slNo), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Optional.ofNullable(record.studentId())
                            .orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    String.valueOf(Optional.ofNullable(record.creditAmt())
                            .orElse(Double.valueOf(String.format(ModelConstants.TWO_DECIMAL_POINT, Optional.ofNullable(record.creditAmt()).orElse(0.00d))))), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, String.valueOf(Optional.ofNullable(record.debitAmt())
                    .orElse(Double.valueOf(String.format(ModelConstants.TWO_DECIMAL_POINT, Optional.ofNullable(record.creditAmt()).orElse(0.00d))))), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, ModelConstants.REMITTER_ACC_NO, dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, ModelConstants.REMITTER_ACC_NAME, dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, ModelConstants.REMITTER_ADDRESS, dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, String.valueOf(10), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, ModelConstants.HYPHEN, dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, ModelConstants.HYPHEN, dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, ModelConstants.HYPHEN, dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, String.valueOf(Optional.ofNullable(record.netBal()).orElse(0.0)), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, String.format(ModelConstants.TWO_DECIMAL_POINT, Optional.ofNullable(record.creditAmt()).orElse(0.00d)), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, Optional.ofNullable(record.acountName()).orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, Optional.ofNullable(record.bankAccountNoOne()).orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, String.valueOf(10), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, ModelConstants.HYPHEN, dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, ModelConstants.HYPHEN, dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, Optional.ofNullable(record.ifsCodeOne()).orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, ModelConstants.HYPHEN, dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, ModelConstants.HYPHEN, dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, String.valueOf(Optional.ofNullable(record.mobNum()).orElse(0L)), dataStyle);
        });
    }

    public StudentHostelRoomVacatingRequestEntity getStudentHostelRoomVacatingById(Long requestId) {
        return studentHostelRoomVacatingRequestRepository.findByIdAndActiveFlag(requestId, ModelConstants.STATUS_ACTIVE);
    }
    
    private List<HostelVacatingAllowedStudentDto> getHostelVacatingStudentDetailsByStudentId(String studentId) {
    	return hostelVacatingAllowedStudentService.getStudentAllottedRoomStatus(studentId)
                .stream().map(this::getHostelVacatingAllowedStudentDto)
                .collect(Collectors.toList());
	}
    
	private void setPdfHostelVacatingDetails(Document document, StudentHostelRoomVacatingRequestDto dto) {
		String[] keys = { "message.label.hostel.vacating.details", "message.label.reason.for.vacating.pdf",
				"message.label.vacating.hostel.date.pdf", "message.label.from.date.pdf", "message.label.to.date.pdf",
				"message.label.place.of.visit.pdf", "message.label.approval.status.pdf",
				"message.label.hostel.name.pdf", "message.label.donation.amount.pdf","message.label.for.pdf" };

		String[] m = Arrays.stream(keys).map(commonResponseUtil::getMessage).toArray(String[]::new);

		Table outerTable = new Table(1);
		outerTable.setWidth(PdfActionService.VALUE_100_P);
		outerTable.setBorder(new SolidBorder(1));
		outerTable.setMarginBottom(5);

		outerTable.addCell(new Cell().add(new Paragraph(m[0]).setBold()).setBorder(Border.NO_BORDER).setPaddingBottom(5));

		Table hostelTable = new Table(new float[] { 2, 3, 2, 3 });
		hostelTable.setWidth(PdfActionService.VALUE_100_P);
        hostelTable.setFontSize(9);
		DateTimeFormatter df = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);

		String vacatingDate = Objects.nonNull(dto.getVacatingDate()) ? dto.getVacatingDate().format(df)
				: ModelConstants.NOT_APPLICABLE;

		// Reason + Vacating date (same row)
		pdfActiveService.addTableTextValue(m[1],
				messageSource.getMessage("message.label.others", null, Locale.getDefault())
						.equals(dto.getVacatingReason()) ? dto.getOthersVacatingReason() : dto.getVacatingReason(),
				hostelTable);
		
		pdfActiveService.addTableTextValue(m[2], vacatingDate, hostelTable);

		// Approval Status
		pdfActiveService.addTableTextValue(m[6], dto.getHostelOrWardenApprovalStatus(), hostelTable);
		
		// Donation amount
		pdfActiveService.addTableTextValue(m[8],
				PdfActionService.RUPEES + ModelConstants.SPACE
						+ utility.formatCommaSeperatedCurrency(
								dto.getDonationAmount() != null && dto.getDonationAmount().doubleValue() > 0 ? dto.getDonationAmount().doubleValue() : 0),
				hostelTable);
		
		// Donation FOR
		if (dto.getDonationAmount() != null && dto.getDonationAmount().doubleValue() > 0) {
			pdfActiveService.addTableTextValue(m[9],Objects.nonNull(dto.getDonatorType()) ? dto.getDonatorType() : ModelConstants.NOT_APPLICABLE,
					hostelTable);
			// Hostel Name
			pdfActiveService.addTableTextValue(m[7],Objects.nonNull(dto.getDonatedHostel())  && !dto.getDonatedHostel().isEmpty()  ? dto.getDonatedHostel()
					: ModelConstants.NOT_APPLICABLE,hostelTable);
		}

		//  EXCHANGE-SPECIFIC DETAILS
		if (StudentConstants.EXCHANGE_VACATING_REASON.getStudentConstant().equals(dto.getVacatingReason())) {

			pdfActiveService.addTableTextValue(m[3],Objects.nonNull(dto.getExchangeProgPeriodFromDate()) ? dto.getExchangeProgPeriodFromDate().format(df)
							: ModelConstants.NOT_APPLICABLE, hostelTable);

			pdfActiveService.addTableTextValue(m[4],
					Objects.nonNull(dto.getExchangeProgPeriodToDate()) ? dto.getExchangeProgPeriodToDate().format(df)
							: ModelConstants.NOT_APPLICABLE,hostelTable);

			pdfActiveService.addTableTextValue(m[5],
					Objects.nonNull(dto.getPlaceOfVisit()) && !dto.getPlaceOfVisit().isEmpty() ? dto.getPlaceOfVisit()
							: ModelConstants.NOT_APPLICABLE,hostelTable, new int[] { 1, 3 });
					
		}
        reAlignPdfContent(hostelTable);
        outerTable.addCell(new Cell().add(hostelTable).setBorder(Border.NO_BORDER));
		document.add(outerTable);
	}
	
	private void setPdfBankDetails(Document document, StudentHostelRoomVacatingRequestDto dto) {
		if (StudentConstants.EXCHANGE_VACATING_REASON
	            .getStudentConstant().equals(dto.getVacatingReason())) {
	        return;
	    }
		String[] keys = { "message.label.bank.details", "message.label.bank.account.no.pdf",
				"message.label.ifsc.code.pdf", "message.label.bank.name.pdf", "message.label.branch.name.pdf",
				"message.label.bank.note.pdf" };

		String[] m = Arrays.stream(keys).map(k -> messageSource.getMessage(k, null, Locale.getDefault()))
				.toArray(String[]::new);

		Table outerTable = new Table(1);
		outerTable.setWidth(PdfActionService.VALUE_100_P);
		outerTable.setBorder(new SolidBorder(1));
		outerTable.setMarginBottom(5);

		outerTable.addCell(new Cell().add(new Paragraph(m[0]).setBold()).setBorder(Border.NO_BORDER).setPaddingBottom(5));

		Table bankTable = new Table(new float[] { 2, 3, 2, 3 });
		bankTable.setWidth(PdfActionService.VALUE_100_P);
        bankTable.setFontSize(9);

		pdfActiveService.addTableTextValue(m[1], dto.getBankAccountNoOne(), bankTable);

		Cell noteCell = new Cell(1, 4)
				.add(new Paragraph(m[5]).setItalic().setFontSize(7).setMarginTop(3).setMarginBottom(5))
				.setBorder(Border.NO_BORDER);
		bankTable.addCell(noteCell);

		pdfActiveService.addTableTextValue(m[2], dto.getIfsCodeOne(), bankTable);

		pdfActiveService.addTableTextValue(m[3], dto.getBankNameOne(), bankTable);

        pdfActiveService.addTableTextValue(m[4], dto.getBranchNameOne(), bankTable, new int[]{1, 3});
		outerTable.addCell(new Cell().add(bankTable).setBorder(Border.NO_BORDER));
        reAlignPdfContent(bankTable);
		document.add(outerTable);

	}

    public void reAlignPdfContent(Table table){
        for (IElement element : table.getChildren()) {
            if (element instanceof Cell cell) {
                cell.setPaddingTop(2f);
                cell.setPaddingBottom(2f);
                cell.setPaddingLeft(2f);
                cell.setPaddingRight(2f);
                for (IElement child : cell.getChildren()) {
                    if (child instanceof Paragraph p) {
                        p.setMultipliedLeading(0.95f);
                    }
                }
            }
        }
    }

    @Transactional
    public String checkAndUpdateDayScholarStatus() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<String> statuses = List.of(WorkflowStatus.APPROVED.getStatus(), WorkflowStatus.WARDEN_APPROVAL_STATUS_COMPLETE.getStatus());
        List<StudentDetailsInfoEntity> studentDetailsInfoEntity = studentHostelRoomVacatingRequestRepository.findStudentsVacatedYesterday(yesterday, statuses, ModelConstants.STATUS_ACTIVE);
        if (!studentDetailsInfoEntity.isEmpty()) {
            studentDetailsInfoEntity.forEach(entity -> {
                entity.setDayScholar(ModelConstants.STATUS_ACTIVE);
                entity.setModifiedBy(SecurityCtxUtil.userId());
                entity.setModifiedAt(LocalDateTime.now());
            });
            studentDetailsInfoRepository.saveAll(studentDetailsInfoEntity);
            return Constants.UPDATED;
        }
        return Constants.ERROR;
    }
}
