package com.iitm.hosteldine.mapper.dashboard.student;

import com.iitm.hosteldine.dto.dashboard.student.WorkflowMasterDto;
import com.iitm.hosteldine.model.dashboard.student.WorkflowMasterEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WorkflowMasterMapper {

    WorkflowMasterMapper INSTANCE = Mappers.getMapper(WorkflowMasterMapper.class);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    WorkflowMasterEntity toEntity(WorkflowMasterDto studentWorkflowMasterDto);

    @Mapping(target = ".", source = ".")
    WorkflowMasterDto toDto(WorkflowMasterEntity studentWorkflowMasterEntity);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    WorkflowMasterEntity onUpdate(WorkflowMasterDto studentWorkflowMasterDto, @MappingTarget WorkflowMasterEntity studentWorkflowMasterEntity);
}