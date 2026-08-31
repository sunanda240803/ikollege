package com.iitm.hosteldine.mapper.hostel;

import com.iitm.hosteldine.dto.dean.MessInspectionReportDto;
import com.iitm.hosteldine.model.mess.MessInspectionReportEntity;
import com.iitm.hosteldine.validator.common.ValidationCommon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {ValidationCommon.class})
public interface MessInspectionReportMapper {
    MessInspectionReportMapper INSTANCE = Mappers.getMapper(MessInspectionReportMapper.class);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "messName", expression = "java(entity.getMessMaster().getMessName())") // This will need to be set separately
    @Mapping(target = "ldapUsername", ignore = true) // This will need to be set separately
    @Mapping(target = "actionList", ignore = true) // This will need to be set separately
    MessInspectionReportDto toDto(MessInspectionReportEntity entity);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "wardenId", ignore = true)
    @Mapping(target = "wardenName", ignore = true)
    @Mapping(target = "messMaster", ignore = true)
    MessInspectionReportEntity toEntity(MessInspectionReportDto dto);
}