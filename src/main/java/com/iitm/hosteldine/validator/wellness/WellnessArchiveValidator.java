package com.iitm.hosteldine.validator.wellness;

import java.time.LocalDate;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.iitm.hosteldine.dto.student.wellness.StudentWellnessCategoricalDataDto;
import com.iitm.hosteldine.dto.student.wellness.StudentWellnessFollowupDataDto;
import com.iitm.hosteldine.validator.common.ValidationConstants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WellnessArchiveValidator {

	private final MessageSource messageSource;

	public void validate(StudentWellnessCategoricalDataDto dto, BindingResult result) {
		if (dto.getCategory()
				.equals(messageSource.getMessage("message.label.category.others", null, Locale.getDefault()))
				&& dto.getId() == null) {
			validateOtherStudName(dto.getOtherStudName(), result);
			validateOtherStudPhone(dto.getOtherStudPhone(), result);
			validateOtherStudEmail(dto.getOtherStudEmail(), result);
		}
		validateReferralDate(dto.getReferralDate(), result);
		validateReferralType(dto.getReferralType(), dto.getReferralOthersDescription(), dto.getReferralBy(), result);
		validateReferralDesc(dto.getReferralType(), dto.getReferralOthersDescription(), result);
		validateCoordinatedName(dto.getCoordinatedName(), result);
		validateCoordinatedEmail(dto.getCoordinatedEmail(), result);
		validateConcernType(dto.getConcernType(), dto.getConcernOthersDescription(), result);
		validateSelfHarm(dto.getSelfHarm(), dto.getSelfHarmType(), result);
		validatePsychiatric(dto.getPsychiatricConsultation(), dto.getPsychiatricName(), result);
	}

	private void validateReferralDate(LocalDate referralDate, BindingResult result) {
		String referralDateField = "referralDate";
		String invalidReferralDateCode = "referralDate.invalid";
		if (referralDate == null) {
			result.rejectValue(referralDateField, invalidReferralDateCode,
					messageSource.getMessage("message.validation.referral.date.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validateReferralType(String referralType, String othersDesc, String referralBy, BindingResult result) {
		String referralTypeField = "referralType";
		String invalidReferralTypeCode = "referralType.invalid";
		if (referralType == null || referralType.isEmpty()) {
			result.rejectValue(referralTypeField, invalidReferralTypeCode,
					messageSource.getMessage("message.validation.referral.type.required", null, Locale.getDefault()));
			return;
		}
		if (referralType.equals("Faculty") || referralType.equals("Student Body") || referralType.equals("Others")) {
			String referralByField = "referralBy";
			String invalidReferralByCode = "referralBy.invalid";
			if (referralBy == null || referralBy.isEmpty()) {
				result.rejectValue(referralByField, invalidReferralByCode,
						messageSource.getMessage("message.validation.referred.by.required", null, Locale.getDefault()));
				return;
			}
		}
	}

	private void validateReferralDesc(String referralType, String othersDesc, BindingResult result) {
		String referralOthersDescriptionField = "referralOthersDescription";
		String invalidReferralOthersDescriptionByCode = "referralOthersDescription.invalid";
		if (referralType.equals("Others") && (othersDesc == null || othersDesc.isEmpty())) {
			result.rejectValue(referralOthersDescriptionField, invalidReferralOthersDescriptionByCode, messageSource
					.getMessage("message.validation.please.specify.others.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validateCoordinatedName(String coordinatedName, BindingResult result) {
		String coordinatedNameField = "coordinatedName";
		String invalidCoordinatedNameCode = "coordinatedName.invalid";
		if (coordinatedName == null || coordinatedName.isEmpty()) {
			result.rejectValue(coordinatedNameField, invalidCoordinatedNameCode, messageSource
					.getMessage("message.validation.welness.coordinator.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validateCoordinatedEmail(String coordinatedEmail, BindingResult result) {
		String coordinatedEmailField = "coordinatedEmail";
		String invalidCoordinatedEmailCode = "coordinatedEmail.invalid";
		if (coordinatedEmail == null || coordinatedEmail.isEmpty()) {
			result.rejectValue(coordinatedEmailField, invalidCoordinatedEmailCode, messageSource
					.getMessage("message.validation.coordinator.email.required", null, Locale.getDefault()));
			return;
		}
		if (!coordinatedEmail.matches(ValidationConstants.EMAIL_PATTERN)) {
			result.rejectValue(coordinatedEmailField, invalidCoordinatedEmailCode,
					messageSource.getMessage("message.validation.invalid.email.id", null, Locale.getDefault()));
			return;
		}
	}

	private void validateConcernType(String concernType, String othersDesc, BindingResult result) {
		String concernTypeField = "concernType";
		String invalidConcernTypeCode = "concernType.invalid";
		if (concernType == null || concernType.isEmpty()) {
			result.rejectValue(concernTypeField, invalidConcernTypeCode,
					messageSource.getMessage("message.validation.type.of.concern.required", null, Locale.getDefault()));
			return;
		}
		String concernOthersDescriptionField = "concernOthersDescription";
		String invalidConcernOthersDescriptionByCode = "concernOthersDescription.invalid";
		if (concernType.equals("Others") && (othersDesc == null || othersDesc.isEmpty())) {
			result.rejectValue(concernOthersDescriptionField, invalidConcernOthersDescriptionByCode, messageSource
					.getMessage("message.validation.please.specify.others.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validateSelfHarm(Boolean selfHarm, String selfHarmType, BindingResult result) {
		String selfHarmField = "selfHarm";
		String invalidSelfHarmCode = "selfHarm.invalid";
		if (selfHarm == null) {
			result.rejectValue(selfHarmField, invalidSelfHarmCode,
					messageSource.getMessage("message.validation.self.harm.required", null, Locale.getDefault()));
			return;
		}
		String selfHarmTypeField = "selfHarmType";
		String invalidSelfHarmTypeCode = "selfHarmType.invalid";
		if (selfHarm && (selfHarmType == null || selfHarmType.isEmpty())) {
			result.rejectValue(selfHarmTypeField, invalidSelfHarmTypeCode, messageSource
					.getMessage("message.validation.type.of.self.harm.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validatePsychiatric(Boolean psychiatricConsultation, String psychiatricName, BindingResult result) {
		String psychiatricConsultationField = "psychiatricConsultation";
		String invalidPsychiatricConsultationCode = "psychiatricConsultation.invalid";
		if (psychiatricConsultation == null) {
			result.rejectValue(psychiatricConsultationField, invalidPsychiatricConsultationCode,
					messageSource.getMessage("message.validation.psychiatric.required", null, Locale.getDefault()));
			return;
		}
		String psychiatricNameField = "psychiatricName";
		String invalidPsychiatricNameCode = "psychiatricName.invalid";
		if (psychiatricConsultation && (psychiatricName == null || psychiatricName.isEmpty())) {
			result.rejectValue(psychiatricNameField, invalidPsychiatricNameCode, messageSource
					.getMessage("message.validation.psychiatrist.name.required", null, Locale.getDefault()));
			return;
		}
	}
	
	private void validateOtherStudName(String otherStudName, BindingResult result) {
		String otherStudNameField = "otherStudName";
		String invalidOtherStudNameCode = "otherStudName.invalid";
		if (otherStudName == null || otherStudName.isEmpty()) {
			result.rejectValue(otherStudNameField, invalidOtherStudNameCode, messageSource
					.getMessage("message.validation.student.details.name", null, Locale.getDefault()));
			return;
		}
	}
	
	private void validateOtherStudPhone(String otherStudPhone, BindingResult result) {
		String otherStudPhoneField = "otherStudPhone";
		String invalidOtherStudPhoneCode = "otherStudPhone.invalid";
		if (otherStudPhone == null || otherStudPhone.isEmpty()) {
			result.rejectValue(otherStudPhoneField, invalidOtherStudPhoneCode, messageSource
					.getMessage("message.validation.student.details.name", null, Locale.getDefault()));
			return;
		}
	}

	private void validateOtherStudEmail(String otherStudEmail, BindingResult result) {
		String otherStudEmailField = "otherStudEmail";
		String invalidOtherStudEmailCode = "otherStudEmail.invalid";
		if (otherStudEmail == null || otherStudEmail.isEmpty()) {
			result.rejectValue(otherStudEmailField, invalidOtherStudEmailCode, messageSource
					.getMessage("message.validation.email.id.required", null, Locale.getDefault()));
			return;
		}
		if (!otherStudEmail.matches(ValidationConstants.EMAIL_PATTERN)) {
			result.rejectValue(otherStudEmailField, invalidOtherStudEmailCode,
					messageSource.getMessage("message.validation.invalid.email.id", null, Locale.getDefault()));
			return;
		}
	}

	public void validate(StudentWellnessFollowupDataDto dto, BindingResult result) {
		validateVisitDate(dto.getVisitDate(), result);
		validateInteractionMode(dto.getInteractionMode(), result);
		validateStatus(dto.getVisitStatus(), result);
		validateDuration(dto.getDuration(), result);	
	}

	private void validateDuration(Short duration, BindingResult result) {
		String durationField = "duration";
		String invalidDurationCode = "duration.invalid";
		if (duration == null || duration == 0) {
			result.rejectValue(durationField, invalidDurationCode, messageSource
					.getMessage("message.validation.status.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validateStatus(String visitStatus, BindingResult result) {
		String visitStatusField = "visitStatus";
		String invalidVisitStatusCode = "visitStatus.invalid";
		if (visitStatus == null || visitStatus.isEmpty()) {
			result.rejectValue(visitStatusField, invalidVisitStatusCode, messageSource
					.getMessage("message.validation.status.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validateInteractionMode(String interactionMode, BindingResult result) {
		String interactionModeField = "interactionMode";
		String invalidInteractionModeCode = "interactionMode.invalid";
		if (interactionMode == null || interactionMode.isEmpty()) {
			result.rejectValue(interactionModeField, invalidInteractionModeCode, messageSource
					.getMessage("message.validation.interaction.mode.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validateVisitDate(LocalDate visitDate, BindingResult result) {
		String visitDateField = "visitDate";
		String invalidVisitDateCode = "visitDate.invalid";
		if (visitDate == null) {
			result.rejectValue(visitDateField, invalidVisitDateCode, messageSource
					.getMessage("message.validation.visit.date.required", null, Locale.getDefault()));
			return;
		}
	}

}
