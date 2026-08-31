package com.iitm.hosteldine.validator.hostel;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.iitm.hosteldine.dto.hostel.StudentsHostelAllotmentDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BulkAllotmentValidator {

	private final MessageSource messageSource;

	public void validate(StudentsHostelAllotmentDto dto, BindingResult result) {
		validateAllotmentType(dto.getUploadType(), result);
	}

	private void validateAllotmentType(String uploadType, BindingResult result) {
		String uploadTypeField = "uploadType";
		String invalidUploadTypeCode = "uploadType.invalid";
		if (uploadType == null || uploadType.isEmpty()) {
			result.rejectValue(uploadTypeField, invalidUploadTypeCode,
					messageSource.getMessage("message.validation.allotment.type.required", null, Locale.getDefault()));
			return;
		}
	}

}
