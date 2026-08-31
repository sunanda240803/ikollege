package com.iitm.hosteldine.mapper.OtherCandidate;

import com.iitm.hosteldine.dto.OtherCandidate.CandidateWorkflowDto;
import com.iitm.hosteldine.model.OtherCandidate.CandidateWorkflowEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CandidateWorkflowMapper {
    CandidateWorkflowMapper INSTANCE = Mappers.getMapper(CandidateWorkflowMapper.class);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "modifiedAt", source = "modifiedAt")
    CandidateWorkflowDto toDto(CandidateWorkflowEntity entity);

    @Mapping(target = ".", source = ".")
    CandidateWorkflowEntity toEntity(CandidateWorkflowDto dto);
}