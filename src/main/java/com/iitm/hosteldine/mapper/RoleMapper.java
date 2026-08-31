package com.iitm.hosteldine.mapper;

import com.iitm.hosteldine.dto.RoleDto;
import com.iitm.hosteldine.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDto toRoleDTO(RoleEntity role);

    RoleEntity toRoleEntity(RoleDto roleDTO);
}
