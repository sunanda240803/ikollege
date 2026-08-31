package com.iitm.hosteldine.mapper.OtherCandidate;

import com.iitm.hosteldine.dto.OtherCandidate.CandidateStayRequestDto;
import com.iitm.hosteldine.model.OtherCandidate.CandidateStayRequestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CandidateStayRequestMapper {
    CandidateStayRequestMapper INSTANCE = Mappers.getMapper(CandidateStayRequestMapper.class);

    @Mapping(target = ".", source = ".")
    CandidateStayRequestDto toDto(CandidateStayRequestEntity entity);

    @Mapping(target = ".", source = ".")
    CandidateStayRequestEntity toEntity(CandidateStayRequestDto dto);
}