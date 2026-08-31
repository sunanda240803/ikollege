package com.iitm.hosteldine.service;

import com.hectrix.www.ACTAtek_xsd.Facial;
import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.dao.biometric.UserFpCardDao;
import com.iitm.hosteldine.dto.student.UserFpCardDto;
import com.iitm.hosteldine.entity.student.UserFpCardEntity;
import com.iitm.hosteldine.mapper.student.UserFpCardMapper;
import com.iitm.hosteldine.repository.student.UserFpCardRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserFpCardService {

	private final UserFpCardRepository userFpCardRepository;

	public String getRfidPinUpdatedDate(String studentId) {
		UserFpCardEntity entity = userFpCardRepository.findByUserId(studentId);
		if (entity != null && entity.getAccessCardSerialNo() != null && entity.getPinNo() != null
				&& entity.getRegAccessModifiedDate() != null) {
			LocalDateTime lastUpdatedDate = entity.getRegAccessModifiedDate();
			ZonedDateTime zonedDateTime = lastUpdatedDate.atZone(ZoneId.systemDefault());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
			return zonedDateTime.toLocalDate().format(formatter);
		}
		return null;
	}

	public UserFpCardDto getUserRfidPinDetails(String studentId) {
		UserFpCardEntity entity = userFpCardRepository.findByUserId(studentId);
		if (entity != null) {
			return UserFpCardMapper.INSTANCE.toDto(entity);
		}
		return new UserFpCardDto();
	}

	@Transactional
	public String saveAndUpdate(UserFpCardDto userFpCardDto) throws Exception{
		String isUpdated = null;
		UserFpCardEntity entity = userFpCardRepository.findByUserId(SecurityCtxUtil.userId().toUpperCase());
		if (entity != null) {
			if (Strings.isNotEmpty(userFpCardDto.getCurrentPin())) {
				UserFpCardMapper.INSTANCE.toEntity(entity, userFpCardDto);
			} else {
				entity.setCardActiveStatus(userFpCardDto.getCardActiveStatus());
			}
//			entity.onUpdate();
			UserFpCardEntity savedEntity = userFpCardRepository.save(entity);
			if (Objects.nonNull(savedEntity)) {
				isUpdated = "updated";
			}
		}
		return isUpdated;
	}

	public String getCurrentRfidPinNumber() {
		UserFpCardEntity entity = userFpCardRepository.findByUserId(SecurityCtxUtil.userId().toUpperCase());
		if (entity != null) {
			return entity.getPinNo();
		}
		return null;
	}
}
