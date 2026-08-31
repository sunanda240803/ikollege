package com.iitm.hosteldine.service.mess;

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
import com.iitm.hosteldine.dto.mess.MessSessionDto;
import com.iitm.hosteldine.dto.mess.MessSessionIdDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.mess.MessSessionMapper;
import com.iitm.hosteldine.model.mess.MessSessionEntity;
import com.iitm.hosteldine.repository.mess.MessSessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessSessionsService {
	private final MessSessionRepository messSessionsRepository;
	private final MessageSource messageSource;

	public List<MessSessionDto> getMessionSessionList() {
		/*
		 * return Optional .ofNullable(
		 * messSessionsRepository.findAllByActiveFlagOrderByModifiedAtDesc(
		 * ModelConstants.STATUS_ACTIVE))
		 * .orElse(Collections.emptyList()).stream().map(MessSessionMapper.INSTANCE::
		 * fromMessSessionEntity) .collect(Collectors.toList());
		 */

		List<Object[]> result = messSessionsRepository
				.findAllByActiveFlagOrderByModifiedAtDesc(ModelConstants.STATUS_ACTIVE);

		return result.stream().map(record -> {
			MessSessionDto dto = new MessSessionDto();
			MessSessionIdDto idDto = new MessSessionIdDto();
			idDto.setMessId(((Long) record[0]).longValue());
			idDto.setSessionName((String) record[1]);
			dto.setId(idDto);
			dto.setStartTime((String) record[2]);
			dto.setEndTime((String) record[3]);
			dto.setMessName((String) record[4]);
			dto.setSessionCode((String) record[5]);
			return dto;
		}).collect(Collectors.toList());

	}

	public MessSessionDto getMessSessionDetailsById(long id, String sessionName) {
		return messSessionsRepository
				.findById_MessIdAndId_SessionNameAndActiveFlag(id, sessionName, ModelConstants.STATUS_ACTIVE)
				.map(MessSessionMapper.INSTANCE::fromMessSessionEntity).orElse(null);
	}

	public String saveUpdateMessSession(MessSessionDto dto) throws Exception {

		String result = Optional.ofNullable(dto.getId())
				.filter(idDto -> (idDto != null && idDto.getMessId() != null && idDto.getSessionName() != null))
				.flatMap(
						idDto -> messSessionsRepository.findById_MessIdAndId_SessionNameAndActiveFlag(idDto.getMessId(),
								idDto.getSessionName(), ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					MessSessionMapper.INSTANCE.onUpdateMessSessionEntity(existingEntity, dto);
					messSessionsRepository.save(existingEntity);
					return Constants.UPDATED;
				}).orElseGet(() -> {
					MessSessionEntity newEntity = MessSessionMapper.INSTANCE.onSaveEntity(dto);
					newEntity.onCreate();
					messSessionsRepository.save(newEntity);
					return Constants.SAVED;
				});

		return result;
	}

	public Boolean deleteMessSession(long id, String sessionName) throws Exception {
		return messSessionsRepository
				.findById_MessIdAndId_SessionNameAndActiveFlag(id, sessionName, ModelConstants.STATUS_ACTIVE)
				.map(entity -> {
					entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
					entity.onUpdate();
					return messSessionsRepository.save(entity).getId().getMessId() != null;
				}).orElseThrow(() -> new RecordNotExistsException(
						messageSource.getMessage("message.validation.error.id.not.found", null, Locale.getDefault())));
	}

	public boolean checkSessionNameExist(long id, String sessionName) {
		return messSessionsRepository
				.findByActiveFlagAndId_MessIdAndId_SessionNameIgnoreCase(ModelConstants.STATUS_ACTIVE,id, sessionName)
				.size() > 0;
	}

	public Page<MessSessionDto> getMessSessionList(PaginationForm form) {
		Page<Object[]> result;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("ms.modified_at").descending());
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			result = messSessionsRepository.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE, pageable);
		} else {
			result = messSessionsRepository.findByMessNameContainingAndActiveFlag(form.getSearch(),
					ModelConstants.STATUS_ACTIVE, pageable);
		}
		return result.map(record -> {
			MessSessionDto dto = new MessSessionDto();
			MessSessionIdDto idDto = new MessSessionIdDto();
			idDto.setMessId(((Long) record[0]).longValue());
			idDto.setSessionName((String) record[1]);
			dto.setId(idDto);
			dto.setStartTime((String) record[2]);
			dto.setEndTime((String) record[3]);
			dto.setMessName((String) record[4]);
			dto.setSessionCode((String) record[5]);
			return dto;
		});
	}

}
