package com.iitm.hosteldine.mapper.OtherCandidate;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.OtherCandidate.CandidateProfileDto;
import com.iitm.hosteldine.model.OtherCandidate.CandidateProfileEntity;


@Mapper
public interface CandidateProfileMapper {
    CandidateProfileMapper INSTANCE = Mappers.getMapper(CandidateProfileMapper.class);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "employeeId", expression = "java(model.getEmployeeId() == null || model.getEmployeeId().isEmpty() ? \"NA\" : model.getEmployeeId())")
    @Mapping(target = "designation", expression = "java(model.getDesignation() == null || model.getDesignation().isEmpty() ? \"NA\" : model.getDesignation())")
    @Mapping(target = "imageName", expression = "java(model.getId() + \"_profile\")")
    CandidateProfileDto toDto(CandidateProfileEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    CandidateProfileEntity toCandidateProfileEntity(CandidateProfileDto modelDto);

    @Mapping(target = ".", source = ".")
    CandidateProfileEntity onUpdateEntity(@MappingTarget CandidateProfileEntity existingEntity, CandidateProfileDto dto);
}