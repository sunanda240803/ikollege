package com.iitm.hosteldine.validator.student;

import java.util.Locale;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.service.SimsConfigDataService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.iitm.hosteldine.dto.student.ConvocationAdditionalCouponsDto;
import com.iitm.hosteldine.dto.student.ConvocationAccommodationDto;
import com.iitm.hosteldine.service.student.AccommodationMessConvocationService;
import com.iitm.hosteldine.validator.common.ValidationConstants;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AccommodationMessConvocationValidator {

	private final MessageSource messageSource;
	private final AccommodationMessConvocationService service;
	private final SimsConfigDataService simsConfigDataService;

	public void validate(ConvocationAdditionalCouponsDto dto, BindingResult result) {
		normalizeAccommodationSelection(dto);
		validateStudentId(dto.getConvocation().getStudentId(), result);
		validateStudentName(dto.getConvocation().getStudentName(), result);
		validateGender(dto.getConvocation().getGender(), result);
		validateEmail(dto.getConvocation().getMailId(), result);
		validateMenu(dto.getConvocation().getMenuType(), result);
		validateAccommodationPreference(dto, result);
		validateHostelName(dto, result);
		validateAccommodationOrComplimentaryOrCoupons(dto, result);
	}

	public boolean validateConvocationForm(BindingResult result) {
		String convocationOpenLink = simsConfigDataService.getSimConfigValue(SimsConfigDataService.CONVOCATION_OPEN_LINK);
		boolean isConvocationOpen = Boolean.parseBoolean(convocationOpenLink);
		if (!isConvocationOpen) {
			result.reject(
					"convocation.closed",
					messageSource.getMessage("message.error.convocation.ended", null, Locale.getDefault())
			);
			return false;
		}
		return true;
	}

	private void validateStudentId(String studentId, BindingResult result) {
		String studentIdField = "convocation.studentId";
		String invalidStudentIdCode = "convocation.studentId.invalid";
		if (studentId == null || studentId.isEmpty()) {
			result.rejectValue(studentIdField, invalidStudentIdCode, messageSource
					.getMessage("message.label.validation.student.id.is.required", null, Locale.getDefault()));
			return;
		}
		String studentName = service.CheckStudentExists(studentId);
		if (studentId != null && !studentId.isEmpty() && (studentName == null || studentName.isEmpty())) {
			result.rejectValue(studentIdField, invalidStudentIdCode,
					messageSource.getMessage("message.label.validation.student.id.exist", null, Locale.getDefault()));
			return;
		}
		boolean isStudentAlreadyAppliedAccommodation = service.isStudentAlreadyAppliedAccommodation(studentId);
		if (isStudentAlreadyAppliedAccommodation) {
			result.rejectValue(studentIdField, invalidStudentIdCode,
					messageSource.getMessage("message.label.student.convocation.accommodation.already.exist", null, Locale.getDefault()));
			return;
		}
	}

	private void validateStudentName(String studentName, BindingResult result) {
		String studentNameField = "convocation.studentName";
		String invalidStudentNameCode = "convocation.studentName.invalid";
		if (studentName == null || studentName.isEmpty()) {
			result.rejectValue(studentNameField, invalidStudentNameCode,
					messageSource.getMessage("message.validation.student.details.name", null, Locale.getDefault()));
			return;
		}
	}

	private void validateGender(String gender, BindingResult result) {
		String genderField = "convocation.gender";
		String invalidGenderCode = "convocation.gender.invalid";
		if (gender == null || gender.isEmpty()) {
			result.rejectValue(genderField, invalidGenderCode,
					messageSource.getMessage("message.validation.student.details.gender", null, Locale.getDefault()));
			return;
		}
	}

	private void validateEmail(String mailId, BindingResult result) {
		String mailField = "convocation.mailId";
		String invalidMailCode = "convocation.mailId.invalid";
		if (mailId == null || mailId.isEmpty()) {
			result.rejectValue(mailField, invalidMailCode,
					messageSource.getMessage("message.validation.email.id.required", null, Locale.getDefault()));
			return;
		}
		if (mailId != null && !mailId.matches(ValidationConstants.EMAIL_PATTERN)) {
			result.rejectValue(mailField, invalidMailCode,
					messageSource.getMessage("message.validation.email.id", null, Locale.getDefault()));
		}
	}

	private void validateMenu(String menuType, BindingResult result) {
		String menuTypeField = "convocation.menuType";
		String invalidMenuTypeCode = "convocation.menuType.invalid";
		if (menuType == null || menuType.isEmpty()) {
			result.rejectValue(menuTypeField, invalidMenuTypeCode,
					messageSource.getMessage("message.validation.menu.required", null, Locale.getDefault()));
			return;
		}
	}

	private void normalizeAccommodationSelection(ConvocationAdditionalCouponsDto dto) {
		ConvocationAccommodationDto convocation = dto.getConvocation();
		String preference = convocation.getAccommodationPreference();
		if (!hasText(preference)) {
			if (Boolean.TRUE.equals(convocation.getAccommodationStatus())) {
				convocation.setAccommodationPreference(ModelConstants.ACCOMMODATION_PREFERENCE_ACCOMMODATION);
			} else if (Boolean.FALSE.equals(convocation.getAccommodationStatus()) && hasText(convocation.getHostelName())) {
				convocation.setAccommodationPreference(ModelConstants.ACCOMMODATION_PREFERENCE_HOSTEL);
			}
		}

		if (isAccommodationNeeded(dto)) {
			convocation.setAccommodationStatus(Boolean.TRUE);
		} else if (isHostelRequired(dto) || isHostelNotNeeded(dto)) {
			convocation.setAccommodationStatus(Boolean.FALSE);
		}
	}

	private void validateAccommodationPreference(ConvocationAdditionalCouponsDto dto, BindingResult result) {
		String accommodationPreferenceField = "convocation.accommodationPreference";
		String invalidAccommodationPreferenceCode = "convocation.accommodationPreference.invalid";
		if (!isAccommodationNeeded(dto) && !isHostelRequired(dto) && !isHostelNotNeeded(dto)) {
			result.rejectValue(accommodationPreferenceField, invalidAccommodationPreferenceCode,
					messageSource.getMessage("message.validation.accommodation.required", null, Locale.getDefault()));
		}
	}

	private void validateHostelName(ConvocationAdditionalCouponsDto dto, BindingResult result) {
		if (!isHostelRequired(dto)) {
			return;
		}

		String hostelName = dto.getConvocation().getHostelName();
		if (hostelName == null || hostelName.trim().isEmpty()) {
			result.rejectValue("convocation.hostelName", "convocation.hostelName.invalid", messageSource
					.getMessage("message.validation.hostel.name.required", null, Locale.getDefault()));
		}
	}

	private void validateAccommodationOrComplimentaryOrCoupons(ConvocationAdditionalCouponsDto dto, BindingResult result) {
		boolean needAccommodation = isAccommodationNeeded(dto);
		boolean noAccommodationWithHostel = isHostelRequired(dto) && hasText(dto.getConvocation().getHostelName());
		boolean hostelNotNeeded = isHostelNotNeeded(dto);
		boolean complimentary = (dto.getBf() != null && !dto.getBf().isEmpty())
				|| (dto.getLn() != null && !dto.getLn().isEmpty()) || (dto.getDn() != null && !dto.getDn().isEmpty());
		boolean additionalCoupons = dto.getConvocation().getOverallAmount() != null
				&& dto.getConvocation().getOverallAmount() > 0;

		if (!(needAccommodation || noAccommodationWithHostel || hostelNotNeeded || complimentary || additionalCoupons)) {
			result.reject("convocation.selection.invalid", messageSource
					.getMessage("message.validation.atleast.one.option.required", null, Locale.getDefault()));
			return;
		}
	}

	private boolean isAccommodationNeeded(ConvocationAdditionalCouponsDto dto) {
		return ModelConstants.ACCOMMODATION_PREFERENCE_ACCOMMODATION
				.equals(dto.getConvocation().getAccommodationPreference());
	}

	private boolean isHostelRequired(ConvocationAdditionalCouponsDto dto) {
		return ModelConstants.ACCOMMODATION_PREFERENCE_HOSTEL
				.equals(dto.getConvocation().getAccommodationPreference());
	}

	private boolean isHostelNotNeeded(ConvocationAdditionalCouponsDto dto) {
		return ModelConstants.ACCOMMODATION_PREFERENCE_HOSTEL_NOT_NEEDED
				.equals(dto.getConvocation().getAccommodationPreference());
	}

	private boolean hasText(String value) {
		return value != null && !value.trim().isEmpty();
	}

}
