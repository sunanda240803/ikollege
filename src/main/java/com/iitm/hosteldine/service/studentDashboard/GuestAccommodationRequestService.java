package com.iitm.hosteldine.service.studentDashboard;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.StudentBioDataFamilyInfoDto;
import com.iitm.hosteldine.dto.hostel.GuestHostelAllotmentDTO;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.student.*;
import com.iitm.hosteldine.dto.studentDashboard.GuestAccommodationGuestDetailsDto;
import com.iitm.hosteldine.dto.studentDashboard.GuestAccommodationRequestDto;
import com.iitm.hosteldine.dto.studentDashboard.ValidatorForm;
import com.iitm.hosteldine.dto.warden.WardenInfoDto;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.form.student.StudentRoomRequestForm;
import com.iitm.hosteldine.mapper.studentDashboard.GuestAccommodationRequestMapper;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.model.studentDashboard.GuestAccommodationRequestEntity;
import com.iitm.hosteldine.model.warden.GuestAccommodationChargesEntity;
import com.iitm.hosteldine.model.warden.GuestRoomAllotmentInfoEntity;
import com.iitm.hosteldine.model.warden.WardenInfoEntity;
import com.iitm.hosteldine.repository.UserManagementRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomAllotmentRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomInfoRepository;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.repository.studentDashboard.GuestAccommodationRequestRepository;
import com.iitm.hosteldine.repository.studentDashboard.GuestRoomAllotmentInfoEntityRepository;
import com.iitm.hosteldine.service.*;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.hostel.HostelRoomAllotmentService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.service.warden.GuestAccommodationChargesService;
import com.iitm.hosteldine.service.warden.WardenInfoService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.styledxmlparser.jsoup.UncheckedIOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class GuestAccommodationRequestService {
	private final GuestAccommodationRequestRepository guestAccommodationRequestRepository;
	private final GuestAccommodationChargesService guestAccommodationChargesService;
	private final GuestAccommodationGuestDetailsService GuestAccommodationGuestDetailsService;
	private final GuestFilesInformationService guestFileInformationService;
	private final HostelRoomAllotmentService hostelRoomAllotmentService;
	private final MailTemplateRepository mailTemplateRepository;
	private final MailQueueService mailQueueService;
	private final StudentDetailsInfoService studentDetailsInfoService;
	private final MessageSource messageSource;
	private final PdfActionService pdfActionService;
	private final SimsConfigDataService simsConfigDataService;
	private final GuestAccommodationGuestDetailsService guestAccommodationGuestDetailsService;
	private final WardenInfoService wardenInfoService;
	private final StudentBioDataFamilyInfoService studentBioDataFamilyInfoService;
	private final StudentDetailsInfoRepository studentDetailsInfoRepository;
	private final UserManagementRepository userManagementRepository;
	private final ExcelUtility excelUtility;
	private final GuestRoomAllotmentInfoEntityRepository guestRoomAllotmentInfoEntityRepository;
	private final HostelRoomAllotmentRepository hostelRoomAllotmentRepository;
	private final HostelRoomInfoRepository hostelRoomInfoRepository;
	private final AllStudentsDetailsViewService allStudentsDetailsViewService;
	private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;
	private final CommonResponseUtil commonResponseUtil;

	@Value("${url.guest.student.view}")
	private String guestAccommodationRequestViewAPI;

	@Value("${url.public.api}")
	private String publicApiUrl;

	public Page<GuestAccommodationRequestDto> getGuestAccommodationRequestList(PaginationForm form) throws Exception {
		Page<GuestAccommodationRequestEntity> result = null;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(),Sort.by("a.created_at").descending());
		List<GuestAccommodationRequestDto> requestDto = new ArrayList<>();
		List<Object[]> result1 = guestAccommodationRequestRepository.getAccommodationRequestList(
				ModelConstants.STATUS_ACTIVE, pageable, SecurityCtxUtil.userId().toUpperCase());
		long totalRecords = guestAccommodationRequestRepository.getAccommodationRequestCount(ModelConstants.STATUS_ACTIVE, SecurityCtxUtil.userId().toUpperCase());
		for (Object[] row : result1) {
			if (row != null) {
				GuestAccommodationRequestDto dto = new GuestAccommodationRequestDto();
				String studentId = row[1] != null ? (String) row[1] : null;
				Long parentRequestId = row[2] != null ? (Long) row[2] : null;
				Long requestId = row[3] != null ? (Long) row[3] : null;
				Date createdAt = row[4] != null ? (java.sql.Date) row[4] : null;

				if (studentId != null && parentRequestId != null ) {
					String encryptedString = studentId + Constants.BACKTICK + requestId +  Constants.BACKTICK + parentRequestId + Constants.BACKTICK + createdAt;
					dto.setEncryptedRequestId(MCrypt.getInstance().encryptToText(encryptedString));
				}
				dto.setStudentId(row[1] != null ? (String) row[1] : null);
				dto.setParentRequestId(row[2] != null ? (Long) row[2] : null);
				dto.setCreatedFrom(row[4] != null ? ((java.sql.Date) row[4]).toLocalDate() : null);
				dto.setFromDate(row[5] != null ? ((java.sql.Date) row[5]).toLocalDate() : null);
				dto.setToDate(row[6] != null ? ((java.sql.Date) row[6]).toLocalDate() : null);
				dto.setStayTo(row[7] != null ? ((java.sql.Date) row[7]).toLocalDate() : null);
				dto.setWardenApprovalStatus(row[8] != null ? (String) row[8] : null);
				dto.setPaymentStatus(row[9] != null ? (String) row[9] : null);
				requestDto.add(dto);
			}
		}
		return new PageImpl<>(requestDto, pageable, totalRecords);
	}

	// View code
	public GuestAccommodationRequestDto getGuestAccommodationRequestById(Long id) throws Exception {
		GuestAccommodationRequestDto dto = new GuestAccommodationRequestDto();
		List<Object[]> result = guestAccommodationRequestRepository.getAccommodationRequestDetailsByRequestId(id,
				SecurityCtxUtil.userId().toUpperCase(), ModelConstants.STATUS_ACTIVE);
		List<GuestAccommodationRequestDto> extensionList = new ArrayList<>();
		for (Object[] row : result) {
			if (row != null) {
				if (row[0] instanceof GuestAccommodationRequestEntity) {
					GuestAccommodationRequestEntity requestEntity = (GuestAccommodationRequestEntity) row[0];

					GuestAccommodationRequestDto guestDto = Optional.ofNullable(requestEntity)
							.map(GuestAccommodationRequestMapper.INSTANCE::fromGuestAccommodationRequestEntity)
							.orElse(new GuestAccommodationRequestDto());
					if(guestDto.getParentRequestId() == 0) {
						guestDto.setParentRequestId(guestDto.getId());
					}else {
						guestDto.setParentRequestId(guestDto.getParentRequestId());
					}
					LocalDateTime createdAt = requestEntity.getCreatedAt();
					String studentId = requestEntity.getStudentDetailsInfo().getStudentId() != null ? requestEntity.getStudentDetailsInfo().getStudentId() : null;
					String encryptedString = studentId + Constants.BACKTICK + guestDto.getParentRequestId() +  Constants.BACKTICK +  createdAt;
					guestDto.setEncryptedRequestId(MCrypt.getInstance().encryptToText(encryptedString));
					List<GuestAccommodationGuestDetailsDto> guestList = guestAccommodationGuestDetailsService.getGuestList(guestDto.getId()) ;
					guestDto.setGuestList(guestList);
					if(guestDto.getMailSentTo() != null) {
						List<WardenInfoDto> wardenDetails = wardenInfoService.getWardenDetails(guestDto.getMailSentTo());
						guestDto.setApprovalList(wardenDetails);
					}
					extensionList.add(guestDto);
					guestDto.setStayExtensionList(extensionList);
					dto = guestDto;
				}
			}

		}
		return dto;
	}

	public String isWardenInfoPresent(String studentId) {
		AllStudentsDetailsViewDto studentDetails = allStudentsDetailsViewService.getCompleteStudentDetails(studentId);
		List<Object[]> wardenDetails = guestAccommodationRequestRepository
				.getWardenDetails(ModelConstants.STATUS_ACTIVE, studentDetails.getHostelId());
		return wardenDetails.isEmpty()
				? messageSource.getMessage("message.label.warden.details.not.present", null, Locale.getDefault())
				: null;
	}

	@Transactional
	public String saveGuestAccommodationRequest(GuestAccommodationRequestDto dto, HttpServletRequest request) throws Exception {
		StudentDetailsInfoEntity studentDetailsInfoEntity = new StudentDetailsInfoEntity();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
		String studentId = Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase();
		long noOfDays = 0;

		AllStudentsDetailsViewDto studentDetails = allStudentsDetailsViewService.getCompleteStudentDetails(studentId);
		List<Object[]> wardenDetails = guestAccommodationRequestRepository.getWardenDetails(ModelConstants.STATUS_ACTIVE, studentDetails.getHostelId());
//		if (!wardenDetails.isEmpty()) {
			if(dto.getEncryptedRequestId() != null) {
				String decodedReference = MCrypt.getInstance().decryptToString(dto.getEncryptedRequestId());
				String[] parts = decodedReference.split(Constants.BACKTICK);
				Long parentId = parts[1] != null && !parts[1].isEmpty() ? Long.parseLong(parts[1]) : 0l;
				dto.setParentRequestId(parentId);
				Optional<GuestAccommodationRequestEntity> optionalEntity = guestAccommodationRequestRepository
						.getLatestRequest(studentId, dto.getParentRequestId() , ModelConstants.STATUS_ACTIVE);
				if (optionalEntity.isPresent()) {
					GuestAccommodationRequestEntity entity = optionalEntity.get();
					// Splitting request start date
					String[] dateTimePart = dto.getStayToDate().split(Constants.TIME);
					LocalDate toDate = LocalDate.parse(dateTimePart[0]);

					noOfDays = calculateNoOfDays(entity.getToDate(), toDate, entity.getCheckoutTime(),dateTimePart[1]);

					if (((entity.getWardenApprovalStatus().equals(WorkflowStatus.WARDEN_APPROVAL_STATUS_COMPLETE.getStatus())
							|| entity.getWardenApprovalStatus().equals(WorkflowStatus.APPROVED.getStatus())
							|| entity.getWardenApprovalStatus().equals(WorkflowStatus.OVERRIDE_AND_APPROVED.getStatus()))
							&& entity.getPaymentStatus().equals(WorkflowStatus.PAID.getStatus()))

							|| ((entity.getWardenApprovalStatus().equals(WorkflowStatus.REJECTED.getStatus())
								|| entity.getWardenApprovalStatus().equals(WorkflowStatus.CANCELLED.getStatus()))
								&& entity.getParentRequestId() != 0)) {
						dto.setId(null);
						dto.setFromDate(entity.getToDate());
						dto.setCheckinTime(entity.getCheckoutTime());
						dto.setAccommodationType(entity.getAccommodationType());
						dto.setToDate(toDate);
						dto.setCheckoutTime(dateTimePart[1]);
					} else {
						throw new IllegalArgumentException(messageSource
								.getMessage("message.request.previous.request.not.approved", null, Locale.getDefault()));
					}
				}
			} else {
				dto.setFromDate(dto.getRequestStartDate());
				dto.setToDate(dto.getRequestEndDate());
				dto.setParentRequestId(0L);

				noOfDays=calculateNoOfDays(dto.getRequestStartDate(), dto.getRequestEndDate(),dto.getCheckinTime(),dto.getCheckoutTime());
			}

			dto.setNoOfDays((int) noOfDays);
			dto.setNoOfPersons(
					dto.getGuestList() != null ?
							(int) dto.getGuestList().stream().filter(GuestAccommodationGuestDetailsDto::isSelected).count() :
							0
			);

			dto.setCancelStatus(ModelConstants.STATUS_INACTIVE);
			dto.setWardenApprovalStatus(WorkflowStatus.VALIDATING.getStatus());
			dto.setPaymentStatus(WorkflowStatus.PENDING.getStatus());
			dto.setApplicableCharges(false);

			GuestAccommodationRequestEntity newEntity = GuestAccommodationRequestMapper.INSTANCE.onSaveEntity(dto);
			studentDetailsInfoEntity.setStudentId(studentId);
			newEntity.setStudentDetailsInfo(studentDetailsInfoEntity);
			newEntity.onCreate();
			GuestAccommodationRequestEntity savedEntity = guestAccommodationRequestRepository.save(newEntity);
		if (savedEntity != null) {
			GuestAccommodationChargesEntity guestAccommodationChargesEntity = guestAccommodationChargesService
					.getGuestAccommodationChargesDate(savedEntity.getFromDate(), savedEntity.getToDate(),
							ModelConstants.STATUS_ACTIVE);
			if (guestAccommodationChargesEntity != null) {
				if (dto.getAccommodationType() != null
						&& dto.getAccommodationType().equalsIgnoreCase(Constants.STAY_WITH_STUDENT)) {
					dto.setAmount(guestAccommodationChargesEntity.getAmount());
				} else if (dto.getAccommodationType() != null
						&& dto.getAccommodationType().equalsIgnoreCase(Constants.INDIVIDUAL_GUEST_ROOM)) {
					if (dto.getNoOfPersons() != 0 && dto.getNoOfPersons() == 1) {
						dto.setAmount(guestAccommodationChargesEntity.getIndividualRoomAmount());
					} else if (dto.getNoOfPersons() != 0 && dto.getNoOfPersons() > 1) {
						// Initialize gender counters
						int maleCount = 0;
						int femaleCount = 0;

						// Iterate over guest list and count genders
						for (GuestAccommodationGuestDetailsDto guest : dto.getGuestList()) {
							if(guest.isSelected()) {
								String gender = (guest.getRelationOfGuest().equalsIgnoreCase(Constants.FATHER) ||
										guest.getRelationOfGuest().equalsIgnoreCase(Constants.BROTHER) ||
										guest.getRelationOfGuest().equalsIgnoreCase(Constants.HUSBAND))
										? Constants.MALE_FULL_FORM : Constants.FEMALE_FULL_FORM;
								if (gender.equalsIgnoreCase(Constants.MALE_FULL_FORM)) {
									maleCount++;
								} else if (gender.equalsIgnoreCase(Constants.FEMALE_FULL_FORM)) {
									femaleCount++;
								}
							}
						}

						if (maleCount == 0 || femaleCount == 0) {
							// All guests are of the same gender, so we can use the regular rate
							dto.setAmount(guestAccommodationChargesEntity.getIndividualRoomMultipleAmount());
						} else {
							// Mixed genders, calculate amount based on count
							if(maleCount==1){
								dto.setAmount(guestAccommodationChargesEntity.getIndividualRoomAmount());
							}else{
								dto.setAmount(guestAccommodationChargesEntity.getIndividualRoomMultipleAmount());
							}
							if(femaleCount==1){
								dto.setSecondaryAmount(guestAccommodationChargesEntity.getIndividualRoomAmount());
							}else{
								dto.setSecondaryAmount(guestAccommodationChargesEntity.getIndividualRoomMultipleAmount());
							}
						}
					}
				}
				savedEntity.setAmount(dto.getAmount());
				savedEntity.setSecondaryAmount(dto.getSecondaryAmount());
				savedEntity = guestAccommodationRequestRepository.save(savedEntity);
			}
		}

		/**
         * Save Guest accomodation request guest details
         */
			if (dto.getGuestList() != null && !dto.getGuestList().isEmpty()) {
				List<GuestAccommodationGuestDetailsDto> selectedGuests = dto.getGuestList().stream()
						.filter(GuestAccommodationGuestDetailsDto::isSelected).collect(Collectors.toList());
				dto.getGuestList().stream()
						.filter(GuestAccommodationGuestDetailsDto::isSelected)
						.forEach(guest -> {
							if(Objects.nonNull(dto.getFamilyDetails()) && !dto.getFamilyDetails().isEmpty()) {
								dto.getFamilyDetails().stream()
										.filter(familyInfo -> familyInfo.getId() != null && familyInfo.getId().equals(guest.getGuestId()))
										.findFirst()
										.ifPresent(familyInfo -> familyInfo.setRelationType(guest.getRelationOfGuest()));
							}
						});
				if (!selectedGuests.isEmpty()) {
					int requestId = savedEntity.getId().intValue();
					boolean guestSave = GuestAccommodationGuestDetailsService.saveGuestDetails(selectedGuests, requestId);
				}
			}

			/**
			 * Save File upload
			 */

			if (savedEntity != null && savedEntity.getId() != 0) {
				Long requestId = savedEntity.getId();
	//			boolean fileUploadStatus = guestFileInformationService.saveGuestFileUpload(dto, requestId);
				boolean fileUpload = studentBioDataFamilyInfoService.saveBioDataFile(dto.getFamilyDetails(),
						Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase());
			}
			GuestAccommodationRequestDto guestRequestDto = new GuestAccommodationRequestDto();
			List<ValidatorForm> validatorList = new ArrayList<>();
			List<String> ids = new ArrayList<>();

			if (!wardenDetails.isEmpty()) {
				for (Object[] details : wardenDetails) {
					ValidatorForm wardenForm = new ValidatorForm();
					ValidatorForm inchargeForm = new ValidatorForm();
					wardenForm.setHostelName((String) details[0]);
					wardenForm.setWardenId((Long) details[1]);
					wardenForm.setWardenName((String) details[2]);
					wardenForm.setWardenEmail((String) details[3]);
					wardenForm.setAlternateEmail((String) details[4]);
					inchargeForm.setInchargeId((Long) details[5]);
					inchargeForm.setInchargeName((String) details[6]);
					inchargeForm.setInchargeEmail((String) details[7]);
					inchargeForm.setInchargAlternateEmail((String) details[8]);
					validatorList.add(wardenForm);
					validatorList.add(inchargeForm);
					ids.add(String.valueOf((Long) details[1]));
					if(details[5]!=null){
						ids.add(String.valueOf((Long) details[5]));
					}

				}
	
				guestRequestDto.setValidatorList(validatorList);
				String wardennId = String.join(Constants.ARRAY_SEPARATOR, ids);
				savedEntity.setMailSentTo(wardennId);
				savedEntity = guestAccommodationRequestRepository.save(savedEntity);
			}

//			if (!validatorList.isEmpty()) {
//				List<String> ids = new ArrayList<>();
//				List<String> validatorName = new ArrayList<>();
//				List<String> validatorEmail = new ArrayList<>();
//				for (ValidatorForm form : validatorList) {
//					if (form.getWardenId() != null) {
//						ids.add(String.valueOf(form.getWardenId()));
//						validatorName.add(form.getWardenName());
//						validatorEmail.add(form.getWardenEmail());
//					} else if (form.getInchargeId() != null) {
//						ids.add(String.valueOf(form.getInchargeId()));
//						validatorName.add(form.getInchargeName());
//						validatorEmail.add(form.getInchargeEmail() != null && !form.getInchargeEmail().isEmpty()
//								? form.getInchargeEmail() : form.getInchargAlternateEmail());
//					}
//				}
//				String wardennId = String.join(Constants.ARRAY_SEPARATOR, ids);
//				String wardennName = String.join(Constants.ARRAY_SEPARATOR, validatorName);
//				String wardennEmail = String.join(Constants.ARRAY_SEPARATOR, validatorEmail);
//
//				savedEntity.setMailSentTo(wardennId);
//				savedEntity.setWardenName(wardennName);
//				savedEntity.setWardenEmail(wardennEmail);
//
//				savedEntity = guestAccommodationRequestRepository.save(savedEntity);
//			}

			Optional<MailTemplateEntity> templateOpt;
			if (savedEntity.getParentRequestId() != 0) {
				templateOpt = mailTemplateRepository.findByMailType(MailTemplateEntity.GUEST_ACCOMMODATION_STAY_REQUEST);
			} else {
				templateOpt = mailTemplateRepository.findByMailType(MailTemplateEntity.GUEST_ACCOMMODATION_REQUEST);
			}
			if (templateOpt.isPresent()) {
				MailTemplateEntity template = templateOpt.get();
				String subject = template.getMailSubject();
				String content = template.getMailTemplate();
				if (!validatorList.isEmpty()) {
					var validator = validatorList.getFirst();
					String wardenEmail = validator != null
							&& validator.getWardenEmail() != null ? validator.getWardenEmail()
							: ModelConstants.EMPTY_STRING;

					String inchargeEmail = validator != null
							&& validator.getAlternateEmail() != null ? validator.getAlternateEmail()
							: ModelConstants.EMPTY_STRING;

					Long wardenId = validator != null
							&& validator.getWardenId() != null ? validator.getWardenId() : 0;

					Long inchargeId = validator != null
							&& validator.getInchargeId() != null ? validator.getInchargeId() : 0;

					// Create a small list to iterate over (warden + incharge)
					List<Map<String, Object>> recipients = List.of(
							Map.of("role", Constants.USER_ROLE_WARDEN, "id", wardenId, "email", wardenEmail),
							Map.of("role", Constants.INCHARGE, "id",inchargeId, "email", inchargeEmail)
					);

					for (Map<String, Object> recipient : recipients) {
						String role = (String) recipient.get("role");
						String email = (String) recipient.get("email");
						Long wardenIds = recipient.get("id") != null ? (Long) recipient.get("id") : 0;

						if (email == null || email.isEmpty()) {
							continue; // Skip if no email
						}

						String encryptedString = studentId + Constants.BACKTICK +
								savedEntity.getId() +  Constants.BACKTICK +  savedEntity.getCreatedAt() + Constants.BACKTICK + wardenIds
								+ Constants.BACKTICK + role + Constants.BACKTICK + savedEntity.getParentRequestId() + Constants.BACKTICK + savedEntity.getAccommodationType();

						encryptedString = MCrypt.getInstance().encryptToText(encryptedString);
						String finalViewUrl = getFinalViewUrl(request, guestAccommodationRequestViewAPI, encryptedString);

						content = content.replaceAll("#%student_id%#", studentId);
						content = content.replaceAll("#%student_name%#",
								studentDetails != null && studentDetails.getStudentName() != null ? studentDetails.getStudentName() : ModelConstants.HYPHEN);
						content = content.replaceAll("#%address%#",
								studentDetails != null && studentDetails.getStudentAddress() != null ? studentDetails.getStudentAddress() : ModelConstants.HYPHEN);
						content = content.replaceAll("#%mobile_number%#",
								studentDetails != null && Objects.nonNull(studentDetails.getStudentMobile())
										? studentDetails.getStudentMobile().toString() : ModelConstants.HYPHEN);
						content = content.replaceAll("#%email%#",
								studentDetails != null && studentDetails.getEmailId() != null ? studentDetails.getEmailId() : ModelConstants.HYPHEN);
						content = content.replaceAll("#%from_date%#",
								savedEntity != null ? formatter.format(savedEntity.getFromDate()) : ModelConstants.HYPHEN);
						content = content.replaceAll("#%to_date%#",
								savedEntity != null ? formatter.format(savedEntity.getToDate()) : ModelConstants.HYPHEN);
						content = content.replaceAll("#%accommodation_type%#",
								savedEntity != null ? savedEntity.getAccommodationType() : ModelConstants.HYPHEN);
						content = content.replaceAll("#%no_of_guest%#",
								savedEntity != null ? savedEntity.getNoOfPersons().toString() : ModelConstants.HYPHEN);
						content = content.replaceAll("#%no_of_days%#",
								savedEntity != null ? savedEntity.getNoOfDays().toString() : ModelConstants.HYPHEN);
						content = content.replaceAll("#%hostel_name%#",
								studentDetails != null && studentDetails.getHostelName() != null ? studentDetails.getHostelName() : ModelConstants.HYPHEN);
						content = content.replaceAll("#%room_no%#",
								studentDetails != null && studentDetails.getRoomNumber() != null ? studentDetails.getRoomNumber() : ModelConstants.HYPHEN);
						content = content.replaceAll("#%stay_todate%#", savedEntity.getParentRequestId()!=null && savedEntity.getParentRequestId()!= 0 ? formatter.format(savedEntity.getToDate()): ModelConstants.HYPHEN);
						content = content.replaceAll("#%view_string%#", finalViewUrl);
						boolean mailQueueStatus = mailQueueService.saveMailQueue(subject, commonResponseUtil.getMessage("message.mail.greetings.for"), content,
								email, Constants.GUEST_ACCOMMODATION_REQUEST, SecurityCtxUtil.userId(), 1, null, null, null, null);
					}
				}
			}
			return Constants.SAVED;
//		}
	}

	private static long calculateNoOfDays(LocalDate startDate, LocalDate endDate, String startTime, String endTime) {
		long noOfDays;
		LocalDateTime startDateTime = LocalDateTime.of(startDate, extractedLocalTime(startTime));
		LocalDateTime endDateTime   = LocalDateTime.of(endDate, extractedLocalTime(endTime));

		long hours = ChronoUnit.HOURS.between(startDateTime, endDateTime);
		noOfDays = (long) Math.ceil(hours / 24.0);
		return noOfDays;
	}

	private static LocalTime extractedLocalTime(String time) {
		String normalized = time.contains(":") ? time : time + ":00";
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(Constants.TIME_FORMAT);
		LocalTime parsedTime = LocalTime.parse(normalized, timeFormatter);
		return parsedTime;
	}


	public GuestAccommodationRequestDto getLatestRequest(String studentId) {
		return guestAccommodationRequestRepository.getLatestRequestByStudentId(studentId, ModelConstants.STATUS_ACTIVE)
				.map(GuestAccommodationRequestMapper.INSTANCE::fromGuestAccommodationRequestEntity)
				.orElse(new GuestAccommodationRequestDto());

	}

	public String cancelGuestAccommodationRequestById(Long requestId) throws Exception {
		String status = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
		Optional<GuestAccommodationRequestEntity> optionalEntity = guestAccommodationRequestRepository
				.cancelGuestAccommodationRequestById(requestId, ModelConstants.STATUS_ACTIVE,
						WorkflowStatus.VALIDATING.getStatus(), SecurityCtxUtil.userId().toUpperCase());
		if (optionalEntity.isPresent()) {
			GuestAccommodationRequestEntity entity = optionalEntity.get();
			entity.onUpdate();
			entity.setWardenApprovalStatus(WorkflowStatus.CANCELLED.getStatus());
			entity.setPaymentStatus(status);
			entity = guestAccommodationRequestRepository.save(entity);
			status = WorkflowStatus.CANCELLED.getStatus();

			StudentDetailsInfoDto studentDetailsInfo = studentDetailsInfoService
					.getStudentInfoDetails(SecurityCtxUtil.userId().toUpperCase());
			GuestAccommodationRequestDto hostelDetails = hostelRoomAllotmentService
					.getHostelDetailsByStudentId(SecurityCtxUtil.userId().toUpperCase());
			Long hostelId = hostelDetails != null && hostelDetails.getHostelId() != null ? hostelDetails.getHostelId() : 0;
			String hostelName = hostelDetails != null ? hostelDetails.getHostelName() : ModelConstants.HYPHEN;
			String roomNo = hostelDetails != null ? hostelDetails.getRoomNo() : ModelConstants.HYPHEN;

			List<Object[]> wardenDetails = guestAccommodationRequestRepository
					.getWardenDetails(ModelConstants.STATUS_ACTIVE, hostelId);
			GuestAccommodationRequestDto guestRequestDto = new GuestAccommodationRequestDto();
			List<ValidatorForm> validatorList = new ArrayList<>();

			if (!wardenDetails.isEmpty()) {
				for (Object[] details : wardenDetails) {
					ValidatorForm wardenForm = new ValidatorForm();
					ValidatorForm inchargeForm = new ValidatorForm();
					wardenForm.setHostelName((String) details[0]);
					wardenForm.setWardenId((Long) details[1]);
					wardenForm.setWardenName((String) details[2]);
					wardenForm.setWardenEmail((String) details[3]);
					wardenForm.setAlternateEmail((String) details[4]);
					inchargeForm.setInchargeId((Long) details[5]);
					inchargeForm.setInchargeName((String) details[6]);
					inchargeForm.setInchargeEmail((String) details[7]);
					inchargeForm.setInchargAlternateEmail((String) details[8]);
					validatorList.add(wardenForm);
					validatorList.add(inchargeForm);
				}
			}
			guestRequestDto.setValidatorList(validatorList);

			if (validatorList != null && !validatorList.isEmpty()) {
				List<String> ids = new ArrayList<>();
				for (ValidatorForm form : validatorList) {
					if (form.getWardenId() != null) {
						ids.add(String.valueOf(form.getWardenId()));
					}
					if (form.getInchargeId() != null) {
						ids.add(String.valueOf(form.getInchargeId()));
					}
				}
				String wardennId = String.join(Constants.ARRAY_SEPARATOR, ids);

				entity.setMailSentTo(wardennId);

				entity = guestAccommodationRequestRepository.save(entity);
			}

			Optional<MailTemplateEntity> templateOpt = mailTemplateRepository
					.findByMailType(MailTemplateEntity.GUEST_ACCOMMODATION_CANCEL_REQUEST);
			if (templateOpt.isPresent()) {
				MailTemplateEntity template = templateOpt.get();
				String subject = template.getMailSubject();
				String content = template.getMailTemplate();

				String wardenEmail = null,wardenAltEmail = null;
				if (validatorList != null && !validatorList.isEmpty()) {
					wardenEmail = validatorList != null && validatorList.getFirst() != null
							&& validatorList.getFirst().getWardenEmail() != null ? validatorList.getFirst().getWardenEmail()
							: ModelConstants.EMPTY_STRING;

					wardenAltEmail = validatorList != null && validatorList.getFirst() != null
							&& validatorList.getFirst().getAlternateEmail() != null ? validatorList.getFirst().getAlternateEmail()
							: ModelConstants.EMPTY_STRING;
				}

				String email = wardenEmail + Constants.ARRAY_SEPARATOR + wardenAltEmail;
				String studentLastName = Objects.nonNull(studentDetailsInfo.getLastName()) ? ModelConstants.SPACE + studentDetailsInfo.getLastName() : ModelConstants.EMPTY_STRING;
				content = content.replaceAll("#%created_at%#", entity.getCreatedAt().toString());
				content = content.replaceAll("#%student_id%#", SecurityCtxUtil.userId().toUpperCase());
				content = content.replaceAll("#%student_name%#", studentDetailsInfo.getFirstName() + studentLastName);
				content = content.replaceAll("#%email%#",
						studentDetailsInfo != null ? studentDetailsInfo.getEmailId() : ModelConstants.HYPHEN);
				content = content.replaceAll("#%from_date%#",
						entity != null ? formatter.format(entity.getFromDate()) : ModelConstants.HYPHEN);
				content = content.replaceAll("#%to_date%#",
						entity != null ? formatter.format(entity.getToDate()) : ModelConstants.HYPHEN);
				content = content.replaceAll("#%submitted_date%#", entity.getCreatedAt().toString());

				boolean mailQueueStatus = mailQueueService.saveMailQueue(subject, SecurityCtxUtil.userName(), content,
						email, Constants.GUEST_ACCOMMODATION_REQUEST, SecurityCtxUtil.userId().toUpperCase(), 1, null, null, null, null);

			}
		}
		return status;
	}

	public GuestAccommodationRequestDto getPreviousStayPeriods(Long requestId) {
		return guestAccommodationRequestRepository
				.getPreviousStayPeriods(SecurityCtxUtil.userId().toUpperCase(), requestId, WorkflowStatus.CANCELLED.getStatus(),
						WorkflowStatus.REJECTED.getStatus())
				.map(GuestAccommodationRequestMapper.INSTANCE::fromGuestAccommodationRequestEntity)
				.orElse(new GuestAccommodationRequestDto());

	}

	private GuestAccommodationRequestDto getGuestAccommodationRequestDetails(Long id) {
		GuestAccommodationRequestDto guestAccommodationRequestDto = guestAccommodationRequestRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
				.map(GuestAccommodationRequestMapper.INSTANCE::fromGuestAccommodationRequestEntity)
				.orElse(new GuestAccommodationRequestDto());
		StudentDetailsInfoEntity studentEntity = studentDetailsInfoRepository
				.findById(guestAccommodationRequestDto.getStudentDetailsInfo().getStudentId()).orElse(null);
		return guestAccommodationRequestRepository
				.getLatestRequest(Objects.requireNonNull(studentEntity).getStudentId(), id, ModelConstants.STATUS_ACTIVE)
				.map(GuestAccommodationRequestMapper.INSTANCE::fromGuestAccommodationRequestEntity)
				.orElse(new GuestAccommodationRequestDto());
	}

	public Resource generatePdf(Long requestID) throws Exception {
		String tempFileLocation = pdfActionService.getTempFileLocation();
		String studentId = Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase();
		String fileName = commonResponseUtil.getMessage("message.label.guest.accommodation.pdf.name") + ModelConstants.UNDERSCORE + System.currentTimeMillis() + PdfActionService.PDF_EXTENSION;
		String outputFilePath = tempFileLocation + fileName;
		GuestAccommodationRequestDto dto = guestAccommodationRequestRepository.findByIdAndActiveFlag(requestID, ModelConstants.STATUS_ACTIVE)
				.map(GuestAccommodationRequestMapper.INSTANCE::fromGuestAccommodationRequestEntity)
				.orElse(new GuestAccommodationRequestDto());
		List<Object[]> hostelDetails = guestAccommodationRequestRepository.findAllottedGuestRoomByRequestId(requestID.toString());

		if (CollectionUtils.isNotEmpty(hostelDetails)) {
			Object[] row = hostelDetails.getFirst();
			dto.setHostelName(row[0] != null ? row[0].toString() : null);
			dto.setRoomNo(row[1] != null ? row[1].toString() : null);
		}

		List<GuestAccommodationGuestDetailsDto> guestList = guestAccommodationGuestDetailsService.getGuestList(dto.getId()) ;
		dto.setGuestList(guestList);

		StudentDetailsWithHostelDTO studentDetailsWithHostelDTO  = new StudentDetailsWithHostelDTO();
		Optional<AllStudentsDetailsViewEntity> studentDetailOpt = allStudentsDetailsViewRepository.findBystudentId(dto.getStudentDetailsInfo().getStudentId());
		if(studentDetailOpt.isPresent()) {
			AllStudentsDetailsViewEntity studentDetail = studentDetailOpt.get();
			studentDetailsWithHostelDTO.setDob(DateUtility.formatDate(studentDetail.getDob()));
			studentDetailsWithHostelDTO.setHostelName(studentDetail.getHostelName());
			studentDetailsWithHostelDTO.setRoomNo(studentDetail.getRoomNumber());
		}
		dto.setStudentDetailsWithHostelDTO(studentDetailsWithHostelDTO);

		dto.initDateTimeFields();
		try (PdfWriter writer = new PdfWriter(outputFilePath);
			 PdfDocument pdfDocument = new PdfDocument(writer)) {
			pdfDocument.setDefaultPageSize(PageSize.A6);
			Document document = new Document(pdfDocument);
			setPdfDocumentHeader(document, messageSource.getMessage("message.label.hostel.copy.pdf",null,Locale.getDefault()));
			setPdfPersonalDetails(document, dto);
			pdfActionService.addPageBreaker(document);
			setPdfDocumentHeader(document, messageSource.getMessage("message.label.student.copy.pdf",null,Locale.getDefault()));
			setPdfPersonalDetails(document, dto);
			pdfActionService.addWatermarkImage(pdfDocument);
			document.close();
			return new FileSystemResource(outputFilePath);
		} catch (IOException e) {
			throw new Exception(messageSource.getMessage("message.label.error.generate.pdf", null, Locale.getDefault()), e);
		}
	}

	private void setPdfDocumentHeader(Document document, String copy) throws IOException {
		addDocumentHeader(document,
				messageSource.getMessage("message.label.heading", null, Locale.getDefault()));
		document.add(new Paragraph(copy)
				.setTextAlignment(TextAlignment.CENTER)
				.setBold()
				.setMarginBottom(0)
				.setFontSize(9)
				.setMarginLeft(20));
		document.add(new Paragraph(messageSource.getMessage("message.label.guest.accommodation.pdf.heading", null, Locale.getDefault()))
				.setTextAlignment(TextAlignment.CENTER)
				.setBold()
				.setFontSize(9)
				.setMarginLeft(10)
				.setMarginTop(0));
	}

	public void addDocumentHeader(Document document, String headerTitle) throws IOException {
		Optional.ofNullable(getClass().getClassLoader().getResourceAsStream(
						simsConfigDataService.getSimConfigValue(SimsConfigDataService.LOGO)))
				.ifPresent(imageStream -> {
					try (imageStream) {
						Image headerLogo = new Image(ImageDataFactory.create(imageStream.readAllBytes()))
								.setHeight(40)
								.setWidth(40)
								.setMarginRight(5)
								.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.LEFT);
						Table headerTable = new Table(new float[]{1, 3})
								.setWidth(UnitValue.createPercentValue(100))
								.addCell(new Cell().add(headerLogo).setBorder(Border.NO_BORDER))
								.addCell(new Cell().add(new Paragraph(headerTitle)
												.setTextAlignment(TextAlignment.CENTER)
												.setBold()
												.setFontSize(12))
										.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER)
										.setBorder(Border.NO_BORDER));
						document.add(headerTable);
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				});
	}

	private void setPdfPersonalDetails(Document document, GuestAccommodationRequestDto dto) throws IOException {
		String[] messageKeys = {
				"message.label.student.id.pdf", "message.label.std.name.pdf", "message.label.hostel.name.pdf", "message.label.guest.details.pdf",
				"message.label.allotted.hostel.pdf", "message.label.allotted.room.number.pdf", "message.label.number.of.days.pdf", "message.label.number.of.persons.pdf",
				"message.label.check.in.pdf", "message.label.check.out.pdf", "message.label.accommodation.type.pdf","message.label.payment.amount.pdf"
		};
		String[] messages = Arrays.stream(messageKeys)
				.map(key -> messageSource.getMessage(key, null, Locale.getDefault()))
				.toArray(String[]::new);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_TIME_FORMAT);
		String formattedCurrentDate = LocalDateTime.now().format(dateFormatter);
		document.add(new LineSeparator(new SolidLine()).setMarginTop(0).setMarginBottom(0));
		Table combinedTable = new Table(2)
				.setWidth(UnitValue.createPercentValue(100));
		combinedTable.addCell(new Cell()
				.add(new Paragraph()
						.add(new Text(messageSource.getMessage("message.label.sl.no.pdf", null, Locale.getDefault()) + ModelConstants.SPACE)
								.setBold())
						.add(new Text(dto.getId() + ModelConstants.EMPTY_STRING))
						.setFontSize(8))
				.setBorder(Border.NO_BORDER)
				.setTextAlignment(TextAlignment.LEFT));
		combinedTable.addCell(new Cell()
				.add(new Paragraph()
						.add(new Text(messageSource.getMessage("message.label.date", null, Locale.getDefault()) + ModelConstants.COLAN + ModelConstants.SPACE)
								.setBold())
						.add(new Text(formattedCurrentDate))
						.setFontSize(8))
				.setBorder(Border.NO_BORDER)
				.setTextAlignment(TextAlignment.RIGHT));
		document.add(combinedTable);
		document.add(new LineSeparator(new SolidLine()).setMarginTop(0).setMarginBottom(0));
		Table personalDetailsTable = createTwoColTable();
		String studentLastName = Objects.nonNull(dto.getStudentDetailsInfo().getLastName()) ? ModelConstants.SPACE + dto.getStudentDetailsInfo().getLastName() : ModelConstants.EMPTY_STRING;
		personalDetailsTable.setWidth(PdfActionService.VALUE_100_P);
		addTableRow(personalDetailsTable, messages[0], dto.getStudentDetailsInfo().getStudentId());
		addTableRow(personalDetailsTable, messages[1], dto.getStudentDetailsInfo().getFirstName() + ModelConstants.SPACE + studentLastName);
		addTableRow(personalDetailsTable, messages[2],  dto.getStudentDetailsWithHostelDTO().getHostelName() + " (Room No.: "+dto.getStudentDetailsWithHostelDTO().getRoomNo()+")");
		document.add(personalDetailsTable);
		String guestDetails = dto.getGuestList().stream()
				.map(guest -> guest.getGuestName() + ModelConstants.RIGHT_BRACKET + guest.getRelationOfGuest() + ModelConstants.LEFT_BRACKET)
				.collect(Collectors.joining(ModelConstants.COMMA+ModelConstants.SPACE));
		Table guestTable = createTwoColTable();
		guestTable.setWidth(PdfActionService.VALUE_100_P);
		guestTable.setKeepTogether(false);
		addTableRow(guestTable, messages[3], guestDetails);
		document.add(guestTable);
		Table personalDetailsTable2 = createTwoColTable();
		personalDetailsTable2.setWidth(PdfActionService.VALUE_100_P);
