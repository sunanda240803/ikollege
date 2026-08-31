package com.iitm.hosteldine.mapper.collegeInfo;

import com.iitm.hosteldine.dto.collegeInfo.DepartmentDto;
import com.iitm.hosteldine.model.collegeInfo.DepartmentEntity;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DepartmentMapper {
    DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

    @Mapping(target = ".", source = ".")
    DepartmentDto fromDepartmentEntity(DepartmentEntity model);

    @Mapping(target = ".", source = ".")
    DepartmentEntity toDepartmentEntity(DepartmentDto modelDto);

    @Mapping(target = ".", source = ".")
	void onUpdateDepartmentEntity(@MappingTarget DepartmentEntity entity, DepartmentDto departmentDto);

    @Mapping(target = ".", source = ".")
	DepartmentEntity onSaveEntity(DepartmentDto departmentDto);
}