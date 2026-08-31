package com.iitm.hosteldine.mapper.collegeInfo;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.collegeInfo.CourseMasterDto;
import com.iitm.hosteldine.model.collegeInfo.CourseMasterEntity;
import com.iitm.hosteldine.validator.common.ValidationCommon;

@Mapper (imports = ValidationCommon.class)
public interface CourseMasterMapper {
    CourseMasterMapper INSTANCE = Mappers.getMapper(CourseMasterMapper.class);

    @Mapping(target = ".", source = ".")
    CourseMasterDto fromCourseMasterEntity(CourseMasterEntity model);

    @Mapping(target = ".", source = ".")
    CourseMasterEntity toCourseMasterEntity(CourseMasterDto modelDto);
    
    @Mapping(target = ".", source = ".")
	void onUpdateCouseMasterEntity(@MappingTarget CourseMasterEntity entity, CourseMasterDto courseMasterDto);

	@Mapping(target = ".", source = ".")
   // @Mapping(target = "courseMasterName", expression = "java(ValidationCommon.trimString(courseMasterDto.getCourseMasterName()))")
	CourseMasterEntity onSaveEntity(CourseMasterDto courseMasterDto);
}