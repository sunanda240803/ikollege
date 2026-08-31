package com.iitm.hosteldine.mapper.OtherCandidate;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.OtherCandidate.StayExtensionRequestWorkflowDto;
import com.iitm.hosteldine.model.OtherCandidate.StayExtensionRequestWorkflowEntity;

@Mapper
public interface StayExtensionRequestWorkflowMapper {
    StayExtensionRequestWorkflowMapper INSTANCE = Mappers.getMapper(StayExtensionRequestWorkflowMapper.class);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "modifiedAt", source = "modifiedAt")
    StayExtensionRequestWorkflowDto fromEntity(StayExtensionRequestWorkflowEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "id", ignore = true)
    StayExtensionRequestWorkflowEntity onSaveEntity(StayExtensionRequestWorkflowDto modelDto);

}
    