package com.iitm.hosteldine.validator.hostel;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.iitm.hosteldine.dto.hostel.HostelEnrollmentConfigurationDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HostelEnrollmentConfigValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return HostelEnrollmentConfigurationDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		HostelEnrollmentConfigurationDto dto = (HostelEnrollmentConfigurationDto) target;
		validateFromDate(dto.getFromDate(), errors);
		validateToDate(dto.getToDate(), dto.getFromDate(), errors);
		validateStartDate(dto.getStartDate(), errors);
	}

	private void validateFromDate(LocalDate fromDate, Errors errors) {
		if (fromDate == null) {
			errors.rejectValue("fromDate", "message.validation.from.date.required");
			return;
		}
		if (fromDate.isBefore(LocalDate.now())) {
			errors.rejectValue("fromDate", "message.validation.from.date.range");
			return;
		}
	}

	private void validateToDate(LocalDate toDate, LocalDate fromDate, Errors errors) {
		if (toDate == null) {
			errors.rejectValue("toDate", "message.validation.to.date.required");
			return;
		}
		if (toDate.isBefore(LocalDate.now())) {
			errors.rejectValue("toDate", "message.validation.to.date.future");
			return;
		}
		if (fromDate != null && (toDate.isEqual(fromDate) || toDate.isBefore(fromDate))) {
			errors.rejectValue("toDate", "message.validation.to.date.greater");
		}
	}

	private void validateStartDate(LocalDate startDate, Errors errors) {
		if (startDate == null) {
			errors.rejectValue("startDate", "message.validation.start.date.required");
			return;
		}
	}
}
