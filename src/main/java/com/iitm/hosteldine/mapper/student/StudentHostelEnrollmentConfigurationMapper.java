package com.iitm.hosteldine.mapper.student;

import com.iitm.hosteldine.dto.student.StudentHostelEnrollmentConfigurationDto;
import com.iitm.hosteldine.model.student.StudentHostelEnrollmentConfigurationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentHostelEnrollmentConfigurationMapper {
    StudentHostelEnrollmentConfigurationMapper INSTANCE = Mappers.getMapper(StudentHostelEnrollmentConfigurationMapper.class);

    @Mapping(target = ".", source = ".")
    StudentHostelEnrollmentConfigurationDto toDto(StudentHostelEnrollmentConfigurationEntity entity);

    @Mapping(target = ".", source = ".")
    StudentHostelEnrollmentConfigurationEntity toEntity(StudentHostelEnrollmentConfigurationDto dto);
}