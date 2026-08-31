package com.iitm.hosteldine.service.collegeInfo;

import java.util.Locale;
import java.util.Optional;

import com.iitm.hosteldine.service.FileService;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.SimsConfigDataDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.warden.GuestAccommodationChargesDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.SimsConfigDataMapper;
import com.iitm.hosteldine.mapper.hostel.HostelMasterMapper;
import com.iitm.hosteldine.mapper.warden.GuestAccommodationChargesMapper;
import com.iitm.hosteldine.model.SimsConfigDataEntity;
import com.iitm.hosteldine.model.hostel.HostelMasterEntity;
import com.iitm.hosteldine.model.student.SickFoodRequestEntity;
import com.iitm.hosteldine.model.warden.GuestAccommodationChargesEntity;
import com.iitm.hosteldine.repository.SimsConfigDataRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SimsConfigService {
	private final SimsConfigDataRepository simsConfigDataRepository;
	private final MessageSource messageSource;
	private final FileService fileService;

	public Page<SimsConfigDataDto> getSimsConfigList(PaginationForm form) {
		Page<SimsConfigDataEntity> result = null;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("createdAt").descending());
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			result = simsConfigDataRepository.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE, pageable);
		}
		else {
			result = simsConfigDataRepository.getSimsSearchList(ModelConstants.STATUS_ACTIVE, pageable,form.getSearch());
		}
		return result.map(SimsConfigDataMapper.INSTANCE::fromSimsConfigDataEntity);
	}

	
	
	

	public String saveUpdateSimsConfig(SimsConfigDataDto dto) {
		String result = Optional.ofNullable(dto.getId()).filter(id -> (id != null && id > 0))
				.flatMap(id -> simsConfigDataRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					SimsConfigDataMapper.INSTANCE.onUpdateEntity(existingEntity, dto);
					existingEntity.setConfigKey(existingEntity.getConfigKey().toUpperCase());
					simsConfigDataRepository.save(existingEntity);
					return Constants.UPDATED;
				}).orElseGet(() -> {
					SimsConfigDataEntity newEntity = SimsConfigDataMapper.INSTANCE.onSaveEntity(dto);
					newEntity.setConfigKey(dto.getConfigKey().toUpperCase());
					simsConfigDataRepository.save(newEntity);
					return Constants.SAVED;
				});
		//Reset the simsconfig location map.
		fileService.resetSimsConfigLocation();
		return result;
	}



	public SimsConfigDataDto getSimsConfigById(long id) {
		return simsConfigDataRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
				.map(SimsConfigDataMapper.INSTANCE::fromSimsConfigDataEntity)
				.orElse(new SimsConfigDataDto());
	}



	
	public boolean deleteSims(Long id) throws RecordNotExistsException {
		return simsConfigDataRepository.findById(id).map(entity -> {
			entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
			simsConfigDataRepository.save(entity);
			return true;
		}).orElseThrow(() -> new RecordNotExistsException(
				messageSource.getMessage("validation.error.id.not.found", null, Locale.getDefault())));
	}





	public boolean checkConfigKeyExists(String configKey,long id) {
		 Optional<SimsConfigDataEntity> existingRequest = simsConfigDataRepository
			        .findTopByActiveFlagIgnoreCaseAndConfigKeyAndIdNot(ModelConstants.STATUS_ACTIVE, configKey,id);
			    return existingRequest.isPresent();
			}





	public void validateConfigKey(@Valid SimsConfigDataDto dto, BindingResult bindingResult) {
	    // Check if the configKey already exists
		 boolean exists = checkConfigKeyExists(dto.getConfigKey(), dto.getId() != null ? dto.getId() : 0L);
		    
		    if (exists) {
	        String errorMessage = messageSource.getMessage("message.label.configkey.exist", null, Locale.getDefault());
	        bindingResult.rejectValue("configKey", "error.configKey", errorMessage);
	    }
	}


}
