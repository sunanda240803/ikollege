package com.iitm.hosteldine.service.hostel;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.AppointmentStatusNote;
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.SimsConfigDataJsonArrayDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentAppointmentRequestDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentDetailsPdfDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentWorkflowDto;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.dashboard.student.StudentAppointmentRequestMapper;
import com.iitm.hosteldine.mapper.dashboard.student.StudentWorkflowMapper;
import com.iitm.hosteldine.model.dashboard.student.StudentAppointmentRequestEntity;
import com.iitm.hosteldine.model.dashboard.student.StudentFilesInfoEntity;
import com.iitm.hosteldine.model.dashboard.student.StudentWorkflowEntity;
import com.iitm.hosteldine.model.dashboard.student.WorkflowMasterEntity;
import com.iitm.hosteldine.model.hostel.CompleteStudentApplicationView;
import com.iitm.hosteldine.repository.dashboard.student.StudentAppointmentRequestRepository;
import com.iitm.hosteldine.repository.dashboard.student.StudentFilesInfoRepository;
import com.iitm.hosteldine.repository.dashboard.student.StudentWorkflowRepository;
import com.iitm.hosteldine.repository.dashboard.student.WorkflowMasterRepository;
import com.iitm.hosteldine.repository.hostel.CompleteStudentApplicationViewRepository;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.PdfActionService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.dashboard.student.StudentAppointmentRequestService;
import com.iitm.hosteldine.service.dashboard.student.WorkflowMasterService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.util.MCrypt;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HostelAccommodationService {

	private final MessageSource messageSource;
	private final FileService fileService;
	private final SimsConfigDataService simsConfigDataService;
	private final WorkflowMasterRepository workflowMasterRepository;
	private final StudentWorkflowRepository studentWorkflowRepository;
	private final StudentAppointmentRequestRepository studentAppointmentRequestRepository;
	private final CompleteStudentApplicationViewRepository completeStudentApplicationViewRepository;
	private final StudentFilesInfoRepository studentFilesInfoRepository;
	private final MailTemplateRepository mailTemplateRepository;
	private final MailQueueService mailQueueService;
	private final PdfActionService pdfActiveService;
	private final StudentAppointmentRequestService studentAppointmentRequestService;

	private final List<String> excludedStatuses = List.of(WorkflowStatus.DELETED.getStatus(),
			WorkflowStatus.CANCELLED.getStatus(), WorkflowStatus.REJECTED.getStatus(),
			WorkflowStatus.APPROVED.getStatus(), WorkflowStatus.CANCELLED_AFTER_APPROVED.getStatus());

	public Page<StudentAppointmentRequestDto> getStudentHostelAccommodationList(PaginationForm form) throws Exception {
		Page<Object[]> result = null;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize());
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			Integer resendMailCount = Integer.parseInt(simsConfigDataService.getSimConfigValue(SimsConfigDataService.RESEND_MAIL));
			String category = messageSource.getMessage(StudentConstants.CATEGORY_SCHOLAR.getStudentConstant(), null, Locale.getDefault());
			result = studentAppointmentRequestRepository.getStudentHostelAccommodationList(resendMailCount, SecurityCtxUtil.userId().toUpperCase(),
					category, ModelConstants.STATUS_ACTIVE, pageable);
		}
		return result.map(record -> {
			try {
				StudentAppointmentRequestDto dto = new StudentAppointmentRequestDto();
				if (record[0] != null && String.valueOf(record[15]).equalsIgnoreCase(WorkflowStatus.VALIDATING.getStatus())) {
					dto.setResendMailStatus(true);
				} else {
					dto.setResendMailStatus(false);
				}
				dto.setRequestId(MCrypt.getInstance().encryptToText(String.valueOf(record[1])));
				dto.setAppointmentFrom(record[3] != null ? ((java.sql.Date) record[3]).toLocalDate() : null);
				dto.setAppointmentTo(record[4] != null ? ((java.sql.Date) record[4]).toLocalDate() : null);
				dto.setStayFrom(record[5] != null ? ((java.sql.Date) record[5]).toLocalDate() : null);
				dto.setStayTo(record[6] != null ? ((java.sql.Date) record[6]).toLocalDate() : null);
				dto.setStatus(String.valueOf(record[15].toString()));
				dto.setCreatedAt(record[17] != null
						? DateUtility.formatDateTime(((java.sql.Timestamp) record[17]).toLocalDateTime())
						: null);
				dto.setStatusNotes(String.valueOf(record[28]));
				return dto;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	@Transactional
	public String saveStudentHostelAccommodationRequest(StudentAppointmentRequestDto dto, HttpServletRequest request) throws Exception {

		List<WorkflowMasterEntity> workflowMasterList = workflowMasterRepository
				.getAuthorityList(StudentConstants.CATEGORY_CCW.getStudentConstant(), ModelConstants.STATUS_ACTIVE);

		if (workflowMasterList != null && !workflowMasterList.isEmpty()) {
			dto.setDining(ModelConstants.STATUS_ACTIVE.toLowerCase());
			StudentAppointmentRequestEntity entity = StudentAppointmentRequestMapper.INSTANCE.toEntity(dto);
			String studentId = SecurityCtxUtil.userId().toUpperCase();

			entity.setStudentId(studentId);
			entity.setStatus(WorkflowStatus.VALIDATING.getStatus());
			entity.setStatusNotes(messageSource.getMessage(AppointmentStatusNote.SCHOLARS_STAY_EXTENSION_VALIDATING_NOTE.getNoteKey(),
							null, Locale.getDefault()));
			entity.setResendDate(DateUtility.getNowDate());
			entity.setOccupancy(Constants.MULTI);
			entity.setThesisSubmittedDate(LocalDate.now());
			entity.onCreate();

			StudentAppointmentRequestEntity savedEntity = studentAppointmentRequestRepository.saveAndFlush(entity);

			Long requestId = savedEntity.getId();

			Optional<CompleteStudentApplicationView> recentStatus = completeStudentApplicationViewRepository
					.getRecentStatus(studentId, dto.getAppointmentFrom(), dto.getAppointmentTo(), requestId);

			if (recentStatus.isPresent()) {
				String status = recentStatus.get().getStatus();
				if (!WorkflowStatus.APPROVED.getStatus().equals(status)
						&& !WorkflowStatus.ALLOTTED.getStatus().equals(status)
						&& !WorkflowStatus.CHECKED_IN.getStatus().equals(status)) {

					studentAppointmentRequestRepository.updatePreviousStatusToCancelled(
							WorkflowStatus.CANCELLED.getStatus(), studentId, DateUtility.getNowTimeInstant(),
							messageSource.getMessage(
									AppointmentStatusNote.SCHOLARS_STAY_EXTENSION_PRE_CANCEL_NOTE.getNoteKey(), null,
									Locale.getDefault()),
							dto.getAppointmentFrom(), dto.getAppointmentTo(), requestId, excludedStatuses);

					studentWorkflowRepository.updatePreviousWorkflowToCancelled(WorkflowStatus.CANCELLED.getStatus(),
							studentId, DateUtility.getNowTimeInstant(), dto.getAppointmentFrom(),
							dto.getAppointmentTo(), requestId);
				}
			}

			List<StudentWorkflowEntity> studentWorkflowEntityList = new ArrayList<>();
			for (WorkflowMasterEntity masterEntity : workflowMasterList) {
				StudentWorkflowEntity workflowEntity = new StudentWorkflowEntity();
				if (masterEntity.getAuthorityType().equalsIgnoreCase(messageSource
						.getMessage(StudentConstants.VALIDATOR.getStudentConstant(), null, Locale.getDefault()))) {
					StudentWorkflowMapper.INSTANCE.workflowValidatorEntity(workflowEntity, masterEntity, savedEntity);
					if (masterEntity.getValidatorName()!=null && !masterEntity.getValidatorName().isBlank()) {
						workflowEntity.setValidatorName(masterEntity.getValidatorName());
					}
					if (masterEntity.getEmail()!=null && !masterEntity.getEmail().isBlank()) {
						workflowEntity.setValidatorEmail(masterEntity.getEmail());
					}
				} else {
					StudentWorkflowMapper.INSTANCE.workflowEntity(workflowEntity, masterEntity, savedEntity);
				}
				if (masterEntity.getApprovalLevel() == 1) {
					workflowEntity.setStatus(WorkflowStatus.PENDING.getStatus());
				} else {
                    workflowEntity.setStatus(WorkflowStatus.DEFAULT.getStatus());
                }
				workflowEntity.onCreate();
				studentWorkflowEntityList.add(workflowEntity);
			}
			if (!studentWorkflowEntityList.isEmpty()) {
				studentWorkflowRepository.saveAll(studentWorkflowEntityList);
			}

			handleFileUploads(dto.getFile(), dto.getFileDescription(), studentId, requestId);

			List<StudentWorkflowEntity> studentWorkflowList = studentWorkflowRepository.getStudentWorkflowList(
					StudentConstants.CATEGORY_CCW.getStudentConstant(), requestId, WorkflowStatus.PENDING.getStatus(), 1);

			if (studentWorkflowList != null && !studentWorkflowList.isEmpty()) {
				studentAppointmentRequestService.mailToValidator(studentId, requestId, StudentConstants.CATEGORY_CCW.getStudentConstant(), request);
			}

			return Constants.SAVED;
		}
		return null;
	}

	@Transactional
	public String resendMail(String requestId, HttpServletRequest request) throws Exception {
	    String studentId = SecurityCtxUtil.userId().toUpperCase();
	    Long id = MCrypt.getInstance().decryptToLong(requestId);

	    return processResendMail(studentId, id, request);
	}

	public String processResendMail(String studentId, Long id, HttpServletRequest request) throws Exception {
	    List<StudentWorkflowEntity> studentWorkflowEntityList = studentWorkflowRepository.getStudentWorkflowList(
	            WorkflowStatus.PENDING.getStatus(), studentId, id);

	    if (studentWorkflowEntityList != null && !studentWorkflowEntityList.isEmpty()) {
	        studentAppointmentRequestRepository.updateResendDate(id, studentId, DateUtility.getNowDate());
			studentAppointmentRequestService.mailToValidator(studentId, id, StudentConstants.CATEGORY_CCW.getStudentConstant(), request);
	        return Constants.SAVED;
	    }
	    return null;
	}

	private void sendAccommodationRequestMails(String studentId, Long requestId,
			List<StudentWorkflowEntity> studentWorkflowEntityList) throws Exception {

	    Object studentEntityList = studentAppointmentRequestRepository
	            .getStudentInformationForMail(ModelConstants.STATUS_ACTIVE, requestId);

	    if (studentEntityList != null) {
	        Object[] studentEntity = (Object[]) studentEntityList;

	        ArrayList<SimsConfigDataJsonArrayDto> category = simsConfigDataService
	                .getSimConfigValueFromJsonArray(SimsConfigDataService.NATURE_OF_APPOINTMENT);

	        String studentCategoryKey = String.valueOf(studentEntity[7]).trim();
	        String categoryValue = "";

	        for (SimsConfigDataJsonArrayDto config : category) {
	            if (studentCategoryKey.equals(config.getId())) {
	                categoryValue = config.getValue();
	                break;
	            }
	        }

	        Optional<MailTemplateEntity> informationMailTemplate = mailTemplateRepository
	                .findByMailType(MailTemplateEntity.HOSTEL_ACCOMMODATION_MAIL);
	        if (informationMailTemplate.isPresent()) {
	            MailTemplateEntity template = informationMailTemplate.get();
	            String subject = template.getMailSubject();

	            for (StudentWorkflowEntity studentWorkflowEntity : studentWorkflowEntityList) {
	                String content = template.getMailTemplate();
	                content = content.replaceAll("#%student_name%#", String.valueOf(studentEntity[9]).trim());
	                content = content.replaceAll("#%student_id%#", studentId);
	                content = content.replaceAll("#%address%#", String.valueOf(studentEntity[0]).trim());
	                content = content.replaceAll("#%contact_number%#", Objects.toString(studentEntity[1], ""));
	                content = content.replaceAll("#%student_email%#", String.valueOf(studentEntity[2]).trim());
	                content = content.replaceAll("#%appointment_from_date%#", DateUtility.formatDate(studentEntity[3]));
	                content = content.replaceAll("#%appointment_to_date%#", DateUtility.formatDate(studentEntity[4]));
	                content = content.replaceAll("#%stay_from_date%#", DateUtility.formatDate(studentEntity[5]));
	                content = content.replaceAll("#%stay_to_date%#", DateUtility.formatDate(studentEntity[6]));
	                content = content.replaceAll("#%category%#", categoryValue);
	                content = content.replaceAll("#%purpose%#", String.valueOf(studentEntity[8]).trim());

	                // Approve and reject button and link details
	                Optional<MailTemplateEntity> approveTemplate = mailTemplateRepository
	                        .findByMailType(MailTemplateEntity.APPROVE_ACCOMMODATION_BUTTON);
	                Optional<MailTemplateEntity> rejectTemplate = mailTemplateRepository
	                        .findByMailType(MailTemplateEntity.REJECT_ACCOMMODATION_BUTTON);
	                Optional<MailTemplateEntity> viewTemplate = mailTemplateRepository
	                        .findByMailType(MailTemplateEntity.VIEW_ACCOMMODATION_BUTTON);
	                Optional<MailTemplateEntity> approveLinkTemplate = mailTemplateRepository
	                        .findByMailType(MailTemplateEntity.APPROVE_ACCOMMODATION_LINK);
	                Optional<MailTemplateEntity> rejectLinkTemplate = mailTemplateRepository
	                        .findByMailType(MailTemplateEntity.REJECT_ACCOMMODATION_LINK);
	                Optional<MailTemplateEntity> viewLinkTemplate = mailTemplateRepository
	                        .findByMailType(MailTemplateEntity.VIEW_ACCOMMODATION_LINK);

	                if (approveTemplate.isPresent() && rejectTemplate.isPresent() && viewTemplate.isPresent()
	                        && approveLinkTemplate.isPresent() && rejectLinkTemplate.isPresent()
	                        && viewLinkTemplate.isPresent()) {

	                    content = content.replaceAll("#%approveButton%#", approveTemplate.get().getMailTemplate());
	                    content = content.replaceAll("#%rejectButton%#", rejectTemplate.get().getMailTemplate());
	                    content = content.replaceAll("#%viewButton%#", viewTemplate.get().getMailTemplate());
	                    content = content.replaceAll("#%approveLink%#", approveLinkTemplate.get().getMailTemplate());
	                    content = content.replaceAll("#%rejectLink%#", rejectLinkTemplate.get().getMailTemplate());
	                    content = content.replaceAll("#%viewLink%#", viewLinkTemplate.get().getMailTemplate());
	                }

	                mailQueueService.saveMailQueue(subject, studentWorkflowEntity.getValidatorName(), content,
	                        studentWorkflowEntity.getValidatorEmail(), WorkflowMasterService.HOSTEL_ACCOMMODATION, studentId, 1,
	                        null, null, null, null);
	            }
	        }
	    }
	}


	private void handleFileUploads(MultipartFile[] files, List<String> descriptions, String studentId, Long requestId) throws Exception {
	    if (files != null && files.length > 0) {
	        List<StudentFilesInfoEntity> studentFilesInfoEntityList = new ArrayList<>();
	        List<String> successfullySavedFileNames = new ArrayList<>();

	        for (int i = 0; i < files.length; i++) {
	            MultipartFile file = files[i];
	            String description = (descriptions != null && descriptions.size() > i) ? descriptions.get(i) : "";

	            if (file != null && !file.isEmpty()) {
	                String originalFileName = file.getOriginalFilename();
	                String fileExtension = "";

	                if (originalFileName != null && originalFileName.contains(ModelConstants.DOT)) {
	                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf(ModelConstants.DOT));
	                }

	                String fileName = MCrypt.getInstance().encryptToText(studentId + ModelConstants.UNDERSCORE + FileService.HOSTEL_ACCOMMODATION
	                        + ModelConstants.UNDERSCORE + System.currentTimeMillis()) + fileExtension;

	                StudentFilesInfoEntity fileEntity = new StudentFilesInfoEntity();
	                fileEntity.setStudentId(studentId);
	                fileEntity.setRequestId(requestId.intValue());
	                fileEntity.setFilename(fileName);
	                fileEntity.setDescription(description);
	                fileEntity.setActiveFlag(ModelConstants.STATUS_ACTIVE);
	                studentFilesInfoEntityList.add(fileEntity);

	                successfullySavedFileNames.add(fileName);
	            }
	        }

	        if (!studentFilesInfoEntityList.isEmpty()) {
	            studentFilesInfoRepository.saveAll(studentFilesInfoEntityList);

	            for (int i = 0; i < successfullySavedFileNames.size(); i++) {
	                try {
	                    MultipartFile file = files[i];
	                    byte[] fileData = file.getBytes();
						fileService.encodeFile(SimsConfigDataService.HOSTEL_ACCOMMODATION, fileData,
								successfullySavedFileNames.get(i));
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }
	}

	@Transactional
	public String saveCancelRequest(StudentAppointmentRequestDto dto) throws Exception {
		String studentId = SecurityCtxUtil.userId().toUpperCase();
		Long requestId = MCrypt.getInstance().decryptToLong(dto.getRequestId());
		String cancelledStatusNotes = messageSource.getMessage(
				AppointmentStatusNote.SCHOLARS_STAY_EXTENSION_CANCEL_NOTE.getNoteKey(), null, Locale.getDefault());
		String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern(
				messageSource.getMessage(StudentConstants.DATE_FORMAT.getStudentConstant(), null, Locale.getDefault()),
				Locale.ENGLISH));
		String note = cancelledStatusNotes + ModelConstants.SPACE + formattedDate;

		if (dto.getStatus().equals(WorkflowStatus.CANCELLED.getStatus())) {
			studentAppointmentRequestRepository.updateCancelRequestForValidatingAccommodation(studentId,
					DateUtility.getNowTimeInstant(), dto.getStatus(), note, requestId);
			studentWorkflowRepository.updateCancelRequestForAccommodation(studentId, DateUtility.getNowTimeInstant(),
					dto.getStatus(), ModelConstants.STATUS_ACTIVE, requestId);
			return Constants.UPDATED;
		}

		if (dto.getStatus().equals(WorkflowStatus.CANCELLED_AFTER_APPROVED.getStatus()) && dto.getCancelDescription() != null) {
			studentAppointmentRequestRepository.updateCancelRequestForApprovedAccommodation(studentId,
					DateUtility.getNowTimeInstant(), dto.getStatus(), note, dto.getCancelDescription(), requestId);
			studentWorkflowRepository.updateCancelRequestForAccommodation(studentId, DateUtility.getNowTimeInstant(),
					dto.getStatus(), ModelConstants.STATUS_ACTIVE, requestId);
			return Constants.UPDATED;
		}
		return null;
	}

	public Resource generatePdf(String studentId, Long requestId) throws Exception {
		String tempFileLocation = pdfActiveService.getTempFileLocation();
        String fileName = ModelConstants.HOSTEL_ACCOMODATION + ModelConstants.UNDERSCORE + System.currentTimeMillis() + PdfActionService.PDF_EXTENSION;
        String outputFilePath = tempFileLocation + fileName;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);

        StudentDetailsPdfDto dto = studentAppointmentRequestService.getStudentDetailsPdf(requestId);
        List<StudentWorkflowDto> workflowDetails = studentAppointmentRequestService.getByStudentIdAndRequestIdAndActiveFlag(requestId, studentId, WorkflowStatus.DEFAULT.getStatus());

        try (PdfWriter writer = new PdfWriter(outputFilePath);
             PdfDocument pdfDocument = new PdfDocument(writer);
             Document document = new Document(pdfDocument)) {
        	pdfActiveService.addDocumentHeader(document, messageSource.getMessage("message.label.heading", null, Locale.getDefault()));

            document.add(new Paragraph(messageSource.getMessage("message.label.appointment.request.details", null, Locale.getDefault()))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setMarginTop(-10)
                    .setFontSize(14));


            document.add(new Paragraph(PdfActionService.NEXT_LINE));

            // Add Personal Details Section
            document.add(pdfActiveService.addFullWidthTitle(messageSource.getMessage("message.label.personal.details", null, Locale.getDefault()), TextAlignment.LEFT));
            Table personalDetailsTable = new Table(new float[]{2, 3, 2, 3});
            personalDetailsTable.setWidth((PdfActionService.VALUE_100_P));
            
            String studentNameMessage = messageSource.getMessage("message.label.student.id.pdf", null,
					Locale.getDefault());
			pdfActiveService.addTableTextValue(studentNameMessage,
					dto.getStudentId() != null ? dto.getStudentId() : null, personalDetailsTable, new int[] { 1, 3 });

			String fullName = messageSource.getMessage("message.label.student.name.pdf", null, Locale.getDefault());
			pdfActiveService.addTableTextValue(fullName, dto.getStudentName() != null ? dto.getStudentName() : null,
					personalDetailsTable, new int[] { 1, 3 });

			String dob = messageSource.getMessage("message.label.dob.pdf", null, Locale.getDefault());
			String dobFrom = dto.getDob() != null ? dto.getDob().format(dateFormatter) : null;
			pdfActiveService.addTableTextValue(dob, dobFrom, personalDetailsTable);

			String gender = messageSource.getMessage("message.label.gender.pdf", null, Locale.getDefault());
			String genderVal = dto.getGender() != null
					? (dto.getGender().equals(Constants.MALE) ? Constants.MALE_FULL_FORM : Constants.FEMALE_FULL_FORM)
					: null;
			pdfActiveService.addTableTextValue(gender, genderVal, personalDetailsTable);

			String phone = messageSource.getMessage("message.label.phone.pdf", null, Locale.getDefault());
			pdfActiveService.addTableTextValue(phone, dto.getStudentMobile() != null ? dto.getStudentMobile() : null,
					personalDetailsTable);

			String email = messageSource.getMessage("message.label.email.pdf", null, Locale.getDefault());
			pdfActiveService.addTableTextValue(email,
					dto.getStudentPersonalEmail() != null ? dto.getStudentPersonalEmail() : null, personalDetailsTable);

			String address = messageSource.getMessage("message.label.pdf.address", null, Locale.getDefault());
			pdfActiveService.addTableTextValue(address,
					dto.getStudentAddress() != null ? dto.getStudentAddress() : null, personalDetailsTable,
					new int[] { 1, 3});
			
            document.add(personalDetailsTable);
            document.add(new LineSeparator(new SolidLine()).setMarginTop(5F).setMarginBottom(5F));

            // Add Appointment Details Section
            document.add(pdfActiveService.addFullWidthTitle(messageSource.getMessage("message.label.appointment.details", null, Locale.getDefault()), TextAlignment.LEFT));
            Table appointmentDetailsTable = new Table(new float[]{2, 3, 2, 3});
            appointmentDetailsTable.setWidth(PdfActionService.VALUE_100_P);
            String appointmentFrom = messageSource.getMessage("message.label.appointment.from.date.colon", null, Locale.getDefault());
            String formattedAppointmentFrom = dto.getAppointmentFrom().format(dateFormatter);
            pdfActiveService.addTableTextValue(appointmentFrom, formattedAppointmentFrom, appointmentDetailsTable);
            String appointmentTo = messageSource.getMessage("message.label.appointment.to.date.colon", null, Locale.getDefault());
            String formattedAppointmentTo = dto.getAppointmentTo().format(dateFormatter);
            pdfActiveService.addTableTextValue(appointmentTo, formattedAppointmentTo, appointmentDetailsTable);
            String stayRequestFrom = messageSource.getMessage("message.label.stay.request.from.pdf", null, Locale.getDefault());
            String requestFrom = dto.getStayFrom().format(dateFormatter);
            pdfActiveService.addTableTextValue(stayRequestFrom, requestFrom, appointmentDetailsTable);
            String stayRequestTo = messageSource.getMessage("message.label.stay.request.to.pdf", null, Locale.getDefault());
            String requestTo = dto.getStayTo().format(dateFormatter);
            pdfActiveService.addTableTextValue(stayRequestTo, requestTo, appointmentDetailsTable);
            String stipend = messageSource.getMessage("message.label.stipend.pdf", null, Locale.getDefault());
            String grossPayWithCurrency = PdfActionService.RUPEES + (dto.getGrossPay() != null ? (int) Math.floor(dto.getGrossPay()) : "0");
            pdfActiveService.addTableTextValue(stipend, grossPayWithCurrency, appointmentDetailsTable);

			pdfActiveService.addTableTextValue(
					messageSource.getMessage("message.label.mess.option.pdf", null, Locale.getDefault()),
					PdfActionService.Y.equalsIgnoreCase(dto.getDining()) ? PdfActionService.YES : PdfActionService.NO, appointmentDetailsTable);
            String validatingAuthority = messageSource.getMessage("message.label.authority.name.pdf", null, Locale.getDefault());
            pdfActiveService.addTableTextValue(validatingAuthority, dto.getValidatingAuthority(), appointmentDetailsTable);
            String emailAddress = messageSource.getMessage("message.label.email.pdf", null, Locale.getDefault());
            pdfActiveService.addTableTextValue(emailAddress, dto.getValidatingAuthorityEmail(), appointmentDetailsTable);
            String category = messageSource.getMessage("message.label.category.pdf", null, Locale.getDefault());
            pdfActiveService. addTableTextValue(category, dto.getCategory(), appointmentDetailsTable);
            String purpose = messageSource.getMessage("message.label.purpose.pdf", null, Locale.getDefault());
            pdfActiveService. addTableTextValue(purpose, dto.getPurpose(), appointmentDetailsTable,new int[]{1, 3});
            document.add(appointmentDetailsTable);

            document.add(new LineSeparator(new SolidLine()).setMarginTop(5F).setMarginBottom(5F));
			document.add(new AreaBreak());

			if (workflowDetails != null && !workflowDetails.isEmpty()) {
				document.add(pdfActiveService.addFullWidthTitle(
						messageSource.getMessage("message.label.approval.details", null, Locale.getDefault()),
						TextAlignment.LEFT));
				for (StudentWorkflowDto workflow : workflowDetails) {
					Table approvalStatusTable = new Table(new float[] { 1, 3 });
					String validatorInfo = workflow.getValidatorName();
					String emailInfo = "(" + workflow.getValidatorEmail() + ") ";
					String statusInfo = workflow.getStatus();
					String formattedText = validatorInfo + Constants.HYPHEN + emailInfo + Constants.HYPHEN + statusInfo;
					pdfActiveService.addTableTextValue1(formattedText, ModelConstants.EMPTY_STRING, approvalStatusTable, new int[] { 4, 0 });
					document.add(approvalStatusTable);
				}
				document.add(new LineSeparator(new SolidLine()).setMarginTop(5F).setMarginBottom(5F));
			}

            // Add Approval notes Section
            document.add(pdfActiveService.addFullWidthTitle(messageSource.getMessage("message.label.approval.notes.pdf", null, Locale.getDefault()), TextAlignment.LEFT));
            Table approvalNotesTable = new Table(new float[]{1,3});
            approvalNotesTable.setWidth((PdfActionService.VALUE_100_P));
            String occupancy = messageSource.getMessage("message.label.recommended.occupancy.pdf", null, Locale.getDefault());
            pdfActiveService.addTableTextValue(occupancy, dto.getOccupancy(), approvalNotesTable);
            String notes = messageSource.getMessage("message.label.approval.notes.pdf", null, Locale.getDefault());
            pdfActiveService. addTableTextValue(notes, dto.getApprovalNotes(), approvalNotesTable);
            document.add(approvalNotesTable);

			
			document.add(new LineSeparator(new SolidLine()).setMarginTop(5F).setMarginBottom(5F));

            // Add Footer
            String authorizedSignatory = messageSource.getMessage("message.label.authorized.signatory", null, Locale.getDefault());
            String ccwOffice = messageSource.getMessage("message.label.ccw.office", null, Locale.getDefault());

            document.add(new Paragraph(PdfActionService.NEXT_LINE.repeat(1) + authorizedSignatory + PdfActionService.NEXT_LINE + ccwOffice)
                    .setTextAlignment(TextAlignment.RIGHT));

			pdfActiveService.addWatermarkImage(pdfDocument);
			document.close();
			return new FileSystemResource(outputFilePath);
		} catch (IOException e) {
            throw new Exception(messageSource.getMessage("message.label.error.generate.pdf", null, Locale.getDefault()), e);
        }
    }

	public ByteArrayResource downloadFile(String fileName){
		String uploadDoc = SimsConfigDataService.HOSTEL_ACCOMMODATION;
        byte[] fileData = null;
        try {
            fileData = fileService.getDecodedFile(uploadDoc, fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ByteArrayResource(fileData);
	}
}
