package com.iitm.hosteldine.service.hostel;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.HostelFloorMasterDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.hostel.HostelFloorMasterMapper;
import com.iitm.hosteldine.model.hostel.HostelFloorMasterEntity;
import com.iitm.hosteldine.repository.hostel.HostelFloorMasterRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomInfoRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class HostelFloorMasterService {
    private final HostelFloorMasterRepository hostelFloorMasterRepository;
    private final MessageSource messageSource;
    private final HostelRoomInfoRepository hostelRoomInfoRepository;

    public List<HostelFloorMasterDto> getAllFloorList () {
    	return hostelFloorMasterRepository.
    			findAllByActiveFlagOrderByHostelHostelNameAscFloorNameAsc(ModelConstants.STATUS_ACTIVE)
    			.stream().map(HostelFloorMasterMapper.INSTANCE::fromHostelFloorMasterEntity)
				.collect(Collectors.toList());
    }
    
    public List<HostelFloorMasterDto> getFloorListByHostelId (long hostelId) {
    	return hostelFloorMasterRepository.
    			findAllByActiveFlagAndHostelIdOrderByFloorName(ModelConstants.STATUS_ACTIVE,hostelId)
    			.stream().map(HostelFloorMasterMapper.INSTANCE::fromHostelFloorMasterEntity)
				.collect(Collectors.toList());
    }
    
    public HostelFloorMasterDto getFloorDetailsById (long floorId) {
        return hostelFloorMasterRepository
				.findByIdAndActiveFlag(floorId, ModelConstants.STATUS_ACTIVE)
				.map(HostelFloorMasterMapper.INSTANCE::fromHostelFloorMasterEntity).orElse(new HostelFloorMasterDto(floorId));
    }
    
    
    public String saveUpdateFloor(HostelFloorMasterDto dto) throws Exception {
		String result = Optional.ofNullable(dto.getId()).filter(id -> (id != null && id > 0))
				.flatMap(id -> hostelFloorMasterRepository.findByIdAndActiveFlag(dto.getId(),
						ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					HostelFloorMasterMapper.INSTANCE.onUpdateEntity(existingEntity, dto);
					hostelFloorMasterRepository.save(existingEntity); 
					return Constants.UPDATED;
				}).orElseGet(() -> {
					HostelFloorMasterEntity newEntity = HostelFloorMasterMapper.INSTANCE.onSaveEntity(dto);
					newEntity.setId(null);
					hostelFloorMasterRepository.save(newEntity); 
					return Constants.SAVED;
				});
		return result;
	}
    
	public boolean deleteFloorById(long id) throws NoSuchMessageException, RecordNotExistsException {
		if (hostelRoomInfoRepository.existsByActiveFlagAndBuildingId(ModelConstants.STATUS_ACTIVE, id)) {
			throw new RecordNotExistsException(
					messageSource.getMessage("message.validation.error.floor.assigned", null, Locale.getDefault()));
		}
		return hostelFloorMasterRepository.findById(id).map(entity -> {
			entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
			return hostelFloorMasterRepository.save(entity).getId() != null;
		}).orElseThrow(() -> new RecordNotExistsException(
				messageSource.getMessage("message.validation.error.id.not.found", null, Locale.getDefault())));

	}

	public boolean checkFloorNameExist(String floorName,long hostelId, long id) {
		return hostelFloorMasterRepository
				.findByActiveFlagAndFloorNameIgnoreCaseAndHostelIdAndIdNot(
						ModelConstants.STATUS_ACTIVE, floorName,hostelId,id).size()>0;
	}
	
	public List<String> getUniqueFloorName() {
		return hostelFloorMasterRepository
				.findDistinctHostelNames(ModelConstants.STATUS_ACTIVE);
		}

	public Page<HostelFloorMasterDto> getAllFloorList(PaginationForm form) {
		Page<HostelFloorMasterEntity> result;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(),
				Sort.by("hostel.hostelName").ascending().and(Sort.by("floorName").ascending()));
	    if (form.getSearch() == null || form.getSearch().isEmpty()) {
	        result = hostelFloorMasterRepository
	                 .findAllByActiveFlag(ModelConstants.STATUS_ACTIVE, pageable);
	    } else {
	        result = hostelFloorMasterRepository.findByFloorMasterSearchList(ModelConstants.STATUS_ACTIVE, pageable,form.getSearch());
	    }
	    return result.map(HostelFloorMasterMapper.INSTANCE::fromHostelFloorMasterEntity);
	}
}
