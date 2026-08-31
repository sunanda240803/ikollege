package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.SimsConfigDataJsonArrayDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.hostel.HostelMasterMapper;
import com.iitm.hosteldine.model.hostel.HostelMasterEntity;
import com.iitm.hosteldine.repository.hostel.HostelFloorMasterRepository;
import com.iitm.hosteldine.repository.hostel.HostelMasterRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.warden.WardenInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HostelMasterService {
    private final HostelMasterRepository hostelMasterRepository;
    private final MessageSource messageSource;
    private final HostelFloorMasterRepository hostelFloorMasterRepository;
	private final SimsConfigDataService simsConfigDataService;
	private final WardenInfoService wardenInfoService;
	private final HostelUserMappingService hostelUserMappingService;


	public List<HostelMasterDto> getHostelList () {
    	return hostelMasterRepository.
    			findAllByActiveFlagOrderByHostelName(ModelConstants.STATUS_ACTIVE)
    			.stream().map(HostelMasterMapper.INSTANCE::fromHostelMasterEntity)
				.collect(Collectors.toList());
    }
    
    public HostelMasterDto getHostelDetailsById (long hostelId) {
        return hostelMasterRepository
				.findByIdAndActiveFlag(hostelId, ModelConstants.STATUS_ACTIVE)
				.map(HostelMasterMapper.INSTANCE::fromHostelMasterEntity).orElse(new HostelMasterDto());
    }
    
    public String saveUpdateHostel(HostelMasterDto dto) throws Exception {
		String result = Optional.ofNullable(dto.getId()).filter(id -> (id != null && id > 0))
				.flatMap(id -> hostelMasterRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					HostelMasterMapper.INSTANCE.onUpdateEntity(existingEntity, dto);
					hostelMasterRepository.save(existingEntity); 
					return Constants.UPDATED;
				}).orElseGet(() -> {
					HostelMasterEntity newEntity = HostelMasterMapper.INSTANCE.onSaveEntity(dto);
					newEntity.setId(null);
					hostelMasterRepository.save(newEntity); 
					return Constants.SAVED;
				});
		return result;
	}
    
	public boolean deleteHostelById(long id) throws NoSuchMessageException, RecordNotExistsException {
		if (hostelFloorMasterRepository.existsByActiveFlagAndHostelId(ModelConstants.STATUS_ACTIVE, id)) {
			throw new RecordNotExistsException(
					messageSource.getMessage("message.validation.error.hostel.assigned", null, Locale.getDefault()));
		}
		return hostelMasterRepository.findById(id).map(entity -> {
			entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
			return hostelMasterRepository.save(entity).getId() != null;
		}).orElseThrow(() -> new RecordNotExistsException(
				messageSource.getMessage("message.validation.error.id.not.found", null, Locale.getDefault())));
	}
    
	public boolean checkHostelNameExist(String hostelName,long id) {
		return hostelMasterRepository
				.findByActiveFlagAndHostelNameIgnoreCaseAndIdNot(
						ModelConstants.STATUS_ACTIVE, hostelName,id).size()>0;
	}
	
	public boolean checkHostelCodeExist(String hostelCode,long id) {
		return hostelMasterRepository
				.findByActiveFlagAndHostelShortCodeIgnoreCaseAndIdNot(
						ModelConstants.STATUS_ACTIVE, hostelCode,id).size()>0;
	}

	public Page<HostelMasterDto> getHostelList(PaginationForm form) {
		Page<HostelMasterEntity> result = null;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("hostelName").ascending());
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			result = hostelMasterRepository.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE, pageable);
		} else
		{
			result = hostelMasterRepository.findByHostelMasterSearchList(ModelConstants.STATUS_ACTIVE, pageable,form.getSearch());
		}

		return result.map(HostelMasterMapper.INSTANCE::fromHostelMasterEntity);
	}

	/**
	 * Retrieves the values of id, hostel name and hostel shortcode from the HOSTEL_MASTER table 
	 */
	public HostelMasterDto checkHostelExist(String value) {
		return getHostelList().stream()
				.filter(hostel -> hostel.getHostelName().equalsIgnoreCase(value)).findFirst()
				.map(hostel -> new HostelMasterDto(hostel.getId(), hostel.getHostelName(), hostel.getHostelShortCode()))
				.orElseThrow(() -> new IllegalArgumentException("Hostel is not found: " + value));
	}

	public List<HostelMasterDto> getHostelListByUser(){
		return getHostelListByUser(SimsConfigDataService.ALL_HOSTEL_VIEW_ROLES);
	}
	
	public List<HostelMasterDto> getHostelListByUser(String allowedRoleKey){
		List<HostelMasterDto> hostelList;
		String userRole = SecurityCtxUtil.userRole();
		String userName = SecurityCtxUtil.userName();
		List<String> allowedRoles = simsConfigDataService.getSimConfigValueFromJsonArray(allowedRoleKey)
				.stream()
				.map(SimsConfigDataJsonArrayDto::getValue)
				.toList();
		if (HostelConstants.WARDEN.getConstants().equals(userRole)) {
			hostelList = wardenInfoService.getWardenDetailsList(userName);
		} else if (allowedRoles.contains(userRole)) {
			hostelList = getHostelList();
		} else {
			hostelList = hostelUserMappingService.getHostelDetailsList(userName);
		}
		return hostelList;
	}

	public List<HostelMasterDto> getHostelList (String hostelName) {
		return hostelMasterRepository.
				findByActiveFlagAndHostelName(ModelConstants.STATUS_ACTIVE, hostelName)
				.stream().map(HostelMasterMapper.INSTANCE::fromHostelMasterEntity)
				.collect(Collectors.toList());
	}
	
}
