package com.iitm.hosteldine.mapper.warden;

import com.iitm.hosteldine.dto.warden.WardenHostelMappingDto;
import com.iitm.hosteldine.model.warden.WardenHostelMappingEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface WardenHostelMappingMapper {

    WardenHostelMappingMapper INSTANCE = Mappers.getMapper(WardenHostelMappingMapper.class);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    WardenHostelMappingEntity toEntity(WardenHostelMappingDto wardenHostelMappingDto);

    @Mapping(target = ".", source = ".")
    WardenHostelMappingDto toDto(WardenHostelMappingEntity wardenHostelMappingEntity);
}