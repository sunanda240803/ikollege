package com.iitm.hosteldine.service.mess;

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

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mess.MessTerminalDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.mess.MessTerminalMapper;
import com.iitm.hosteldine.model.mess.MessTerminalEntity;
import com.iitm.hosteldine.repository.mess.MessTerminalRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessTerminalService {
	private final MessTerminalRepository messTerminalRepo;
	private final MessageSource messageSource;

	public List<MessTerminalDto> getMessTerminalList() {
		return Optional
				.ofNullable(messTerminalRepo.findAllByActiveFlagAndMessMasterActiveFlagOrderByModifiedAtDesc(ModelConstants.STATUS_ACTIVE,ModelConstants.STATUS_ACTIVE))
				.orElse(Collections.emptyList()).stream().map(MessTerminalMapper.INSTANCE::fromMessTerminalEntity)
				.collect(Collectors.toList());
	}

	public MessTerminalDto getMessTerminalDetailsById(long id) {
		return messTerminalRepo.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
				.map(MessTerminalMapper.INSTANCE::fromMessTerminalEntity).orElse(new MessTerminalDto());
	}

	@Transactional
	public String saveAndUpdate(MessTerminalDto dto) throws Exception {
		String result = Optional.ofNullable(dto.getId()).filter(id -> (id != null && id > 0))
				.flatMap(id -> messTerminalRepo.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					MessTerminalMapper.INSTANCE.onUpdateEntity(existingEntity, dto);
					messTerminalRepo.save(existingEntity); 
					return Constants.UPDATED;
				}).orElseGet(() -> {
					dto.setId(null);
					MessTerminalEntity newEntity = MessTerminalMapper.INSTANCE.toMessTerminalEntity(dto);
					messTerminalRepo.save(newEntity); 
					return Constants.SAVED;
				});
		return result;
	}
	
	public boolean deleteMessTerminalById(long id) throws Exception {
		return messTerminalRepo.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE).map(entity -> {
			entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
			return messTerminalRepo.save(entity).getId() != null;
		}).orElseThrow(() -> new RecordNotExistsException(
				messageSource.getMessage("message.validation.error.id.not.found", null, Locale.getDefault())));
	}


	public boolean checkTerminalIpExist(String terminalIp,long id) {
		return messTerminalRepo.findAllByActiveFlagAndTerminalIpIgnoreCaseAndIdNot(ModelConstants.STATUS_ACTIVE, terminalIp,id)
				.size()>0;
	}

	public Page<MessTerminalDto> getMessTerminalList(PaginationForm form) {
		Page<MessTerminalEntity> result = null;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(), 
			    Sort.by("messMaster.messName").ascending());
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			result = messTerminalRepo.findAllByActiveFlagAndMessMasterActiveFlag(ModelConstants.STATUS_ACTIVE, ModelConstants.STATUS_ACTIVE,pageable);
		} else {
			result = messTerminalRepo.findByMessTerminalSearchList(ModelConstants.STATUS_ACTIVE,pageable,form.getSearch());
		}

		return result.map(MessTerminalMapper.INSTANCE::fromMessTerminalEntity);
	}
}
