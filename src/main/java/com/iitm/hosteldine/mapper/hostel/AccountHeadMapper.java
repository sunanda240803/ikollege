package com.iitm.hosteldine.mapper.hostel;

import com.iitm.hosteldine.dto.hostel.AccountHeadDto;
import com.iitm.hosteldine.model.hostel.AccountHeadEntity;

import com.iitm.hosteldine.validator.common.ValidationCommon;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {ValidationCommon.class})
public interface AccountHeadMapper {
    AccountHeadMapper INSTANCE = Mappers.getMapper(AccountHeadMapper.class);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    AccountHeadEntity toEntity(AccountHeadDto dto);

    @Mapping(target = ".", source = ".")
    AccountHeadDto toDto(AccountHeadEntity entity);
    
    @Mapping(target = "accname", expression = "java(ValidationCommon.trimString(dto.getAccname()))")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "opdate", source = "opdate")
    @Mapping(target = "cldate", source = "cldate")
    @BeanMapping(ignoreByDefault = true)
	void onUpdateEntity(@MappingTarget AccountHeadEntity existingEntity, AccountHeadDto dto);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "accname", expression = "java(ValidationCommon.trimString(dto.getAccname()))")
	AccountHeadEntity onSaveEntity(AccountHeadDto dto);
}