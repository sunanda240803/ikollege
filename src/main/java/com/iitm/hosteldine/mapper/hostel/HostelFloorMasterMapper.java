package com.iitm.hosteldine.mapper.hostel;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.hostel.HostelFloorMasterDto;
import com.iitm.hosteldine.model.hostel.HostelFloorMasterEntity;
import com.iitm.hosteldine.validator.common.ValidationCommon;

@Mapper (imports = ValidationCommon.class)
public interface HostelFloorMasterMapper {
    HostelFloorMasterMapper INSTANCE = Mappers.getMapper(HostelFloorMasterMapper.class);

    @Mapping(target = ".", source = ".")
    HostelFloorMasterDto fromHostelFloorMasterEntity(HostelFloorMasterEntity model);

    @Mapping(target = ".", source = ".")
    HostelFloorMasterEntity toHostelFloorMasterEntity(HostelFloorMasterDto modelDto);
    
    @Mapping(target = ".", source = ".")
    @Mapping(target = "floorName", expression = "java(ValidationCommon.trimString(modelDto.getFloorName()))")
    HostelFloorMasterEntity onSaveEntity(HostelFloorMasterDto modelDto);
    
    @Mapping(target = "floorName", expression = "java(ValidationCommon.trimString(modelDto.getFloorName()))")
    @Mapping(target = "floorDesc", source = "floorDesc")
    @Mapping(target = "floorSize", source = "floorSize")
    @BeanMapping(ignoreByDefault = true)
    void onUpdateEntity(@MappingTarget HostelFloorMasterEntity existing, HostelFloorMasterDto modelDto);
    
    @Mapping(target = "id", source = "id")
    @BeanMapping(ignoreByDefault = true)
    HostelFloorMasterEntity haveOnlyId(Long id);
}