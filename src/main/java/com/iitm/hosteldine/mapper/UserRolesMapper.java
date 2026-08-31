package com.iitm.hosteldine.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.UserRolesDto;
import com.iitm.hosteldine.entity.UserRolesEntity;

@Mapper
public interface UserRolesMapper {
    UserRolesMapper INSTANCE = Mappers.getMapper(UserRolesMapper.class);

    @Mapping(target = ".", source = ".")
    UserRolesDto fromUserRolesEntity(UserRolesEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    UserRolesEntity toUserRolesEntity(UserRolesDto modelDto);
}