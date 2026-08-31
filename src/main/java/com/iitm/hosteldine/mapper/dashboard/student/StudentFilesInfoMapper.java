package com.iitm.hosteldine.mapper.dashboard.student;

import com.iitm.hosteldine.dto.dashboard.student.StudentFilesInfoDto;
import com.iitm.hosteldine.model.dashboard.student.StudentFilesInfoEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentFilesInfoMapper {

    StudentFilesInfoMapper INSTANCE = Mappers.getMapper(StudentFilesInfoMapper.class);

    @Mapping(target = "activeFlag", ignore = true)
    StudentFilesInfoEntity toEntity(StudentFilesInfoDto studentFilesInfoDto);

    @Mapping(target = ".", source = ".")
    StudentFilesInfoDto toDto(StudentFilesInfoEntity studentFilesInfoEntity);

    @Mapping(target = "activeFlag", ignore = true)
    StudentFilesInfoEntity onUpdate(StudentFilesInfoDto studentFilesInfoDto, @MappingTarget StudentFilesInfoEntity studentFilesInfoEntity);
}