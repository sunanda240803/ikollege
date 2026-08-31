package com.iitm.hosteldine.validator.warden;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.iitm.hosteldine.dto.warden.WardenInfoDto;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.validator.common.ValidationCommon;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WardenInfoValidator {

	private final MessageSource messageSource;
	private final SimsConfigDataService simsConfigDataService;

	public void validate(WardenInfoDto dto, BindingResult result) throws Exception {
		String guideEmail = simsConfigDataService.getSimConfigValue(SimsConfigDataService.GUIDE_EMAIL);
		validateWardenName(dto.getWardenName(), result);
		validateOfficeNo(dto.getOfficeNo(), result);
		validateEmailId(dto.getWardenEmail(), result, guideEmail);
		validateAlternateEmail(dto.getAlternateEmail(), result, guideEmail);
		ValidationCommon.validatePhoneNumber(dto.getPhoneNumber(), result, messageSource);
		validateLdapUsername(dto.getLdapUsername(), result);
		validateHostelId(dto.getHostelId(), result);
		validateImage(dto.getImageContent(), dto.getImageFileName(), result, dto);
	}

	private void validateWardenName(String wardenName, BindingResult result) {
		if (wardenName == null || wardenName.trim().isEmpty()) {
			result.rejectValue("wardenName", "wardenName.invalid", messageSource.getMessage("message.validation.warden.name.required", null, Locale.getDefault()));
		} else if (wardenName.length() > 128) {
			result.rejectValue("wardenName", "wardenName.length", messageSource.getMessage("message.validation.warden.name.character.limit", null, Locale.getDefault()));
		}
	}

	private void validateOfficeNo(String officeNo, BindingResult result) {
		if (officeNo == null || officeNo.trim().isEmpty()) {
			result.rejectValue("officeNo", "officeNo.invalid", messageSource.getMessage("message.validation.office.number.required", null, Locale.getDefault()));
		}
	}

	private void validateEmailId(String email, BindingResult result, String emailSuffix) {
		String domainPattern = emailSuffix.replaceAll(",\\s*", "|").replaceAll("@", "");
	    String emailRegex = "^[^\\s@]+@(" + domainPattern + ")$";
		if (email == null || email.trim().isEmpty()) {
			result.rejectValue("wardenEmail", "wardenEmail.invalid", messageSource.getMessage("message.validation.warden.email.required", null, Locale.getDefault()));
		} else if (!email.matches(emailRegex)) {
			result.rejectValue("wardenEmail", "wardenEmail.format", messageSource.getMessage("message.validation.email.valid", null, Locale.getDefault()));
		}
	}

	private void validateAlternateEmail(String alternateEmail, BindingResult result, String emailSuffix) {
		String domainPattern = emailSuffix.replaceAll(",\\s*", "|").replaceAll("@", "");
	    String emailRegex = "^[^\\s@]+@(" + domainPattern + ")$";
		if (alternateEmail != null && !alternateEmail.trim().isEmpty() && !alternateEmail.matches(emailRegex)) {
			result.rejectValue("alternateEmail", "alternateEmail.format", messageSource.getMessage("message.validation.alternate.email.valid", null, Locale.getDefault()));
		}
	}	

	private void validateLdapUsername(String ldapUsername, BindingResult result) {
		if (ldapUsername == null || ldapUsername.trim().isEmpty()) {
			result.rejectValue("ldapUsername", "ldapUsername.invalid", messageSource.getMessage("message.validation.ldap.username.required", null, Locale.getDefault()));
		}
	}

	private void validateHostelId(Long hostelId, BindingResult result) {
		if (hostelId == null) {
			result.rejectValue("hostelId", "hostelId.invalid", messageSource.getMessage("message.validation.hostel.required", null, Locale.getDefault()));
		}
	}

	private void validateImage(String imageContent, String imageFileName, BindingResult result, WardenInfoDto dto) {
		boolean isImageRequired = (dto.getId() == null);
		if (isImageRequired && (imageContent == null || imageContent.trim().isEmpty())) {
			result.rejectValue("imageContent", "imageContent.invalid", messageSource.getMessage("message.validation.photo.required", null, Locale.getDefault()));
		}
		if (isImageRequired && imageFileName != null && !imageFileName.isEmpty()) {
			String lower = imageFileName.toLowerCase();
		    if (!isValidImageType(lower)) {
		        result.rejectValue("imageFileName", "imageFileName.invalid", messageSource.getMessage("message.validation.image.file.type", null, Locale.getDefault()));
		    }
		}
	}

	private boolean isValidImageType(String lower) {
		String fileTypes = messageSource.getMessage("message.accepted.image.types", null, Locale.getDefault());
		boolean valid = false;
		for (String ext : fileTypes.replace(" ", "").split(",")) {
		    if (lower.endsWith(ext)) {
		        valid = true;
		        break;
		    }
		}
		return valid;
	}
}