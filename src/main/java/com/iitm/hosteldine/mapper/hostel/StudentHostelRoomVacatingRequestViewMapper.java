package com.iitm.hosteldine.mapper.hostel;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.hostel.StudentHostelRoomVacatingRequestViewDto;
import com.iitm.hosteldine.model.hostel.StudentHostelRoomVacatingRequestViewEntity;

@Mapper
public interface StudentHostelRoomVacatingRequestViewMapper {
    StudentHostelRoomVacatingRequestViewMapper INSTANCE = Mappers.getMapper(StudentHostelRoomVacatingRequestViewMapper.class);

    @Mapping(target = ".", source = ".")
    StudentHostelRoomVacatingRequestViewDto fromStudentHostelRoomVacatingRequestViewEntity(StudentHostelRoomVacatingRequestViewEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentHostelRoomVacatingRequestViewEntity toStudentHostelRoomVacatingRequestViewEntity(StudentHostelRoomVacatingRequestViewDto modelDto);
}