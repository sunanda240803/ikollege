package com.iitm.hosteldine.mapper.student;

import com.iitm.hosteldine.dto.student.StudentSearchViewDto;
import com.iitm.hosteldine.model.student.StudentSearchViewEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentSearchViewMapper {

    StudentSearchViewMapper INSTANCE = Mappers.getMapper(StudentSearchViewMapper.class);

    @Mapping(target = ".", source = ".")
    StudentSearchViewEntity toEntity(StudentSearchViewDto studentSearchViewDto);

    @Mapping(target = "allStudentsDetailsViewList", ignore = true)
    @Mapping(target = "dayScholarStatus", ignore = true)
    @Mapping(target = "field", ignore = true)
    @Mapping(target = "searchCriteria", ignore = true)
    @Mapping(target = "searchString", ignore = true)
    @Mapping(target = "vacationCategoryStatus", ignore = true)
    @Mapping(target = ".", source = ".")
    StudentSearchViewDto toDto(StudentSearchViewEntity studentSearchViewEntity);
}