package com.iitm.hosteldine.mapper.dean;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.dean.DashboardTabMasterDto;
import com.iitm.hosteldine.model.dean.DashboardTabMasterEntity;

@Mapper
public interface DashboardTabMasterMapper {
    DashboardTabMasterMapper INSTANCE = Mappers.getMapper(DashboardTabMasterMapper.class);

    @Mapping(target = ".", source = ".")
    DashboardTabMasterDto fromDashboardTabMasterEntity(DashboardTabMasterEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    DashboardTabMasterEntity toDashboardTabMasterEntity(DashboardTabMasterDto modelDto);
}