package com.iitm.hosteldine.mapper.studentDashboard;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.studentDashboard.StudentMessPriorityRegistrationDto;
import com.iitm.hosteldine.model.mess.StudentMessPriorityRegistrationEntity;

@Mapper
public interface StudentMessPriorityRegistrationMapper {
    StudentMessPriorityRegistrationMapper INSTANCE = Mappers.getMapper(StudentMessPriorityRegistrationMapper.class);

    @Mapping(target = ".", source = ".")
    StudentMessPriorityRegistrationDto fromStudentMessPriorityRegistrationEntity(StudentMessPriorityRegistrationEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentMessPriorityRegistrationEntity toStudentMessPriorityRegistrationEntity(StudentMessPriorityRegistrationDto modelDto);
}