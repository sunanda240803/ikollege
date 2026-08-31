package com.iitm.hosteldine.validator.hostel;

import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.UploadReceiptsDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UploadReceiptsValidator {

	private final MessageSource messageSource;

	public void validate(UploadReceiptsDto dto, BindingResult result) {
		validateReceiptType(dto.getReceiptType(), result);
		validateVoucherDate(dto.getDate(), result);
		validateBookType(dto.getBookType(), result);
		if (dto.getReceiptType() != null && !dto.getReceiptType().isEmpty()) {
			if (dto.getReceiptType().equals("fee")) {
				validateFeeTypes(dto.getFeeTypes(), result);
			}
			if (dto.getReceiptType().equals("scholarship")) {
				validateDescription(dto.getDescription(), result);
			}
			if (!dto.getReceiptType().equals("mess") && !dto.getReceiptType().equals("rebate")) {
				validateBank(dto.getBank(), result);
			} else {
				validateAccHead(dto.getAccHead(), result);
				validateDescription(dto.getDescription(), result);
				validateFromDate(dto.getFromDate(), result);
				validateToDate(dto.getToDate(), result);
			}
		}
	}

	private void validateVoucherDate(LocalDate voucherDate, BindingResult result) {
		validateRequiredDateField(voucherDate, "voucherDate", "voucherDate.invalid", "message.label.voucher.date", result);
	}

	private void validateFeeTypes(String feeTypes, BindingResult result) {
		validateRequiredField(feeTypes, "feeTypes", "feeTypes.invalid", "message.label.fee.types", result);
	}

	private void validateReceiptType(String uploadType, BindingResult result) {
		validateRequiredField(uploadType, "receiptType", "receiptType.invalid", "message.label.receipt.type", result);
	}

	private void validateBookType(String bookType, BindingResult result) {
		validateRequiredField(bookType, "bookType", "bookType.invalid", "message.label.mess.card.type", result);
	}

	private void validateBank(String bank, BindingResult result) {
		validateRequiredField(bank, "bank", "bank.invalid", "message.label.bank", result);
	}

	private void validateAccHead(String accHead, BindingResult result) {
		validateRequiredField(accHead, "accHead", "accHead.invalid", "message.label.account.head", result);
	}

	private void validateDescription(String description, BindingResult result) {
		validateRequiredField(description, "description", "description.invalid", "message.label.description", result);
	}

	private void validateFromDate(LocalDate fromDate, BindingResult result) {
		validateRequiredDateField(fromDate, "fromDate", "fromDate.invalid", "message.label.from.date", result);
	}

	private void validateToDate(LocalDate toDate, BindingResult result) {
		validateRequiredDateField(toDate, "toDate", "toDate.invalid", "message.label.to.date", result);
	}
	
	private void validateRequiredField(String value, String fieldName, String errorCode, String labelKey, BindingResult result) {
		if (value == null || value.isEmpty()) {
			String message = messageSource.getMessage(labelKey, null, Locale.getDefault())
					+ ModelConstants.SPACE + messageSource.getMessage("message.validation.is.required", null, Locale.getDefault());
			result.rejectValue(fieldName, errorCode, message);
		}
	}
	
	private void validateRequiredDateField(LocalDate value, String fieldName, String errorCode, String labelKey, BindingResult result) {
		if (value == null) {
			String message = messageSource.getMessage(labelKey, null, Locale.getDefault())
					+ ModelConstants.SPACE + messageSource.getMessage("message.validation.is.required", null, Locale.getDefault());
			result.rejectValue(fieldName, errorCode, message);
		}
	}

}
