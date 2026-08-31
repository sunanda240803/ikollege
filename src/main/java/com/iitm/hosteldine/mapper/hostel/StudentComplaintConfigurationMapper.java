package com.iitm.hosteldine.mapper.hostel;

import com.iitm.hosteldine.dto.hostel.ShowEventMasterDto;
import com.iitm.hosteldine.dto.hostel.StudentComplaintConfigurationDto;
import com.iitm.hosteldine.model.hostel.ShowEventMasterEntity;
import com.iitm.hosteldine.model.student.StudentComplaintConfigurationEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentComplaintConfigurationMapper {

    StudentComplaintConfigurationMapper INSTANCE = Mappers.getMapper(StudentComplaintConfigurationMapper.class);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentComplaintConfigurationEntity toEntity(StudentComplaintConfigurationDto studentComplaintConfigurationDto);

    @Mapping(target = ".", source = ".")
    StudentComplaintConfigurationDto toDto(StudentComplaintConfigurationEntity studentComplaintConfigurationEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    StudentComplaintConfigurationEntity partialUpdate(StudentComplaintConfigurationDto studentComplaintConfigurationDto, @MappingTarget StudentComplaintConfigurationEntity studentComplaintConfigurationEntity);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    @Mapping(target = "id", ignore = true)
    void onUpdateEntity(@MappingTarget StudentComplaintConfigurationEntity existingEntity, StudentComplaintConfigurationDto dto);
}