package com.iitm.hosteldine.validator.dashboard;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import com.iitm.hosteldine.dto.student.StudentRollnoChangeDto;
import com.iitm.hosteldine.service.student.StudentRollnoChangeService;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import com.iitm.hosteldine.validator.common.ValidationConstants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RollNumberValidator implements Validator {

	private final StudentRollnoChangeService studentRollnoChangeService;

	@Override
	public boolean supports(Class<?> clazz) {
		return StudentRollnoChangeDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		StudentRollnoChangeDto dto = (StudentRollnoChangeDto) target;
		validateNewRollNo(dto, errors);
		validateFile(dto, errors);
	}

	private void validateNewRollNo(StudentRollnoChangeDto studentDto, Errors errors) {
		try {
			String newRollNo = studentDto.getNewRollNo();
			if (newRollNo == null || newRollNo.isEmpty()) {
				errors.rejectValue("newRollNo", "message.validation.no.roll.no");
				return;
			}
			if (newRollNo.length() < 6) {
				errors.rejectValue("newRollNo", "message.validation.min.roll.no");
				return;
			}
			if (!newRollNo.matches(ValidationConstants.ALPHANUMERIC_PATTERN)) {
				errors.rejectValue("newRollNo", "message.validation.alpha.numeric.roll.no");
				return;
			}
			String rollNoExists = studentRollnoChangeService.checkRollNoExist(newRollNo);
			if (rollNoExists != null) {
				errors.rejectValue("newRollNo", "message.validation.roll.no.already.exists");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void validateFile(StudentRollnoChangeDto studentDto, Errors errors) {
		MultipartFile file = studentDto.getFile();

		if (file == null || file.isEmpty()) {
			errors.rejectValue("file", "message.validation.roll.no.file.required");
			return;
		}
		String contentType = file.getContentType();
		if (contentType == null || !contentType.equals(FileUploadConstants.PDF)) {
			errors.rejectValue("file", "message.validation.error.invalid.file.type");
			return;
		}
        if (file.getSize() > FileUploadConstants.MAX_FILE_SIZE) {
            errors.rejectValue("file", "message.validation.error.invalid.file.size");
            return;
        }
	}
	
}
