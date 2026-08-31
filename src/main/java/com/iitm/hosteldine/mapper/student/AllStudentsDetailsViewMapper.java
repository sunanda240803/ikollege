package com.iitm.hosteldine.mapper.student;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.student.AllStudentsDetailsViewDto;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;

@Mapper
public interface AllStudentsDetailsViewMapper {
    AllStudentsDetailsViewMapper INSTANCE = Mappers.getMapper(AllStudentsDetailsViewMapper.class);

    @Mapping(target = "allStudentsDetailsViewList", ignore = true)
	@Mapping(target = "dayScholarStatus", ignore = true)
	@Mapping(target = "field", ignore = true)
	@Mapping(target = "searchCriteria", ignore = true)
	@Mapping(target = "searchString", ignore = true)
	@Mapping(target = "vacationCategoryStatus", ignore = true)
	@Mapping(target = ".", source = ".")
    AllStudentsDetailsViewDto fromAllStudentsDetailsViewEntity(AllStudentsDetailsViewEntity model);

    @Mapping(target = ".", source = ".")
    AllStudentsDetailsViewEntity toAllStudentsDetailsViewEntity(AllStudentsDetailsViewDto modelDto);
}