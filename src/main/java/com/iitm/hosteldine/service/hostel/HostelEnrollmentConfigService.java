package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.HostelEnrollmentConfigurationDto;
import com.iitm.hosteldine.mapper.hostel.HostelEnrollmentConfigurationMapper;
import com.iitm.hosteldine.model.hostel.StudentHostelEnrollmentDateConfigurationEntity;
import com.iitm.hosteldine.repository.hostel.HostelEnrollmentConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class HostelEnrollmentConfigService {
	
    private final HostelEnrollmentConfigurationRepository hostelEnrollmentConfigurationRepository;

	public HostelEnrollmentConfigurationDto getEnrollmentDates() {
		StudentHostelEnrollmentDateConfigurationEntity entity = hostelEnrollmentConfigurationRepository
				.findTopByActiveFlagOrderByModifiedAtDesc(ModelConstants.STATUS_ACTIVE);
		if (entity != null) {
			return HostelEnrollmentConfigurationMapper.INSTANCE.toDto(entity);
		}
		return new HostelEnrollmentConfigurationDto();
	}

	@Transactional
	public String saveUpdateHostelConfig(HostelEnrollmentConfigurationDto dto) throws Exception {
		hostelEnrollmentConfigurationRepository.deactivateHostelEnrollmentDates(ModelConstants.STATUS_INACTIVE);

		StudentHostelEnrollmentDateConfigurationEntity entity = HostelEnrollmentConfigurationMapper.INSTANCE.toEntity(dto);
		entity.onCreate();
		hostelEnrollmentConfigurationRepository.save(entity);
		return Constants.SAVED;
	}

	public HostelEnrollmentConfigurationDto getByActiveFlag(){
		return hostelEnrollmentConfigurationRepository.findByActiveFlag(ModelConstants.STATUS_ACTIVE)
				.map(HostelEnrollmentConfigurationMapper.INSTANCE::toDto)
				.orElse(null);
	}

}
