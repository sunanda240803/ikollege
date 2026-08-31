package com.iitm.hosteldine.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.DashboardWidgetMasterDto;
import com.iitm.hosteldine.model.DashboardWidgetMasterEntity;

@Mapper
public interface DashboardWidgetMasterMapper {
    DashboardWidgetMasterMapper INSTANCE = Mappers.getMapper(DashboardWidgetMasterMapper.class);

    @Mapping(target = ".", source = ".")
    DashboardWidgetMasterDto fromDashboardWidgetMasterEntity(DashboardWidgetMasterEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    DashboardWidgetMasterEntity toDashboardWidgetMasterEntity(DashboardWidgetMasterDto modelDto);
}