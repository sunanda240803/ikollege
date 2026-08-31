package com.iitm.hosteldine.validator.hostel;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.StudentDebitAccheadConfigDto;
import com.iitm.hosteldine.form.common.TransactionDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentCreditDebitUploadValidator {
	
	private final MessageSource messageSource;

	public void validate(TransactionDto dto, BindingResult result) {
		validateVoucherDate(dto.getDate(), result);
		validateDescription(dto.getDescription(), result);
		validateDebitOrCredit(dto.getDebitOrCredit(), result);
		validateAccHead(dto.getAccHead(), result);
		validateBookType(dto.getBookType(), result);
	}

	private void validateVoucherDate(LocalDate voucherDate, BindingResult result) {
		String fieldName = "date";
		String errorCode = "date.invalid";
		if (voucherDate == null) {
			result.rejectValue(fieldName, errorCode,
					messageSource.getMessage("message.label.voucher.date", null, Locale.getDefault()) + ModelConstants.SPACE
							+ messageSource.getMessage("message.validation.is.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validateDescription(String description, BindingResult result) {
		String fieldName = "description";
		String errorCode = "description.invalid";
		if (description == null || description.isEmpty()) {
			result.rejectValue(fieldName, errorCode,
					messageSource.getMessage("message.label.description", null, Locale.getDefault()) + ModelConstants.SPACE
					+ messageSource.getMessage("message.validation.is.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validateDebitOrCredit(String debitOrCredit, BindingResult result) {
		String fieldName = "debitOrCredit";
		String errorCode = "debitOrCredit.invalid";
		if (debitOrCredit == null || debitOrCredit.isEmpty()) {
			result.rejectValue(fieldName, errorCode,
					messageSource.getMessage("message.label.upload.type", null, Locale.getDefault()) + ModelConstants.SPACE
					+ messageSource.getMessage("message.validation.is.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validateAccHead(String accHead, BindingResult result) {
		String fieldName = "accHead";
		String errorCode = "accHead.invalid";
		if (accHead == null || accHead.isEmpty()) {
			result.rejectValue(fieldName, errorCode,
					messageSource.getMessage("message.label.account.head", null, Locale.getDefault()) + ModelConstants.SPACE
					+ messageSource.getMessage("message.validation.is.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validateBookType(String bookType, BindingResult result) {
		String fieldName = "bookType";
		String errorCode = "bookType.invalid";
		if (bookType == null || bookType.isEmpty()) {
			result.rejectValue(fieldName, errorCode,
					messageSource.getMessage("message.label.select.book.type", null, Locale.getDefault()) + ModelConstants.SPACE
					+ messageSource.getMessage("message.validation.is.required", null, Locale.getDefault()));
			return;
		}
	}
	
	public void validateDemandsUpload(TransactionDto dto, BindingResult result) {
		validateVoucherDate(dto.getDate(), result);
		validateDescription(dto.getDescription(), result);
		validateAccHeadAndCharges(dto.getAccheadInputs(), result);
	}

	private void validateAccHeadAndCharges(List<StudentDebitAccheadConfigDto> accheadInputs, BindingResult result) {
		boolean hasInput = false;

		if (accheadInputs != null) {
			for (int i = 0; i < accheadInputs.size(); i++) {
				StudentDebitAccheadConfigDto dto = accheadInputs.get(i);
				Double value = dto.getValue();

				if (value != null) {
					hasInput = true;

					if (value <= 0) {
						result.rejectValue("accheadInputs[" + i + "].value", "value.invalid",
								messageSource.getMessage("message.label.charge.amount", null, Locale.getDefault())
										+ ModelConstants.SPACE + messageSource.getMessage(
												"message.validation.must.be.positive", null, Locale.getDefault()));
						return;
					}
				}
			}
		}

		if (!hasInput) {
			result.rejectValue("accheadInputs", "accheadInputs.required",
					messageSource.getMessage("message.label.acchead", null, Locale.getDefault()) + ModelConstants.SPACE
							+ messageSource.getMessage("message.validation.at.least.one.required", null,
									Locale.getDefault()));
			return;
		}
	}

}
