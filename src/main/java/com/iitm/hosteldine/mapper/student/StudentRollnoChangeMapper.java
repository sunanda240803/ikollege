package com.iitm.hosteldine.mapper.student;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.student.StudentRollnoChangeDto;
import com.iitm.hosteldine.model.student.StudentRollnoChangeEntity;

@Mapper
public interface StudentRollnoChangeMapper {
    StudentRollnoChangeMapper INSTANCE = Mappers.getMapper(StudentRollnoChangeMapper.class);

    @Mapping(target = ".", source = ".")
    StudentRollnoChangeDto fromStudentRollnoChangeEntity(StudentRollnoChangeEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentRollnoChangeEntity toStudentRollnoChangeEntity(StudentRollnoChangeDto modelDto);
}