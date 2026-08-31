package com.iitm.hosteldine.mapper;

import com.iitm.hosteldine.dto.SimsConfigDataDto;
import com.iitm.hosteldine.model.SimsConfigDataEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SimsConfigDataMapper {
    SimsConfigDataMapper INSTANCE = Mappers.getMapper(SimsConfigDataMapper.class);

    @Mapping(target = ".", source = ".")
    SimsConfigDataDto fromSimsConfigDataEntity(SimsConfigDataEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    SimsConfigDataEntity toSimsConfigDataEntity(SimsConfigDataDto modelDto);
    
    @Mapping(target = ".", source = ".")
	void onUpdateEntity(@MappingTarget SimsConfigDataEntity existingEntity, SimsConfigDataDto dto);

	SimsConfigDataEntity onSaveEntity(SimsConfigDataDto dto);
}