package com.iitm.hosteldine.mapper.OtherCandidate;

import com.iitm.hosteldine.dto.OtherCandidate.CandidateAppointmentRequestDto;
import com.iitm.hosteldine.model.OtherCandidate.CandidateAppointmentRequestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CandidateAppointmentRequestMapper {
    CandidateAppointmentRequestMapper INSTANCE = Mappers.getMapper(CandidateAppointmentRequestMapper.class);

    @Mapping(target = ".", source = ".")
    CandidateAppointmentRequestDto fromCandidateAppointmentRequestEntity(CandidateAppointmentRequestEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    CandidateAppointmentRequestEntity toCandidateAppointmentRequestEntity(CandidateAppointmentRequestDto modelDto);


    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidateId", ignore = true)
    @Mapping(target = "resendDate", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "categoryOthers", ignore = true)
    @Mapping(target = "dining", ignore = true)
    @Mapping(target = "occupancy", ignore = true)
    @Mapping(target = "hostelManagement", ignore = true)
    @Mapping(target = "messOption", ignore = true)
    void updateEntity(@MappingTarget CandidateAppointmentRequestEntity existingEntity, CandidateAppointmentRequestDto dto);
}