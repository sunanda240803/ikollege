package com.iitm.hosteldine.service.helper;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.constant.dean.AssetCategory;
import com.iitm.hosteldine.dto.dashboard.student.StudentHostelRoomVacatingRequestDto;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.exception.RecordListEmptyException;
import com.iitm.hosteldine.model.dashboard.student.VacatingHostelStudentWorkflowEntity;
import com.iitm.hosteldine.repository.dashboard.student.StudentHostelRoomVacatingRequestRepository;
import com.iitm.hosteldine.repository.dashboard.student.VacatingHostelStudentWorkflowRepository;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.service.dashboard.student.WorkflowMasterService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.validator.common.ValidationCommon;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VacatingStudentsServiceHelper {

	private final StudentHostelRoomVacatingRequestRepository studentHostelRoomVacatingRequestRepository;
	private final VacatingHostelStudentWorkflowRepository vacatingHostelStudentWorkflowRepository;
	private final MailTemplateRepository mailTemplateRepository;
	private final MailQueueService mailQueueService;
	private final Utility utility;
	private final MessageSource messageSource;

	public void fetchStudentVacatingDetails(String studentId, Long requestId, String authorityType, StudentHostelRoomVacatingRequestDto dto) throws RecordListEmptyException {
		Object[] vacatingStudentDetails = studentHostelRoomVacatingRequestRepository.getVacatingStudentDetails(studentId, requestId);

		if (vacatingStudentDetails == null || vacatingStudentDetails.length == 0) {
			throw new RecordListEmptyException(messageSource.getMessage("message.vacating.student.empty", null, Locale.getDefault()));
		}
		Object[] data = (Object[]) vacatingStudentDetails[0];
		dto.setStudentId(ValidationCommon.toString(data[0]));
		dto.setStudentName(ValidationCommon.toString(data[1]));
		dto.setGender(Utility.getGender(data[2]));
		dto.setDob(ValidationCommon.formatDate(data[3], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
		dto.setId(utility.parseLong(data[4]));
		dto.setHostelOrWardenApprovalStatus(ValidationCommon.toString(data[5]));
		dto.setRoomNo(utility.parseInt(data[6]));
		dto.setHostelName(ValidationCommon.toString(data[7]));
		dto.setVacatingDateStr(ValidationCommon.formatDate(data[8], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
		dto.setVacatingReason(ValidationCommon.toString(data[9]));
		dto.setMobileNo(utility.parseLong(data[10]));
		dto.setEmailId(ValidationCommon.toString(data[11]));
		dto.setRecommendedBy(ValidationCommon.toString(data[12]));
		dto.setCheckedBy(ValidationCommon.toString(data[13]));
		dto.setEmployeeId(ValidationCommon.toString(data[14]));
		dto.setRoomId(utility.parseInt(data[15]));
		dto.setExchangeProgPeriodFromDateStr(ValidationCommon.formatDate(data[16], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
		dto.setExchangeProgPeriodToDateStr(ValidationCommon.formatDate(data[17], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
		dto.setDonationAmount(utility.parseLong(data[18]));
		dto.setDonatorType(ValidationCommon.toString(data[19]));
		dto.setPenaltyAmount(utility.parseLong(data[20]));
		dto.setPenaltyReason(ValidationCommon.toString(data[21]));
		dto.setRoomPaintingType(ValidationCommon.toString(data[22]));
		dto.setStudentAddress(ValidationCommon.toString(data[23]));
		dto.setAuthorityType(authorityType);
		if (Strings.isNotBlank(dto.getRoomPaintingType())) {
			dto.setRoomCondition(AssetCategory.BAD_CONDITION.getValue());
		} else {
			dto.setRoomCondition(AssetCategory.GOOD_CONDITION.getValue());
		}
	}

	public String sendMailToValidators(String studentId, Long requestId, String approverName, String url) throws Exception {
		StudentHostelRoomVacatingRequestDto dto = new StudentHostelRoomVacatingRequestDto();
		fetchStudentVacatingDetails(studentId, requestId, null, dto);

		List<VacatingHostelStudentWorkflowEntity> vacatingHostelStudentWorkflowEntityList = vacatingHostelStudentWorkflowRepository.findByRequestIdAndStudentIdAndActiveFlag(requestId, studentId,
				Constants.ACTIVE_FLAG);

		// Sort the list based on the approval level
		vacatingHostelStudentWorkflowEntityList.sort(Comparator.comparingInt(VacatingHostelStudentWorkflowEntity::getApprovalLevel));
		String status = null;
		for (VacatingHostelStudentWorkflowEntity vacatingHostelStudentWorkflowEntity : vacatingHostelStudentWorkflowEntityList) {
			if (WorkflowStatus.PENDING.getStatus().equals(vacatingHostelStudentWorkflowEntity.getStatus()) && vacatingHostelStudentWorkflowEntity.getApprovalEmail() != null) {
				String urlData = url.replace("/studentVacating", "/public/studentVacating") + "/view?data=" + Utility.encryptAccommodationRequestUrl(studentId, requestId, vacatingHostelStudentWorkflowEntity.getAuthorityType());
				status = sendMail(dto, vacatingHostelStudentWorkflowEntity.getApprovalName(), vacatingHostelStudentWorkflowEntity.getApprovalEmail(),
						vacatingHostelStudentWorkflowEntity.getAuthorityType(), urlData, approverName);
			}
		}
		return status;
	}

	public String sendMail(StudentHostelRoomVacatingRequestDto dto, String name, String email, String authorityType, String urlData, String approverName) throws Exception {
		Optional<MailTemplateEntity> informationMailTemplate = mailTemplateRepository.findByMailType(MailTemplateEntity.STUDENT_VACATING_APPROVAL_MAIL);

		if (informationMailTemplate.isPresent()) {
			MailTemplateEntity template = informationMailTemplate.get();
			String subject = template.getMailSubject();

			String content = template.getMailTemplate();
			String emailHeading = Strings.EMPTY;
			if(approverName != null) {
				emailHeading = messageSource.getMessage("message.vacating.student.email.approval.content", null, Locale.getDefault());
			} else {
				emailHeading = messageSource.getMessage("message.vacating.student.email.request.content", null, Locale.getDefault());
			}
			content = content.replaceAll("#%emailHeading%#", emailHeading);
			content = content.replaceAll("#%studentName%#", dto.getStudentName() != null ? dto.getStudentName() : Strings.EMPTY);
			content = content.replaceAll("#%studentId%#", dto.getStudentId());
			content = content.replaceAll("#%address%#", dto.getStudentAddress() != null ? dto.getStudentAddress() : Strings.EMPTY);
			content = content.replaceAll("#%contactNumber%#", dto.getMobileNo() != null ? dto.getMobileNo().toString() : Strings.EMPTY);
			content = content.replaceAll("#%emailID%#", dto.getEmailId() != null ? dto.getEmailId() : Strings.EMPTY);
			content = content.replaceAll("#%vacationDate%#", dto.getVacatingDateStr() != null ? dto.getVacatingDateStr() : Strings.EMPTY);
			content = content.replaceAll("#%reasonForVacating%#", dto.getVacatingReason() != null ? dto.getVacatingReason() : Strings.EMPTY);
			content = content.replaceAll("#%hostelName%#", dto.getHostelName() != null ? dto.getHostelName() : Strings.EMPTY);
			content = content.replaceAll("#%roomNumber%#", dto.getRoomNo() != null ? dto.getRoomNo().toString() : Strings.EMPTY);
			content = content.replaceAll("#%negativeBalNote%#", Strings.EMPTY);			
			name = messageSource.getMessage("message.mail.greetings.for",null, Locale.getDefault());
			// View button and link details
			Optional<MailTemplateEntity> viewTemplate = mailTemplateRepository.findByMailType(MailTemplateEntity.VIEW_DETAILS_VACATING_STUDENT_BUTTON);

			if (viewTemplate.isPresent()) {
				String viewButtonContent = viewTemplate.get().getMailTemplate();
				viewButtonContent = viewButtonContent.replaceAll("#%viewUrl%#", urlData);

				content = content.replaceAll("#%approveOrViewButtons%#", viewButtonContent);
			}

			mailQueueService.saveMailQueue(subject, name, content, email, WorkflowMasterService.VACATING_STUDENTS, dto.getStudentId(), 1, null, null, null, null);
		}
		return "Sent";
	}
}
