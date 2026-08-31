package com.iitm.hosteldine.dto.OtherCandidate;

import com.iitm.hosteldine.model.OtherCandidate.CandidateFilesInformationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CandidateFilesInformationMapper {
    CandidateFilesInformationMapper INSTANCE = Mappers.getMapper(CandidateFilesInformationMapper.class);

    @Mapping(target = ".", source = ".")
    CandidateFilesInformationDto toDto(CandidateFilesInformationEntity entity);

    @Mapping(target = ".", source = ".")
    CandidateFilesInformationEntity toEntity(CandidateFilesInformationDto dto);
}