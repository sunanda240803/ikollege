package com.iitm.hosteldine.validator.mess;

import java.time.LocalDate;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.mess.MessAllottedListDTO;
import com.iitm.hosteldine.dto.mess.MessBillSummaryDto;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.service.mess.MessAllottedListService;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessAllotmentListValidator {

	private final MessageSource messageSource;
	private final MessAllottedListService messAllottedListService;
	private final MessMasterCommonService messMasterCommonService;

	public void validate(MessAllottedListDTO dto, BindingResult result) {
		if (WorkflowStatus.CHANGE.getStatus().equalsIgnoreCase(dto.getType())) {
			validateChangeMessName(dto.getChangeMessId(), result);
			validateDescription(dto.getDescription(), result);
			validateEffectiveFromDate(dto.getFromDate(), dto.getEffectiveFromDate(), result);
		}
		if (WorkflowStatus.REMOVE.getStatus().equalsIgnoreCase(dto.getType())) {
			validateDescription(dto.getDescription(), result);
			validateEffectiveTill(dto.getFromDate(), dto.getEffectiveTill(), result);
		}
		if (messageSource.getMessage("message.button.add", null, Locale.getDefault()).equalsIgnoreCase(dto.getType())) {
			validateStudentId(dto.getStudentId(), result);
			validateFromDate(dto.getFromDate(), result);
			validateNewMessName(dto.getMessId(), result);
		}
	}

	private void validateStudentId(String studentId, BindingResult result) {
		String studentIdField = "studentId";
		String invalidStudentIdCode = "studentId.invalid";
		if (studentId == null || studentId.isEmpty()) {
			result.rejectValue(studentIdField, invalidStudentIdCode, messageSource
					.getMessage("message.label.validation.student.id.is.required", null, Locale.getDefault()));
			return;
		}
		String studentName = messAllottedListService.CheckStudentExists(studentId);
		if (studentId != null && !studentId.isEmpty() && (studentName == null || studentName.isEmpty())) {
			result.rejectValue(studentIdField, invalidStudentIdCode,
					messageSource.getMessage("message.label.validation.student.id.exist", null, Locale.getDefault()));
			return;
		}

		if (messAllottedListService.checkStudentInCurrentMessPeriod(studentId)) {
			result.rejectValue(studentIdField, invalidStudentIdCode, messageSource
					.getMessage("message.validation.student.already.added.mess.period", null, Locale.getDefault()));
			return;
		}
	}

	private void validateFromDate(LocalDate fromDate, BindingResult result) {
		validateDateField(fromDate, "fromDate", "fromDate.invalid", "message.validation.from.date.required", result);
	}

	private void validateEffectiveFromDate(LocalDate fromDate, LocalDate effectiveFromDate, BindingResult result) {
		validateEffectiveDateField(fromDate, effectiveFromDate, "effectiveFromDate", "effectiveFromDate.invalid",
				"message.validation.effective.from.date.required", result);
	}

	private void validateEffectiveDateField(LocalDate fromDate, LocalDate date, String fieldName, String errorCode,
			String requiredMessageKey, BindingResult result) {
		if (date == null) {
			result.rejectValue(fieldName, errorCode,
					messageSource.getMessage(requiredMessageKey, null, Locale.getDefault()));
			return;
		}

		MessMasterControllerDto currentMessPeriod = messMasterCommonService.getCurrentMessPeriod();
		LocalDate diningToDate = currentMessPeriod.getDiningToDate();

		if (!date.isAfter(fromDate) || date.isAfter(diningToDate)) {
			result.rejectValue(fieldName, errorCode, messageSource
					.getMessage("message.label.effective.date.period.limit", null, Locale.getDefault()));
		}
	}

	private void validateEffectiveTill(LocalDate fromDate, LocalDate effectiveTill, BindingResult result) {
		String fieldName = "effectiveTill";
		String errorCode = "effectiveTill.invalid";
		if (effectiveTill == null) {
			result.rejectValue(fieldName, errorCode,
					messageSource.getMessage("message.validation.effective.till.required", null, Locale.getDefault()));
			return;
		}

		MessMasterControllerDto currentMessPeriod = messMasterCommonService.getCurrentMessPeriod();
		LocalDate diningToDate = currentMessPeriod.getDiningToDate();

		if (effectiveTill.isBefore(fromDate) || effectiveTill.isAfter(diningToDate)) {
			result.rejectValue(fieldName, errorCode, messageSource
					.getMessage("message.label.effective.date.period.limit", null, Locale.getDefault()));
		}
	}

	private void validateDateField(LocalDate date, String fieldName, String errorCode, String requiredMessageKey,
			BindingResult result) {
		if (date == null) {
			result.rejectValue(fieldName, errorCode,
					messageSource.getMessage(requiredMessageKey, null, Locale.getDefault()));
			return;
		}

		MessMasterControllerDto currentMessPeriod = messMasterCommonService.getCurrentMessPeriod();

		if (date.isBefore(LocalDate.now()) || date.isBefore(currentMessPeriod.getDiningFromDate()) || date.isAfter(currentMessPeriod.getDiningToDate()))  {
			result.rejectValue(fieldName, errorCode, messageSource
					.getMessage("message.validation.you.selected.out.of.mess.period", null, Locale.getDefault()));
		}
	}

	private void validateChangeMessName(Long messId, BindingResult result) {
		validateMessName(messId, "changeMessId", "changeMessId.invalid",
				"message.validation.effective.from.date.required", result);
	}

	private void validateNewMessName(Long messId, BindingResult result) {
		validateMessName(messId, "messId", "messId.invalid",
				"message.validation.student.select.mess", result);
	}

	private void validateMessName(Long messId, String fieldName, String errorCode, String requiredMessageKey,
			BindingResult result) {
		if (messId == null || messId == 0) {
			result.rejectValue(fieldName, errorCode,
					messageSource.getMessage(requiredMessageKey, null, Locale.getDefault()));
			return;
		}
	}
	
	private void validateDescription(String description, BindingResult result) {
		String descriptionField = "description";
		String invalidDescriptionCode = "description.invalid";
		if (description == null || description.isEmpty()) {
			result.rejectValue(descriptionField, invalidDescriptionCode, messageSource
					.getMessage("message.validation.mess.allotted.list.description", null, Locale.getDefault()));
			return;
		}
	}

	public void validateBulkUpload(MessAllottedListDTO dto, BindingResult result) {
		validateMessPeriod(dto.getMessPeriod(), result);
		validateAllocationType(dto.getAllocationType(), result);
	}

	private void validateMessPeriod(String messPeriod, BindingResult result) {
		String messPeriodField = "messPeriod";
		String invalidMessPeriodCode = "messPeriod.invalid";
		if (messPeriod == null || messPeriod.isEmpty()) {
			result.rejectValue(messPeriodField, invalidMessPeriodCode,
					messageSource.getMessage("message.validation.mess.period.required", null, Locale.getDefault()));
			return;
		}
	}

	private void validateAllocationType(String allocationType, BindingResult result) {
		String allocationTypeField = "allocationType";
		String invalidAllocationTypeCode = "allocationType.invalid";
		if (allocationType == null || allocationType.isEmpty()) {
			result.rejectValue(allocationTypeField, invalidAllocationTypeCode,
					messageSource.getMessage("message.label.allocation.type.required", null, Locale.getDefault()));
			return;
		}
	}

	public void validateMessBill(MessBillSummaryDto dto, BindingResult result) {
		validateMessStudentId(dto.getStudentId(), result);
		validateMessFromDate(dto.getEffectiveFromDate(), result);
		validateComments(dto.getComments(), result);
	}

	private void validateMessStudentId(String studentId, BindingResult result) {
		String studentIdField = "studentId";
		String invalidStudentIdCode = "studentId.invalid";
		if (studentId == null || studentId.isEmpty()) {
			result.rejectValue(studentIdField, invalidStudentIdCode, messageSource
					.getMessage("message.label.validation.student.id.is.required", null, Locale.getDefault()));
			return;
		}
		String studentName = messAllottedListService.CheckStudentExists(studentId);
		if (studentId != null && !studentId.isEmpty() && (studentName == null || studentName.isEmpty())) {
			result.rejectValue(studentIdField, invalidStudentIdCode,
					messageSource.getMessage("message.label.validation.student.id.exist", null, Locale.getDefault()));
			return;
		}

		if (!messAllottedListService.checkStudentInCurrentMessPeriod(studentId)) {
			result.rejectValue(studentIdField, invalidStudentIdCode, messageSource
					.getMessage("message.validation.student.not.exist.mess.period", null, Locale.getDefault()));
			return;
		}
	}

	private void validateMessFromDate(LocalDate fromDate, BindingResult result) {
		validateDateField(fromDate, "effectiveFromDate", "effectiveFromDate.invalid",
				"message.validation.effective.from.date.required", result);
	}

	private void validateComments(String comments, BindingResult result) {
		String field = "comments";
		String errorCode = "comments.invalid";
		if (comments == null || comments.isEmpty()) {
			result.rejectValue(field, errorCode,
					messageSource.getMessage("message.validation.comments.required", null, Locale.getDefault()));
			return;
		}
	}
	
}
