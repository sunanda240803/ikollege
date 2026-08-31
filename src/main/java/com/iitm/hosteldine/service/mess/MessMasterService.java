package com.iitm.hosteldine.service.mess;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.dto.mess.MessVendorAllocationDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.mess.MessMasterMapper;
import com.iitm.hosteldine.model.mess.MessMasterEntity;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.repository.mess.MessSessionRepository;
import com.iitm.hosteldine.repository.mess.MessTerminalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessMasterService {
    private final MessMasterRepository messMasterRepo;
    private final MessageSource messageSource;
    private final MessTerminalRepository messTerminalRepository;
    private final MessSessionRepository messSessionRepository;

    public List<MessMasterDto> getMessMasterList() {
        return Optional.ofNullable(messMasterRepo.findAllByActiveFlagOrderByMessNameAsc(ModelConstants.STATUS_ACTIVE)).orElse(Collections.emptyList()).stream().map(MessMasterMapper.INSTANCE::fromMessMasterEntity).collect(Collectors.toList());
    }

    public String getOnlineCouponMessIdList(String vegOrNonVeg) {
        return Optional.ofNullable(
                        messMasterRepo.findAllByActiveFlagAndOnlineCouponAndIsVegNonVegOrderByMessNameAsc(
                                ModelConstants.STATUS_ACTIVE, true, vegOrNonVeg
                        )
                ).orElse(Collections.emptyList())
                .stream()
                .map(mess -> String.valueOf(mess.getId()))
                .collect(Collectors.joining(","));
    }


    public MessMasterDto getMessMasterDetailsById(long id) {
        return messMasterRepo.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE).map(MessMasterMapper.INSTANCE::fromMessMasterEntity).orElse(new MessMasterDto());
    }

    public String saveAndUpdate(MessMasterDto dto) throws Exception {
        String result = Optional.ofNullable(dto.getId()).filter(id -> (id != null && id > 0)).flatMap(id -> messMasterRepo.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)).map(existingEntity -> {
            MessMasterMapper.INSTANCE.onUpdateEntity(existingEntity, dto);
            messMasterRepo.save(existingEntity);
            return Constants.UPDATED;
        }).orElseGet(() -> {
            MessMasterEntity newEntity = MessMasterMapper.INSTANCE.toMessMasterEntity(dto);
            newEntity.setId(null);
            messMasterRepo.save(newEntity);
            return Constants.SAVED;
        });
        return result;
    }

    public boolean deleteMessMasterById(long id) throws  RecordNotExistsException {
        if (messSessionRepository.existsByActiveFlagAndIdMessId(ModelConstants.STATUS_ACTIVE, id)) {
            throw new RecordNotExistsException(messageSource.getMessage("message.validation.error.mess.session.assigned", null, Locale.getDefault()));
        }
        if (messTerminalRepository.existsByActiveFlagAndMessMasterId(ModelConstants.STATUS_ACTIVE, id)) {
            throw new RecordNotExistsException(messageSource.getMessage("message.validation.error.mess.terminal.assigned", null, Locale.getDefault()));
        }
        return messMasterRepo.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE).map(entity -> {
            entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
            return messMasterRepo.save(entity).getId() != null;
        }).orElseThrow(() -> new RecordNotExistsException(messageSource.getMessage("message.validation.error.id.not.found", null, Locale.getDefault())));
    }

    public boolean checkMessNameExist(String messName, long id) {
        return messMasterRepo.findAllByActiveFlagAndMessNameIgnoreCaseAndIdNot(ModelConstants.STATUS_ACTIVE, messName, id).size() > 0;
    }

    public boolean checkMessHeadExist(String messHead, long id) {
        return messMasterRepo.findAllByActiveFlagAndMessHeadIgnoreCaseAndIdNot(ModelConstants.STATUS_ACTIVE, messHead, id).size() > 0;
    }

    public MessMasterDto getMessCapacityList() {
        List<Object[]> messMasterConfigList = messMasterRepo.getMessMasterConfigList(ModelConstants.STATUS_ACTIVE);

        // Map each Object[] to a new MessMasterDto
        List<MessMasterDto> messMasterDtoList = messMasterConfigList.stream().map(o -> {
            MessMasterDto messMasterDto = new MessMasterDto(); // Create a new instance for each record

            Long allocatedCount = o[4] != null ? Long.parseLong(String.valueOf(o[4])) : null;
            String messName = o[1] != null ? String.valueOf(o[1]) : null;
            Integer capacity = o[3] != null ? Integer.parseInt(String.valueOf(o[3])) : null;
            Long vacancyCount = o[5] != null ? Long.parseLong(String.valueOf(o[5])) : null;
            Boolean applicableStatus = o[2] != null && Boolean.parseBoolean(String.valueOf(o[2]));
            Long messId = o[0] != null ? Long.parseLong(String.valueOf(o[0])) : null;

            // Set values on the new MessMasterDto object
            messMasterDto.setId(messId);
            messMasterDto.setAllocatedCount(allocatedCount);
            messMasterDto.setMessName(messName);
            messMasterDto.setCapacity(capacity);
            messMasterDto.setVacancyCount(vacancyCount);
            messMasterDto.setApplicableStatus(applicableStatus);

            return messMasterDto;
        }).toList();
        MessMasterDto messMasterDto = new MessMasterDto();
        messMasterDto.setMessMasterConfigList(messMasterDtoList);
        // Return the list of MessMasterDto objects
        return messMasterDto;
    }

    public String updateMessCapacity(MessMasterDto messMasterDto) {
        List<MessMasterEntity> messMasterEntityList = messMasterDto.getMessMasterConfigList().stream().map(MessMasterMapper.INSTANCE::toMessMasterEntity).collect(Collectors.toList());
        messMasterEntityList.forEach(messMasterDto1 -> {
            Optional<MessMasterEntity> optionalMessMasterEntity = messMasterRepo.findByIdAndActiveFlag(messMasterDto1.getId(), ModelConstants.STATUS_ACTIVE);
            MessMasterEntity messMasterEntity1 = new MessMasterEntity();
            if (optionalMessMasterEntity.isPresent()) {
                messMasterEntity1 = optionalMessMasterEntity.get();
            }
            messMasterEntity1.setCapacity(messMasterDto1.getCapacity()!=null?messMasterDto1.getCapacity():0);
            messMasterEntity1.setApplicableStatus(messMasterDto1.getApplicableStatus());
            messMasterRepo.save(messMasterEntity1);
        });
        return Constants.UPDATED;

    }

	public Page<MessMasterDto> getMessMasterList(PaginationForm form) {
		Page<MessMasterEntity> result = null;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("messName").ascending());
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			result = messMasterRepo.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE, pageable);
		} else {
			result = messMasterRepo.getMessMasterSearchList(ModelConstants.STATUS_ACTIVE, pageable,form.getSearch());
			
		}
		return result.map(MessMasterMapper.INSTANCE::fromMessMasterEntity);
	}

	public List<MessMasterEntity> getPriorityMessMasterList(List<Long> girlsOptionOneLong, String statusActive) {
        return messMasterRepo.getPriorityMessMasterList(girlsOptionOneLong, statusActive);
	}
	

	public MessMasterDto getMessList() {
        String studentId = SecurityCtxUtil.userId().toUpperCase();
        List<Object[]> result = messMasterRepo.getMessList(studentId, ModelConstants.STATUS_ACTIVE);
        MessMasterDto dto = new MessMasterDto();
        for (Object[] row : result) {
            dto.setId((Long) row[0]);
            dto.setMessName((String) row[1]);
           
        }

        return dto;
    }

    public List<MessVendorAllocationDto> getMessFloors(String isActive) {
        return messMasterRepo.getMessFloorsByActiveFlag(isActive);
    }
    
    public boolean checkMessIdValidOrNot(Long messId) {
		Optional<MessMasterEntity> messEntity = messMasterRepo.findByIdAndActiveFlag(messId,
				ModelConstants.STATUS_ACTIVE);
		if (messId != null && messId != 0 && !messEntity.isEmpty() && messEntity.get().getId() != null) {
			return true;
		}
		
		return false;
	}
    
	public List<MessMasterDto> getMessListForAllotment(Long messId, String gender) {
		return Optional.ofNullable(messMasterRepo.findAllByActiveFlagOrderByMessNameAsc(ModelConstants.STATUS_ACTIVE))
				.orElse(Collections.emptyList()).stream().filter(mess -> !Objects.equals(mess.getId(), messId))
				.filter(mess -> {
					if (Constants.MALE.equalsIgnoreCase(gender)) {
						String genderOption = mess.getGenderOption();
						return Constants.BOYS_HOSTEL_B.equalsIgnoreCase(genderOption)
								|| Constants.BOYS_HOSTEL_BT.equalsIgnoreCase(genderOption);
					}
					return true;
				}).map(MessMasterMapper.INSTANCE::fromMessMasterEntity).collect(Collectors.toList());
	}

}
