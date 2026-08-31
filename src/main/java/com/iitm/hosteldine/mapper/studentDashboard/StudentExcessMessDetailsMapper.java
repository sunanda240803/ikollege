package com.iitm.hosteldine.mapper.studentDashboard;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.studentDashboard.StudentExcessMessDetailsDto;
import com.iitm.hosteldine.model.mess.StudentExcessMessDetailsEntity;

@Mapper
public interface StudentExcessMessDetailsMapper {
    StudentExcessMessDetailsMapper INSTANCE = Mappers.getMapper(StudentExcessMessDetailsMapper.class);

    @Mapping(target = ".", source = ".")
    StudentExcessMessDetailsDto fromStudentExcessMessDetailsEntity(StudentExcessMessDetailsEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentExcessMessDetailsEntity toStudentExcessMessDetailsEntity(StudentExcessMessDetailsDto modelDto);
}