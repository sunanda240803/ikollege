package com.iitm.hosteldine.mapper.mess;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.mess.MessSessionsMasterDto;
import com.iitm.hosteldine.model.mess.MessSessionsMasterEntity;

@Mapper
public interface MessSessionsMasterMapper {
    MessSessionsMasterMapper INSTANCE = Mappers.getMapper(MessSessionsMasterMapper.class);

    @Mapping(target = ".", source = ".")
    MessSessionsMasterDto fromMessSessionsMasterEntity(MessSessionsMasterEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    MessSessionsMasterEntity toMessSessionsMasterEntity(MessSessionsMasterDto modelDto);
}