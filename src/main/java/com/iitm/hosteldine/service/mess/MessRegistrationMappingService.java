package com.iitm.hosteldine.service.mess;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.mapper.mess.MessMasterMapper;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mess.MessRegistrationMappingDto;
import com.iitm.hosteldine.mapper.mess.MessRegistrationMappingMapper;
import com.iitm.hosteldine.repository.mess.MessRegistrationMappingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessRegistrationMappingService {
	private final MessRegistrationMappingRepository messRegistrationMappingRepository;
	private final MessMasterRepository messMasterRepository;
	private final MessMasterService messMasterService;

	public List<MessMasterDto> getMessMasterList() {
		return Optional.ofNullable(messMasterRepository.getMessMasterList(ModelConstants.STATUS_ACTIVE))
				.orElse(Collections.emptyList()).stream()
				.map(MessMasterMapper.INSTANCE::fromMessMasterEntity).collect(Collectors.toList());
	}

	public List<MessMasterDto> getMessMasterListByCaterer() {
		String userName = SecurityCtxUtil.userName();
		List<Object[]> resultList = messMasterRepository.getMessDetailsForCaterer(userName, ModelConstants.STATUS_ACTIVE);
		List<MessMasterDto> messMasterDtoList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(resultList)) {
			for (Object[] result : resultList) {
				MessMasterDto dto = new MessMasterDto();
				dto.setId(result[1] != null ? Long.valueOf(result[1].toString()) : 0L);
				dto.setMessName(result[2] != null ? result[2].toString() : "");
				messMasterDtoList.add(dto);
			}
		}
		return messMasterDtoList;
	}

	public List<List<MessMasterDto>> getMessPreferences() {
		List<MessRegistrationMappingDto> messRegistrationMappingList = getMappingList();
		if (messRegistrationMappingList.isEmpty()) {
			return List.of(List.of(), List.of(), List.of());
		}
		MessRegistrationMappingDto firstMapping = messRegistrationMappingList.get(0);
		Function<String, List<MessMasterDto>> fetchMessMasterDtosWithCheck = preference -> {
			List<Long> preferenceIds = Arrays.stream(
							preference.replaceAll(Constants.SYMBOL_PATTERN, "").split(Constants.ARRAY_SEPARATOR))
					.map(Long::valueOf)
					.toList();
			return messMasterService.getPriorityMessMasterList(preferenceIds, ModelConstants.STATUS_ACTIVE)
					.stream()
					.map(MessMasterMapper.INSTANCE::fromMessMasterEntity)
					.peek(dto -> dto.setCheck(preferenceIds.contains(dto.getId())))
					.toList();
		};
		return List.of(
				fetchMessMasterDtosWithCheck.apply(firstMapping.getGirlsOptionOne()),
				fetchMessMasterDtosWithCheck.apply(firstMapping.getGirlsOptionTwo()),
				fetchMessMasterDtosWithCheck.apply(firstMapping.getBoysOption())
		);
	}

	public List<MessRegistrationMappingDto> getMappingList() {
		return Optional.ofNullable(messRegistrationMappingRepository.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE))
				.orElse(Collections.emptyList()).stream()
				.map(MessRegistrationMappingMapper.INSTANCE::fromMessRegistrationMappingEntity).collect(Collectors.toList());
	}

	public MessRegistrationMappingDto getMessRegistrationMapping() {
		return messRegistrationMappingRepository.findById(1L)
				.map(MessRegistrationMappingMapper.INSTANCE::fromMessRegistrationMappingEntity)
				.orElse(new MessRegistrationMappingDto());
	}

	public String saveMessRegistrationMapping(MessRegistrationMappingDto messRegistrationMappingDto) {
		return Optional.ofNullable(messRegistrationMappingDto.getId())
				.filter(id -> id > 0L)
				.flatMap(messRegistrationMappingRepository::findById)
				.map(existingEntity -> {
					MessRegistrationMappingMapper.INSTANCE.onUpdateEntity(existingEntity, messRegistrationMappingDto);
					messRegistrationMappingRepository.save(existingEntity);
					return Constants.UPDATED;
				})
				.orElse(Constants.UPDATED);
	}
}
