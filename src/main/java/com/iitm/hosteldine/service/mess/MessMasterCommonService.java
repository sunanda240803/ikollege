package com.iitm.hosteldine.service.mess;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.mapper.mess.MessMasterControllerMapper;
import com.iitm.hosteldine.model.mess.MessMasterControllerEntity;
import com.iitm.hosteldine.model.mess.StudentMessCatererFeedbackEntity;
import com.iitm.hosteldine.repository.mess.MessMasterControllerRepository;
import com.iitm.hosteldine.service.studentDashboard.StudentMessCatererFeedbackService;
import com.iitm.hosteldine.service.studentDashboard.StudentMessDetailsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessMasterCommonService {

	private final MessMasterControllerRepository messMasterCommonRepository;
	private final StudentMessCatererFeedbackService studentMessCatererFeedbackService;
	private final StudentMessDetailsService studentMessDetailsService;

	public List<MessMasterControllerDto> getMessMasterControllerList() {
		return Optional.ofNullable(messMasterCommonRepository.findAllByCurrentActiveFlag(ModelConstants.STATUS_ACTIVE))
				.orElse(Collections.emptyList()).stream()
				.map(MessMasterControllerMapper.INSTANCE::fromMessMasterControllerEntity).collect(Collectors.toList());
	}

	public MessMasterControllerDto getStudentMessFeedbackStatus(Long previousId, String studentId) {
		return messMasterCommonRepository.getStudentMessFeedBackStaus(previousId,studentId)
				.map(MessMasterControllerMapper.INSTANCE::fromMessMasterControllerEntity).orElse(new MessMasterControllerDto());
	}
	
	public Optional<MessMasterControllerDto> getMessPeriodDetails() {
		return messMasterCommonRepository.getMessPeriodDetails().flatMap(row -> {
			if (row.length == 0 || !(row[0] instanceof Object[] nestedArray)) {
				return Optional.empty();
			}

			try {
				var dto = new MessMasterControllerDto();

				// Extract and parse the nested array values using inline casting/conversion
				Long id = nestedArray[0] instanceof Number number ? number.longValue()
						: nestedArray[0] instanceof String str ? Long.parseLong(str) : null;

				Long previousId = nestedArray[1] instanceof Number number ? number.longValue() : null;

				String month = nestedArray[2] instanceof String ? (String) nestedArray[2] : null;
				String previousMonth = nestedArray[3] instanceof String ? (String) nestedArray[3] : null;

				Boolean feedbackStatus = nestedArray[4] instanceof Boolean bool ? bool
						: nestedArray[4] instanceof String str ? Boolean.parseBoolean(str)
								: nestedArray[4] instanceof Number number ? number.intValue() != 0 : false;

				Boolean prevFbStatus = nestedArray[5] instanceof Boolean bool ? bool
						: nestedArray[5] instanceof String str ? Boolean.parseBoolean(str)
								: nestedArray[5] instanceof Number number ? number.intValue() != 0 : false;

				// Populate DTO fields
				dto.setId(id);
				dto.setPreviousId(previousId);
				dto.setMonth(month);
				dto.setPreviousMonth(previousMonth);
				dto.setFeedbackStatus(feedbackStatus);
				dto.setPrevFbStatus(prevFbStatus);

				return Optional.of(dto);
			} catch (Exception e) {
				return Optional.empty();
			}
		});
	}

	
	@SuppressWarnings("unused")
	public MessMasterControllerDto getStudentDetails(String studentId, Long previousId) {
		boolean linkStatus = true;
		Long messId = null;
		List<StudentMessCatererFeedbackEntity> messCatererFeedbackEntityList;
		MessMasterControllerDto messRegistrationForm = new MessMasterControllerDto();
		List<Object[]> dates = messMasterCommonRepository.checkRegistrationDate(ModelConstants.STATUS_ACTIVE);
		if (dates != null) {
			List<Object[]> studentDetails = studentMessDetailsService.fetchStudentDetails(studentId, previousId,
					ModelConstants.STATUS_ACTIVE);
			if (!studentDetails.isEmpty()) {
				for (Object[] detail : studentDetails) {
					messId = (Long) detail[1];
					messRegistrationForm.setMessId((Long) detail[1]);
					messRegistrationForm.setMessName((String) detail[2]);
				}
			}
			if (linkStatus) {
				if (messId != null) {
					// Check feedback already exist
					messCatererFeedbackEntityList = studentMessCatererFeedbackService.checkFeedbackAlreadyExistByMessId(studentId, messId, previousId);
				} else {
					// Check feedback already exist
					messCatererFeedbackEntityList = studentMessCatererFeedbackService.checkFeedbackAlreadyExistByPreviousId(studentId, previousId);
				}

				if (!messCatererFeedbackEntityList.isEmpty()) {
					linkStatus = true;
				} else {
					linkStatus = false;
				}

			}
			messRegistrationForm.setFeedbackStatus(linkStatus);

		}
		return messRegistrationForm;

	}

	@SuppressWarnings("null")
	public MessMasterControllerDto getStudentMessDetails(Long previousId, String studentId) {
		List<Object[]> studentMessDetails = messMasterCommonRepository.getStudentMessDetails(studentId, previousId,
				ModelConstants.STATUS_ACTIVE);
		MessMasterControllerDto messMasterControllerDto = null;
		if (!studentMessDetails.isEmpty()) {
			for (Object[] record : studentMessDetails) {
				// Extract the entity from Object[0]
				MessMasterControllerEntity messEntity = (MessMasterControllerEntity) record[0];

				// Use the mapper to convert the entity to the DTO
				messMasterControllerDto = MessMasterControllerMapper.INSTANCE
						.fromMessMasterControllerEntity(messEntity);

				// Extract additional fields from Object[1] and Object[2] if needed
				Long messId = (Long) record[1];
				String messName = (String) record[2];

				// Set these fields in the DTO
				messMasterControllerDto.setMessId(messId);
				messMasterControllerDto.setMessName(messName);
			}
		}

		return messMasterControllerDto;
	}

	public List<Object[]> getRegistrationDate() {
		List<Object[]> dates = messMasterCommonRepository.checkRegistrationDate(ModelConstants.STATUS_ACTIVE);
		return dates;
	}

	public MessMasterControllerDto getActiveMessMaster(){
		return messMasterCommonRepository.findByCurrentActiveFlag(ModelConstants.STATUS_ACTIVE)
				.map(MessMasterControllerMapper.INSTANCE::fromMessMasterControllerEntity)
				.orElse(null);
	}

	public MessMasterControllerDto getCurrentMessPeriod(){
		return messMasterCommonRepository.getCurrentMessPeriod()
				.map(MessMasterControllerMapper.INSTANCE::fromMessMasterControllerEntity)
				.orElse(null);
	}

	public MessMasterControllerDto getNextMessPeriod() {
		return messMasterCommonRepository.getNextMessPeriod()
				.map(MessMasterControllerMapper.INSTANCE::fromMessMasterControllerEntity)
				.orElse(null);
	}

	
}
