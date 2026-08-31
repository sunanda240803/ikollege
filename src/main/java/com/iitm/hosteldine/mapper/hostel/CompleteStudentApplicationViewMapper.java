package com.iitm.hosteldine.mapper.hostel;

import com.iitm.hosteldine.model.hostel.CompleteStudentApplicationView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.hostel.CompleteStudentApplicationViewDto;

@Mapper
public interface CompleteStudentApplicationViewMapper {
    CompleteStudentApplicationViewMapper INSTANCE = Mappers.getMapper(CompleteStudentApplicationViewMapper.class);

    @Mapping(target = ".", source = ".")
    CompleteStudentApplicationViewDto fromCompleteStudentApplicationViewEntity(CompleteStudentApplicationView model);

    @Mapping(target = ".", source = ".")
    CompleteStudentApplicationView toCompleteStudentApplicationViewEntity(CompleteStudentApplicationViewDto modelDto);
}