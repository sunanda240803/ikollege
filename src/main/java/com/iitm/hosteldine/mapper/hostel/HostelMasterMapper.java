package com.iitm.hosteldine.mapper.hostel;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.model.hostel.HostelMasterEntity;
import com.iitm.hosteldine.validator.common.ValidationCommon;

@Mapper (imports = ValidationCommon.class)
public interface HostelMasterMapper {
    HostelMasterMapper INSTANCE = Mappers.getMapper(HostelMasterMapper.class);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "vegAmount", source = "vegAmount")
    @Mapping(target = "nonVegAmount", source = "nonVegAmount")
    HostelMasterDto fromHostelMasterEntity(HostelMasterEntity model);

    @Mapping(target = ".", source = ".")
    HostelMasterEntity toHostelMasterEntity(HostelMasterDto modelDto);
    
    @Mapping(target = ".", source = ".")
    @Mapping(target = "hostelName", expression = "java(ValidationCommon.trimString(modelDto.getHostelName()))")
    HostelMasterEntity onSaveEntity(HostelMasterDto modelDto);
    
    @Mapping(target = "hostelName", expression = "java(ValidationCommon.trimString(modelDto.getHostelName()))")
    @Mapping(target = "hostelGenderType", source = "hostelGenderType")
    @Mapping(target = "hostelOfficeEmail", source = "hostelOfficeEmail")
    @Mapping(target = "hostelShortCode", source = "hostelShortCode")
    @Mapping(target = "extensionFlag", source = "extensionFlag")
    @Mapping(target = "vegAmount", source = "vegAmount")
    @Mapping(target = "nonVegAmount", source = "nonVegAmount")
    @BeanMapping(ignoreByDefault = true)
    void onUpdateEntity(@MappingTarget HostelMasterEntity existing, HostelMasterDto modelDto);
    
    @Mapping(target = "id", source = "id")
    @BeanMapping(ignoreByDefault = true)
    HostelMasterEntity haveOnlyId(Long id);
}