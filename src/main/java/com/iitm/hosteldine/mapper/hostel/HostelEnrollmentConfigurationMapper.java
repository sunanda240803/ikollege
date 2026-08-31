package com.iitm.hosteldine.mapper.hostel;

import com.iitm.hosteldine.dto.hostel.HostelEnrollmentConfigurationDto;
import com.iitm.hosteldine.model.hostel.StudentHostelEnrollmentDateConfigurationEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HostelEnrollmentConfigurationMapper {
    HostelEnrollmentConfigurationMapper INSTANCE = Mappers.getMapper(HostelEnrollmentConfigurationMapper.class);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentHostelEnrollmentDateConfigurationEntity toEntity(HostelEnrollmentConfigurationDto hostelEnrollmentConfigurationDto);

    @Mapping(target = ".", source = ".")
    HostelEnrollmentConfigurationDto toDto(StudentHostelEnrollmentDateConfigurationEntity hostelEnrollmentConfigurationEntity);

    @Mapping(target = "id", source = "id")
	@Mapping(target = "fromDate", source = "fromDate")
	@Mapping(target = "toDate", source = "toDate")	
	@Mapping(target = "startDate", source = "startDate")			
    @BeanMapping(ignoreByDefault = true)
    void onUpdateEntity(@MappingTarget StudentHostelEnrollmentDateConfigurationEntity existingEntity, HostelEnrollmentConfigurationDto dto);
}

