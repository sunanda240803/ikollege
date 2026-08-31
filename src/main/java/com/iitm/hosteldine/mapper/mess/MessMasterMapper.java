package com.iitm.hosteldine.mapper.mess;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.model.mess.MessMasterEntity;
import com.iitm.hosteldine.validator.common.ValidationCommon;

@Mapper (imports = ValidationCommon.class)
public interface MessMasterMapper {
    MessMasterMapper INSTANCE = Mappers.getMapper(MessMasterMapper.class);

    @Mapping(target = ".", source = ".")
    MessMasterDto fromMessMasterEntity(MessMasterEntity model);
    
    @Mapping(target = ".", source = ".")
    MessMasterEntity toMessMasterEntityAll(MessMasterDto dto);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    @Mapping(target = "messName", expression = "java(ValidationCommon.trimString(modelDto.getMessName()))")
    MessMasterEntity toMessMasterEntity(MessMasterDto modelDto);

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "activeFlag", ignore = true)
	@Mapping(target = "messName", expression = "java(ValidationCommon.trimString(dto.getMessName()))")
	void onUpdateEntity(@MappingTarget MessMasterEntity existingEntity, MessMasterDto dto);
	
	 @Mapping(target = "id", source = "id")
	 @Mapping(target = "messName", source = "messName")
	 @Mapping(target = "description", source = "description")
	 @BeanMapping(ignoreByDefault = true)
	 MessMasterDto fromMessEntityForOnlineMessCoupon(MessMasterEntity model);
	
}