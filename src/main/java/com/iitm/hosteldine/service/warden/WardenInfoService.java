package com.iitm.hosteldine.service.warden;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.warden.WardenInfoDto;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.hostel.HostelMasterMapper;
import com.iitm.hosteldine.mapper.warden.WardenInchargeDetailsMapper;
import com.iitm.hosteldine.mapper.warden.WardenInfoMapper;
import com.iitm.hosteldine.model.warden.WardenHostelMappingEntity;
import com.iitm.hosteldine.model.warden.WardenHostelMappingEntityId;
import com.iitm.hosteldine.model.warden.WardenInchargeDetailsEntity;
import com.iitm.hosteldine.model.warden.WardenInfoEntity;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.repository.warden.WardenHostelMappingRepository;
import com.iitm.hosteldine.repository.warden.WardenInchargeDetailsRepository;
import com.iitm.hosteldine.repository.warden.WardenInfoRepository;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.Utility;

import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class WardenInfoService {
	
	private final FileService fileService;	
	private final MessageSource messageSource;
	private final WardenInfoRepository wardenInfoRepository;
	private final WardenHostelMappingRepository wardenHostelMappingRepository;
	private final WardenInchargeDetailsRepository wardenInchargeDetailsRepository;
	private final SimsConfigDataService simsConfigDataService;
	private final CommonResponseUtil commonResponseUtil;
	private final MailQueueService mailQueueService;
	private final MailTemplateRepository mailTemplateRepository;

	public List<WardenInfoDto> getWardenDetails(String wardenId) {

		List<Long> wardenIds = Arrays.stream(wardenId.split(Constants.ARRAY_SEPARATOR))
		        .map(String::trim)
		        .filter(s -> !s.isEmpty())
		        .peek(System.out::println) 
		        .map(Long::valueOf)
		        .collect(Collectors.toList());

		    if (wardenIds.isEmpty()) {
		        return Collections.emptyList();
		    }

		return Optional.ofNullable(wardenInfoRepository.getWardenDetails(wardenIds, ModelConstants.STATUS_ACTIVE))
				.orElse(Collections.emptyList()).stream().map(WardenInfoMapper.INSTANCE::fromWardenInfoEntity)
				.collect(Collectors.toList());

	}

	public List<WardenInfoDto> getWardenDetailsByEmail(String wardenEmail) {

		if (wardenEmail.isEmpty()) {
			return Collections.emptyList();
		}

		return Optional.ofNullable(wardenInfoRepository.findByWardenEmailAndActiveFlag(wardenEmail, ModelConstants.STATUS_ACTIVE))
				.orElse(Collections.emptyList()).stream().map(WardenInfoMapper.INSTANCE::fromWardenInfoEntity)
				.collect(Collectors.toList());

	}

	public List<HostelMasterDto> getWardenDetailsList(String userId) {
		return Optional.ofNullable(wardenInfoRepository.getWardenDetails(userId, ModelConstants.STATUS_ACTIVE))
				.orElse(Collections.emptyList()).stream().map(HostelMasterMapper.INSTANCE::fromHostelMasterEntity)
				.collect(Collectors.toList());
	}

	public WardenInfoDto getWardenDetailsByLDAPUsername(String username) {
		return wardenInfoRepository.findFirstByLdapUsernameAndActiveFlag(username, ModelConstants.STATUS_ACTIVE)
				.map(WardenInfoMapper.INSTANCE::fromWardenInfoEntity).orElse(null);
	}

	public List<WardenInfoDto> getAllWardenDetailsList() {
		return Optional.ofNullable(wardenInfoRepository.findAllByActiveFlagOrderByModifiedAtDesc(ModelConstants.STATUS_ACTIVE))
				.orElse(Collections.emptyList()).stream().map(WardenInfoMapper.INSTANCE::fromWardenInfoEntity)
				.collect(Collectors.toList());
	}

	public Page<WardenInfoDto> getWardenInOutDetails(PaginationForm form) {
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize());
		Page<Object[]> result;
		result = wardenInfoRepository.getWardenInOutDetails(SecurityCtxUtil.userName(),pageable);
		 return result.map(objects -> {
			 WardenInfoDto dto = new WardenInfoDto();
			 	dto.setId((Long) objects[0]);
			 	dto.setWardenName((String) objects[1]);
			 	dto.setWardenEmail((String) objects[2]);
			 	dto.setOfficeNo((String) objects[3]);
			 	dto.setInchargeName((String) objects[5]);
			 	dto.setInchargeEmail((String) objects[6]);
			 	dto.setInchargePhone((String) objects[7]);
			 	dto.setHostelId(((Integer) objects[9]).longValue());
			 	dto.setHostelName((String) objects[10]);
			 	dto.setAwayFrom(((Date) objects[11]).toLocalDate());
			 	dto.setAwayTo(((Date) objects[12]).toLocalDate());
			 	dto.setAwayDescription((String) objects[13]);
			 	dto.setInchargeId((Long) objects[4]);
		        return dto;
		    });
	}

	public WardenInfoDto getWardenDetailsByUserName(String userName) {
		WardenInfoDto wardenInfoDto = new WardenInfoDto();
		List<Object[]> wardenDetails = wardenInfoRepository.getWardenDetailsByLdap(SecurityCtxUtil.userName(),ModelConstants.STATUS_ACTIVE);
		if (wardenDetails.isEmpty()) {
            return null;
        }
		Object[] data = wardenDetails.get(0);
		wardenInfoDto.setId((Long) data[0]);
		wardenInfoDto.setWardenName(data[1] != null ? data[1].toString() : null);
		wardenInfoDto.setWardenEmail(data[2] != null ? data[2].toString() : null);
		wardenInfoDto.setPhoneNumber(data[3] != null ? data[3].toString() : null);
		wardenInfoDto.setOfficeNo(data[4] != null ? data[4].toString() : null);
		wardenInfoDto.setHostelId(data[5] != null ? Long.parseLong(data[5].toString()) : null);
		wardenInfoDto.setHostelName(data[6] != null ? data[6].toString() : null);
		return wardenInfoDto;
	}

	public WardenInfoDto getInchargeDetailsByHostelId(String id) {
		WardenInfoDto wardenInfoDto = new WardenInfoDto();
		List<Object[]> wardenDetails = wardenInfoRepository.getInchargeDetailsByHostelId(ModelConstants.STATUS_ACTIVE,Long.parseLong(id));
		if (wardenDetails.isEmpty()) {
            return null;
        }
		Object[] data = wardenDetails.get(0);
		wardenInfoDto.setId((Long) data[0]);
		wardenInfoDto.setWardenName(data[1] != null ? data[1].toString() : null);
		wardenInfoDto.setWardenEmail(data[2] != null ? data[2].toString() : null);
		wardenInfoDto.setPhoneNumber(data[3] != null ? data[3].toString() : null);
		wardenInfoDto.setOfficeNo(data[4] != null ? data[4].toString() : null);
		return wardenInfoDto;
	}

	public WardenInfoDto getWardenDetailsByInchargeId(String id) {
		WardenInfoDto dto = new WardenInfoDto();
		List<Object[]> wardenDetails = wardenInfoRepository.getWardenDetailsByInchargeId(ModelConstants.STATUS_ACTIVE,Long.parseLong(id));
		if (wardenDetails.isEmpty()) {
            return null;
        }
		Object[] objects = wardenDetails.get(0);
		dto.setId((Long) objects[4]);
	 	dto.setWardenName((String) objects[1]);
	 	dto.setWardenEmail((String) objects[2]);
	 	dto.setOfficeNo((String) objects[3]);
	 	dto.setInchargeId((Long) objects[0]);
	 	dto.setInchargeName((String) objects[5]);
	 	dto.setInchargeEmail((String) objects[6]);
	 	dto.setInchargePhone((String) objects[7]);
	 	dto.setInchargeOfficeNo((String) objects[8]);
	 	dto.setHostelId(((Integer) objects[9]).longValue());
	 	dto.setHostelName((String) objects[10]);
	 	dto.setAwayFrom(((Date) objects[11]).toLocalDate());
	 	dto.setAwayTo(((Date) objects[12]).toLocalDate());
	 	dto.setAwayDescription((String) objects[13]);
	 	dto.setWardenId(((Integer) objects[14]).longValue());
		return dto;
	}
	
	public Page<WardenInfoDto> getWardenInfoList(PaginationForm form, String baseUrl) {
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize());
		Page<WardenInfoDto> result = Page.empty();
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			result = wardenInfoRepository.findAllByActiveFlagOrderByLdapUsername(ModelConstants.STATUS_ACTIVE, pageable).map(WardenInfoMapper.INSTANCE::fromWardenInfoEntity);
		} else {
			result = wardenInfoRepository
			        .searchByFields(ModelConstants.STATUS_ACTIVE, form.getSearch(), pageable)
			        .map(WardenInfoMapper.INSTANCE::fromWardenInfoEntity);
		}
		result.map(wardenInfoDto -> {
			String deleteUrl = getUrl(baseUrl, wardenInfoDto.getId(), messageSource.getMessage("url.delete", null, Locale.getDefault()));
			String editUrl = getUrl(baseUrl, wardenInfoDto.getId(), messageSource.getMessage("url.edit", null, Locale.getDefault()));
			wardenInfoDto.setDeleteUrl(deleteUrl);
			wardenInfoDto.setEditUrl(editUrl);
			return wardenInfoDto;
		});
		return result;
	}

	private String getUrl(String url, Long wardenId, String action) {
		try {
			url = url + action + "?data=" + encryptWardenRequestUrl(wardenId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}

	public boolean saveWardenInfo(WardenInfoDto dto) throws Exception {
		long currentMillis = System.currentTimeMillis();
		String fileExtension = "";
		if (dto.getImageName() != null && !dto.getImageName().isEmpty() && dto.getImageBytes()!=null) {
			fileExtension = dto.getImageName().substring(dto.getImageName().lastIndexOf("."));
			String fileName = ModelConstants.WARDEN_IMAGE + ModelConstants.UNDERSCORE + dto.getLdapUsername()+ ModelConstants.UNDERSCORE +currentMillis + fileExtension;
			dto.setImageName(fileName);
		}

		WardenInfoEntity wardenInfoEntity = saveWardenInfoEntity(dto);
		if (dto.getId() == null) {
			saveWardenImage(dto, wardenInfoEntity.getId());
		}		
		saveWardenHostelMappingEntity(dto, wardenInfoEntity);
		return true;
	}

	@Transactional
	public WardenInfoEntity saveWardenInfoEntity(WardenInfoDto dto) {
		WardenInfoEntity wardenInfoEntity = new WardenInfoEntity();
		if (dto.getId() != null) {
			wardenInfoEntity = getWardenInfoEntity(dto.getId());
			wardenInfoEntity.onUpdate();
		} else {
			wardenInfoEntity.onCreate();
//			wardenInfoEntity.setImageName(dto.getImageName());
//			wardenInfoEntity.setImageBytes(dto.getImageBytes());
			wardenInfoEntity.setWardenName(dto.getWardenName());
		}		
		wardenInfoEntity.setWardenEmail(dto.getWardenEmail());
		wardenInfoEntity.setOfficeNo(dto.getOfficeNo());
		wardenInfoEntity.setPhoneNumber(dto.getPhoneNumber());
		wardenInfoEntity.setLdapUsername(dto.getLdapUsername());
		wardenInfoEntity.setAlternateEmail(dto.getAlternateEmail());
		wardenInfoEntity.setWardenInfoUrl(dto.getWardenInfoUrl());
		wardenInfoEntity.setAssociateLdapUsername(dto.getAssociateLdapUsername());
		wardenInfoEntity.setAssociateWardenName(dto.getAssociateWardenName());
		wardenInfoEntity.setImageName(dto.getImageName());
		wardenInfoEntity = wardenInfoRepository.saveAndFlush(wardenInfoEntity);
		return wardenInfoEntity;
	}

	@Transactional
	public boolean saveWardenHostelMappingEntity(WardenInfoDto dto, WardenInfoEntity wardenInfoEntity) {
		WardenHostelMappingEntity wardenHostelMappingEntity = new WardenHostelMappingEntity();
		WardenHostelMappingEntityId wardenHostelMappingEntityId = new WardenHostelMappingEntityId();

		wardenHostelMappingEntityId.setHostelId(dto.getHostelId());
		wardenHostelMappingEntityId.setWardenId(dto.getId() != null ? dto.getId() : wardenInfoEntity.getId());

		try {
			wardenHostelMappingEntity = getWardenHostelMappingEntity(wardenHostelMappingEntityId);
			wardenHostelMappingEntity.onUpdate();
		} catch (EntityNotFoundException e) {
			wardenHostelMappingEntity.setId(wardenHostelMappingEntityId);
			wardenHostelMappingEntity.onCreate();
			wardenHostelMappingEntity.setWithEffectiveDate(LocalDate.now());
		}

		wardenHostelMappingRepository.save(wardenHostelMappingEntity);
		return true;
	}

	public WardenInfoDto getWardenInfo(Long id) {
		WardenInfoEntity wardenInfoEntity = getWardenInfoEntity(id);
		WardenInfoDto dto = WardenInfoMapper.INSTANCE.fromWardenInfoEntity(wardenInfoEntity);
		Long hostelId = wardenHostelMappingRepository.getHostelId(Constants.ACTIVE_FLAG, dto.getId());
		dto.setHostelId(hostelId != null ? hostelId : 0);
		dto.setImageFileName(dto.getImageName());
		return dto;
	}

	public WardenInfoEntity getWardenInfoById(Long id) {
		Optional<WardenInfoEntity> optionalEntity = wardenInfoRepository.findByIdAndActiveFlag(id,ModelConstants.STATUS_ACTIVE);
		if (optionalEntity.isPresent()) {
			return optionalEntity.get();
		}else{
			return null;
		}
	}

	private WardenInfoEntity getWardenInfoEntity(Long id) {
		Optional<WardenInfoEntity> optionalEntity = wardenInfoRepository.findById(id);
		if (optionalEntity.isPresent()) {
			return optionalEntity.get();
		} else {
			throw new EntityNotFoundException(messageSource.getMessage("message.warden.info.record.not.found", null, Locale.getDefault()) + " " + id);
		}
	}

	private WardenHostelMappingEntity getWardenHostelMappingEntity(WardenHostelMappingEntityId wardenHostelMappingEntityId) {
		Optional<WardenHostelMappingEntity> optionalEntity = wardenHostelMappingRepository.findById(wardenHostelMappingEntityId);
		if (optionalEntity.isPresent()) {
			return optionalEntity.get();
		} else {
			throw new EntityNotFoundException(messageSource.getMessage("message.warden.hostel.mapping.record.not.found", null, Locale.getDefault()) + " " + wardenHostelMappingEntityId);
		}
	}

	private String encryptWardenRequestUrl(Long wardenId) throws Exception {
		String encryptKey = wardenId + Constants.BACKTICK + Utility.getCurrentTimeStamp();
		return MCrypt.getInstance().encryptToText(encryptKey);
	}
	
	@Transactional
	public boolean deleteWardenInfo(Long id) {
		try {
			Long hostelId = wardenHostelMappingRepository.getHostelId(Constants.ACTIVE_FLAG, id);
			WardenHostelMappingEntityId wardenHostelMappingEntityId = new WardenHostelMappingEntityId();
			wardenHostelMappingEntityId.setHostelId(hostelId);
			wardenHostelMappingEntityId.setWardenId(id);
			
			WardenInfoEntity wardenInfoEntity = getWardenInfoEntity(id);
			wardenInfoEntity.setActiveFlag(Constants.CANCEL_STATUS);
			wardenInfoEntity.onUpdate();
			wardenInfoRepository.save(wardenInfoEntity);
			
			WardenHostelMappingEntity wardenHostelMappingEntity = getWardenHostelMappingEntity(wardenHostelMappingEntityId);
			wardenHostelMappingEntity.setActiveFlag(Constants.CANCEL_STATUS);
			wardenHostelMappingEntity.onUpdate();		
			
			wardenHostelMappingRepository.save(wardenHostelMappingEntity);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void saveWardenImage(WardenInfoDto dto, Long id) throws Exception {
//		String fileName = "/" + id + ModelConstants.UNDERSCORE + ModelConstants.WARDEN_PROFILE;
		fileService.encodeFile(ModelConstants.FILE_PATH_WARDEN_IMAGE, dto.getImageBytes(), dto.getImageName());
	}

	@Transactional
	public String saveOrUpdateWardenRegistration(@Valid WardenInfoDto wardenInfoDto) {
		WardenInchargeDetailsEntity inchargeDetailsEntity = new WardenInchargeDetailsEntity();
		WardenInchargeDetailsEntity savedEntity = new WardenInchargeDetailsEntity();
		Optional<WardenInchargeDetailsEntity> optionlEntity = wardenInchargeDetailsRepository.findByIdAndActiveFlag(wardenInfoDto.getInchargeDetailsId(), ModelConstants.STATUS_ACTIVE);
		if(optionlEntity.isPresent()) {
			inchargeDetailsEntity = optionlEntity.get();
			WardenInchargeDetailsMapper.INSTANCE.updateInchargeEntity(inchargeDetailsEntity, wardenInfoDto);
			inchargeDetailsEntity.onUpdate();
		}else {
			WardenInchargeDetailsMapper.INSTANCE.onSaveEntity(inchargeDetailsEntity, wardenInfoDto);
			inchargeDetailsEntity.onCreate();
		}
		savedEntity = wardenInchargeDetailsRepository.save(inchargeDetailsEntity);
		if(savedEntity!=null) {
			try {
				sendMail(inchargeDetailsEntity);
			} catch (Exception e) {
				return Constants.ERROR;
			}
			return Constants.SAVED;
		}else {
			return null;
		}
	}

    private void sendMail(WardenInchargeDetailsEntity inchargeDetailsEntity) {
		WardenAwayRequestRecord record = wardenInchargeDetailsRepository.getWardenAwayRequestDetailsById(inchargeDetailsEntity.getId(), ModelConstants.STATUS_ACTIVE).orElse(null);
		if (Objects.nonNull(record)) {
			Optional<MailTemplateEntity> templateOpt = mailTemplateRepository.findByMailType(MailTemplateEntity.WARDEN_INCHARGE_INTIMATION);
			if (templateOpt.isPresent()) {
				MailTemplateEntity template = templateOpt.get();
				String subject = template.getMailSubject();
				String content = template.getMailTemplate();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
				String awayFrom = Objects.nonNull(record.awayFrom()) ? record.awayFrom().toLocalDate().format(formatter) : ModelConstants.NOT_APPLICABLE;
				String awayTo = Objects.nonNull(record.awayTo()) ? record.awayTo().toLocalDate().format(formatter) : ModelConstants.NOT_APPLICABLE;
				String toEmails = simsConfigDataService.getSimConfigValue(SimsConfigDataService.WARDEN_AWAY_MAIL);
				String finalEmails = Stream.of(toEmails, record.inchargerEmail(), record.hostelOfficeEmails())
						.filter(Objects::nonNull)
						.flatMap(s -> Arrays.stream(s.split(ModelConstants.COMMA)))
						.map(String::trim)
						.filter(s -> !s.isEmpty())
						.distinct()
						.collect(Collectors.joining(ModelConstants.COMMA));

				content = content.replaceAll("#%warden_name%#", Objects.nonNull(record.wardenName()) ? record.wardenName() : ModelConstants.NOT_APPLICABLE);
				content = content.replaceAll("#%warden_hostel%#", Objects.nonNull(record.wardenHostels()) ? record.wardenHostels() : ModelConstants.NOT_APPLICABLE);
				content = content.replaceAll("#%away_dates%#", awayFrom + " to " + awayTo);
				content = content.replaceAll("#%reason%#", Objects.nonNull(record.awayDescription()) ? record.awayDescription() : ModelConstants.NOT_APPLICABLE);
				content = content.replaceAll("#%incharge_name%#", Objects.nonNull(record.inchargerName()) ? record.inchargerName() : ModelConstants.NOT_APPLICABLE);
				content = content.replaceAll("#%incharge_hostel%#", Objects.nonNull(record.inchargeHostels()) ? record.inchargeHostels() : ModelConstants.NOT_APPLICABLE);
                try {
                    mailQueueService.saveMailQueue(subject, commonResponseUtil.getMessage("message.mail.greetings.for"), content,
							finalEmails, commonResponseUtil.getMessage("message.warden.incharge.mail.module"), SecurityCtxUtil.userId(),
							1, null, null, null, null);
                } catch (Exception e) {
					log.error("Failed to queue warden incharge mail for id: {}", inchargeDetailsEntity.getId(), e);
					throw new RuntimeException(e);
				}
            }
		}
    }

	public void validateWardenRegistration(WardenInfoDto wardenInfoDto, BindingResult bindingResult) {
		String errorMessage = "";
			if(wardenInfoDto.getAwayFrom()==null || wardenInfoDto.getAwayFrom().equals("")) {
				errorMessage = messageSource.getMessage("message.label.away.from.date", null, Locale.getDefault()) +" "+ messageSource.getMessage("message.validation.is.required", null, Locale.getDefault());
				 bindingResult.rejectValue("awayFrom", "error.awayFrom", errorMessage);
			}
			if(wardenInfoDto.getAwayTo()==null || wardenInfoDto.getAwayTo().equals("")) {
				errorMessage = messageSource.getMessage("message.label.away.to.date", null, Locale.getDefault()) +" "+ messageSource.getMessage("message.validation.is.required", null, Locale.getDefault());
				bindingResult.rejectValue("awayTo", "error.awayTo", errorMessage);
			}
			if(wardenInfoDto.getAwayDescription()==null || wardenInfoDto.getAwayDescription().isEmpty()) {
				errorMessage = messageSource.getMessage("message.label.away.description", null, Locale.getDefault()) +" "+ messageSource.getMessage("message.validation.is.required", null, Locale.getDefault());
				bindingResult.rejectValue("awayDescription", "error.awayDescription", errorMessage);
			}
			if(wardenInfoDto.getHostelId()==null || wardenInfoDto.getHostelId().equals("")) {
				errorMessage = messageSource.getMessage("message.validation.hostel.name", null, Locale.getDefault());
				bindingResult.rejectValue("hostelId", "error.hostelId", errorMessage);
			}
			if(wardenInfoDto.getInchargeName()==null || wardenInfoDto.getInchargeName().isEmpty()) {
				errorMessage = messageSource.getMessage("message.label.incharge.name", null, Locale.getDefault()) +" "+ messageSource.getMessage("message.validation.is.required", null, Locale.getDefault());
				bindingResult.rejectValue("inchargeName", "error.inchargeName", errorMessage);
			}
			if(wardenInfoDto.getInchargeEmail()==null || wardenInfoDto.getInchargeEmail().isEmpty()) {
				errorMessage = messageSource.getMessage("message.label.incharge.email", null, Locale.getDefault()) +" "+ messageSource.getMessage("message.validation.is.required", null, Locale.getDefault());
				bindingResult.rejectValue("inchargeEmail", "error.inchargeEmail", errorMessage);
			}
			if(wardenInfoDto.getInchargePhone()==null || wardenInfoDto.getInchargePhone().isEmpty()) {
				errorMessage = messageSource.getMessage("message.label.mobile.phone", null, Locale.getDefault()) +" "+ messageSource.getMessage("message.validation.is.required", null, Locale.getDefault());
				bindingResult.rejectValue("inchargePhone", "error.inchargePhone", errorMessage);
			}
			if(wardenInfoDto.getInchargeOfficeNo()==null || wardenInfoDto.getInchargeOfficeNo().isEmpty()) {
				errorMessage = messageSource.getMessage("message.label.office.phone", null, Locale.getDefault()) +" "+ messageSource.getMessage("message.validation.is.required", null, Locale.getDefault());
				bindingResult.rejectValue("inchargeOfficeNo", "error.inchargeOfficeNo", errorMessage);
			}
			if((wardenInfoDto.getAwayFrom()!=null && !wardenInfoDto.getAwayFrom().equals("")) && (wardenInfoDto.getAwayTo()!=null && !wardenInfoDto.getAwayTo().equals(""))) {
				if(wardenInfoDto.getAwayFrom().isAfter(wardenInfoDto.getAwayTo()) || wardenInfoDto.getAwayTo().isBefore(wardenInfoDto.getAwayFrom())) {
					errorMessage = messageSource.getMessage("message.validation.away.from.away.to.date", null, Locale.getDefault());
					bindingResult.rejectValue("validDate", "error.validDate", errorMessage);
				}
			}
		if (!errorMessage.isEmpty()) {
			bindingResult.rejectValue(null,errorMessage);
	    }
		
	}
}
