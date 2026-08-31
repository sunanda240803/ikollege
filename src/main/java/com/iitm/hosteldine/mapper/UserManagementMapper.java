package com.iitm.hosteldine.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.UserManagementDto;
import com.iitm.hosteldine.entity.UserManagementEntity;
import com.iitm.hosteldine.model.hostel.HostelMasterEntity;

@Mapper
public interface UserManagementMapper {
	UserManagementMapper INSTANCE = Mappers.getMapper(UserManagementMapper.class);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "roleId", source = "role.roleId")
    @Mapping(target = "secondaryRoleId", source = "roleSecondary.roleId")
    UserManagementDto fromUserManagementEntity(UserManagementEntity model);

    @Mapping(target = ".", source = ".")
    UserManagementEntity toUserManagementEntity(UserManagementDto modelDto);
    
    @Mapping(target = "id.username", source = "id")
    @BeanMapping(ignoreByDefault = true)
    UserManagementEntity haveOnlyUserName(String id);

}
