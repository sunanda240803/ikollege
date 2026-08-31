package com.iitm.hosteldine.service.OtherCandidate;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.CategoryEnum;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.OtherCandidate.*;
import com.iitm.hosteldine.dto.SimsConfigDataDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.mailQueue.MailTemplateDto;
import com.iitm.hosteldine.entity.mailQueue.MailQueueDetailsEntity;
import com.iitm.hosteldine.form.StayExtensionRequestForm;
import com.iitm.hosteldine.mapper.OtherCandidate.CandidateAppointmentRequestMapper;
import com.iitm.hosteldine.mapper.OtherCandidate.CandidateProfileMapper;
import com.iitm.hosteldine.mapper.OtherCandidate.StayExtensionRequestMapper;
import com.iitm.hosteldine.mapper.OtherCandidate.StayExtensionRequestWorkflowMapper;
import com.iitm.hosteldine.mapper.SimsConfigDataMapper;
import com.iitm.hosteldine.mapper.mailQueue.MailTemplateMapper;
import com.iitm.hosteldine.model.OtherCandidate.*;
import com.iitm.hosteldine.model.dashboard.student.WorkflowMasterEntity;
import com.iitm.hosteldine.repository.OtherCandidate.*;
import com.iitm.hosteldine.repository.SimsConfigDataRepository;
import com.iitm.hosteldine.repository.dashboard.student.WorkflowMasterRepository;
import com.iitm.hosteldine.repository.mailQueue.MailQueueDetailsRepository;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StayExtensionRequestService {
	
	private final CommonResponseUtil commonResponseUtil;
	private final StayExtensionRequestRepository stayExtensionRequestRepository;
	private final CandidateAppointmentRequestRepository candidateAppointmentRequestRepository;
	private final MessageSource messageSource;
	private final StayExtensionRequestWorkflowRepository stayExtensionRequestWorkflowRepository;
	private final HostelMasterService hostelMasterService;
	private final WorkflowMasterRepository workflowMasterRepository;
	private final CandidateFilesInformationRepository candidateFilesInformationRepository;
	private final FileService fileService;
	private final MailTemplateRepository mailTemplateRepository;
	private final CandidateProfileRepository candidateProfileRepository;
	private final SimsConfigDataRepository simsConfigDataRepository;
	private final MailQueueDetailsRepository mailQueueDetailsRepository;
	private final Utility utility;
	private final CandidateStayRequestRepository candidateStayRequestRepository;

	public StayExtensionRequestForm getStayExtensionDetailsById(Long candidateId, Long requestId, Long stayId,String encryptedKey,
																boolean checkProfileStatus) {
		if (checkProfileStatus && SecurityCtxUtil.candidateId().equals(0L)) {
			throw new IllegalArgumentException(commonResponseUtil.getMessage("message.exception.fill.profile.info"));
		}

        //Declaring Dto's
        StayExtensionRequestWorkflowDto requestWorkflowDto = new StayExtensionRequestWorkflowDto();
        StayExtensionRequestDto requestDto = StayExtensionRequestDto.builder().build();
		CandidateProfileDto candidateProfileDto = candidateProfileRepository.findByIdAndActiveFlag(candidateId, ModelConstants.STATUS_ACTIVE)
				.map(CandidateProfileMapper.INSTANCE::toDto)
				.orElse(new CandidateProfileDto());
        //Checking stay work flow request available ?
		List<StayExtensionRequestWorkflowDto> workflowDtos = null;
        if(stayId>0) {
        	workflowDtos = stayExtensionRequestWorkflowRepository.getStauWorkflowRequestList(stayId,ModelConstants.STATUS_ACTIVE,WorkflowStatus.DEFAULT.getStatus() )
					.stream()
					.map(StayExtensionRequestWorkflowMapper.INSTANCE::fromEntity)
					.toList();
        }
        //checking stay extension available ?
        Optional<StayExtensionRequestEntity> stayEntity = stayExtensionRequestRepository.findByStayIdAndCandidateIdAndAppointmentIdAndActiveFlag(stayId,candidateId, requestId, ModelConstants.STATUS_ACTIVE);
        if(stayEntity.isPresent()) {
        	requestDto = StayExtensionRequestMapper.INSTANCE.fromStayExtensionRequestEntity(stayEntity.get());
        }else {
        	requestDto.setCandidateId(candidateId);
        	requestDto.setAppointmentId(requestId);
        	
        }
        //Getting hostel list and setting in dto
        requestDto.setHostelList(hostelMasterService.getHostelList());
        if(stayId>0 && requestDto.getHostelId()!=null) {
	        for(HostelMasterDto dto : requestDto.getHostelList()) {
	        	if(dto.getId()==Long.parseLong(requestDto.getHostelId().toString())) {
	        		requestDto.setHostelName(dto.getHostelName());
	        		break;
	        	}
	        }
        }
        //Getting file information
        List<CandidateFilesInformationDto> filesInformationDto = candidateFilesInformationRepository
				.findAllByCandidateIdAndRequestIdAndStayIdAndActiveFlag(candidateId,requestId,stayId,ModelConstants.STATUS_ACTIVE)
				.map(list->list.stream().map(CandidateFilesInformationMapper.INSTANCE::toDto).toList()).orElse(Collections.emptyList());
        //getting appointment request entity and building the dto
        CandidateAppointmentRequestDto candidateAppointmentRequestDto = candidateAppointmentRequestRepository.findByIdAndCandidateIdAndActiveFlag(requestId, candidateId,
                ModelConstants.STATUS_ACTIVE)
        .map(CandidateAppointmentRequestMapper.INSTANCE::fromCandidateAppointmentRequestEntity)
        .orElse(CandidateAppointmentRequestDto.builder().build());
        return StayExtensionRequestForm
                .builder()
                .candidateProfileDto(candidateProfileDto)
                .candidateAppointmentRequestDto(candidateAppointmentRequestDto)
                .candidateFilesInformationDto(filesInformationDto)
                .stayExtensionRequestDto(requestDto)
                .encryptedKey(encryptedKey)
				.workflowList(workflowDtos)
                .build();
	}

	@Transactional
	public String saveUpdateStayExtensionRequest(StayExtensionRequestForm stayExtensionDetails, HttpServletRequest request) throws Exception{
		if(SecurityCtxUtil.candidateId().equals(0L)){
            throw new IllegalArgumentException(commonResponseUtil.getMessage("message.exception.fill.profile.info"));
        }
		String status = null;
		//Decrypting key and local variables
		String[] split = MCrypt.getInstance().decryptToString(stayExtensionDetails.getEncryptedKey()).split(Constants.BACKTICK);
        Long candidateId = SecurityCtxUtil.candidateId();
        Long requestId = Long.parseLong(split[2]);
        Long stayId = Long.parseLong(split[3]);
        String requestStatus = WorkflowStatus.VALIDATING.getStatus();
        String categoryCode = CategoryEnum.CCW.getValue();
        int updateStatus = 0;
		String validatorName = null;
		String validatorEmail = null;

		ExistingStayExtensionDetails existingStayExtensionDetails = stayExtensionRequestRepository.getStayExtensionDetailsByRequestIdAndStayId(candidateId, requestId, stayId, ModelConstants.STATUS_ACTIVE)
				.orElse(null);

		if(Objects.nonNull(existingStayExtensionDetails)){
			validatorName = existingStayExtensionDetails.validatingAuthority();
			validatorEmail = existingStayExtensionDetails.validatingAuthorityEmail();
		}
		else{
			CandidateAppointmentRequestEntity candidateAppointmentRequestEntity = candidateAppointmentRequestRepository.findByIdAndActiveFlag(requestId, ModelConstants.STATUS_ACTIVE)
					.orElse(null);
			if(Objects.nonNull(candidateAppointmentRequestEntity)){
				validatorName = candidateAppointmentRequestEntity.getValidatingAuthority();
				validatorEmail = candidateAppointmentRequestEntity.getValidatingAuthorityEmail();
			}
			else{
				validatorName = Strings.EMPTY;
				validatorEmail = Strings.EMPTY;
			}
		}

		//Declaration of entity
        StayExtensionRequestEntity extensionRequestEntity = new StayExtensionRequestEntity();
        StayExtensionRequestEntity savedEntity = null;
        //Getting appointment request entity for the current approval status
        Optional<CandidateAppointmentRequestEntity> candidateAppointmentRequestEntity = candidateAppointmentRequestRepository.findByIdAndCandidateIdAndActiveFlag(requestId, candidateId,
        		ModelConstants.STATUS_ACTIVE);
		if (candidateAppointmentRequestEntity.isEmpty()) {
			throw new IllegalArgumentException("Invalid Appointment Request.");
		}
        stayExtensionDetails.setCandidateAppointmentRequestDto(CandidateAppointmentRequestMapper.INSTANCE.fromCandidateAppointmentRequestEntity(candidateAppointmentRequestEntity.get()));
        if(candidateAppointmentRequestEntity.isPresent()) {
        	requestStatus = candidateAppointmentRequestEntity.get().getApprovalStatus();
        }
        //Update or Save Stay request
        Optional<StayExtensionRequestEntity> stayEntity = stayExtensionRequestRepository.findByStayIdAndCandidateIdAndAppointmentIdAndActiveFlag(stayId, candidateId, requestId, ModelConstants.STATUS_ACTIVE);
        if(stayEntity.isPresent()) {
        	extensionRequestEntity = StayExtensionRequestMapper.INSTANCE.onUpdateEntity(stayEntity.get(), stayExtensionDetails.getStayExtensionRequestDto());
        	extensionRequestEntity.setApprovalStatus(WorkflowStatus.VALIDATING.getStatus());
        	extensionRequestEntity.setModifiedBy(candidateId.toString());
        	savedEntity = stayExtensionRequestRepository.saveAndFlush(extensionRequestEntity);
            requestStatus = WorkflowStatus.REJECTED.getStatus().equalsIgnoreCase(split[0]) ? split[0] : extensionRequestEntity.getApprovalStatus();
        	if(savedEntity!=null) {
        		stayId = savedEntity.getStayId();
        		status = Constants.UPDATED;
        	}
        }else {
        	extensionRequestEntity = StayExtensionRequestMapper.INSTANCE.onSaveEntity(stayExtensionDetails.getStayExtensionRequestDto());
        	extensionRequestEntity.setStatusNotes(commonResponseUtil.getMessage("message.stay.extension.request.sub.subject"));
        	extensionRequestEntity.setCreatedBy(candidateId.toString());
        	extensionRequestEntity.setModifiedBy(candidateId.toString());
        	extensionRequestEntity.setDining(false);
        	extensionRequestEntity.setApprovalStatus(WorkflowStatus.VALIDATING.getStatus());
        	savedEntity = stayExtensionRequestRepository.save(extensionRequestEntity);
        	if(savedEntity!=null) {
        		stayId = savedEntity.getStayId();
        		status = Constants.SAVED;
        	}
        }
        //Update of Save file information
        if(stayExtensionDetails.getFile()!=null && !stayExtensionDetails.getFile().isEmpty()) {
        	try {
        		CandidateFilesInformationEntity candidateFilesInformationEntity = new CandidateFilesInformationEntity();
				byte[] fileBytes = stayExtensionDetails.getFile().getBytes();
				String unique = UUID.randomUUID().toString().substring(0, 8);
				String fileName = SecurityCtxUtil.candidateId() + Constants.BACKTICK + requestId + Constants.BACKTICK + stayId + Constants.BACKTICK + ModelConstants.UNDERSCORE + unique;
				String extension = FilenameUtils.getExtension(stayExtensionDetails.getFile().getOriginalFilename());
				String encryptedFileName = MCrypt.getInstance().encryptToText(fileName + Utility.getCurrentTimeStamp()) + "." + extension;
				boolean fileSaved = fileService.encodeFile(ModelConstants.CANDIDATE_FILE, fileBytes, encryptedFileName);
				if(fileSaved) {
					Optional<CandidateFilesInformationEntity> fileEntity = candidateFilesInformationRepository.findByCandidateIdAndRequestIdAndStayIdAndActiveFlag(candidateId,requestId,stayId,ModelConstants.STATUS_ACTIVE);
		        	if(fileEntity.isPresent()) {
		        		fileEntity.get().setFilename(encryptedFileName);
		        		candidateFilesInformationRepository.saveAndFlush(fileEntity.get());
		        	}else {
		        		candidateFilesInformationEntity.setCandidateId(Integer.parseInt(candidateId.toString()));
		        		candidateFilesInformationEntity.setRequestId(Integer.parseInt(requestId.toString()));
		        		candidateFilesInformationEntity.setFilename(encryptedFileName);
		        		candidateFilesInformationEntity.setDescription(stayExtensionDetails.getFileDescription());
		        		candidateFilesInformationEntity.setActiveFlag(ModelConstants.STATUS_ACTIVE);
		        		candidateFilesInformationEntity.setStayId(stayId);
		        		candidateFilesInformationRepository.save(candidateFilesInformationEntity);
		        	}
				}
			} catch (Exception e) {
			    e.printStackTrace();
			}
        }
        //Checking for workflow entity available for stay id and approval status rejected , if condition matches updating all existing to active status no
        List<StayExtensionRequestWorkflowEntity> workflowEntity = null;
        if(requestStatus!=null && requestStatus.equals(WorkflowStatus.REJECTED.getStatus())) {
	        workflowEntity = stayExtensionRequestWorkflowRepository.findByCandidateIdAndAppointmentIdAndStayIdAndActiveFlag(candidateId,requestId,stayId,ModelConstants.STATUS_ACTIVE);
	        if(workflowEntity!=null && workflowEntity.size()>0) {
	        	updateStatus = stayExtensionRequestWorkflowRepository.updateWorkflowRequestToInactive(SecurityCtxUtil.userId(),candidateId,requestId,stayId,ModelConstants.NO);
	        }
        }
        //After updating the stay work flow , insert new work flow in stay request
        if((updateStatus!=0 || status.equals(Constants.SAVED))) {
	        List<WorkflowMasterEntity> workflowMasterEntity = workflowMasterRepository.getWorkflowMasterList(categoryCode, ModelConstants.STATUS_ACTIVE);
	        if(workflowMasterEntity!=null && workflowMasterEntity.size()>0) {
				Integer firstApprovalLevel = 0;
	        	for(WorkflowMasterEntity masterEntity : workflowMasterEntity) {
	        		StayExtensionRequestWorkflowEntity entity = new StayExtensionRequestWorkflowEntity();
					if(firstApprovalLevel.equals(0)){
						firstApprovalLevel = masterEntity.getApprovalLevel();
					}
	        		entity.setCandidateId(candidateId);
	        		entity.setAppointmentId(requestId);
	        		entity.setStayId(stayId);
	        		entity.setCategory(CategoryEnum.STAY_EXTENSION.getValue());
	        		entity.setAuthorityType(masterEntity.getAuthorityType().equals(ModelConstants.VALIDATOR) ? ModelConstants.VALIDATOR : masterEntity.getAuthorityType());
	        		entity.setApprovalLevel(masterEntity.getApprovalLevel());
	        		entity.setValidatorName(masterEntity.getAuthorityType().equals(ModelConstants.VALIDATOR) ? validatorName : masterEntity.getValidatorName());
	        		entity.setValidatorEmail(masterEntity.getAuthorityType().equals(ModelConstants.VALIDATOR) ? validatorEmail : masterEntity.getEmail());
					entity.setApprovalStatus(Objects.equals(firstApprovalLevel, masterEntity.getApprovalLevel()) ?
							WorkflowStatus.PENDING.getStatus() : WorkflowStatus.DEFAULT.getStatus());
	        		entity.setAuthenticationType(masterEntity.getAuthenticationType());
	        		stayExtensionRequestWorkflowRepository.save(entity);
	        	}
	        }

			//Prepare Mail for stay extension request
			Long atomicStayId = stayId;
            List<String> approvalStatuses = List.of(WorkflowStatus.PENDING.getStatus(), WorkflowStatus.REJECTED.getStatus());
			stayExtensionRequestWorkflowRepository
					.findAllByCandidateIdAndAppointmentIdAndStayIdAndApprovalStatusInAndActiveFlag(candidateId, requestId, stayId, approvalStatuses,
							ModelConstants.STATUS_ACTIVE)
					.stream()
					.map(StayExtensionRequestWorkflowMapper.INSTANCE::fromEntity)
					.toList()
					.forEach(w-> {
                        try {
                            stayExtensionRequestMail(w,candidateId,requestId,atomicStayId,categoryCode,request);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
		}
		return status;
	}

	public void validateStayExtensionRequest(StayExtensionRequestForm stayExtensionDetails,
			BindingResult bindingResult){
        
		String errorMessage = "";
		
		if(stayExtensionDetails.getStayExtensionRequestDto().getStayFrom()==null || stayExtensionDetails.getStayExtensionRequestDto().getStayFrom().equals("")) {
			errorMessage = messageSource.getMessage("message.validation.stay.from.date.required", null, Locale.getDefault());
			 bindingResult.rejectValue("stayExtensionRequestDto.stayFrom", "error.stayExtensionRequestDto.stayFrom", errorMessage);
		}
		if(stayExtensionDetails.getStayExtensionRequestDto().getStayTo()==null || stayExtensionDetails.getStayExtensionRequestDto().getStayTo().equals("")) {
			errorMessage = messageSource.getMessage("message.validation.stay.to.date.required", null, Locale.getDefault());
			bindingResult.rejectValue("stayExtensionRequestDto.stayTo", "error.stayExtensionRequestDto.stayTo", errorMessage);
		}
		if(stayExtensionDetails.getStayExtensionRequestDto().getStayFrom()!=null && stayExtensionDetails.getStayExtensionRequestDto().getStayTo()!=null &&
				stayExtensionDetails.getStayExtensionRequestDto().getStayFrom().isAfter(stayExtensionDetails.getStayExtensionRequestDto().getStayTo())) {
			errorMessage = messageSource.getMessage("message.validation.stay.to.date", null, Locale.getDefault());
			bindingResult.rejectValue("stayExtensionRequestDto.stayTo", "error.stayExtensionRequestDto.stayTo", errorMessage);
		}
		if(stayExtensionDetails.getStayExtensionRequestDto().getHostelId()==null || stayExtensionDetails.getStayExtensionRequestDto().getHostelId().equals("") && 
				stayExtensionDetails.getStayExtensionRequestDto().getHostelId()==0) {
			errorMessage = messageSource.getMessage("message.validation.current.hostel.required", null, Locale.getDefault());
			bindingResult.rejectValue("stayExtensionRequestDto.hostelId", "error.stayExtensionRequestDto.hostelId", errorMessage);
		}
		if(stayExtensionDetails.getStayExtensionRequestDto().getRoomNo()==null || stayExtensionDetails.getStayExtensionRequestDto().getRoomNo().equals("") && 
				stayExtensionDetails.getStayExtensionRequestDto().getRoomNo()==0) {
			errorMessage = messageSource.getMessage("message.validation.room.no.required", null, Locale.getDefault());
			bindingResult.rejectValue("stayExtensionRequestDto.roomNo", "error.stayExtensionRequestDto.roomNo", errorMessage);
		}
		if(stayExtensionDetails.getStayExtensionRequestDto().getDescription()==null || stayExtensionDetails.getStayExtensionRequestDto().getDescription().equals("")) {
			errorMessage = messageSource.getMessage("message.validation.description.required", null, Locale.getDefault());
			bindingResult.rejectValue("stayExtensionRequestDto.description", "error.stayExtensionRequestDto.description", errorMessage);
		}
		
		if (!errorMessage.isEmpty()) {
			bindingResult.rejectValue(null,errorMessage);
	    }
	}


	public String cancelStayRequestById(String encryptedKey) throws Exception {
		if(SecurityCtxUtil.candidateId().equals(0L)){
			throw new IllegalArgumentException(commonResponseUtil.getMessage("message.exception.fill.profile.info"));
		}
		//Decrypting key
		String[] split = MCrypt.getInstance().decryptToString(encryptedKey).split(Constants.BACKTICK);
	    Long candidateId = SecurityCtxUtil.candidateId();
	    Long requestId = Long.parseLong(split[2]);
	    Long stayId = Long.parseLong(split[3]);
	    String status = null;
	    Optional<StayExtensionRequestEntity> stayEntity = stayExtensionRequestRepository.findByStayIdAndCandidateIdAndAppointmentIdAndActiveFlag(stayId, candidateId, requestId, ModelConstants.STATUS_ACTIVE);
        if(stayEntity.isPresent()) {
        	stayEntity.get().setApprovalStatus(WorkflowStatus.CANCELLED.getStatus());
        	stayEntity.get().setModifiedBy(candidateId.toString());
        	StayExtensionRequestEntity savedEntity = stayExtensionRequestRepository.saveAndFlush(stayEntity.get());
        	if(savedEntity!=null) {
        		status = WorkflowStatus.CANCELLED.getStatus();
        	}
        }
		return status;
	}
	
	public boolean stayExtensionRequestMail(StayExtensionRequestWorkflowDto workflowDto,Long candidateId, Long requestId, Long stayId,
											String category, HttpServletRequest request) throws Exception {

		CandidateProfileDto candidateProfileDto = candidateProfileRepository.findByIdAndActiveFlag(candidateId, ModelConstants.STATUS_ACTIVE)
				.map(CandidateProfileMapper.INSTANCE::toDto)
				.orElse(null);
		CandidateAppointmentRequestDto candidateAppointmentRequestDto =
				candidateAppointmentRequestRepository.findByIdAndCandidateIdAndActiveFlag(requestId,
								candidateId, ModelConstants.STATUS_ACTIVE)
						.map(CandidateAppointmentRequestMapper.INSTANCE::fromCandidateAppointmentRequestEntity)
						.orElse(null);
		CandidateStayRequestEntity stayReqEntity=new CandidateStayRequestEntity();
		if (Objects.nonNull(stayId) && stayId > 0) {
			stayReqEntity = candidateStayRequestRepository.findByStayIdAndActiveFlag(stayId, ModelConstants.STATUS_ACTIVE).orElse(null);
		}

		String workFlowStatus = workflowDto.getId() + Constants.BACKTICK + workflowDto.getModifiedAt();
		String requestStatus = candidateId + Constants.BACKTICK + requestId + Constants.BACKTICK + stayId;
		String approvalStatusStr =
				MCrypt.getInstance().encryptToText(requestStatus + Constants.BACKTICK + workFlowStatus + Constants.BACKTICK + WorkflowStatus.APPROVED.getStatus());
		String rejectedStatusStr =
				MCrypt.getInstance().encryptToText(requestStatus + Constants.BACKTICK + workFlowStatus + Constants.BACKTICK + WorkflowStatus.REJECTED.getStatus());
		String statusStr =
				MCrypt.getInstance().encryptToText(requestStatus + Constants.BACKTICK + workFlowStatus + Constants.BACKTICK + workflowDto.getAuthorityType());

		boolean status = false;
		//Prepare mail content
		String template = mailTemplateRepository
				.findByActiveFlagAndMailType(ModelConstants.STATUS_ACTIVE,ModelConstants.STAY_EXTENSION_REQUEST).map(MailTemplateMapper.INSTANCE::toDto)
				.map(MailTemplateDto::getMailTemplate).orElse(new MailTemplateDto().getMailTemplate());
		//updating dynamic values
		template = template.replace("#%subject%#", commonResponseUtil.getMessage("message.validate.stay.extension.request"));
		template = template.replace("#%date%#", DateUtility.formatDateInd(new java.util.Date()));
		//template = template.replace("#%subSubject%#", "The following particulars have been applied for stay extension in one of our campus hostels.");
		template = template.replace("#%subSubject%#", commonResponseUtil.getMessage("message.mail.particulars.stay.request")+candidateProfileDto.getFirstName()+" "+candidateProfileDto.getLastName());
		template = template.replace("#%heading%#", ModelConstants.CANDIDATE_PROFILE);
		template = template.replace("#%candidateName%#", candidateProfileDto.getFirstName()+" "+candidateProfileDto.getLastName());
		template = template.replace("#%address%#", candidateProfileDto.getAddress()+", "+candidateProfileDto.getCity()+", "+
				candidateProfileDto.getState()+", "+candidateProfileDto.getPin());
		template = template.replace("#%contactNum%#", candidateProfileDto.getMobileNumber());
		template = template.replace("#%email%#", candidateProfileDto.getEmail());
		template = template.replace("#%appFromDate%#", DateUtility.formatDateInd(candidateAppointmentRequestDto.getAppointmentFrom()));
		template = template.replace("#%appToDate%#", DateUtility.formatDateInd(candidateAppointmentRequestDto.getAppointmentTo()));
		template = template.replace("#%stayFromDate%#", DateUtility.formatDateInd((Objects.nonNull(stayId) && stayId > 0) ?
				stayReqEntity.getStayFrom(): candidateAppointmentRequestDto.getStayFrom()));
		template = template.replace("#%stayToDate%#", DateUtility.formatDateInd((Objects.nonNull(stayId) && stayId > 0) ?
				stayReqEntity.getStayTo(): candidateAppointmentRequestDto.getStayTo()));
		template = template.replace("#%category%#", candidateAppointmentRequestDto.getCategory()!=null ? candidateAppointmentRequestDto.getCategory() : category);

		String domainUrl = Utility.getDomainUrl(request);
		String url = commonResponseUtil.getMessage("url.domain") + commonResponseUtil.getMessage("url.public.api") +
				commonResponseUtil.getMessage("url.other.candidate.user");
		url = url.replace("#%domainUrl%#", domainUrl);

		String approvalLink = url + ModelConstants.SLASH + WorkflowStatus.APPROVE.getStatus() + ModelConstants.SLASH + approvalStatusStr;;
		String rejectLink = url + ModelConstants.SLASH + WorkflowStatus.REJECTED.getStatus() + ModelConstants.SLASH + rejectedStatusStr;
		String viewLink = url + ModelConstants.SLASH + WorkflowStatus.VIEW.getStatus() + ModelConstants.SLASH + statusStr;

		String approveButton = commonResponseUtil.getMessage("message.approve.button")
				.replace("#%url%#", approvalLink);
		String rejectButton = commonResponseUtil.getMessage("message.reject.button")
				.replace("#%url%#", rejectLink);
		String viewButton = commonResponseUtil.getMessage("message.view.button")
				.replace("#%url%#", viewLink);


		template = template.replace("#%approvaBtn%#",approveButton);
		template = template.replace("#%rejectBtn%#", rejectButton);
		template = template.replace("#%viewBtn%#", viewButton);
		template = template.replace("#%approveLink%#",approvalLink);
		template = template.replace("#%rejectLink%#",rejectLink);
		template = template.replace("#%viewLink%#",viewLink);
		//Saving in mail queue table 
		MailQueueDetailsEntity mailQueueDetails = new MailQueueDetailsEntity();
		mailQueueDetails.setMailTo(workflowDto.getValidatorEmail());
		mailQueueDetails.setMailSubject(commonResponseUtil.getMessage("message.validate.stay.extension.request"));
		mailQueueDetails.setMailContent(template);
		mailQueueDetails.setSubmittedModule(ModelConstants.CANIDADATE_STAY_REQUEST);	
		mailQueueDetails.setMailPriority(1);
		mailQueueDetails.setMailStatus(1);
		mailQueueDetails.setRetryCount(0);
		mailQueueDetails.setMailFrom(simsConfigDataRepository.findByConfigKeyIgnoreCaseAndActiveFlag(SimsConfigDataService.MAIL_FROM,
							ModelConstants.STATUS_ACTIVE).map(SimsConfigDataMapper.INSTANCE::fromSimsConfigDataEntity)
					.map(SimsConfigDataDto::getConfigValue).orElse(new SimsConfigDataDto().getConfigValue()));
		mailQueueDetails.onCreate();
		return mailQueueDetailsRepository.save(mailQueueDetails)!=null;
	}


	public List<StayExtensionRequestWorkflowDto> getStayExtensionWorkFlowDetails(String encryptedKey) throws Exception {
		List<StayExtensionRequestWorkflowDto> requestWorkflowDtos = new ArrayList<StayExtensionRequestWorkflowDto>();
		//Decrypting key
		String[] split = MCrypt.getInstance().decryptToString(encryptedKey).split(Constants.BACKTICK);
        Long candidateId = SecurityCtxUtil.candidateId();
        Long requestId = Long.parseLong(split[2]);
        Long stayId = Long.parseLong(split[3]);
        //Getting workflow details
        List<StayExtensionRequestWorkflowEntity> requestWorkflowEntities = stayExtensionRequestWorkflowRepository.findAllByCandidateIdAndAppointmentIdAndStayIdAndActiveFlag(candidateId, requestId, stayId, ModelConstants.STATUS_ACTIVE);
        if(requestWorkflowEntities!=null && requestWorkflowEntities.size()>0) {
        	for(StayExtensionRequestWorkflowEntity entity : requestWorkflowEntities) {
        		StayExtensionRequestWorkflowDto dto = new StayExtensionRequestWorkflowDto();
        		dto = StayExtensionRequestWorkflowMapper.INSTANCE.fromEntity(entity);
        		requestWorkflowDtos.add(dto);
        	}
        }
		return requestWorkflowDtos;
	}

    public List<StayExtensionRequestDto> getStayExtensionDetails(Long candidateId, Long requestId) {
        return stayExtensionRequestRepository.findAllByCandidateIdAndAppointmentIdAndApprovalStatusAndActiveFlag(candidateId, requestId, WorkflowStatus.APPROVED.getStatus(), ModelConstants.STATUS_ACTIVE).stream().map(StayExtensionRequestMapper.INSTANCE::fromStayExtensionRequestEntity).collect(Collectors.toList());
    }
}
