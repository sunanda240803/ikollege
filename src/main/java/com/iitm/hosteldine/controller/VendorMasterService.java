package com.iitm.hosteldine.controller;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.iitm.hosteldine.dto.mess.MessVendorAllocationDto;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mess.MessVendorMasterDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.mess.MessVendorMasterMapper;
import com.iitm.hosteldine.model.mess.MessVendorMasterEntity;
import com.iitm.hosteldine.repository.mess.MessVendorMasterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorMasterService {
	private final MessVendorMasterRepository vendorMasterRepo;
	private final MessageSource messageSource;

	public List<MessVendorMasterDto> getVendorMasterList() {
		 return Optional.ofNullable(
		        vendorMasterRepo.findAllByActiveFlagOrderByModifiedAtDesc(ModelConstants.STATUS_ACTIVE)
			    )
			    .orElse(Collections.emptyList())
			    .stream()
			    .map(MessVendorMasterMapper.INSTANCE::fromMessVendorMasterEntity)
			    .collect(Collectors.toList());
	}

	public MessVendorMasterDto getVendorMasterDetailsById(String id) {
		return vendorMasterRepo.findByVendorCodeAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
				.map(MessVendorMasterMapper.INSTANCE::fromMessVendorMasterEntity).orElse(new MessVendorMasterDto());
	}

	public boolean deleteVendorMasterById(String id) throws Exception {
		return vendorMasterRepo.findByVendorCodeAndActiveFlag(id, ModelConstants.STATUS_ACTIVE).map(entity -> {
			entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
			entity.onUpdate();
			return vendorMasterRepo.save(entity).getVendorCode() != null;
		}).orElseThrow(() -> new RecordNotExistsException(
				messageSource.getMessage("validation.error.id.not.found", null, Locale.getDefault())));
	}

	public String saveAndUpdate(MessVendorMasterDto dto) throws Exception {
		return Optional.ofNullable(dto.getVendorCode()).filter(id -> id != null)
				.flatMap(id -> vendorMasterRepo.findByVendorCodeAndActiveFlag(id, ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					MessVendorMasterMapper.INSTANCE.onUpdateEntity(existingEntity, dto);
					existingEntity.onUpdate();
					vendorMasterRepo.save(existingEntity);
					return Constants.UPDATED;
				}).orElseGet(() -> {
					MessVendorMasterEntity newEntity = MessVendorMasterMapper.INSTANCE.toMessVendorMasterEntity(dto);
					newEntity.onCreate();
					vendorMasterRepo.save(newEntity);
					return Constants.SAVED;
				});
	}

	public boolean checkVendorCodeExist(String value) {
		return vendorMasterRepo.findAllByActiveFlagAndVendorCodeIgnoreCase(ModelConstants.STATUS_ACTIVE, value)
				.size() > 0;
	}

	public Page<MessVendorMasterDto> getVendorMasterList(PaginationForm form) {
		Page<MessVendorMasterEntity> result = null;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("modifiedAt").descending());
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			result = vendorMasterRepo.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE, pageable);
		} else {
			result = vendorMasterRepo.findByVendorMasterSearchList(ModelConstants.STATUS_ACTIVE, pageable,form.getSearch());
		}
		return result.map(MessVendorMasterMapper.INSTANCE::fromMessVendorMasterEntity);
	}

	public List<MessVendorAllocationDto> getVendorNames(String isActive) {
		return vendorMasterRepo.getVendorNamesByActiveStatus(isActive);
	}
	
}
