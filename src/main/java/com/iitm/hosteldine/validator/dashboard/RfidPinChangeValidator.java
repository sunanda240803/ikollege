package com.iitm.hosteldine.validator.dashboard;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.iitm.hosteldine.dto.student.UserFpCardDto;
import com.iitm.hosteldine.service.UserFpCardService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RfidPinChangeValidator implements Validator {

	private final UserFpCardService userFpCardService;

	@Override
	public boolean supports(Class<?> clazz) {
		return UserFpCardDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserFpCardDto dto = (UserFpCardDto) target;
		if (Strings.isNotEmpty(dto.getCurrentPin())) {
			validateCurrentPin(dto.getCurrentPin(), errors);
			validateNewPinNo(dto.getCurrentPin(), dto.getPinNo(), errors);
			validateRetypeNewPinNo(dto.getPinNo(), dto.getRetypePinNo(), errors);
		}
	}

	private void validateCurrentPin(String currentPin, Errors errors) {
		try {
			if (currentPin == null || currentPin.isEmpty()) {
				errors.rejectValue("currentPin", "message.validation.currentpin.required");
				return;
			}
			if (currentPin.length() < 3) {
				errors.rejectValue("currentPin", "message.validation.pin.length");
				return;
			}
			String accessPinNo = userFpCardService.getCurrentRfidPinNumber();
			if (!currentPin.equals(accessPinNo)) {
				errors.rejectValue("currentPin", "message.validation.currentpin.mismatch");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void validateNewPinNo(String currentPin, String newPinNo, Errors errors) {
		if (newPinNo == null || newPinNo.isEmpty()) {
			errors.rejectValue("pinNo", "message.validation.newpin.required");
			return;
		}
		if (newPinNo.length() < 3) {
			errors.rejectValue("pinNo", "message.validation.pin.length");
			return;
		}
		if (newPinNo.equals(currentPin)) {
			errors.rejectValue("pinNo", "message.validation.new.pin.mismatch");
			return;
		}
	}

	private void validateRetypeNewPinNo(String newPinNo, String retypePinNo, Errors errors) {
		if (retypePinNo == null || retypePinNo.isEmpty()) {
			errors.rejectValue("retypePinNo", "message.validation.retypepin.required");
			return;
		}
		if (retypePinNo.length() < 3) {
			errors.rejectValue("retypePinNo", "message.validation.pin.length");
			return;
		}
		if (!newPinNo.equals(retypePinNo)) {
			errors.rejectValue("retypePinNo", "message.validation.retype.pin.mismatch");
			return;
		}
	}

}
