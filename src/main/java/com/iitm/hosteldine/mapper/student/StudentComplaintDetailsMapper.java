package com.iitm.hosteldine.mapper.student;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.student.StudentComplaintDetailsDto;
import com.iitm.hosteldine.model.student.StudentComplaintDetailsEntity;

@Mapper
public interface StudentComplaintDetailsMapper {
    StudentComplaintDetailsMapper INSTANCE = Mappers.getMapper(StudentComplaintDetailsMapper.class);

    @Mapping(target = ".", source = ".")
    StudentComplaintDetailsDto fromStudentComplaintDetailsEntity(StudentComplaintDetailsEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentComplaintDetailsEntity toStudentComplaintDetailsEntity(StudentComplaintDetailsDto modelDto);

	StudentComplaintDetailsEntity onSaveEntity(StudentComplaintDetailsDto dto);
}