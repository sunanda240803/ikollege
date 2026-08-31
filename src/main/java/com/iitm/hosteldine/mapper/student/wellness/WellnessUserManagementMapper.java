package com.iitm.hosteldine.mapper.student.wellness;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.student.wellness.WellnessUserManagementDto;
import com.iitm.hosteldine.model.student.wellness.WellnessUserManagementEntity;

@Mapper
public interface WellnessUserManagementMapper {
    WellnessUserManagementMapper INSTANCE = Mappers.getMapper(WellnessUserManagementMapper.class);

    @Mapping(target = ".", source = ".")
    WellnessUserManagementDto fromWellnessUserManagementEntity(WellnessUserManagementEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    WellnessUserManagementEntity toWellnessUserManagementEntity(WellnessUserManagementDto modelDto);
}