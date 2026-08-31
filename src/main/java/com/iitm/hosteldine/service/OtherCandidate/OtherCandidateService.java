package com.iitm.hosteldine.service.OtherCandidate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.OtherCandidate.CandidateProfileDto;
import com.iitm.hosteldine.dto.OtherCandidate.CandidateStayDateViewDto;
import com.iitm.hosteldine.entity.UserManagementOnlineEntity;
import com.iitm.hosteldine.mapper.OtherCandidate.CandidateProfileMapper;
import com.iitm.hosteldine.mapper.OtherCandidate.CandidateStayDateMapper;
import com.iitm.hosteldine.model.OtherCandidate.CandidateAppointmentRequestEntity;
import com.iitm.hosteldine.model.OtherCandidate.CandidateProfileEntity;
import com.iitm.hosteldine.repository.OtherCandidate.CandidateAppointmentRequestRepository;
import com.iitm.hosteldine.repository.OtherCandidate.CandidateProfileRepository;
import com.iitm.hosteldine.repository.OtherCandidate.CandidateStayDateViewRepository;
import com.iitm.hosteldine.repository.UserManagementOnlineRepository;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OtherCandidateService {

	private final CandidateProfileRepository candidateProfileRepository;
    private final MessageSource messageSource;
    private final UserManagementOnlineRepository onlineRepository;
    private final FileService fileService;
    private final SimsConfigDataService simsConfigDataService;
    private final CandidateStayDateViewRepository candidateStayDateRepository;
	
	public CandidateProfileDto getOtherCandidateDetails(Long userId) throws Exception {
		Optional<UserManagementOnlineEntity> userEntity = onlineRepository.findByUserIdAndActiveFlag(userId,ModelConstants.STATUS_ACTIVE);
		CandidateProfileDto profileDto = new CandidateProfileDto();
		if(userEntity.isPresent()) {
			userEntity.get().setApplicationId(userEntity.get().getApplicationId()!=null ? userEntity.get().getApplicationId() : "0");
			profileDto =  candidateProfileRepository
				.findByIdAndActiveFlag(Long.parseLong(userEntity.get().getApplicationId()), ModelConstants.STATUS_ACTIVE)
				.map(CandidateProfileMapper.INSTANCE::toDto).orElse(new CandidateProfileDto());
			profileDto.setEmail(profileDto.getEmail()!=null ? profileDto.getEmail() :userEntity.get().getEmail());
			profileDto.setImageName(Objects.nonNull(profileDto.getId()) ? profileDto.getId() + "_profile" : ModelConstants.EMPTY_STRING);
			populateCandidatePostData(profileDto);

			//Get accommodation request available
			if(profileDto.getId()!=null && profileDto.getId()>0) {
				var pageRequest = PageRequest.of(0, 25);
				Page<CandidateStayDateViewDto> requestList = candidateStayDateRepository.findAllById_CandidateId(profileDto.getId(), pageRequest)
		                .map(CandidateStayDateMapper.INSTANCE::toDto);
				if(requestList != null && !requestList.getContent().isEmpty()) {
					profileDto.setAccommodationRequest(true);
				}
			}
			return profileDto;
		}else {
			return profileDto;
		}
	}

	public void populateCandidatePostData(CandidateProfileDto profileDto) throws Exception {
		String postData = simsConfigDataService.getSimConfigValue(Constants.CANDIDATE_POST);
		if(postData!=null && !postData.isEmpty()) {
			ObjectMapper objectMapper = new ObjectMapper();
			profileDto.setPostData(objectMapper.readValue(postData, Map.class));
		}
	}

	public String saveUpdateOtherCandidate(CandidateProfileDto dto) {
		//Getting User details from UserManagementOnline Entity
		Optional<UserManagementOnlineEntity> userEntity = onlineRepository.findByUserIdAndActiveFlag(dto.getUserId(),ModelConstants.STATUS_ACTIVE);
		dto.setMobileNumber(normalizeMobileNumber(dto.getMobileNumber()));
		String result = Optional.ofNullable(dto.getId()).filter(id -> (id != null && id > 0))
				.flatMap(id -> candidateProfileRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					CandidateProfileMapper.INSTANCE.onUpdateEntity(existingEntity, dto);
					candidateProfileRepository.save(existingEntity); 
					return Constants.UPDATED;
				}).orElseGet(() -> {
					CandidateProfileEntity newEntity = CandidateProfileMapper.INSTANCE.toCandidateProfileEntity(dto);
					CandidateProfileEntity savedEntity =  candidateProfileRepository.save(newEntity);
					if(savedEntity!=null && userEntity.isPresent()) {
						userEntity.get().setApplicationId(savedEntity.getId().toString());
						onlineRepository.saveAndFlush(userEntity.get());
					}
					return Constants.SAVED;
				});
		//for saving image name in UserManagementOnlineEntity
		String imageName=userEntity.get().getApplicationId()+"_profile";
		if(dto.getStudentImg()!=null && !dto.getStudentImg().isEmpty()) {
			try {
				byte[] imageBytes = dto.getStudentImg().getBytes();
				boolean imageSaved = fileService.encodeFile(ModelConstants.IMAGE_CANDIDATE_PROFILE, imageBytes,
						imageName);
//				if(imageSaved) {
//					dto.setImageName(imageName);
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private String normalizeMobileNumber(String mobileNumber) {
        return Objects.nonNull(mobileNumber) ? mobileNumber.replaceAll(ModelConstants.NON_DIGIT, ModelConstants.EMPTY_STRING) : null;
	}


	public void validateOtherCandidate(CandidateProfileDto profileDto, BindingResult bindingResult) {
		String errorMessage = "";
		
		if(profileDto.getPostSelect()==null || profileDto.getPostSelect().isEmpty()) {
			errorMessage = messageSource.getMessage("message.validation.post.required", null, Locale.getDefault());
			 bindingResult.rejectValue("postSelect", "error.postSelect", errorMessage);
		}
		if(profileDto.getFirstName()==null || profileDto.getFirstName().isEmpty()) {
			errorMessage = messageSource.getMessage("message.validation.first.name.required", null, Locale.getDefault());
			bindingResult.rejectValue("firstName", "error.firstName", errorMessage);
		}
		/*if(profileDto.getLastName()==null || profileDto.getLastName().isEmpty()) {
			errorMessage = messageSource.getMessage("message.validation.last.name.required", null, Locale.getDefault());
			bindingResult.rejectValue("lastName", "error.lastName", errorMessage);
		}*/
		if(profileDto.getDob()==null || profileDto.getDob().equals("")) {
			errorMessage = messageSource.getMessage("message.validation.date.of.birth.required", null, Locale.getDefault());
			bindingResult.rejectValue("dob", "error.dob", errorMessage);
		}
		if(profileDto.getGender()==null || profileDto.getGender().isEmpty()) {
			errorMessage = messageSource.getMessage("message.validation.gender.option.required", null, Locale.getDefault());
			bindingResult.rejectValue("gender", "error.gender", errorMessage);
		}
		if(profileDto.getEmail()==null || profileDto.getEmail().isEmpty()) {
			errorMessage = messageSource.getMessage("message.validation.email.id.required", null, Locale.getDefault());
			bindingResult.rejectValue("email", "error.email", errorMessage);
		}
		if(profileDto.getAddress()==null || profileDto.getAddress().isEmpty()) {
			errorMessage = messageSource.getMessage("message.validation.address.required", null, Locale.getDefault());
			bindingResult.rejectValue("address", "error.address", errorMessage);
		}
		if(profileDto.getCity()==null || profileDto.getCity().isEmpty()) {
			errorMessage = messageSource.getMessage("message.validation.city.required", null, Locale.getDefault());
			bindingResult.rejectValue("city", "error.city", errorMessage);
		}
		if(profileDto.getState()==null || profileDto.getState().isEmpty()) {
			errorMessage = messageSource.getMessage("message.validation.state.required", null, Locale.getDefault());
			bindingResult.rejectValue("state", "error.state", errorMessage);
		}
		if(profileDto.getPin()==null || profileDto.getPin().equals("")) {
			errorMessage = messageSource.getMessage("message.validation.pin.code.required", null, Locale.getDefault());
			bindingResult.rejectValue("pin", "error.pin", errorMessage);
		}
		if(profileDto.getMobileNumber()==null || profileDto.getMobileNumber().isEmpty()) {
			errorMessage = messageSource.getMessage("message.validation.mobile.number.required", null, Locale.getDefault());
			bindingResult.rejectValue("mobileNumber", "error.mobileNumber", errorMessage);
		} else if(profileDto.getMobileNumber().startsWith("0")) {
			errorMessage = messageSource.getMessage("message.validation.mobile.number.not.start.with.zero", null, Locale.getDefault());
			bindingResult.rejectValue("mobileNumber", "error.mobileNumber", errorMessage);
		}

		if (profileDto.getStudentImg() != null && !profileDto.getStudentImg().isEmpty()
				&& profileDto.getStudentImg().getSize() > 1 * 1024 * 1024) {
			errorMessage = "Photo size should be 1 MB or less.";
			bindingResult.rejectValue("studentImg", "error.studentImg", errorMessage);
		}
	
	if (!errorMessage.isEmpty()) {
		bindingResult.rejectValue(null,errorMessage);
    }
		
	}

}
