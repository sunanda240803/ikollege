package com.iitm.hosteldine.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.MenuListDto;
import com.iitm.hosteldine.model.MenuListEntity;

@Mapper
public interface MenuListMapper {
    MenuListMapper INSTANCE = Mappers.getMapper(MenuListMapper.class);

    @Mapping(target = ".", source = ".")
    MenuListDto fromMenuListEntity(MenuListEntity model);

    @Mapping(target = ".", source = ".")
    MenuListEntity toMenuListEntity(MenuListDto modelDto);

    @Mapping(target = "menuId", source = "menuId")
    @Mapping(target = "menuHeading", source = "menuHeading")
    @Mapping(target = "imageName", source = "imageName")
    @Mapping(target = "iconName", source = "iconName")
    @Mapping(target = "bannerName", source = "bannerName")
    @Mapping(target = "menuOrder", source = "menuOrder")
    @Mapping(target = "mainMenuOrder", source = "mainMenuOrder")
    @Mapping(target = "subMenuOrder", source = "subMenuOrder")
    @Mapping(target = "subSubMenuOrder", source = "subSubMenuOrder")
    @BeanMapping(ignoreByDefault = true)
    MenuListDto fromMenuListEntityOnlyName(MenuListEntity model);

    @Mapping(target = "subMenu", source = "subMenu", ignore = true)
    @Mapping(target = ".", source = ".")
    MenuListDto excludeSubmenu(MenuListDto model);
}