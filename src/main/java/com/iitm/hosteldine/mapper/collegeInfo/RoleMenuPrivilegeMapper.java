package com.iitm.hosteldine.mapper.collegeInfo;

import com.iitm.hosteldine.dto.MenuListDto;
import com.iitm.hosteldine.dto.collegeInfo.RolePrivilegeDto;
import com.iitm.hosteldine.mapper.MenuListMapper;
import com.iitm.hosteldine.mapper.RoleMapper;
import com.iitm.hosteldine.model.collegeInfo.RoleMenuPrivilegeEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {SchoolGeographyInfoMapper.class, RoleMapper.class, MenuListMapper.class})
public interface RoleMenuPrivilegeMapper {
    RoleMenuPrivilegeMapper INSTANCE = Mappers.getMapper(RoleMenuPrivilegeMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "role", expression = "java(RoleMapper.INSTANCE.toRoleDTO(model.getRole()))")
    @Mapping(target = "menu", expression = "java(MenuListMapper.INSTANCE.fromMenuListEntityOnlyName(model.getMenu()))")
    @Mapping(target = "activeFlag", source = "activeFlag")
    @BeanMapping(ignoreByDefault = true)
    RolePrivilegeDto fromRoleMenuPrivilegeEntity(RoleMenuPrivilegeEntity model);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "menu", expression = "java(MenuListMapper.INSTANCE.fromMenuListEntityOnlyName(model.getMenu()))")
    @Mapping(target = "activeFlag", source = "activeFlag")
    @BeanMapping(ignoreByDefault = true)
    RolePrivilegeDto fromRoleMenuPrivilegeEntityWithoutRole(RoleMenuPrivilegeEntity model);

    @Mapping(target = "role", expression = "java(RoleMapper.INSTANCE.toRoleEntity(modelDto.getRole()))")
    @Mapping(target = ".", source = ".")
    RoleMenuPrivilegeEntity toRoleMenuPrivilegeEntity(RolePrivilegeDto modelDto);

    @Mapping(target = "submenu", source = "submenu", ignore = true)
    @Mapping(target = ".", source = ".")
    RolePrivilegeDto excludeSubmenu(RolePrivilegeDto model);

}