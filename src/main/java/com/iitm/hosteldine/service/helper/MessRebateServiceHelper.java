package com.iitm.hosteldine.service.helper;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.dean.DeanMessRebateDto;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.exception.RecordListEmptyException;
import com.iitm.hosteldine.model.mess.MessRebateWorkflowEntity;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.repository.mess.MessRebateRepository;
import com.iitm.hosteldine.repository.mess.MessRebateWorkflowRepository;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.dashboard.student.WorkflowMasterService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.validator.common.ValidationCommon;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessRebateServiceHelper {
	
	private final MessRebateRepository messRebateRepository;
	private final FileService fileService;
	private final MessRebateWorkflowRepository messRebateWorkflowRepository;
	private final MailTemplateRepository mailTemplateRepository;
	private final MailQueueService mailQueueService;
	private final MessageSource messageSource;
	
	public String sendMailToValidators(String studentId, Long requestId, String url, HttpServletRequest request) throws Exception {
		DeanMessRebateDto dto = new DeanMessRebateDto();
		fetchStudentMessRebateDetails(studentId, requestId, dto);
		
		List<MessRebateWorkflowEntity> messRebateWorkflowEntityList = new ArrayList<>(
				messRebateWorkflowRepository.findByRequestIdAndStudentIdAndActiveFlag(requestId, studentId, Constants.ACTIVE_FLAG));	
		
		// Sort the list based on the approval level
		messRebateWorkflowEntityList.sort(Comparator.comparingInt(MessRebateWorkflowEntity::getApprovalLevel));
		String status = null;
		for (MessRebateWorkflowEntity messRebateWorkflowEntity : messRebateWorkflowEntityList) {
			if (WorkflowStatus.PENDING.getStatus().equals(messRebateWorkflowEntity.getApprovalStatus()) && messRebateWorkflowEntity.getGuideEmail() != null) {
                String modifiedUrl = url.replace(request.getContextPath(), request.getContextPath() + "/public");
                String urlData = modifiedUrl + "/view?data=" + Utility.encryptAccommodationRequestUrl(studentId, requestId, messRebateWorkflowEntity.getAuthorityType());
				status = sendMail(dto, messRebateWorkflowEntity.getGuideName(), messRebateWorkflowEntity.getGuideEmail(), urlData, MailTemplateEntity.MESS_REBATE_APPROVAL_MAIL, Strings.EMPTY, Strings.EMPTY);
			}
		}
		return status;
	}
	
	public String sendMailToStudent(String studentId, Long requestId, String url, String rejectionReason) throws Exception {
		DeanMessRebateDto dto = new DeanMessRebateDto();
		fetchStudentMessRebateDetails(studentId, requestId, dto);
		
		String status = null;
		if ((WorkflowStatus.APPROVED.getStatus().equals(dto.getStatus()) || WorkflowStatus.REJECTED.getStatus().equals(dto.getStatus()))
				&& dto.getStudentEmail() != null) {
			String emailHeading = Strings.EMPTY;
			if(WorkflowStatus.APPROVED.getStatus().equals(dto.getStatus())){
				emailHeading = messageSource.getMessage("message.rebate.mail.approval", null, Locale.getDefault());
			} else {
				emailHeading = messageSource.getMessage("message.rebate.mail.rejected", null, Locale.getDefault());
			}
			status = sendMail(dto, dto.getStudentName(), dto.getStudentEmail(), Strings.EMPTY, MailTemplateEntity.MESS_REBATE_STUDENT_APPROVAL, emailHeading, rejectionReason);
		}
		return status;
	}
	
	private String sendMail(DeanMessRebateDto dto, String name, String email, String urlData, String mailType, String emailHeading, String rejectionReason) throws Exception {
		Optional<MailTemplateEntity> informationMailTemplate = mailTemplateRepository.findByMailType(mailType);

		if (informationMailTemplate.isPresent()) {
			MailTemplateEntity template = informationMailTemplate.get();
			String subject = template.getMailSubject();

			String content = template.getMailTemplate();
			content = content.replaceAll("#%email_heading%#", emailHeading);
			content = content.replaceAll("#%student_name%#", dto.getStudentName() != null ?  dto.getStudentName() : Strings.EMPTY);
			content = content.replaceAll("#%student_id%#", dto.getStudentId());
			content = content.replaceAll("#%address%#", dto.getStudentAddress() != null ?  dto.getStudentAddress() : Strings.EMPTY);
			content = content.replaceAll("#%contact_number%#", dto.getStudentMobile() != null ?  dto.getStudentMobile() : Strings.EMPTY);
			content = content.replaceAll("#%student_email%#", dto.getStudentEmail() != null ?  dto.getStudentEmail() : Strings.EMPTY);
			content = content.replaceAll("#%rebate_from_date%#", dto.getFromDateRebate() != null ?  dto.getFromDateRebate() : Strings.EMPTY);
			content = content.replaceAll("#%rebate_to_date%#", dto.getToDateRebate() != null ?  dto.getToDateRebate() : Strings.EMPTY);

			// View button and link details
			Optional<MailTemplateEntity> viewTemplate = mailTemplateRepository.findByMailType(MailTemplateEntity.VIEW_DETAILS_REBATE_BUTTON);
			Optional<MailTemplateEntity> viewLinkTemplate = mailTemplateRepository.findByMailType(MailTemplateEntity.VIEW_REBATE_LINK);
			Optional<MailTemplateEntity> rebateRejectReasonTemplate = mailTemplateRepository.findByMailType(MailTemplateEntity.REBATE_REJECT_REASON);

			if (viewTemplate.isPresent() && viewLinkTemplate.isPresent()) {
				String viewButtonContent = viewTemplate.get().getMailTemplate();
				viewButtonContent = viewButtonContent.replaceAll("#%viewUrl%#", urlData);
				
				String viewLinkContent = viewLinkTemplate.get().getMailTemplate();
				viewLinkContent = viewLinkContent.replaceAll("#%viewUrl%#", urlData);
				
				content = content.replaceAll("#%viewButton%#", viewButtonContent);
				content = content.replaceAll("#%viewLink%#", viewLinkContent);

				content = content.replaceAll("#%approveButton%#", Strings.EMPTY);
				content = content.replaceAll("#%rejectButton%#", Strings.EMPTY);
				content = content.replaceAll("<br>#%approveLink%#", Strings.EMPTY);
				content = content.replaceAll("<br>#%rejectLink%#", Strings.EMPTY);
			}
			
			if(rebateRejectReasonTemplate.isPresent() && StringUtils.isNotEmpty(rejectionReason)) {
				String rejectReasonContent = rebateRejectReasonTemplate.get().getMailTemplate();
				rejectReasonContent = rejectReasonContent.replaceAll("#%rejection_reason%#", rejectionReason);
				content = content + rejectReasonContent;
			}

			mailQueueService.saveMailQueue(subject, name, content, email, WorkflowMasterService.MESS_REBATE, dto.getStudentId(), 1, null, null, null, null);

		}
		return "Sent";
	}
	
	public void fetchStudentMessRebateDetails(String studentId, Long id, DeanMessRebateDto dto) throws RecordListEmptyException {
		Object[] studentMessRebateDetails = messRebateRepository.getMessRebateDetails(studentId, id);
		if (studentMessRebateDetails == null || studentMessRebateDetails.length == 0) {
			throw new RecordListEmptyException("Student Mess Rebate Details cannot be null or empty.");
		}
		Object[] data = (Object[]) studentMessRebateDetails[0];
		dto.setStudentId(ValidationCommon.toString(data[0]));
		dto.setStudentName(ValidationCommon.toString(data[1]));
		dto.setDob(ValidationCommon.formatDate(data[2], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
		dto.setGender(Utility.getGender(data[3]));
		dto.setStudentAddress(ValidationCommon.toString(data[4]));
		dto.setCity(ValidationCommon.toString(data[5]));
		dto.setPincode(ValidationCommon.toString(data[6]));
		dto.setState(ValidationCommon.toString(data[7]));
		dto.setStudentMobile(ValidationCommon.toString(data[8]));
		dto.setStudentEmail(ValidationCommon.toString(data[9]));
		dto.setFromDateLeave(ValidationCommon.formatDate(data[10], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
		dto.setToDateLeave(ValidationCommon.formatDate(data[11], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
		dto.setFromDateRebate(ValidationCommon.formatDate(data[12], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
		dto.setToDateRebate(ValidationCommon.formatDate(data[13], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
		dto.setReason(ValidationCommon.toString(data[14]));
		dto.setNoOfDays((int)data[15]);
		dto.setGuideName(ValidationCommon.toString(data[16]));
		dto.setGuideEmail(ValidationCommon.toString(data[17]));
		dto.setDocumentType(ValidationCommon.toString(data[18]));
		dto.setStatus(ValidationCommon.toString(data[19]));
		dto.setSlNo((Long) data[20]);
		dto.setVacateDate(ValidationCommon.formatDate(data[24], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
		dto.setHomeTown(ValidationCommon.toString(data[25]));
		dto.setSelfDeclarationDate(ValidationCommon.formatDate(data[26], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
		dto.setSelfDeclartionSignature(ValidationCommon.toString(data[27]));
		dto.setBioDataId((Long) data[28]);
		dto.setFileName(ValidationCommon.toStringOrNull(data[29]));
		setStudentProfileImage(dto);
	}
	
	private void setStudentProfileImage(DeanMessRebateDto dto) {
		try {
			if (dto.getBioDataId() != null) {
				String profileFileName = dto.getStudentId() + ModelConstants.UNDERSCORE + ModelConstants.FILE_STUDENT_PROFILE;
				dto.setImageBytes(fileService.getDecodedFile(ModelConstants.IMAGE_BIO_DATA_PROFILE, profileFileName));
				if (dto.getImageBytes() != null) {
					dto.setImageStr(Base64.getEncoder().encodeToString(dto.getImageBytes()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
