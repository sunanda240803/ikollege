package com.iitm.hosteldine.mapper.mess;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.mess.MessRegistrationMappingDto;
import com.iitm.hosteldine.model.mess.MessRegistrationMappingEntity;

@Mapper
public interface MessRegistrationMappingMapper {
    MessRegistrationMappingMapper INSTANCE = Mappers.getMapper(MessRegistrationMappingMapper.class);

    @Mapping(target = ".", source = ".")
    MessRegistrationMappingDto fromMessRegistrationMappingEntity(MessRegistrationMappingEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    MessRegistrationMappingEntity toMessRegistrationMappingEntity(MessRegistrationMappingDto modelDto);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    void onUpdateEntity(@MappingTarget MessRegistrationMappingEntity existingEntity, MessRegistrationMappingDto dto);
}