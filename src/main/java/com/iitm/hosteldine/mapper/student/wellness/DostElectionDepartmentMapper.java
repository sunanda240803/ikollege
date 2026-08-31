package com.iitm.hosteldine.mapper.student.wellness;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.student.wellness.DostElectionDepartmentDto;
import com.iitm.hosteldine.model.student.wellness.DostElectionDepartmentEntity;

@Mapper
public interface DostElectionDepartmentMapper {
    DostElectionDepartmentMapper INSTANCE = Mappers.getMapper(DostElectionDepartmentMapper.class);

    @Mapping(target = ".", source = ".")
    DostElectionDepartmentDto fromDostElectionDepartmentEntity(DostElectionDepartmentEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    DostElectionDepartmentEntity toDostElectionDepartmentEntity(DostElectionDepartmentDto modelDto);
}