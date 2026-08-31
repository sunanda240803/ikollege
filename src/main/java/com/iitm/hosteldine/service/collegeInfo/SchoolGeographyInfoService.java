package com.iitm.hosteldine.service.collegeInfo;

import java.util.Optional;

import com.iitm.hosteldine.mapper.hostel.ShowEventMasterMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.collegeInfo.SchoolGeographyInfoDto;
import com.iitm.hosteldine.mapper.collegeInfo.SchoolGeographyInfoMapper;
import com.iitm.hosteldine.model.collegeInfo.SchoolGeographyInfoEntity;
import com.iitm.hosteldine.repository.collegeInfo.SchoolGeographyInfoRepository;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchoolGeographyInfoService {
	
	private final SchoolGeographyInfoRepository sgiRepository;
	private final SchoolGeographyInfoRepository schoolGeographyInfoRepository;

	public SchoolGeographyInfoDto getSchoolDetails() {
	        SchoolGeographyInfoDto sgiDto = null;
	        Optional<SchoolGeographyInfoEntity> optionalSGI = sgiRepository
	                .findByActiveFlag(ModelConstants.STATUS_ACTIVE);
	        if (optionalSGI.isPresent()) {
	            sgiDto = SchoolGeographyInfoMapper.INSTANCE.fromSGI(optionalSGI.get());
	        }
	        return sgiDto;
	    }

	public String saveOrUpdateSchoolGeographyInfo(SchoolGeographyInfoDto schoolGeographyInfoDto) {
		return Optional.ofNullable(schoolGeographyInfoDto.getSchoolId())
				.filter(id -> id > 0)
				.flatMap(schoolGeographyInfoRepository::findBySchoolId)
				.map(existingEntity -> {
					SchoolGeographyInfoMapper.INSTANCE.onUpdateEntity(existingEntity, schoolGeographyInfoDto);
					schoolGeographyInfoRepository.save(existingEntity);
					return Constants.UPDATED;
				})
				.orElse(Constants.ERROR);
	}
}
