package com.iitm.hosteldine.mapper.dashboard.student;

import com.iitm.hosteldine.dto.dashboard.student.StudentHostelRoomVacatingRequestDto;
import com.iitm.hosteldine.model.dashboard.student.StudentHostelRoomVacatingRequestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentHostelRoomVacatingRequestMapper {
    StudentHostelRoomVacatingRequestMapper INSTANCE = Mappers.getMapper(StudentHostelRoomVacatingRequestMapper.class);


    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentHostelRoomVacatingRequestEntity toEntity(StudentHostelRoomVacatingRequestDto studentHostelRoomVacatingRequestDto);

    @Mapping(target = ".", source = ".")  // This line is not required
    StudentHostelRoomVacatingRequestDto toDto(StudentHostelRoomVacatingRequestEntity studentHostelRoomVacatingRequestEntity);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentHostelRoomVacatingRequestEntity onUpdate(StudentHostelRoomVacatingRequestDto studentHostelRoomVacatingRequestDto, @MappingTarget StudentHostelRoomVacatingRequestEntity studentHostelRoomVacatingRequestEntity);

    StudentHostelRoomVacatingRequestEntity onSaveEntity(StudentHostelRoomVacatingRequestDto dto);
}
