package com.iitm.hosteldine.mapper.mess;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.mess.MessTerminalDto;
import com.iitm.hosteldine.model.mess.MessTerminalEntity;
import com.iitm.hosteldine.validator.common.ValidationCommon;

@Mapper (imports = ValidationCommon.class)
public interface MessTerminalMapper {
    MessTerminalMapper INSTANCE = Mappers.getMapper(MessTerminalMapper.class);

    @Mapping(target = ".", source = ".")
    MessTerminalDto fromMessTerminalEntity(MessTerminalEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    @Mapping(target = "terminalIp", expression = "java(ValidationCommon.trimString(modelDto.getTerminalIp()))")
    MessTerminalEntity toMessTerminalEntity(MessTerminalDto modelDto);
    
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "activeFlag", ignore = true)
	@Mapping(target = "terminalIp", expression = "java(ValidationCommon.trimString(dto.getTerminalIp()))")
	@Mapping(target = "messMaster", expression = "java(MessMasterMapper.INSTANCE.toMessMasterEntityAll(dto.getMessMaster()))")
	void onUpdateEntity(@MappingTarget MessTerminalEntity existingEntity, MessTerminalDto dto);
}