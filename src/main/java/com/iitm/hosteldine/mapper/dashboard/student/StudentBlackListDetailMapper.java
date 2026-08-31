package com.iitm.hosteldine.mapper.dashboard.student;

import com.iitm.hosteldine.dto.dashboard.student.StudentBlackListDetailDto;
import com.iitm.hosteldine.model.dashboard.student.StudentBlackListDetailEntity;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentBlackListDetailMapper {

    StudentBlackListDetailMapper INSTANCE = Mappers.getMapper(StudentBlackListDetailMapper.class);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentBlackListDetailEntity toEntity(StudentBlackListDetailDto studentBlackListDetailDto);

    @Mapping(target = ".", source = ".")
    StudentBlackListDetailDto toDto(StudentBlackListDetailEntity studentBlackListDetailEntity);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentBlackListDetailEntity onUpdate(StudentBlackListDetailDto studentBlackListDetailDto, @MappingTarget StudentBlackListDetailEntity studentBlackListDetailEntity);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    @Mapping(target = "id", ignore = true)
    void onUpdateEntity(@MappingTarget StudentBlackListDetailEntity existingEntity, StudentBlackListDetailDto studentBlackListDetailDto);
}