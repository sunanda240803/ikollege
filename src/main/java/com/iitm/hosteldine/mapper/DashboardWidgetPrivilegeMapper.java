package com.iitm.hosteldine.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.DashboardWidgetPrivilegeDto;
import com.iitm.hosteldine.model.DashboardWidgetPrivilegeEntity;

@Mapper
public interface DashboardWidgetPrivilegeMapper {
    DashboardWidgetPrivilegeMapper INSTANCE = Mappers.getMapper(DashboardWidgetPrivilegeMapper.class);

    @Mapping(target = ".", source = ".")
    DashboardWidgetPrivilegeDto fromDashboardWidgetPrivilegeEntity(DashboardWidgetPrivilegeEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    DashboardWidgetPrivilegeEntity toDashboardWidgetPrivilegeEntity(DashboardWidgetPrivilegeDto modelDto);
}