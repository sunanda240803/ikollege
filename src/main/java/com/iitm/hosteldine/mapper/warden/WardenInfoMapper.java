package com.iitm.hosteldine.mapper.warden;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.warden.WardenInfoDto;
import com.iitm.hosteldine.model.warden.WardenInfoEntity;

@Mapper
public interface WardenInfoMapper {
    WardenInfoMapper INSTANCE = Mappers.getMapper(WardenInfoMapper.class);

    @Mapping(target = ".", source = ".")
    WardenInfoDto fromWardenInfoEntity(WardenInfoEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    WardenInfoEntity toWardenInfoEntity(WardenInfoDto modelDto);
}