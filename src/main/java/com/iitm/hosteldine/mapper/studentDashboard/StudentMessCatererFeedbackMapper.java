package com.iitm.hosteldine.mapper.studentDashboard;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.studentDashboard.StudentMessCatererFeedbackDto;
import com.iitm.hosteldine.model.mess.StudentMessCatererFeedbackEntity;

@Mapper
public interface StudentMessCatererFeedbackMapper {
    StudentMessCatererFeedbackMapper INSTANCE = Mappers.getMapper(StudentMessCatererFeedbackMapper.class);

    @Mapping(target = ".", source = ".")
    StudentMessCatererFeedbackDto fromStudentMessCatererFeedbackEntity(StudentMessCatererFeedbackEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentMessCatererFeedbackEntity toStudentMessCatererFeedbackEntity(StudentMessCatererFeedbackDto modelDto);
}