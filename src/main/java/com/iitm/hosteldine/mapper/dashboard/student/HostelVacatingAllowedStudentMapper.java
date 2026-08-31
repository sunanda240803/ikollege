package com.iitm.hosteldine.mapper.dashboard.student;

import com.iitm.hosteldine.dto.dashboard.student.HostelVacatingAllowedStudentDto;
import com.iitm.hosteldine.model.dashboard.student.HostelVacatingAllowedStudentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HostelVacatingAllowedStudentMapper {

    HostelVacatingAllowedStudentMapper INSTANCE = Mappers.getMapper(HostelVacatingAllowedStudentMapper.class);


    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    HostelVacatingAllowedStudentEntity toEntity(HostelVacatingAllowedStudentDto hostelVacatingAllowedStudentDto);

    @Mapping(target = ".", source = ".")  // This line is not required
    HostelVacatingAllowedStudentDto toDto(HostelVacatingAllowedStudentEntity hostelVacatingAllowedStudentEntity);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    HostelVacatingAllowedStudentEntity onUpdate(HostelVacatingAllowedStudentDto hostelVacatingAllowedStudentDto, @MappingTarget HostelVacatingAllowedStudentEntity hostelVacatingAllowedStudentEntity);
}
