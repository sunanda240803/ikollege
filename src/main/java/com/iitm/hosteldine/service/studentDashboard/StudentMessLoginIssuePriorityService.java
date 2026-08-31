package com.iitm.hosteldine.service.studentDashboard;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.dto.studentDashboard.StudentMessPriorityRegistrationDto;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.model.mess.MessMasterEntity;
import com.iitm.hosteldine.model.mess.StudentMessLoginIssuePriorityEntity;
import com.iitm.hosteldine.model.mess.StudentMessLoginIssuePriorityId;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;
import com.iitm.hosteldine.repository.studentDashboard.StudentMessLoginIssuePriorityRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentMessLoginIssuePriorityService {
	private final StudentMessLoginIssuePriorityRepository studentMessLoginIssuePriorityRepository;
	private final MessageSource messageSource;
	private final MessMasterCommonService messMasterCommonService;
	private final MailTemplateRepository mailTemplateRepository;
	private final MessMasterRepository messMasterRepository;
	private final SimsConfigDataService simsConfigDataService;
	private final MailQueueService mailQueueService;
	private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;

	public String checkStudentRegistration(String statusActive, String studentId, int currentId) {
		String status = "";
		List<StudentMessLoginIssuePriorityRepository> loginIssue = studentMessLoginIssuePriorityRepository
				.findByActiveFlagAndId_StudentIdAndId_MmcNId(ModelConstants.STATUS_ACTIVE,
						studentId, currentId);
		if (loginIssue != null && !loginIssue.isEmpty()) {
			status = messageSource.getMessage("message.register.already", null, Locale.getDefault());
		} else {
			status = messageSource.getMessage("message.not.register.already", null, Locale.getDefault());
		}
		return status;
	}

	public String saveLoginIssueMessPriorityRegistration(StudentMessPriorityRegistrationDto dto) throws Exception {
		String dinningFromDate = "", dinningToDate = "";
		String status = null ,month = "";
		Long previousId = null , currentId = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
		String studentId=dto.getId().getStudentId().toUpperCase();

		//getting dining dates (for Mail purpose)
		List<MessMasterControllerDto> messControllerList = messMasterCommonService.getMessMasterControllerList();
		if(messControllerList != null && !messControllerList.isEmpty()) {
			MessMasterControllerDto messMasterControllerDto =messControllerList.get(0);
			dinningFromDate = messMasterControllerDto.getDiningFromDate().format(formatter);
			dinningToDate = messMasterControllerDto.getDiningToDate().format(formatter);;
		}

		// Getting current and previous Id from mess master controller
		Optional<MessMasterControllerDto> messPeriodDetails = messMasterCommonService.getMessPeriodDetails();
		if (messPeriodDetails.isPresent()) {
			MessMasterControllerDto messMasterControllerDto = messPeriodDetails.get();
			currentId =  messMasterControllerDto.getId();
		}
		//Get student details
		AllStudentsDetailsViewEntity studentDetail=new AllStudentsDetailsViewEntity();
		Optional<AllStudentsDetailsViewEntity> studentDetailOpt = allStudentsDetailsViewRepository.findBystudentId(studentId);
		if(studentDetailOpt.isPresent()) {
			studentDetail = studentDetailOpt.get();
		}

		if(dto.getId().getPriorityMessId()!=0) {
			StudentMessLoginIssuePriorityEntity entity = new StudentMessLoginIssuePriorityEntity();
			StudentMessLoginIssuePriorityId messRegId = new StudentMessLoginIssuePriorityId();
			messRegId.setMmcNId(currentId.intValue());
			messRegId.setPriorityMessId(dto.getId().getPriorityMessId());
			messRegId.setPriorityOrder(1);
			messRegId.setStudentId(studentId);
			entity.setId(messRegId);
			entity.setStudentName(studentDetail.getStudentName());
			entity.setGender(studentDetail.getGender());
			entity.setDescription(dto.getDescription());
			entity.setHostelId(Math.toIntExact(studentDetail.getHostelId()));
			entity.setRoomNo(studentDetail.getRoomNumber());
			entity.onCreate();
			entity = studentMessLoginIssuePriorityRepository.save(entity);

			if (entity != null) {
				status = Constants.SAVED;
					Optional<MessMasterEntity> messEntity = messMasterRepository.findByIdAndActiveFlag(Long.valueOf(entity.getId().getPriorityMessId()),
							ModelConstants.STATUS_ACTIVE);
					if (messEntity != null && !messEntity.isEmpty()) {
						MessMasterEntity mess = messEntity.get();
						dto.setMessName(mess.getMessName());
					}

				Optional<MailTemplateEntity> templateOpt = mailTemplateRepository
						.findByMailType(MailTemplateEntity.LOGIN_ISSUE_PRIORITY_MESS);
				if (templateOpt.isPresent()) {
					MailTemplateEntity template = templateOpt.get();
					String subject = template.getMailSubject();
					String content = template.getMailTemplate();
					DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_TIME_FORMAT);
					String currentDateTime = LocalDateTime.now().format(dateFormatter);
					content = content.replaceAll("#%dinning_from%#", dinningFromDate);
					content = content.replaceAll("#%dinning_to%#", dinningToDate);
					content = content.replaceAll("#%student_id%#", studentId);
					content = content.replaceAll("#%student_name%#", studentDetail.getStudentName() != null ? studentDetail.getStudentName() : ModelConstants.HYPHEN);
					content = content.replaceAll("#%mess_name%#", dto.getMessName() != null ? dto.getMessName() : ModelConstants.HYPHEN);
					content = content.replaceAll("#%register_date%#", currentDateTime);
					String toEmail = studentId+simsConfigDataService.getSimConfigValue(SimsConfigDataService.IIT_STUDENT_MAIL_ADD_DOMAIN);

					boolean mailStatus = mailQueueService.saveMailQueue(subject,
							studentDetail.getStudentName() +" ("+ studentId +")", content,
							toEmail, Constants.LOGIN_ISSUE_MESS_PRIORITY,
							studentId, 1, null, null, null, null);
				}

			}
		}

		return status;
	}

}
