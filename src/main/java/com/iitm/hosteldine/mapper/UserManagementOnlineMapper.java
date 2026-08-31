package com.iitm.hosteldine.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.UserManagementOnlineDto;
import com.iitm.hosteldine.entity.UserManagementOnlineEntity;

@Mapper
public interface UserManagementOnlineMapper {

	UserManagementOnlineMapper INSTANCE = Mappers.getMapper(UserManagementOnlineMapper.class);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "userEmailId", source = "userEmailId")
    @Mapping(target = "activeStatus", source = "activeStatus")
    @Mapping(target = "applicantName", expression = "java(model.getFirstName() + ' ' + model.getLastName())")
    UserManagementOnlineDto fromUserManagementOnlineEntity(UserManagementOnlineEntity model);

    @Mapping(target = ".", source = ".")
    UserManagementOnlineEntity toUserManagementOnlineEntity(UserManagementOnlineDto modelDto);

}