//		addTableRow(personalDetailsTable2, messages[3], guestDetails);
		addTableRow(personalDetailsTable2, messages[4], dto.getHostelName());
		addTableRow(personalDetailsTable2, messages[5], dto.getRoomNo());
		addTableRow(personalDetailsTable2, messages[6], dto.getNoOfDays().toString());
		addTableRow(personalDetailsTable2, messages[7], dto.getNoOfPersons().toString());
		addTableRow(personalDetailsTable2, messages[8], dto.getCheckInDateTime().format(dateFormatter));
		addTableRow(personalDetailsTable2, messages[9], dto.getCheckOutDateTime().format(dateFormatter));
		addTableRow(personalDetailsTable2, messages[10], dto.getAccommodationType());
		addTableRow(personalDetailsTable2, messages[11], dto.getPaymentAmount().toString());
		document.add(personalDetailsTable2);
		Table signatureTable = new Table(1)
				.setWidth(UnitValue.createPercentValue(100))
				.setFixedLayout()
				.addCell(new Cell()
						.add(new Paragraph(messageSource.getMessage("message.label.authority.sign.pdf", null, Locale.getDefault()))
								.setFontSize(10)
								.setBold())
						.setBorder(Border.NO_BORDER)
						.setPaddingBottom(0)
						.setTextAlignment(TextAlignment.RIGHT))
				.addCell(new Cell()
						.setBorder(Border.NO_BORDER)
						.setPaddingTop(-5)
						.setTextAlignment(TextAlignment.RIGHT)
						.add(pdfActionService.getAuthoritySignature()))
				.setKeepTogether(true);
		document.add(signatureTable);
	}

	private Table createTwoColTable() {
		return new Table(new float[]{3, 7}).setWidth(UnitValue.createPercentValue(100)).setFixedLayout();
	}

	private void addTableRow(Table table, String label, String value) {
		table.addCell(new Cell()
				.add(new Paragraph(label)
						.setFontSize(6)
						.setBold())
				.setTextAlignment(TextAlignment.LEFT)
				.setBorder(Border.NO_BORDER)
				.setPadding(2));

		table.addCell(new Cell()
				.add(new Paragraph(value != null ? value : ModelConstants.NOT_APPLICABLE)
						.setFontSize(6)
						.setMultipliedLeading(1.1f))
				.setTextAlignment(TextAlignment.LEFT)
				.setBorder(Border.NO_BORDER)
				.setPadding(2));
	}


	public List<StudentRoomDTO> getGuestRequestList(PaginationForm pageForm, StudentRoomRequestForm form,List<HostelMasterDto> hostelList) {
		String userRole = SecurityCtxUtil.userRole();
		String userName = SecurityCtxUtil.userName();
		int page = pageForm.getPage() - 1;
		Pageable pageable = PageRequest.of(page, pageForm.getSize());

		List<StudentRoomDTO> studentRoomDTOS = guestAccommodationRequestRepository.getStudentRoomDetails(
				form.getApprovalFromDate() != null ? form.getApprovalFromDate() : null,
				form.getApprovalToDate() != null ? form.getApprovalToDate() : null,
				form.getSubmittedFromDate() != null ? form.getSubmittedFromDate() : null,
				form.getSubmittedToDate() != null ? form.getSubmittedToDate() : null,
				form.getHostelId() != null ? form.getHostelId() : 0,
				form.getPaymentFromDate() != null ? form.getPaymentFromDate() : null,
				form.getPaymentToDate() != null ? form.getPaymentToDate() : null,
				form.getStudentFullName() != null ? form.getStudentFullName() : "",
				form.getStudentID() != null ? form.getStudentID() : "",
				form.getValidatorName() != null ? form.getValidatorName() : null,
				form.getValidatorEmail1() != null ? form.getValidatorEmail1() : null,
				form.getApprovalStatus() != null ? form.getApprovalStatus() : "",
				form.getPaymentApprovalStatus() != null ? form.getPaymentApprovalStatus() : "",
				form.getStayType() != null ? form.getStayType() : null,
				userRole != null ? userRole : null,
				userName != null ? userName : null
		);

		// ✅ Map gender-based hostel list & encrypt student details
		studentRoomDTOS.forEach(d -> {
			try {
				// Encrypt student details
				String studentDetailString = new MCrypt().encryptToText(
						d.getStudentId() + Constants.BACKTICK +
								d.getRequestId() + Constants.BACKTICK +
								d.getCreatedAt() + Constants.BACKTICK +
								d.getParentRequestId() + Constants.BACKTICK +
								d.getPaymentStatus() + Constants.BACKTICK +
								d.getWardenApprovalStatus() + Constants.BACKTICK
				);
				d.setStudentDetailString(studentDetailString);

				// Gender-based hostel filter
				String guestGender = d.getGuestGender();
				List<HostelMasterDto> genderBasedList;

				if (Constants.MALE.equalsIgnoreCase(guestGender) || Constants.MALE_FULL_FORM.equalsIgnoreCase(guestGender)) {
					genderBasedList = hostelList.stream()
							.filter(hostel -> hostel.getHostelGenderType() != null &&
									(hostel.getHostelGenderType().equalsIgnoreCase(Constants.MALE) ||
											hostel.getHostelGenderType().equalsIgnoreCase(Constants.MALE_FULL_FORM)))
							.toList();
				} else {
					genderBasedList = hostelList.stream()
							.filter(hostel -> hostel.getHostelGenderType() != null &&
									(hostel.getHostelGenderType().equalsIgnoreCase(Constants.FEMALE) ||
											hostel.getHostelGenderType().equalsIgnoreCase(Constants.FEMALE_FULL_FORM)))
							.toList();
				}
				// Set filtered list in DTO
				d.setHostelListGenderBased(genderBasedList);

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return studentRoomDTOS;
	}

	public StudentGuestAccomDTO getGuestAccomForm(StudentRoomDTO inputParam) {
		StudentGuestAccomDTO studentGuestAccomDTO = new StudentGuestAccomDTO();
		List<GuestAccommodationGuestDetailsDto> guestReqList = new ArrayList<>();
		List<FileInfoDTO> displayFileList = new ArrayList<>();
		int amount=0;
		int secondaryAmount=0;
		String wardenMailIds=null;
		studentGuestAccomDTO.setPaymentStatus(inputParam.getPaymentStatus());
		// Fetch Guest Accommodation Details
		List<Object[]> guestAccommodationDetails = guestAccommodationRequestRepository.findGuestAccommodationDetails(
				inputParam.getStudentId(), inputParam.getRequestId());

		if (!guestAccommodationDetails.isEmpty()) {
			Object[] row = guestAccommodationDetails.getFirst();
			// Set the fields for the DTO
			studentGuestAccomDTO.setRequestId(inputParam.getRequestId());

			DateTimeFormatter onlyDateFormatter = DateTimeFormatter.ofPattern(Constants.BACKEND_DATE_FORMAT);
// Safely cast each field, checking for nulls and ensuring correct types
			studentGuestAccomDTO.setCreatedAt(row[1] != null ? row[1].toString() : null);
			studentGuestAccomDTO.setFromDate(row[2] != null ? LocalDate.parse(row[2].toString(), onlyDateFormatter) : null);
			studentGuestAccomDTO.setToDate(row[3] != null ? LocalDate.parse(row[3].toString(), onlyDateFormatter) : null);
			studentGuestAccomDTO.setPurposeOfVisit(row[4] != null ? row[4].toString() : null);
			studentGuestAccomDTO.setNoOfDays(row[5] instanceof Integer ? (Integer) row[5] : null);
			studentGuestAccomDTO.setNoOfPersons(row[6] instanceof Integer ? (Integer) row[6] : null);
			studentGuestAccomDTO.setWardenEmail(row[7] != null ? row[7].toString() : null);
			studentGuestAccomDTO.setWardenName(row[8] != null ? row[8].toString() : null);
			studentGuestAccomDTO.setWardenApprovalStatus(row[9] != null ? row[9].toString() : null);
			studentGuestAccomDTO.setCancelStatus(row[10] != null ? row[10].toString() : null);
			studentGuestAccomDTO.setStudentId(row[11] != null ? row[11].toString() : null);
			studentGuestAccomDTO.setApprovalDate(row[12] != null ? row[12].toString() : null);
			studentGuestAccomDTO.setApplicableCharges(row[13] instanceof Boolean ? (Boolean) row[13] : null);
			studentGuestAccomDTO.setDocumentsUploaded(row[14] instanceof Boolean ? (Boolean) row[14] : null);
			studentGuestAccomDTO.setAccommodationType(row[15] != null ? row[15].toString() : null);
			studentGuestAccomDTO.setApprovalNotes(row[16] != null ? row[16].toString() : null);
			studentGuestAccomDTO.setRejectionDescription(row[17] != null ? row[17].toString() : null);
			studentGuestAccomDTO.setParentReqId(row[18] instanceof Integer ? (Integer) row[18] : null);
			studentGuestAccomDTO.setBloodRelationStatus(row[19] instanceof Boolean ? (Boolean) row[19] : null);
			studentGuestAccomDTO.setAllocationStatus(row[20] != null ? row[20].toString() : null);

// Handle check-in and check-out times, default to "-" if null or empty
			studentGuestAccomDTO.setCheckInTime(row[21] != null && !row[21].toString().isEmpty() ? row[21].toString() : "");
			studentGuestAccomDTO.setCheckOutTime(row[22] != null && !row[22].toString().isEmpty() ? row[22].toString() : "");

// Set the ArName
			studentGuestAccomDTO.setArName(row[23] != null ? row[23].toString() : null);

			studentGuestAccomDTO.setPaidAmount(row[24] instanceof Integer ? (Integer) row[24] : null);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.BACKEND_DATETIME_FORMAT);

			studentGuestAccomDTO.setPaymentDate(row[25] != null ? LocalDateTime.parse(row[25].toString(), formatter) : null);
			studentGuestAccomDTO.setPaymentReferenceNumber(row[26] != null ? row[26].toString() : null);
			 amount = (row[27] instanceof Integer ? (Integer) row[27] : 0);
			 secondaryAmount = (row[28] instanceof Integer ? (Integer) row[28] : 0);
			 wardenMailIds = (row[29] != null ? row[29].toString() : null);

		}

		//Warden Info
		if(wardenMailIds!=null){
			List<WardenInfoDto> wardenDetails = wardenInfoService.getWardenDetails(wardenMailIds);
			studentGuestAccomDTO.setApprovalList(wardenDetails);

//			WardenInfoEntity wardenInfoEntity = wardenInfoService.getWardenInfoById(inputParam.getWardenId());
//			if(wardenInfoEntity!=null) {
//				studentGuestAccomDTO.setWardenId(String.valueOf(wardenInfoEntity.getId()));
//				studentGuestAccomDTO.setWardenName(wardenInfoEntity.getWardenName());
//				studentGuestAccomDTO.setWardenEmail(wardenInfoEntity.getWardenEmail());
//			}
		}


		// Fetch Guest Details
		List<Object[]> guestDetails = guestAccommodationRequestRepository.findGuestDetails(inputParam.getRequestId());
		for (Object[] guestRow : guestDetails) {
			GuestAccommodationGuestDetailsDto guestDetailsDTO = new GuestAccommodationGuestDetailsDto();
			guestDetailsDTO.setGuestName(guestRow[0] != null ? guestRow[0].toString() : null);
			guestDetailsDTO.setRelationOfGuest(guestRow[1] != null ? guestRow[1].toString() : null);
			guestDetailsDTO.setGuestGender(guestRow[2] != null ? guestRow[2].toString() : null);
			guestDetailsDTO.setGuestOtherRelation(guestRow[3] != null ? guestRow[3].toString() : null);
			guestDetailsDTO.setAllotedHostelName(guestRow[4] != null ? guestRow[4].toString() : null);
			guestDetailsDTO.setAllotedRoomNo(guestRow[5] != null ? guestRow[5].toString() : null);
			guestReqList.add(guestDetailsDTO);
		}
		studentGuestAccomDTO.setGuestReqList(guestReqList);

		//Calculate the payment amount
		if (studentGuestAccomDTO.getAccommodationType() != null) {
			if (studentGuestAccomDTO.getAccommodationType() != null
					&& studentGuestAccomDTO.getAccommodationType().equalsIgnoreCase(Constants.STAY_WITH_STUDENT)) {
				studentGuestAccomDTO.setPaymentAmount(amount * studentGuestAccomDTO.getNoOfDays() * studentGuestAccomDTO.getNoOfPersons());
			} else if (studentGuestAccomDTO.getAccommodationType() != null
					&& studentGuestAccomDTO.getAccommodationType().equalsIgnoreCase(Constants.INDIVIDUAL_GUEST_ROOM)) {
				if (studentGuestAccomDTO.getNoOfPersons() != 0 && studentGuestAccomDTO.getNoOfPersons() == 1) {
					studentGuestAccomDTO.setPaymentAmount(amount * studentGuestAccomDTO.getNoOfDays() * studentGuestAccomDTO.getNoOfPersons());
				} else if (studentGuestAccomDTO.getNoOfPersons() != 0 && studentGuestAccomDTO.getNoOfPersons() > 1) {
					int maleCount = 0;
					int femaleCount = 0;

					// Determine gender based on the relation and count them
					for (GuestAccommodationGuestDetailsDto guest : guestReqList) {
						String gender = guest.getGuestGender();
						if (gender.equalsIgnoreCase(Constants.MALE_FULL_FORM)) {
							maleCount++;
						} else if (gender.equalsIgnoreCase(Constants.FEMALE_FULL_FORM)) {
							femaleCount++;
						}

						if (maleCount == 0 || femaleCount == 0) {
							studentGuestAccomDTO.setPaymentAmount(amount * studentGuestAccomDTO.getNoOfDays());
						} else {
							studentGuestAccomDTO.setPaymentAmount((amount + secondaryAmount) * studentGuestAccomDTO.getNoOfDays());
						}
					}
				}
			}
		}

		// Fetch File Information
        List<GuestAccommodationGuestDetailsDto> guestDtos = guestAccommodationGuestDetailsService.getGuestId(Long.valueOf(inputParam.getRequestId()));
        List<Long> ids = Objects.nonNull(guestDtos) && !guestDtos.isEmpty() ? guestDtos.stream().map(GuestAccommodationGuestDetailsDto::getGuestId).toList() : Collections.emptyList();
        List<StudentBioDataFamilyInfoDto> bioDataFamilyInfoDtoList = studentBioDataFamilyInfoService.getFileUploadDetails(ids, inputParam.getStudentId());
        bioDataFamilyInfoDtoList.forEach(studentFile -> {
            FileInfoDTO fileInfoDTO = new FileInfoDTO();
            fileInfoDTO.setRequestId(inputParam.getRequestId());
            fileInfoDTO.setUploadModifiedFileName(studentFile.getProofFileName());
            fileInfoDTO.setFileDescrption(studentFile.getProofFileName());
            displayFileList.add(fileInfoDTO);
        });

//        List<Object[]> fileInformation = guestAccommodationRequestRepository.findFileInformation(inputParam.getRequestId());
//        for (Object[] fileRow : fileInformation) {
//            FileInfoDTO fileInfoDTO = new FileInfoDTO();
//            fileInfoDTO.setFileId(fileRow[0] instanceof Integer ? (Integer) fileRow[0] : 0);
//            fileInfoDTO.setRequestId(fileRow[1] instanceof Integer ? (Integer) fileRow[1] : 0);
//            fileInfoDTO.setUploadModifiedFileName(fileRow[2] != null ? fileRow[2].toString() : null);
//            fileInfoDTO.setFileDescrption(fileRow[3] != null ? fileRow[3].toString() : null);
//
//            displayFileList.add(fileInfoDTO);
//        }

		studentGuestAccomDTO.setUploadFileList(displayFileList);
		studentGuestAccomDTO.setMinToDate(simsConfigDataService.getSimConfigValue(SimsConfigDataService.GUEST_MAILVIEW_MAX_TODATE));

		List<Object[]> result = studentDetailsInfoRepository.findStudentDetailsWithHostelInfo(studentGuestAccomDTO.getStudentId());
		StudentDetailsWithHostelDTO studentDetailsWithHostelDTO  = CollectionUtils.isNotEmpty(result) ? new StudentDetailsWithHostelDTO(result.getFirst()) : new StudentDetailsWithHostelDTO();
		Optional<AllStudentsDetailsViewEntity> studentDetailOpt = allStudentsDetailsViewRepository.findBystudentId(studentGuestAccomDTO.getStudentId());
		if(studentDetailOpt.isPresent()) {
			AllStudentsDetailsViewEntity studentDetail = studentDetailOpt.get();
			studentDetailsWithHostelDTO.setDob(DateUtility.formatDate(studentDetail.getDob()));
			studentDetailsWithHostelDTO.setHostelName(studentDetail.getHostelName());
			studentDetailsWithHostelDTO.setRoomNo(studentDetail.getRoomNumber());
		}
		studentGuestAccomDTO.setStudentInfo(studentDetailsWithHostelDTO);
		return studentGuestAccomDTO;
	}


	public boolean updateStudentRoomRequest(StudentGuestAccomDTO studentGuestAccomDTO) {

		try {
			Optional<GuestAccommodationRequestEntity> guestAccommodationRequestEntity =
					guestAccommodationRequestRepository.findByIdAndActiveFlag(Long.valueOf(studentGuestAccomDTO.getRequestId()), ModelConstants.STATUS_ACTIVE);
			if(guestAccommodationRequestEntity.isPresent()) {
				GuestAccommodationRequestEntity entity= guestAccommodationRequestEntity.get();
				entity.setPaymentType(studentGuestAccomDTO.getPaymentType());
				entity.setPaymentReferenceNo(studentGuestAccomDTO.getPaymentReferenceNumber());
				entity.setPaymentAmount(studentGuestAccomDTO.getPaymentAmount());
				entity.setPaymentDate(studentGuestAccomDTO.getPaymentDate());
				GuestAccommodationRequestEntity savedEntity = guestAccommodationRequestRepository.save(entity);
				if(savedEntity != null) {
					savedEntity.setPaymentStatus(Constants.PAYMENT_STATUS_PAID);
					GuestAccommodationRequestEntity saved = guestAccommodationRequestRepository.save(entity);
					if(saved != null) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return false;
	}

	public boolean updateWardenStatusOnRequest(OverrideApproveGuestAccomDTO requestDto, HttpServletRequest httpServletRequest) {
		try {
			String userRole = Constants.USER_ROLE_CCW_DEAN;
			String userName = Constants.USER_NAME_CCW;
			Optional<GuestAccommodationRequestEntity> guestAccommodationRequestEntityOpt =
					guestAccommodationRequestRepository.findByIdAndActiveFlag(Long.valueOf(requestDto.getRequestId()), ModelConstants.STATUS_ACTIVE);
			GuestAccommodationRequestEntity entity = guestAccommodationRequestEntityOpt.get();


			String wardenApprovalStatusBeforeUpdate = entity.getWardenApprovalStatus();
			if(userRole.equalsIgnoreCase(SecurityCtxUtil.userRole())){
				if(!StringUtils.equalsAnyIgnoreCase(wardenApprovalStatusBeforeUpdate,WorkflowStatus.VALIDATING.getStatus() ,
						WorkflowStatus.REJECTED.getStatus())) {
					return false;
				}
			}else{
				if(!StringUtils.equalsAnyIgnoreCase(wardenApprovalStatusBeforeUpdate,WorkflowStatus.VALIDATING.getStatus())) {
					return false;
				}
			}

			long noOfDays=calculateNoOfDays(requestDto.getFromDate(), requestDto.getToDate(),requestDto.getCheckInTime(),requestDto.getCheckOutTime());

			//Warden approval
			if((!StringUtils.equalsAnyIgnoreCase(SecurityCtxUtil.userRole(), Constants.USER_ROLE_AR, Constants.USER_ROLE_CCW_DEAN))
				&& requestDto.getStatus().equalsIgnoreCase(WorkflowStatus.WARDEN_APPROVAL_STATUS_COMPLETE.getStatus())) { //Warden approval
					//requestDto.setStatus(WorkflowStatus.WARDEN_APPROVAL_STATUS_COMPLETE.getStatus());
//					List<WardenInfoDto> wardenDetails = wardenInfoService.getWardenDetailsByEmail(requestDto.getWardenEmail());

					WardenInfoEntity wardenInfoEntity = wardenInfoService.getWardenInfoById(requestDto.getWardenId());
					if(wardenInfoEntity!=null) {
						entity.setWardenName(wardenInfoEntity.getWardenName());
						entity.setWardenEmail(wardenInfoEntity.getWardenEmail());
					}
			}
			entity.setWardenApprovalStatus(requestDto.getStatus());
			entity.setApprovalDate(DateUtility.getNowDate());
			entity.setFromDate(requestDto.getFromDate());
			entity.setToDate(requestDto.getToDate());
			entity.setCheckinTime(requestDto.getCheckInTime());
			entity.setCheckoutTime(requestDto.getCheckOutTime());
			entity.setNoOfDays(Math.toIntExact(noOfDays));

			if (requestDto.getStatus() != null && requestDto.getStatus().equalsIgnoreCase(WorkflowStatus.REJECTED.getStatus())) {
				entity.setRejectionDescription(requestDto.getRejectionDescription());
			} else {
				entity.setApprovalNotes(requestDto.getApprovalNotes());
			}

			GuestAccommodationRequestEntity studentGuestAccomVo = guestAccommodationRequestRepository.save(entity);

			//String allocationStatus = studentGuestAccomVo.getAllocationStatus();
			String paymentStatus = studentGuestAccomVo.getPaymentStatus();
			String userEmail = "";
			String userFullName = "";
			List<Object[]> userResultsSet = userManagementRepository.getUserEmailAndUserFullNameByUserName(userName);
			if(CollectionUtils.isNotEmpty(userResultsSet)) {
				Object[] userObj = userResultsSet.getFirst();
				userEmail = userObj.length > 0 && userObj[0] != null ? userObj[0].toString() : null;
				userFullName = userObj.length > 1 && userObj[1] != null ? userObj[1].toString() : null;
			}

			try {
//				if( ( (StringUtils.equalsIgnoreCase(userRole, Constants.USER_ROLE_CCW_DEAN))
//						&& (StringUtils.equalsIgnoreCase(wardenApprovalStatusBeforeUpdate,WorkflowStatus.VALIDATING.getStatus())) ) ||
//						( (StringUtils.equalsIgnoreCase(userRole, Constants.USER_ROLE_AR))
//								&&(StringUtils.equalsIgnoreCase(requestDto.getStatus(),WorkflowStatus.REJECTED.getStatus())) )
//				) {
					sendFinalEmailToStudentsOverride(studentGuestAccomVo, userEmail, userFullName, userRole, userName,
							requestDto, httpServletRequest);
//				}


//				if( StringUtils.equalsAnyIgnoreCase(studentGuestAccomVo.getWardenApprovalStatus(),WorkflowStatus.APPROVED.getStatus(),
//						WorkflowStatus.WARDEN_APPROVAL_STATUS_COMPLETE.getStatus(),WorkflowStatus.OVERRIDE_AND_APPROVED.getStatus())
//						&& (paymentStatus!=null && paymentStatus.equalsIgnoreCase(WorkflowStatus.PAID.getStatus())) ) {
//					sendEmailToWardenOnPaid(studentGuestAccomVo, userEmail, userFullName, userRole, userName,
//							requestDto, httpServletRequest);
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}


			if(studentGuestAccomVo != null) {
				return true;
			}


		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return false;
	}



	private void sendFinalEmailToStudentsOverride(GuestAccommodationRequestEntity studentGuestAccomVo, String userEmail,
												  String userFullName, String userRole,String userName, OverrideApproveGuestAccomDTO overrideApproveGuestAccomDTO,
												  HttpServletRequest request) throws Exception {
		String studentId = StringUtils.upperCase(overrideApproveGuestAccomDTO.getStudentId());
		String subject = "";
		String authority="";

		if(!(StringUtils.equalsAnyIgnoreCase(SecurityCtxUtil.userRole(), Constants.USER_ROLE_AR, Constants.USER_ROLE_CCW_DEAN))){
			authority=Constants.USER_ROLE_WARDEN;
		}else{
			authority=Constants.CCW;
		}

		Optional<MailTemplateEntity> templateOpt = Optional.empty();
		if( StringUtils.equalsAnyIgnoreCase(studentGuestAccomVo.getWardenApprovalStatus(),WorkflowStatus.APPROVED.getStatus(),
				WorkflowStatus.WARDEN_APPROVAL_STATUS_COMPLETE.getStatus(),WorkflowStatus.OVERRIDE_AND_APPROVED.getStatus()))
		{
			subject = messageSource
					.getMessage("message.mail.subject.accommodation.request.approved", null, Locale.getDefault());
			templateOpt = mailTemplateRepository.findByMailType(MailTemplateEntity.GUEST_ACCOMMODATION_REQUEST_APPROVED);

		}else if(studentGuestAccomVo.getWardenApprovalStatus().equalsIgnoreCase(WorkflowStatus.REJECTED.getStatus())){
			subject = messageSource
					.getMessage("message.mail.subject.accommodation.request.rejected", null, Locale.getDefault());
			templateOpt = mailTemplateRepository.findByMailType(MailTemplateEntity.GUEST_ACCOMMODATION_REQUEST_REJECTED);
		}

		if (templateOpt.isPresent()) {
			MailTemplateEntity template = templateOpt.get();
			String content = template.getMailTemplate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
			LocalDateTime createdAt = studentGuestAccomVo.getCreatedAt();
			content = content.replaceAll("#%created_at%#",formatter.format(createdAt));
			content = content.replaceAll("#%authority%#", authority!=null ? authority : Constants.CCW);
			content = content.replaceAll("#%reject_reason%#",studentGuestAccomVo.getRejectionDescription() != null ? studentGuestAccomVo.getRejectionDescription() : ModelConstants.HYPHEN);

			String emailTo = studentId+simsConfigDataService.getSimConfigValue(SimsConfigDataService.IIT_STUDENT_MAIL_ADD_DOMAIN);
			boolean mailQueueStatus = mailQueueService.saveMailQueue(subject,ModelConstants.STUDENT + " ("+studentId+")", content,
					emailTo, Constants.GUEST_ACCOMMODATION_REQUEST, SecurityCtxUtil.userId(), 1, null, null, null,  null);
		}

	}

	private String getFinalViewUrl(HttpServletRequest request,String controllerAPI, String encryptedString) {
//		String serverName = request.getServerName();
//		int serverPort = request.getServerPort();
//		String contextPath = request.getContextPath();
		String baseUrl = Utility.getDomainUrl(request);
		String publicApi = simsConfigDataService.getSimConfigValue(SimsConfigDataService.PUBLIC_API);
		publicApi = StringUtils.isNotEmpty(publicApi) ? publicApi : publicApiUrl;
		return baseUrl + publicApi + controllerAPI  + "/" + encryptedString;
	}


	private void sendEmailToWardenOnPaid(GuestAccommodationRequestEntity studentGuestAccomVo, String userEmail,
										 String userFullName, String userRole,String userName, OverrideApproveGuestAccomDTO overrideApproveGuestAccomDTO,
										 HttpServletRequest request) throws Exception {
		String studentId = overrideApproveGuestAccomDTO.getStudentId();
		String createdAt = "";
		String subject = "";


		Optional<MailTemplateEntity> templateOpt = Optional.empty();
		if(StringUtils.equalsAnyIgnoreCase(studentGuestAccomVo.getPaymentStatus(),WorkflowStatus.PAID.getStatus()))
		{
			subject = messageSource
					.getMessage("message.mail.subject.accommodation.request.paid", null, Locale.getDefault());
			templateOpt = mailTemplateRepository.findByMailType(MailTemplateEntity.GUEST_ACCOMMODATION_REQUEST_PAID);

		}
//		StudentDetailsInfoDto studentDetailsInfo = studentDetailsInfoService
//				.getStudentInfoDetails(studentId);
		AllStudentsDetailsViewEntity studentDetailsInfo = allStudentsDetailsViewRepository.findBystudentId(studentId)
				.orElse(null);
		GuestAccommodationRequestDto hostelDetails = hostelRoomAllotmentService
				.getHostelDetailsByStudentId(studentId);

		Long hostelId = hostelDetails != null && hostelDetails.getHostelId() != null ? hostelDetails.getHostelId() : 0;
		String hostelName = hostelDetails != null ? hostelDetails.getHostelName() : ModelConstants.HYPHEN;
		String roomNo = hostelDetails != null ? hostelDetails.getRoomNo() : ModelConstants.HYPHEN;

		String wardenEmail = null;
		if (studentDetailsInfo != null) {
			wardenEmail = studentDetailsInfo != null
					&& studentGuestAccomVo.getWardenEmail() != null ?studentGuestAccomVo.getWardenEmail()
					: ModelConstants.EMPTY_STRING;
		}

		String email = wardenEmail;


		String encryptedString = studentId + Constants.BACKTICK +
				studentGuestAccomVo.getId() +  Constants.BACKTICK +  createdAt + Constants.BACKTICK + studentGuestAccomVo.getMailSentTo()
				+ Constants.BACKTICK + email + Constants.BACKTICK + studentGuestAccomVo.getParentRequestId() + Constants.BACKTICK + studentGuestAccomVo.getAccommodationType();
		encryptedString = MCrypt.getInstance().encryptToText(encryptedString);

		String finalViewUrl = getFinalViewUrl(request, guestAccommodationRequestViewAPI, encryptedString);

		if (templateOpt.isPresent()) {
			MailTemplateEntity template = templateOpt.get();
			String content = template.getMailTemplate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.BACKEND_DATETIME_FORMAT);
			content = content.replaceAll("#%student_id%#", studentId);
			content = content.replaceAll("#%student_name%#", studentDetailsInfo.getStudentName());
			content = content.replaceAll("#%address%#",
					studentDetailsInfo != null ? studentDetailsInfo.getStudentAddress() : ModelConstants.HYPHEN);
			content = content.replaceAll("#%mobile_number%#",
					studentDetailsInfo != null ? studentDetailsInfo.getStudentMobile()+""
							: ModelConstants.HYPHEN);
			content = content.replaceAll("#%email%#",
					studentDetailsInfo != null ? studentDetailsInfo.getEmailId() : ModelConstants.HYPHEN);
			content = content.replaceAll("#%from_date%#",
					studentGuestAccomVo != null ? formatter.format(studentGuestAccomVo.getFromDate()) : ModelConstants.HYPHEN);
			content = content.replaceAll("#%to_date%#",
					studentGuestAccomVo != null ? formatter.format(studentGuestAccomVo.getToDate()) : ModelConstants.HYPHEN);
			content = content.replaceAll("#%accommodation_type%#",
					studentGuestAccomVo != null ? studentGuestAccomVo.getAccommodationType() : ModelConstants.HYPHEN);
			content = content.replaceAll("#%no_of_guest%#",
					studentGuestAccomVo != null ? studentGuestAccomVo.getNoOfPersons().toString() : ModelConstants.HYPHEN);
			content = content.replaceAll("#%no_of_days%#",
					studentGuestAccomVo != null ? studentGuestAccomVo.getNoOfDays().toString() : ModelConstants.HYPHEN);
			content = content.replaceAll("#%hostel_name%#", hostelName != null ? hostelName : ModelConstants.HYPHEN );
			content = content.replaceAll("#%room_no%#", roomNo != null ? roomNo : ModelConstants.HYPHEN);
			content = content.replaceAll("#%stay_todate%#", studentGuestAccomVo.getParentRequestId()!=null && studentGuestAccomVo.getParentRequestId()!= 0 ? formatter.format(studentGuestAccomVo.getToDate()): ModelConstants.HYPHEN);
			content = content.replaceAll("#%view_string%#", finalViewUrl);
			boolean mailQueueStatus = mailQueueService.saveMailQueue(subject, userName, content,
					email, Constants.GUEST_ACCOMMODATION_REQUEST, SecurityCtxUtil.userId(), 1, null, null, null,  null);
		}

	}


	public Workbook guestAllotmentReport(List<StudentRoomDTO> guestRequestList) throws Exception {
		String schoolName = " ";
		String address = " ";
		XSSFWorkbook workbook = null;
		int colCount = 0;
		ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

		try {
			workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(messageSource
					.getMessage("message.accommodation.request.export.sheet.name", null, Locale.getDefault()));

			// Create styles using ExcelUtility
			XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
			XSSFCellStyle centerAlignStyle = excelUtility.setCenterAlignStyle(workbook);
			XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);

			// Create header rows
			/*XSSFRow rowheadZero = sheet.createRow(0);
			rowheadZero.setHeightInPoints(45);

			// Create merged cell for school name and address
			excelUtility.createCell(rowheadZero, 0, schoolName + "\n" + address, headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 17));*/

			// Create second header row
			XSSFRow rowheadFirst = sheet.createRow(1);
			excelUtility.createCell(rowheadFirst, 0, messageSource
					.getMessage("message.accommodation.request.export.sheet.header", null, Locale.getDefault()), headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 17));

			// Create third header row for report date
			XSSFRow rowheadSecond = sheet.createRow(2);
			DateFormat dateFormat = new SimpleDateFormat(Constants.BACKEND_DATETIME_FORMAT_2);
			Date date = new Date();
			excelUtility.createCell(rowheadSecond, 0, messageSource
					.getMessage("message.label.report.date", null, Locale.getDefault()) + dateFormat.format(date), headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 17));

			// Create column headers
			XSSFRow rowhead = sheet.createRow(3);
			String[] headers = {
					messageSource.getMessage("message.label.sl.no.pdf", null, Locale.getDefault()),
					messageSource.getMessage("message.label.submitted.date", null, Locale.getDefault()),
					messageSource.getMessage("message.label.studentID", null, Locale.getDefault()),
					messageSource.getMessage("message.label.studentName", null, Locale.getDefault()),
					messageSource.getMessage("message.label.hostelName", null, Locale.getDefault()),
					messageSource.getMessage("message.label.from.date", null, Locale.getDefault()),
					messageSource.getMessage("message.label.to.date", null, Locale.getDefault()),
					messageSource.getMessage("message.label.approval.status", null, Locale.getDefault()),
					messageSource.getMessage("message.label.payment.status", null, Locale.getDefault()),
					messageSource.getMessage("message.label.allotted.hostel.name", null, Locale.getDefault()),
					messageSource.getMessage("message.label.allotted.room.number", null, Locale.getDefault())
			};

			// Add headers to the sheet
			for (String header : headers) {
				excelUtility.createCell(rowhead, colCount, header, headerStyle);
				sheet.setColumnWidth(colCount, 4000); // Set column width
				colCount++;
			}

			// Populate data rows
			int rowcount = 3;
			int sNo = 1;
			if (CollectionUtils.isNotEmpty(guestRequestList)) {
				for (StudentRoomDTO bo : guestRequestList) {
					rowcount++;
					XSSFRow row = sheet.createRow(rowcount);

					excelUtility.createCell(row, 0, sNo, dataStyle);
					excelUtility.createCell(row, 1, bo.getSubmittedDate(), dataStyle);
					excelUtility.createCell(row, 2, bo.getStudentId(), dataStyle);
					excelUtility.createCell(row, 3, bo.getStudentName() != null ? bo.getStudentName() : "", dataStyle);
					excelUtility.createCell(row, 4, bo.getHostelName(), dataStyle);
					excelUtility.createCell(row, 5, bo.getFromDate(), dataStyle);
					excelUtility.createCell(row, 6, bo.getToDate(), dataStyle);
					excelUtility.createCell(row, 7, bo.getWardenApprovalStatus(), dataStyle);
					excelUtility.createCell(row, 8, bo.getPaymentStatus(), dataStyle);
					excelUtility.createCell(row, 9, bo.getAllottedHostelName(), dataStyle);
					excelUtility.createCell(row, 10, bo.getAllottedRoomNo(), dataStyle);

					sNo++;
				}
			}

		} catch (Exception exception) {
			exception.printStackTrace();
			throw new Exception("Error generating report", exception);
		}
		return workbook;
	}


	public boolean processGuestAllotment(GuestHostelAllotmentDTO guestHostelAllotmentDTO, HttpServletRequest request) {
		Optional<GuestAccommodationRequestEntity> guestAccommodationRequestOpt =
				guestAccommodationRequestRepository.findById(Long.valueOf(guestHostelAllotmentDTO.getRequestId()));

		if(guestAccommodationRequestOpt.isPresent()) {
			GuestAccommodationRequestEntity guestAccommodationRequest = guestAccommodationRequestOpt.get();

			if((StringUtils.equalsAnyIgnoreCase(guestAccommodationRequest.getWardenApprovalStatus(),
					WorkflowStatus.OVERRIDE_AND_APPROVED.getStatus(),WorkflowStatus.WARDEN_APPROVAL_STATUS_COMPLETE.getStatus(),
					WorkflowStatus.APPROVED.getStatus()) ||
					StringUtils.equalsIgnoreCase(guestAccommodationRequest.getAllocationStatus(),WorkflowStatus.ALLOTTED.getStatus()))
					&& StringUtils.equalsIgnoreCase(guestAccommodationRequest.getPaymentStatus(),WorkflowStatus.PAID.getStatus())) {

				List<Object[]> roomOccupiedData = guestRoomAllotmentInfoEntityRepository.checkRoomOccupied(Integer.parseInt(guestHostelAllotmentDTO.getBuildingId())
						, Integer.parseInt(guestHostelAllotmentDTO.getRoomId()), guestAccommodationRequest.getFromDate(), guestAccommodationRequest.getToDate());
				if(CollectionUtils.isNotEmpty(roomOccupiedData)){
					throw new RuntimeException(messageSource.getMessage("message.accommodation.request.allot.room.occupied", null, Locale.getDefault()));
				}

				GuestRoomAllotmentInfoEntity guestRoomAllotmentInfoEntity;
				List<GuestRoomAllotmentInfoEntity> guestRoomAllotmentInfoList = guestRoomAllotmentInfoEntityRepository.findByRequestIdAndGuestId
						(String.valueOf(guestAccommodationRequest.getId()),String.valueOf(guestHostelAllotmentDTO.getGuestId()));

				if(CollectionUtils.isNotEmpty(guestRoomAllotmentInfoList)) {
					guestRoomAllotmentInfoEntity = guestRoomAllotmentInfoList.getFirst();
					guestRoomAllotmentInfoEntity.setActiveFlag(ModelConstants.YES);
				} else {
					guestRoomAllotmentInfoEntity = new GuestRoomAllotmentInfoEntity();
					guestRoomAllotmentInfoEntity.setStudentId(guestAccommodationRequest.getStudentDetailsInfo().getStudentId());
					guestRoomAllotmentInfoEntity.setRequestId(String.valueOf(guestAccommodationRequest.getId()));
//					List<GuestAccommodationGuestDetailsDto> guestList = guestAccommodationGuestDetailsService.getGuestList(guestAccommodationRequest.getId());
//					if (CollectionUtils.isNotEmpty(guestList)) {
//						String guestIdList = guestList.stream().map(data -> String.valueOf(data.getGuestId())).collect(Collectors.joining(","));
//						guestRoomAllotmentInfoEntity.setGuestId(guestIdList);
//					}
					guestRoomAllotmentInfoEntity.setGuestId(guestHostelAllotmentDTO.getGuestId());
					guestRoomAllotmentInfoEntity.setFromDate(guestAccommodationRequest.getFromDate());
					guestRoomAllotmentInfoEntity.setToDate(guestAccommodationRequest.getToDate());
					guestRoomAllotmentInfoEntity.setOccupiedStatus(1);
				}

				guestRoomAllotmentInfoEntity.setRoomId(Long.valueOf(guestHostelAllotmentDTO.getRoomId()));
				guestRoomAllotmentInfoEntity.setBuildingId(Long.valueOf(guestHostelAllotmentDTO.getBuildingId()));
				GuestRoomAllotmentInfoEntity allotmentInfoEntity = guestRoomAllotmentInfoEntityRepository.save(guestRoomAllotmentInfoEntity);
				if(allotmentInfoEntity != null) {
					guestAccommodationRequest.setAllocationStatus(WorkflowStatus.ALLOTTED.getStatus());
					guestAccommodationRequestRepository.save(guestAccommodationRequest);
				}

			} else {
				throw new RuntimeException(messageSource.getMessage("message.accommodation.request.allot.not.approved", null, Locale.getDefault()));
			}
		}

		return true;
	}

	public boolean processGuestAllotmentRetain(GuestHostelAllotmentDTO guestHostelAllotmentDTO, HttpServletRequest request) {
		Optional<GuestAccommodationRequestEntity> guestAccommodationRequestOpt =
				guestAccommodationRequestRepository.findById(Long.valueOf(guestHostelAllotmentDTO.getRequestId()));

		if(guestAccommodationRequestOpt.isPresent()) {
			GuestAccommodationRequestEntity guestAccommodationRequest = guestAccommodationRequestOpt.get();

			GuestRoomAllotmentInfoEntity guestRoomAllotmentInfoEntity;
			List<GuestRoomAllotmentInfoEntity> guestRoomAllotmentInfoList = guestRoomAllotmentInfoEntityRepository.findByRequestIdAndGuestId
					(String.valueOf(guestAccommodationRequest.getId()),String.valueOf(guestHostelAllotmentDTO.getGuestId()));

			if(CollectionUtils.isNotEmpty(guestRoomAllotmentInfoList)) {
				guestRoomAllotmentInfoEntity = guestRoomAllotmentInfoList.getFirst();
				//guestRoomAllotmentInfoEntity.setRoomId(null);
				//guestRoomAllotmentInfoEntity.setBuildingId(null);
				guestRoomAllotmentInfoEntity.setActiveFlag(ModelConstants.NO);
				GuestRoomAllotmentInfoEntity allotmentInfoEntity = guestRoomAllotmentInfoEntityRepository.save(guestRoomAllotmentInfoEntity);
				if(allotmentInfoEntity != null) {
					guestAccommodationRequest.setAllocationStatus(null);
					guestAccommodationRequestRepository.save(guestAccommodationRequest);
				}
			} else {
				throw new RuntimeException(messageSource.getMessage("message.accommodation.request.allot.not.found ", null, Locale.getDefault()));
			}

		}

		return true;
	}

	public List<String> getWardenNote(StudentGuestAccomDTO dto) {
        try {
            String type = dto.getAccommodationType();
            List<Object[]> resultList= guestAccommodationRequestRepository.getApprovalCountByStayType(dto.getFromDate());
			Long warden_count_individual = 0L;
			Long ar_count_individual = 0L;
			Long warden_count_along_student = 0L;
			Long ar_count_along_student = 0L;
            if(CollectionUtils.isNotEmpty(resultList)){
				Object[] result = resultList.getFirst();
				warden_count_individual = (result[0] instanceof Long ? (Long) result[0] : 0);
                ar_count_individual = (result[1] instanceof Long ? (Long) result[1] : 0);
                warden_count_along_student = (result[2] instanceof Long ? (Long) result[2] : 0);
                ar_count_along_student = (result[3] instanceof Long ? (Long) result[3] : 0);
            }
            List<String> notes = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
            String formattedDate = dto.getFromDate().format(formatter);
            if(type !=null && type.equalsIgnoreCase(Constants.INDIVIDUAL_GUEST_ROOM)){
                notes.add(messageSource.getMessage("message.warden.approval.count", null, Locale.getDefault())+" "+formattedDate+" "
						+messageSource.getMessage("message.for.individual.type", null, Locale.getDefault())+" : " + warden_count_individual);
				notes.add(messageSource.getMessage("message.ar.approval.count", null, Locale.getDefault())+" "+formattedDate+" "
						+messageSource.getMessage("message.for.individual.type", null, Locale.getDefault())+" : " + ar_count_individual);
            }else{
				notes.add(messageSource.getMessage("message.warden.approval.count", null, Locale.getDefault())+" "+formattedDate+" "
						+messageSource.getMessage("message.for.stay.along.type", null, Locale.getDefault())+" : " + warden_count_along_student);
				notes.add(messageSource.getMessage("message.ar.approval.count", null, Locale.getDefault())+" "+formattedDate+" "
						+messageSource.getMessage("message.for.stay.along.type", null, Locale.getDefault())+" : " + ar_count_along_student);
            }
            return notes;
        } catch (Exception e) {
            e.printStackTrace();
			return null;
        }
    }
	
	public boolean studentHostelAllotted(String studentId) throws Exception {
		return hostelRoomAllotmentRepository.existsByStudentIdAndActiveFlag(studentId, ModelConstants.STATUS_ACTIVE);
	}

	public String checkHostelRoomOccupancy(Long hostelId, Long roomId, LocalDate checkIn, LocalDate checkOut) {
	    Optional<List<Object[]>> result = hostelRoomInfoRepository.getHostelRoomOccupancy(hostelId, roomId, checkIn, checkOut);

	    if (result.isPresent() && !result.get().isEmpty()) {
	        Object[] obj = result.get().getFirst();
	        Integer capacity = (Integer) obj[2];
	        String numbersString = (String) obj[3];
	        
	        String[] numbersArray = numbersString.split(ModelConstants.COMMA);
	        for (String numberStr : numbersArray) {
	            Integer number = Integer.parseInt(numberStr.trim());
	            if (number.equals(capacity)) {
	                return WorkflowStatus.ALLOTTED.getStatus();
	            }
	        }
	    }
	    return null;
	}


}