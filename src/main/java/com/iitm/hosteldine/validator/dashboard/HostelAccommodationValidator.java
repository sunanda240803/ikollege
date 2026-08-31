package com.iitm.hosteldine.validator.dashboard;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

import com.iitm.hosteldine.model.hostel.CompleteStudentApplicationView;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.dashboard.student.StudentAppointmentRequestDto;
import com.iitm.hosteldine.repository.hostel.CompleteStudentApplicationViewRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import com.iitm.hosteldine.validator.common.ValidationConstants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HostelAccommodationValidator {

	private final MessageSource messageSource;
	private final SimsConfigDataService simsConfigDataService;
	private final CompleteStudentApplicationViewRepository completeStudentApplicationViewRepository;

	public void validate(StudentAppointmentRequestDto dto, BindingResult result) {
		validateAppointmentFromDate(dto.getAppointmentFrom(), dto.getAppointmentTo(), dto.getId(), result);
		validateAppointmentToDate(dto.getAppointmentTo(), dto.getAppointmentFrom(), result);
		validateStayFromDate(dto.getStayFrom(), dto.getAppointmentFrom(), dto.getAppointmentTo(), result);
		validateStayToDate(dto.getStayTo(), dto.getStayFrom(), dto.getAppointmentFrom(), dto.getAppointmentTo(),
				result);
		//validateGrossPay(dto.getGrossPay(), result);
		//validateDining(dto.getDining(), result);
		validateCategory(dto.getCategory(), dto.getCategoryOthers(), result);
		validatePurpose(dto.getPurpose(), result);
		validateValidatingAuthority(dto.getValidatingAuthority(), result);
		validateValidatingAuthorityEmail(dto.getValidatingAuthorityEmail(), result);
		validateHostelRules(dto.getHostelRules(), result);
		validateApplicableCharges(dto.getApplicableCharges(), result);
		if (dto.getFile() != null) {
			validateFile(dto.getFile(), result);
		}
	}

	private void validateAppointmentFromDate(LocalDate appointmentFrom, LocalDate appointmentTo, Long requestId,
			BindingResult result) {
		String appointmentFromField = "appointmentFrom";
		String invalidAppointmentFromCode = "appointmentFrom.invalid";
		if (appointmentFrom == null) {
			result.rejectValue(appointmentFromField, invalidAppointmentFromCode,
					messageSource.getMessage("message.validation.from.date.required", null, Locale.getDefault()));
			return;
		}
		if (appointmentTo != null) {
			Optional<CompleteStudentApplicationView> recentStatus = completeStudentApplicationViewRepository
					.getRecentStatus(SecurityCtxUtil.userId().toUpperCase(), appointmentFrom, appointmentTo,
							requestId != null ? requestId : 0L);

			if (recentStatus.isPresent()) {
				String status = recentStatus.get().getStatus();
				if (WorkflowStatus.APPROVED.getStatus().equals(status)
						|| WorkflowStatus.ALLOTTED.getStatus().equals(status)
						|| WorkflowStatus.CHECKED_IN.getStatus().equals(status)) {
					result.rejectValue(appointmentFromField, invalidAppointmentFromCode,
							messageSource.getMessage("message.validation.already.approved", null, Locale.getDefault()));
					return;
				}
			}
		}
	}

	private void validateAppointmentToDate(LocalDate appointmentTo, LocalDate appointmentFrom, BindingResult result) {
		String appointmentToField = "appointmentTo";
		String invalidAppointmentToCode = "appointmentTo.invalid";
		if (appointmentTo == null) {
			result.rejectValue(appointmentToField, invalidAppointmentToCode,
					messageSource.getMessage("message.validation.to.date.required", null, Locale.getDefault()));
			return;
		}
		if (appointmentFrom != null
				&& (appointmentTo.isEqual(appointmentFrom) || appointmentTo.isBefore(appointmentFrom))) {
			result.rejectValue(appointmentToField, invalidAppointmentToCode, messageSource.getMessage(
					"message.validation.appointment.to.date.greater.than.from.date", null, Locale.getDefault()));
			return;
		}
	}

	private void validateStayFromDate(LocalDate stayFrom, LocalDate appointmentFrom, LocalDate appointmentTo,
			BindingResult result) {
		String stayFromField = "stayFrom";
		String invalidStayFromCode = "stayFrom.invalid";
		if (stayFrom == null) {
			result.rejectValue(stayFromField, invalidStayFromCode,
					messageSource.getMessage("message.validation.from.date.required", null, Locale.getDefault()));
			return;
		}
		if (stayFrom.isBefore(appointmentFrom) || stayFrom.isAfter(appointmentTo)) {
			result.rejectValue(stayFromField, invalidStayFromCode, messageSource.getMessage(
					"message.validation.stay.from.date.between.appointment.date", null, Locale.getDefault()));
			return;
		}
	}

	private void validateStayToDate(LocalDate stayTo, LocalDate stayFrom, LocalDate appointmentFrom,
			LocalDate appointmentTo, BindingResult result) {
		String stayToField = "stayTo";
		String invalidStayToCode = "stayTo.invalid";
		if (stayTo == null) {
			result.rejectValue(stayToField, invalidStayToCode,
					messageSource.getMessage("message.validation.to.date.required", null, Locale.getDefault()));
			return;
		}
		if (stayFrom != null && (stayTo.isEqual(stayFrom) || stayTo.isBefore(stayFrom))) {
			result.rejectValue(stayToField, invalidStayToCode, messageSource
					.getMessage("message.validation.stay.to.date.greater.than.from.date", null, Locale.getDefault()));
			return;
		}
		if (stayTo.isBefore(appointmentFrom) || stayTo.isAfter(appointmentTo)) {
			result.rejectValue(stayToField, invalidStayToCode, messageSource
					.getMessage("message.validation.stay.to.date.between.appointment.date", null, Locale.getDefault()));
			return;
		}
	}

	private void validateGrossPay(Double grossPay, BindingResult result) {
		String grossPayField = "grossPay";
		String invalidGrossPayCode = "grossPay.invalid";
		if (grossPay == null) {
			result.rejectValue(grossPayField, invalidGrossPayCode,
					messageSource.getMessage("message.validation.stipend.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validateDining(String dining, BindingResult result) {
		String diningField = "dining";
		String invalidDiningCode = "dining.invalid";
		if (dining == null) {
			result.rejectValue(diningField, invalidDiningCode,
					messageSource.getMessage("message.validation.dining.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validateCategory(String category, String categoryOthers, BindingResult result) {
		String categoryField = "category";
		String invalidCategoryCode = "category.invalid";
		String categoryOthersField = "categoryOthers";
		String invalidCategoryOthersCode = "categoryOthers.invalid";
		if (category == null || category.isEmpty() || category.equals("0")) {
			result.rejectValue(categoryField, invalidCategoryCode, messageSource
					.getMessage("message.validation.appointment.nature.required", null, Locale.getDefault()));
			return;
		}

		if (ModelConstants.OTHER_LOGIN_TYPE.equalsIgnoreCase(categoryOthers)
				&& (categoryOthers == null || categoryOthers.isEmpty())) {
			result.rejectValue(categoryOthersField, invalidCategoryOthersCode,
					messageSource.getMessage("message.validation.category.others.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validatePurpose(String purpose, BindingResult result) {
		String purposeField = "purpose";
		String invalidPurposeCode = "purpose.invalid";
		if (purpose == null || purpose.isEmpty()) {
			result.rejectValue(purposeField, invalidPurposeCode, messageSource
					.getMessage("message.validation.stay.request.purpose.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validateValidatingAuthority(String validatingAuthority, BindingResult result) {
		String validatingAuthorityField = "validatingAuthority";
		String invalidValidatingAuthorityCode = "validatingAuthority.invalid";
		if (validatingAuthority == null || validatingAuthority.isEmpty()) {
			result.rejectValue(validatingAuthorityField, invalidValidatingAuthorityCode,
					messageSource.getMessage("message.validation.authority.name.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validateValidatingAuthorityEmail(String validatingAuthorityEmail, BindingResult result) {
		String validatingAuthorityEmailField = "validatingAuthorityEmail";
		String invalidValidatingAuthorityEmailCode = "validatingAuthorityEmail.invalid";
		if (validatingAuthorityEmail == null || validatingAuthorityEmail.isEmpty()) {
			result.rejectValue(validatingAuthorityEmailField, invalidValidatingAuthorityEmailCode,
					messageSource.getMessage("message.validation.authority.email.required", null, Locale.getDefault()));
			return;
		}
		if (!validatingAuthorityEmail.matches(ValidationConstants.EMAIL_PATTERN)) {
			result.rejectValue(validatingAuthorityEmailField, invalidValidatingAuthorityEmailCode,
					messageSource.getMessage("message.validation.email.id", null, Locale.getDefault()));
			return;
		}
		if (!hasValidIITMEmailDomain(validatingAuthorityEmail)) {
			result.rejectValue(validatingAuthorityEmailField, invalidValidatingAuthorityEmailCode,
					messageSource.getMessage("message.validation.email.valid", null, Locale.getDefault()));
			return;
		}
	}

	private boolean hasValidIITMEmailDomain(String email) {
		String lowerCaseEmail = email.toLowerCase(Locale.ROOT);
		for (String domain : ValidationConstants.VALIDATING_AUTHORITY_EMAIL_DOMAINS) {
			if (lowerCaseEmail.endsWith(domain)) {
				return true;
			}
		}
		return false;
	}

	private void validateHostelRules(Boolean hostelRules, BindingResult result) {
		String hostelRulesField = "hostelRules";
		String invalidHostelRulesCode = "hostelRules.invalid";
		if (hostelRules == null) {
			result.rejectValue(hostelRulesField, invalidHostelRulesCode);
			return;
		}
	}

	private void validateApplicableCharges(Boolean applicableCharges, BindingResult result) {
		String applicableChargesField = "applicableCharges";
		String invalidApplicableChargesCode = "applicableCharges.invalid";
		if (applicableCharges == null) {
			result.rejectValue(applicableChargesField, invalidApplicableChargesCode);
			return;
		}
	}

	private void validateFile(MultipartFile[] files, BindingResult result) {
		String fileField = "file";
		String invalidFileCode = "file.invalid";
		Long maxFileSize = Long
				.parseLong(simsConfigDataService.getSimConfigValue(SimsConfigDataService.DEFAULT_FILE_SIZE));

		for (MultipartFile file : files) {
			if (!file.isEmpty()) {
				// Check file size
				if (file.getSize() > maxFileSize) {
					result.rejectValue(fileField, invalidFileCode, messageSource
							.getMessage("message.validation.file.size.exceeded", null, Locale.getDefault()));
					return;
				}
				// Check file extension
				String fileName = file.getOriginalFilename();
				if (fileName != null && !isAllowedFileExtension(fileName,
						FileUploadConstants.HOSTEL_ACCOMMODATION_ALLOWED_EXTENSIONS)) {
					result.rejectValue(fileField, invalidFileCode, messageSource
							.getMessage("message.validation.file.format.invalid", null, Locale.getDefault()));
					return;
				}
			}
		}
	}

	private boolean isAllowedFileExtension(String fileName, String[] allowedExtensions) {
		String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
		for (String allowedExtension : allowedExtensions) {
			if (fileExtension.equals(allowedExtension)) {
				return true;
			}
		}
		return false;
	}
}
