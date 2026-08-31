package com.iitm.hosteldine.mapper.dashboard.student;

import com.iitm.hosteldine.dto.dashboard.student.SeasonMasterDto;
import com.iitm.hosteldine.model.dashboard.student.SeasonMasterEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SeasonMasterMapper {

    SeasonMasterMapper INSTANCE = Mappers.getMapper(SeasonMasterMapper.class);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    SeasonMasterEntity toEntity(SeasonMasterDto seasonMasterDto);

    @Mapping(target = ".", source = ".")
    SeasonMasterDto toDto(SeasonMasterEntity seasonMasterEntity);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    SeasonMasterEntity onUpdate(SeasonMasterDto seasonMasterDto, @MappingTarget SeasonMasterEntity seasonMasterEntity);
}