package com.iitm.hosteldine.mapper.hostel;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.hostel.StudentDebitAccheadConfigDto;
import com.iitm.hosteldine.model.hostel.StudentDebitAccheadConfigEntity;

@Mapper
public interface StudentDebitAccheadConfigMapper {
    StudentDebitAccheadConfigMapper INSTANCE = Mappers.getMapper(StudentDebitAccheadConfigMapper.class);

    @Mapping(target = ".", source = ".")
    StudentDebitAccheadConfigDto fromStudentDebitAccheadConfigEntity(StudentDebitAccheadConfigEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentDebitAccheadConfigEntity toStudentDebitAccheadConfigEntity(StudentDebitAccheadConfigDto modelDto);
}