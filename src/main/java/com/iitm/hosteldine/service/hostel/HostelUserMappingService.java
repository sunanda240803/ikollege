package com.iitm.hosteldine.service.hostel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.hostel.HostelUserMappingDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.hostel.HostelMasterMapper;
import com.iitm.hosteldine.mapper.hostel.HostelUserMappingMapper;
import com.iitm.hosteldine.model.hostel.HostelUserMappingEntity;
import com.iitm.hosteldine.model.hostel.HostelUserMappingId;
import com.iitm.hosteldine.repository.UserManagementRepository;
import com.iitm.hosteldine.repository.hostel.HostelUserMappingRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HostelUserMappingService {

	private final HostelUserMappingRepository mappingRepo;
	private final MessageSource messageSource;
	private final UserManagementRepository userManagementRepository;

	public List<HostelUserMappingDto> getMappingList() {
		return Optional.ofNullable(mappingRepo.findAllByActiveFlagOrderByModifiedAtDesc(ModelConstants.STATUS_ACTIVE))
				.orElse(Collections.emptyList()).stream()
				.map(HostelUserMappingMapper.INSTANCE::fromHostelUserMappingEntity).collect(Collectors.toList());
	}

	public boolean deleteMappingById(HostelUserMappingDto dto) throws Exception {
		return mappingRepo.findByIdHostelIdAndIdUserAndActiveFlag(dto.getHostelId(), dto.getUserName(),
				ModelConstants.STATUS_ACTIVE).map(entity -> {
					entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
					entity.onUpdate();
					return mappingRepo.save(entity).getId() != null;
				}).orElseThrow(() -> new RecordNotExistsException(
						messageSource.getMessage("message.validation.error.id.not.found", null, Locale.getDefault())));
	}

	public Boolean saveAndUpdate(HostelUserMappingDto dto) {
		Boolean status = false;
		List<HostelUserMappingEntity> entityList = new ArrayList<HostelUserMappingEntity>();
			if (dto.getId() != null && dto.getId().getUser() != null) {
			int count = 0;
			for (HostelMasterDto hostelDto : dto.getHostelMasterList()) {
				if (hostelDto.getStatus()) {
					Optional<HostelUserMappingEntity> hstlUserMapping = mappingRepo
							.findByIdHostelIdAndIdUserAndActiveFlag(dto.getHostelId(), dto.getUserName(),
									ModelConstants.STATUS_ACTIVE);
					HostelUserMappingEntity entity = new HostelUserMappingEntity();
					if (hstlUserMapping.isPresent()) {
						entity = hstlUserMapping.get();
						entity.onUpdate();
					} else {
						entity.setId(new HostelUserMappingId());
						entity.getId().setHostel(HostelMasterMapper.INSTANCE.toHostelMasterEntity(hostelDto));
						entity.getId().setUser(dto.getId().getUser());
						entity.onCreate();
					}
					entityList.add(entity);
					count++;
				}
			}
			if (count > 0) {
				mappingRepo.saveAll(entityList);
			} else {
				new RecordNotExistsException(messageSource.getMessage("message.validation.error.user.mapping.hostel.required",
						null, Locale.getDefault()));
			}
			status = true;
		} else {
			new RecordNotExistsException(
					messageSource.getMessage("message.validation.error.user.name.not.valid", null, Locale.getDefault()));
		}

		return status;
	}

	public Page<HostelUserMappingDto> getMappingList(PaginationForm form) {
		Page<HostelUserMappingEntity> result = null;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("modifiedAt").ascending());
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			result = mappingRepo.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE, pageable);
		} else {
			result = mappingRepo.findByHostelUserMappingSearchList(ModelConstants.STATUS_ACTIVE, pageable,form.getSearch());
		}
		return result.map(HostelUserMappingMapper.INSTANCE::fromHostelUserMappingEntity);
	}

	public List<HostelMasterDto> getHostelDetailsList(String userId) {
		return Optional.ofNullable(mappingRepo.getHostelUserMappingDetails(userId, ModelConstants.STATUS_ACTIVE))
				.orElse(Collections.emptyList()).stream().map(HostelMasterMapper.INSTANCE::fromHostelMasterEntity)
				.collect(Collectors.toList());
	}

	public HostelUserMappingDto getUserMapping(String userName) {
		return mappingRepo.findFirstByIdUserAndActiveFlagOrderByCreatedAtDesc(userName, ModelConstants.STATUS_ACTIVE)
				.map(HostelUserMappingMapper.INSTANCE::fromHostelUserMappingEntity)
				.orElse(null);
	}
}
