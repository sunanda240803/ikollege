package com.iitm.hosteldine.validator.student;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import com.iitm.hosteldine.validator.common.ValidationConstants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentBioDataValidator {

	private final MessageSource messageSource;
	private final StudentDetailsInfoRepository studentDetailsInfoRepository;
	private final SimsConfigDataService simsConfigDataService;

	public void validate(StudentBioDataFormDetailDto dto, BindingResult result) {
		validateStudentProfileFile(dto, result);
		validateStudentSignatureFile(dto, result);
		validateParentSignatureFile(dto, result);
		validateSignedParentName(dto.getSignedParentName(), result);
		// Undertaking Form
		validateStudentId(dto.getStudentId(), result);
		validateStudentName(dto.getStudentName(), result);
		validateGender(dto.getGender(), result);
		validateDob(dto.getDob(), result);
		validateStudentPeronalEmail(dto.getStudentPersonalEmail(), result);
		validateMessName(dto.getMessName(), result);
		validateStudentAddress(dto.getStudentAddress(), result);
		validatePwd(dto.getPwd(), dto.getPwdPercentage(), dto.getPwdDescription(), result);
		validateStudentMobile(dto.getStudentMobile(), result);
		validateCategory(dto.getCategory(), result);
		validateFacultyDetails(dto.getFacultyName(), dto.getFacultyEmail(), dto.getFacultyContactNo(), result);
		validateAadhaarNumber(dto.getAadhaarNumber(), result);
		validatePanNum(dto.getPanNum(), result);
//		validateFamDetails(dto.getFamilyDetails(), dto.getSignedParentName(), dto.getStudentMobile(),
//				dto.getStudentPersonalEmail(), dto.getGuardianStatus(), dto.getGuardianDetails(), result);
	}

//	private void validateFamDetails(List<StudentBioDataFamilyInfoDto> familyDetails, String signedParentName,
//			Long personalMobileNo, String personalEmail, String guardianStatus,
//			StudentBioDataFamilyInfoDto guardianDetails, BindingResult result) {
//		StudentBioDataFamilyInfoDto familyDto = new StudentBioDataFamilyInfoDto();
//		familyDto.setErrorList(new ArrayList<>());
//		List<String> relationNames = new ArrayList<>();
//		if (guardianStatus != null && guardianStatus.equals(ModelConstants.STATUS_ACTIVE)) {
//			if (guardianDetails == null || guardianDetails.getRelationName() == null) {
//				result.rejectValue("guardianDetails.relationName", "guardianDetails.relationName.invalid",
//						messageSource.getMessage("message.validation.local.guardian.details.relationName", null,
//								Locale.getDefault()));
//			} else {
//				relationNames.add(guardianDetails.getRelationName().trim());
//			}
//			if (guardianDetails == null || guardianDetails.getEmail() == null) {
//				result.rejectValue("guardianDetails.email", "guardianDetails.email.invalid",
//						messageSource.getMessage("message.validation.email.id.required", null, Locale.getDefault()));
//			}
//			if (guardianDetails != null && guardianDetails.getEmail() != null
//					&& !guardianDetails.getEmail().matches(ValidationConstants.EMAIL_PATTERN)) {
//				result.rejectValue("guardianDetails.email", "guardianDetails.email.invalid", messageSource
//						.getMessage("message.validation.local.guardian.details.email", null, Locale.getDefault()));
//			}
//			if (guardianDetails == null || guardianDetails.getMobileNo() == null) {
//				result.rejectValue("guardianDetails.mobileNo", "guardianDetails.mobileNo.invalid", messageSource
//						.getMessage("message.validation.student.details.mob.no.required", null, Locale.getDefault()));
//			}
//			if (guardianDetails != null && guardianDetails.getMobileNo() != null
//					&& (guardianDetails.getMobileNo() < 10 || guardianDetails.getMobileNo() > 10)) {
//				result.rejectValue("guardianDetails.mobileNo", "guardianDetails.mobileNo.invalid", messageSource
//						.getMessage("message.validation.student.details.mob.no", null, Locale.getDefault()));
//			}
//			if (guardianDetails == null || guardianDetails.getAddress() == null) {
//				result.rejectValue("guardianDetails.address", "guardianDetails.address.invalid", messageSource
//						.getMessage("message.validation.local.guardian.details.address", null, Locale.getDefault()));
//			}
//		}
//
//		for (int i = 0; i < familyDetails.size(); i++) {
//			boolean error = false;
//			StringBuilder errorDetails = new StringBuilder();
//			StudentBioDataFamilyInfoDto dto = familyDetails.get(i);
//			String relationType = dto.getRelationType();
//
//			if (relationType != null && !relationType.isEmpty()) {
//				String relationName = dto.getRelationName();
//				Long mobileNo = dto.getMobileNo();
//				String email = dto.getEmail();
//
//				if (relationName != null && !relationName.trim().isEmpty()) {
//					relationNames.add(relationName.trim());
//				}
//
//				if (Constants.FATHER.equalsIgnoreCase(relationType)
//						|| Constants.MOTHER.equalsIgnoreCase(relationType)) {
//					if (relationName == null || relationName.trim().isEmpty()) {
//						handleCellError(messageSource.getMessage("message.validation.family.details.name", null,
//								Locale.getDefault()), i, dto, errorDetails);
//						error = true;
//					}
//					if (mobileNo == null) {
//						handleCellError(messageSource.getMessage("message.validation.family.details.mob.no", null,
//								Locale.getDefault()), i, dto, errorDetails);
//						error = true;
//					}
//					if (dto.getAge() == null) {
//						handleCellError(messageSource.getMessage("message.validation.family.details.age", null,
//								Locale.getDefault()), i, dto, errorDetails);
//						error = true;
//					}
//				} else {
//					if (relationName != null && !relationName.trim().isEmpty()) {
//						if (mobileNo == null) {
//							handleCellError(messageSource.getMessage("message.validation.family.details.mob.no", null,
//									Locale.getDefault()), i, dto, errorDetails);
//							error = true;
//						}
//						if (dto.getAge() == null) {
//							handleCellError(messageSource.getMessage("message.validation.family.details.age", null,
//									Locale.getDefault()), i, dto, errorDetails);
//							error = true;
//						}
//					}
//				}
//
//				if (personalMobileNo != null && personalMobileNo.equals(mobileNo)) {
//					handleCellError(messageSource.getMessage("message.validation.family.details.mob.no.personal.match", null,
//							Locale.getDefault()), i, dto, errorDetails);
//					error = true;
//				}
//
//				if (personalEmail != null && !personalEmail.trim().isEmpty() && email != null && !email.trim().isEmpty()
//						&& personalEmail.trim().equalsIgnoreCase(email.trim())) {
//					handleCellError(messageSource.getMessage("message.validation.family.details.email.personal.match", null,
//							Locale.getDefault()), i, dto, errorDetails);
//					error = true;
//				}
//				if (error) {
//					familyDto.getErrorList().add(ModelConstants.ROW + ModelConstants.SPACE + (i + 1)
//						+ ModelConstants.COLAN + ModelConstants.SPACE + errorDetails.toString());
//				}
//			}
//		}
//
//		boolean matched = relationNames.stream()
//				.anyMatch(name -> name.equalsIgnoreCase(signedParentName != null ? signedParentName.trim() : ""));
//
//		if (!matched) {
//			result.rejectValue("signedParentName", "signedParentName.invalid", messageSource
//					.getMessage("message.validation.family.details.name.mismatch", null, Locale.getDefault()));
//		}
//	}
//
//	private void handleCellError(String errorMessage, int rowNumber, StudentBioDataFamilyInfoDto dto,
//			StringBuilder errorDetails) {
//		cellError(errorMessage, rowNumber, dto);
//		dto.setError("error");
//		errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg()).append(ModelConstants.NEW_LINE);
//	}
//
//	private static void cellError(String msg, int rowCount, StudentBioDataFamilyInfoDto object) {
//		if (rowCount == 0)
//			object.setExcelErrorMsg(msg);
//		else
//			object.setExcelErrorMsg(" Family Detail's Row " + (rowCount + 1) + " : " + msg);
//		object.setError("error");
//	}
	
	private void validateStudentProfileFile(StudentBioDataFormDetailDto dto, BindingResult result) {
		MultipartFile file = dto.getStudentProfile();
		String field = "studentProfile";
		String errorCode = "studentProfile.invalid";

		if (file == null || file.isEmpty()) {
			result.rejectValue(field, errorCode,
					messageSource.getMessage("message.validation.upload.documents.picture", null, Locale.getDefault()));
			return;
		}
		String contentType = file.getContentType();
		if (contentType == null || (!contentType.equals(FileUploadConstants.JPG)
				&& !contentType.equals(FileUploadConstants.PNG) && !contentType.equals(FileUploadConstants.SVG_XML))) {
			result.rejectValue(field, errorCode,
					messageSource.getMessage("message.invalid.file.format", null, Locale.getDefault()));
			return;
		}
		if (file.getSize() > FileUploadConstants.MAX_FILE_SIZE) {
			result.rejectValue(field, errorCode,
					messageSource.getMessage("message.validation.image.size", null, Locale.getDefault()));
			return;
		}
	}

	private void validateStudentSignatureFile(StudentBioDataFormDetailDto dto, BindingResult result) {
		MultipartFile file = dto.getStudentSignature();
		String field = "parentSignature";
		String errorCode = "parentSignature.invalid";

		if (file == null || file.isEmpty()) {
			result.rejectValue(field, errorCode,
					messageSource.getMessage("message.validation.upload.documents.picture", null, Locale.getDefault()));
			return;
		}
		String contentType = file.getContentType();
		if (contentType == null || (!contentType.equals(FileUploadConstants.JPG)
				&& !contentType.equals(FileUploadConstants.PNG) && !contentType.equals(FileUploadConstants.SVG_XML))) {
			result.rejectValue(field, errorCode,
					messageSource.getMessage("message.invalid.file.format", null, Locale.getDefault()));
			return;
		}
		if (file.getSize() > FileUploadConstants.MAX_FILE_SIZE) {
			result.rejectValue(field, errorCode,
					messageSource.getMessage("message.validation.image.size", null, Locale.getDefault()));
			return;
		}
	}

	private void validateParentSignatureFile(StudentBioDataFormDetailDto dto, BindingResult result) {
		MultipartFile file = dto.getStudentSignature();
		String field = "studentSignature";
		String errorCode = "studentSignature.invalid";

		if (file == null || file.isEmpty()) {
			result.rejectValue(field, errorCode, messageSource.getMessage("message.validation.upload.documents.signature", null, Locale.getDefault()));
			return;
		}
		String contentType = file.getContentType();
		if (contentType == null || (!contentType.equals(FileUploadConstants.JPG)
				&& !contentType.equals(FileUploadConstants.PNG) && !contentType.equals(FileUploadConstants.SVG_XML))) {
			result.rejectValue(field, errorCode,
					messageSource.getMessage("message.invalid.file.format", null, Locale.getDefault()));
			return;
		}
		if (file.getSize() > FileUploadConstants.MAX_FILE_SIZE) {
			result.rejectValue(field, errorCode,
					messageSource.getMessage("message.validation.image.size", null, Locale.getDefault()));
			return;
		}
	}

	private void validateSignedParentName(String signedParentName, BindingResult result) {
		validateRequiredField(signedParentName, "signedParentName", "signedParentName.invalid",
				"message.validation.upload.documents.parent.name", result);
	}

	private void validateStudentId(String studentId, BindingResult result) {
		String field = "studentId";
		String errorCode = "studentId.invalid";

		validateRequiredField(studentId, field, errorCode, "message.validation.no.roll.no", result);
		if (studentId.length() < 6) {
			result.rejectValue(field, errorCode,
					messageSource.getMessage("message.validation.min.roll.no", null, Locale.getDefault()));
			return;
		}
		if (!studentId.matches(ValidationConstants.ALPHANUMERIC_PATTERN)) {
			result.rejectValue(field, errorCode,
					messageSource.getMessage("message.validation.alpha.numeric.roll.no", null, Locale.getDefault()));
			return;
		}
		Optional<StudentDetailsInfoEntity> studentDetails = studentDetailsInfoRepository.checkStudentIdExist(studentId,
				ModelConstants.STATUS_ACTIVE);
		if (studentDetails != null && !studentDetails.isEmpty()) {
			result.rejectValue(field, errorCode,
					messageSource.getMessage("message.validation.roll.no.already.exists", null, Locale.getDefault()));
			return;
		}
	}

	private void validateStudentName(String studentName, BindingResult result) {
		validateRequiredField(studentName, "studentName", "studentName.invalid",
				"message.validation.student.details.name", result);
	}

	private void validateGender(String gender, BindingResult result) {
		validateRequiredField(gender, "gender", "gender.invalid", "message.validation.student.details.gender", result);
	}

	private void validateDob(LocalDate dob, BindingResult result) {
		String field = "dob";
		String errorCode = "dob.invalid";
		if (dob == null) {
			result.rejectValue(field, errorCode,
					messageSource.getMessage("message.validation.student.details.dob", null, Locale.getDefault()));
			return;
		}

		LocalDate minDate = LocalDate.of(1900, 1, 1);
		LocalDate maxDate = LocalDate.of(LocalDate.now().getYear() - 16, 12, 31);

		if (dob.isBefore(minDate) || dob.isAfter(maxDate)) {
			result.rejectValue(field, errorCode, messageSource
					.getMessage("message.validation.student.details.dob.range", null, Locale.getDefault()));
		}
	}

	private void validateStudentPeronalEmail(String studentPersonalEmail, BindingResult result) {
		if (studentPersonalEmail != null && !studentPersonalEmail.isEmpty()) {
			if (!studentPersonalEmail.matches(ValidationConstants.EMAIL_PATTERN)) {
				result.rejectValue("studentPersonalEmail", "studentPersonalEmail.invalid",
						messageSource.getMessage("message.validation.email.id", null, Locale.getDefault()));
				return;
			}
		}
	}

	private void validateMessName(String messName, BindingResult result) {
		validateRequiredField(messName, "messName", "messName.invalid", "message.validation.student.details.mess.pref",
				result);
	}

	private void validateStudentAddress(String studentAddress, BindingResult result) {
		validateRequiredField(studentAddress, "studentAddress", "studentAddress.invalid",
				"message.validation.student.registration.address.required", result);
	}

	private void validatePwd(String pwd, Integer pwdPercentage, String pwdDesc, BindingResult result) {
		if (pwd != null && pwd.equals(ModelConstants.STATUS_ACTIVE)) {
			if (pwdPercentage == null) {
				result.rejectValue("pwdPercentage", "pwdPercentage.invalid", messageSource
						.getMessage("message.validation.pwd.percentage.required", null, Locale.getDefault()));
			}
			if (pwdDesc == null || pwdDesc.isEmpty()) {
				result.rejectValue("pwdDescription", "pwdDescription.invalid", messageSource
						.getMessage("message.validation.pwd.description.required", null, Locale.getDefault()));
			}
		}
	}

	private void validateStudentMobile(Long studentMobile, BindingResult result) {
		String field = "studentMobile";
		String errorCode = "studentMobile.invalid";
		if (studentMobile == null) {
			result.rejectValue(field, errorCode, messageSource
					.getMessage("message.validation.student.details.mob.no.required", null, Locale.getDefault()));
			return;
		}
		if (studentMobile < 10 || studentMobile > 10) {
			result.rejectValue(field, errorCode,
					messageSource.getMessage("message.validation.student.details.mob.no", null, Locale.getDefault()));
		}
	}

	private void validateCategory(String category, BindingResult result) {
		validateRequiredField(category, "category", "category.invalid", "message.validation.student.details.category",
				result);
	}

	private void validateFacultyDetails(String facultyName, String facultyEmail, Long facultyContactNo,
			BindingResult result) {
		String field = "facultyEmail";
		String errorCode = "facultyEmail.invalid";
		String field1 = "facultyContactNo";
		String errorCode1 = "facultyContactNo.invalid";

		String guideEmail = simsConfigDataService.getSimConfigValue(SimsConfigDataService.GUIDE_EMAIL);

		if (facultyName != null && !facultyName.isEmpty() && !facultyName.equals("0")) {
			if (facultyEmail == null || facultyEmail.isEmpty()) {
				result.rejectValue(field, errorCode,
						messageSource.getMessage("message.label.faculty.email.required", null, Locale.getDefault()));
			} else {
				List<String> allowedDomains = Arrays.stream(guideEmail.split(",")).map(String::trim)
						.collect(Collectors.toList());

				boolean validDomain = allowedDomains.stream().anyMatch(domain -> facultyEmail.endsWith(domain));

				if (!validDomain) {
					result.rejectValue(field, errorCode, messageSource
							.getMessage("message.validation.invalid.email.domain", null, Locale.getDefault()));
				}
			}

			if (facultyContactNo == null) {
				result.rejectValue(field1, errorCode1, messageSource
						.getMessage("message.validation.student.details.mob.no", null, Locale.getDefault()));
			} else if (facultyContactNo < 10 || facultyContactNo > 10) {
				result.rejectValue(field1, errorCode1, messageSource
						.getMessage("message.validation.student.details.mob.no", null, Locale.getDefault()));
			}
		}
	}

	private void validateAadhaarNumber(Long aadhaarNumber, BindingResult result) {
		if (aadhaarNumber != null) {
			if (aadhaarNumber < 12 || aadhaarNumber > 12) {
				result.rejectValue("aadhaarNumber", "aadhaarNumber.invalid", messageSource
						.getMessage("message.validation.student.details.aadhar", null, Locale.getDefault()));
			}
		}
	}

	private void validatePanNum(String panNum, BindingResult result) {
		if (panNum != null && !panNum.isEmpty()) {
			if (!panNum.matches(ValidationConstants.PAN_NUMBER_PATTERN)) {
				result.rejectValue("panNum", "panNum.invalid",
						messageSource.getMessage("message.validation.student.details.pan", null, Locale.getDefault()));
			}
		}
	}

	private void validateRequiredField(String value, String field, String errorCode, String messageKey,
			BindingResult result) {
		if (value == null || value.isEmpty()) {
			result.rejectValue(field, errorCode, messageSource.getMessage(messageKey, null, Locale.getDefault()));
			return;
		}
	}
}
