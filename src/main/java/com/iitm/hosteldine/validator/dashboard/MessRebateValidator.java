package com.iitm.hosteldine.validator.dashboard;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.dto.mess.MessRebateDto;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentBioDataService;
import com.iitm.hosteldine.service.mess.MessRebateService;
import com.iitm.hosteldine.validator.common.ValidationConstants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessRebateValidator {

	private final MessageSource messageSource;	
	private final MessRebateService messRebateService;
	private final SimsConfigDataService simsConfigDataService;
	private final StudentBioDataService studentBioDataService;

	public void validate(MessRebateDto dto, BindingResult result) throws Exception {
		validateRebateFrom(dto.getRebateFrom(), dto.getRebateTo(), result);
		validateRebateTo(dto.getRebateTo(), dto.getRebateFrom(), dto.getFile(), result);
		validateRebateReason(dto.getRebateReason(), result);
		validateGuideName(dto.getGuideName(), result);
		validateGuideEmail(dto.getGuideEmail(), result);
	}

	private void validateRebateFrom(LocalDate rebateFrom, LocalDate rebateTo, BindingResult result) throws Exception {
		String rebateFromField = "rebateFrom";
		String invalidRebateFromCode = "rebateFrom.invalid";
		if (rebateFrom == null) {
			result.rejectValue(rebateFromField, invalidRebateFromCode,
					messageSource.getMessage("message.validation.rebate.from.date.required", null, Locale.getDefault()));
			return;
		}

	    // Convert configuration values to integers
	    int minFromDate = Integer.parseInt(simsConfigDataService.getSimConfigValue(SimsConfigDataService.MESS_REBATE_MINIMUM_FROM_DATE));
	    int maxFromDate = Integer.parseInt(simsConfigDataService.getSimConfigValue(SimsConfigDataService.MESS_REBATE_MAXIMUM_FROM_DATE));

	    // Calculate min and max valid dates
	    LocalDate today = LocalDate.now();
	    LocalDate minDate = today.plusDays(minFromDate);
	    LocalDate maxDate = today.plusDays(maxFromDate);

		// Validate rebateFrom date
		if (rebateFrom.isBefore(minDate) || rebateFrom.isAfter(maxDate)) {
			String invalidRebateDates = messageSource.getMessage("message.validation.rebate.from.date.invalid.one", null,
					Locale.getDefault()) + ModelConstants.SPACE + minFromDate + ModelConstants.SPACE
					+ messageSource.getMessage("message.validation.rebate.from.date.invalid.two", null, Locale.getDefault())
					+ ModelConstants.SPACE + maxFromDate + ModelConstants.SPACE + messageSource
							.getMessage("message.validation.rebate.from.date.invalid.three", null, Locale.getDefault());
			result.rejectValue(rebateFromField, invalidRebateFromCode, invalidRebateDates);
			return;
		}

		if (messRebateService.checkFromDateAndToDateExists(rebateFrom, rebateTo) != null && !messRebateService.checkFromDateAndToDateExists(rebateFrom, rebateTo).isEmpty()) {
			result.rejectValue(rebateFromField, invalidRebateFromCode, messageSource
					.getMessage("message.validation.rebate.from.already.requested", null, Locale.getDefault()));
			return;
		}
	}
	
	private void validateRebateTo(LocalDate rebateTo, LocalDate rebateFrom, MultipartFile file, BindingResult result) {
		String rebateToField = "rebateTo";
		String invalidRebateToCode = "rebateTo.invalid";
		String fileField = "file";
		String invalidFileCode = "file.invalid";

		// Temporary changes for the date 16th, 17th and 18th of Jan 2026.
//		LocalDate exceptionStartDate = LocalDate.of(2026, 1, 16);
//		LocalDate exceptionEndDate   = LocalDate.of(2026, 1, 18);
//		// Temporary changes
//		boolean isWithinExceptionWindow =
//				(rebateTo.isEqual(exceptionStartDate) || rebateTo.isAfter(exceptionStartDate))
//						&& (rebateTo.isEqual(exceptionEndDate) || rebateTo.isBefore(exceptionEndDate));

		if (rebateTo == null) {
			result.rejectValue(rebateToField, invalidRebateToCode,
					messageSource.getMessage("message.validation.rebate.to.date.required", null, Locale.getDefault()));
			return;
		}
//		if (rebateTo != null && (rebateTo.isEqual(rebateFrom) || rebateTo.isBefore(rebateFrom)) && !isWithinExceptionWindow) {
		if (rebateTo != null && (rebateTo.isEqual(rebateFrom) || rebateTo.isBefore(rebateFrom))) {
			result.rejectValue(rebateToField, invalidRebateToCode,
					messageSource.getMessage("message.validation.rebate.to.date.greater", null, Locale.getDefault()));
			return;
		}

		// Retrieve minPeriod from configuration
		int minPeriod = Integer.parseInt(simsConfigDataService.getSimConfigValue(SimsConfigDataService.MESS_REBATE_MINIMUM_PERIOD));
	    int maxPeriod = Integer.parseInt(simsConfigDataService.getSimConfigValue(SimsConfigDataService.MESS_REBATE_MAXIMUM_PERIOD));

		// Calculate the number of days between rebateFrom and rebateTo
		long noOfDays = ChronoUnit.DAYS.between(rebateFrom, rebateTo) + 1;

		// Validate minimum period
//		if (noOfDays < minPeriod && !isWithinExceptionWindow) {
		if (noOfDays < minPeriod) {
			String invalidRebatePeriod = messageSource.getMessage("message.validation.rebate.to.min.period.one", null,
					Locale.getDefault()) + ModelConstants.SPACE + minPeriod + ModelConstants.SPACE
					+ messageSource.getMessage("message.validation.rebate.to.min.period.two", null, Locale.getDefault())
					+ ModelConstants.SPACE + minPeriod + ModelConstants.SPACE + messageSource
							.getMessage("message.validation.rebate.from.date.invalid.three", null, Locale.getDefault());
			result.rejectValue(rebateToField, invalidRebateToCode, invalidRebatePeriod);
		}

		// Validate maximum period
//		if (noOfDays > maxPeriod) {
//			if (file == null || file.isEmpty()) {
//				result.rejectValue(fileField, invalidFileCode,
//						messageSource.getMessage("message.validation.reported.document.required", null, Locale.getDefault()));
//				return;
//			}
//		}
	}

	private void validateRebateReason(String rebateReason, BindingResult result) {
		String rebateReasonField = "rebateReason";
		String invalidRebateReasonCode = "rebateReason.invalid";
		if (rebateReason == null || rebateReason.isEmpty()) {
			result.rejectValue(rebateReasonField, invalidRebateReasonCode,
					messageSource.getMessage("message.validation.rebate.reason.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validateGuideName(String guideName, BindingResult result) {
		String guideNameField = "guideName";
		String invalidGuideNameCode = "guideName.invalid";
		
		StudentBioDataFormDetailDto studentDetails = studentBioDataService
				.getStudentDetails(SecurityCtxUtil.userId().toUpperCase());
		if (studentDetails.getFacultyName() == null || studentDetails.getFacultyName().isEmpty() || studentDetails.getFacultyName().equals("0")) {
			if (guideName == null || guideName.isEmpty()) {
				result.rejectValue(guideNameField, invalidGuideNameCode,
						messageSource.getMessage("message.label.guideName.required", null, Locale.getDefault()));
				return;
			}
		}
	}

	private void validateGuideEmail(String guideEmail, BindingResult result) {
		String guideEmailField = "guideEmail";
		String invalidGuideEmailCode = "guideEmail.invalid";

		StudentBioDataFormDetailDto studentDetails = studentBioDataService
				.getStudentDetails(SecurityCtxUtil.userId().toUpperCase());
		if (studentDetails.getFacultyEmail() == null || studentDetails.getFacultyEmail().isEmpty()) {
			if (guideEmail == null || guideEmail.isEmpty()) {
				result.rejectValue(guideEmailField, invalidGuideEmailCode, messageSource
						.getMessage("message.label.guideEmail.required", null, Locale.getDefault()));
				return;
			}
			if (!guideEmail.matches(ValidationConstants.EMAIL_PATTERN)) {
				result.rejectValue(guideEmailField, invalidGuideEmailCode,
						messageSource.getMessage("message.validation.email.id", null, Locale.getDefault()));
				return;
			}
		}
	}
	
}
