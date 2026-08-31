package com.iitm.hosteldine.mapper.studentDashboard;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.studentDashboard.StudentMessLoginIssuePriorityDto;
import com.iitm.hosteldine.model.mess.StudentMessLoginIssuePriorityEntity;

@Mapper
public interface StudentMessLoginIssuePriorityMapper {
    StudentMessLoginIssuePriorityMapper INSTANCE = Mappers.getMapper(StudentMessLoginIssuePriorityMapper.class);

    @Mapping(target = ".", source = ".")
    StudentMessLoginIssuePriorityDto fromStudentMessLoginIssuePriorityEntity(StudentMessLoginIssuePriorityEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentMessLoginIssuePriorityEntity toStudentMessLoginIssuePriorityEntity(StudentMessLoginIssuePriorityDto modelDto);
}