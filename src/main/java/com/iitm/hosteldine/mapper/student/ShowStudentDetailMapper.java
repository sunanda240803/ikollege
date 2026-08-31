package com.iitm.hosteldine.mapper.student;

import com.iitm.hosteldine.dto.student.ShowStudentDetailDto;
import com.iitm.hosteldine.mapper.hostel.ShowEventMasterMapper;
import com.iitm.hosteldine.model.student.ShowStudentDetailEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShowStudentDetailMapper {

    ShowStudentDetailMapper INSTANCE = Mappers.getMapper(ShowStudentDetailMapper.class);

    ShowStudentDetailEntity toEntity(ShowStudentDetailDto showStudentDetailDto);

    @Mapping(target = ".", source = ".")
    ShowStudentDetailDto toDto(ShowStudentDetailEntity showStudentDetailEntity);

}