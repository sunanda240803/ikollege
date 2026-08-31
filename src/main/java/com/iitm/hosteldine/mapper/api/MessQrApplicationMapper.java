package com.iitm.hosteldine.mapper.api;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.generated.model.MessQrApplicationEntity;
import com.iitm.hosteldine.dto.api.MessQrApplicationDto;

@Mapper
public interface MessQrApplicationMapper {
    MessQrApplicationMapper INSTANCE = Mappers.getMapper(MessQrApplicationMapper.class);

    @Mapping(target = ".", source = ".")
    MessQrApplicationDto fromMessQrApplicationEntity(MessQrApplicationEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    MessQrApplicationEntity toMessQrApplicationEntity(MessQrApplicationDto modelDto);
}