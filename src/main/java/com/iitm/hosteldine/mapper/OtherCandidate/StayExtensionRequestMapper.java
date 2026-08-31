package com.iitm.hosteldine.mapper.OtherCandidate;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.OtherCandidate.StayExtensionRequestDto;
import com.iitm.hosteldine.model.OtherCandidate.StayExtensionRequestEntity;

@Mapper
public interface StayExtensionRequestMapper {
    StayExtensionRequestMapper INSTANCE = Mappers.getMapper(StayExtensionRequestMapper.class);

    @Mapping(target = ".", source = ".")
    StayExtensionRequestDto fromStayExtensionRequestEntity(StayExtensionRequestEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "stayId", ignore = true)
    StayExtensionRequestEntity onSaveEntity(StayExtensionRequestDto modelDto);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "stayFrom", source = "dto.stayFrom")
    @Mapping(target = "stayTo", source = "dto.stayTo")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "hostelId", source = "dto.hostelId")
    @Mapping(target = "roomNo", source = "dto.roomNo")
    @Mapping(target = "messOption", source = "dto.messOption")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StayExtensionRequestEntity onUpdateEntity(@MappingTarget StayExtensionRequestEntity model, StayExtensionRequestDto dto);
}
    