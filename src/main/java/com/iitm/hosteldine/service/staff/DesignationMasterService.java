package com.iitm.hosteldine.service.staff;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.staff.StaffDesignationMasterDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.mapper.staff.StaffDesignationMasterMapper;
import com.iitm.hosteldine.model.staff.StaffDesignationMasterEntity;
import com.iitm.hosteldine.repository.staff.StaffDesignationMasterRepository;
import com.iitm.hosteldine.repository.staff.StaffDetailsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DesignationMasterService {
	private final StaffDesignationMasterRepository staffDesignationMasterRepository;
	private final StaffDetailsRepository staffDetailsRepository;
	private final MessageSource messageSource;

	
	public List<StaffDesignationMasterDto> getDesignationList() {
		return staffDesignationMasterRepository.findAllByActiveFlagOrderByDesignationName(ModelConstants.STATUS_ACTIVE)
				.stream().map(StaffDesignationMasterMapper.INSTANCE::fromStaffDesignationMasterEntity)
				.collect(Collectors.toList());
	}


	public StaffDesignationMasterDto getDesignationMasterById(long id) {
		return staffDesignationMasterRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
				.map(StaffDesignationMasterMapper.INSTANCE::fromStaffDesignationMasterEntity).orElse(null);
	}


	
	public String saveUpdateDesignation(StaffDesignationMasterDto designationMasterDto) throws Exception {
		String result = Optional.ofNullable(designationMasterDto.getId()).filter(id -> (id != null && id > 0))
				.flatMap(id -> staffDesignationMasterRepository.findByIdAndActiveFlag(designationMasterDto.getId(),
						ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					StaffDesignationMasterMapper.INSTANCE.onUpdateDesignationEntity(existingEntity,
							designationMasterDto);
					staffDesignationMasterRepository.save(existingEntity);
					return Constants.UPDATED;
				}).orElseGet(() -> {
					StaffDesignationMasterEntity newEntity = StaffDesignationMasterMapper.INSTANCE
							.onSaveEntity(designationMasterDto);
					staffDesignationMasterRepository.save(newEntity);
					return Constants.SAVED;
				});
		return result;
	}

	public boolean checkdesignationNameExist(String designationName, long id) {
		return staffDesignationMasterRepository
				.findByActiveFlagAndDesignationNameIgnoreCaseAndIdNot(ModelConstants.STATUS_ACTIVE, designationName, id)
				.isPresent();
	}



	public Boolean deleteDesignationMasterById(long id) throws NoSuchMessageException, RecordNotExistsException {
		Long count = staffDetailsRepository.countByDesignationIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE);
		if (count > 0) {
			throw new RecordNotExistsException(
					messageSource.getMessage("message.validation.error.designation.assigned", null, Locale.getDefault()));
		} else {
			Optional<StaffDesignationMasterEntity> optionalEntity = staffDesignationMasterRepository.findById(id);

			if (optionalEntity.isPresent()) {
				StaffDesignationMasterEntity entity = optionalEntity.get();
				entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
				entity.onUpdate();
				return staffDesignationMasterRepository.save(entity).getId() != null;
			} else {
				throw new RecordNotExistsException(
						messageSource.getMessage("message.validation.error.id.not.found", null, Locale.getDefault()));
			}
		}
	}

	



	

}
