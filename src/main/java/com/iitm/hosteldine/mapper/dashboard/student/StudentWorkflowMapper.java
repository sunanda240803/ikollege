package com.iitm.hosteldine.mapper.dashboard.student;

import com.iitm.hosteldine.dto.dashboard.student.StudentWorkflowDto;
import com.iitm.hosteldine.model.dashboard.student.StudentAppointmentRequestEntity;
import com.iitm.hosteldine.model.dashboard.student.StudentWorkflowEntity;
import com.iitm.hosteldine.model.dashboard.student.WorkflowMasterEntity;

import java.util.List;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentWorkflowMapper {

    StudentWorkflowMapper INSTANCE = Mappers.getMapper(StudentWorkflowMapper.class);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentWorkflowEntity toEntity(StudentWorkflowDto studentWorkflowDto);

    @Mapping(target = "workFlowList", ignore = true)
    StudentWorkflowDto toDto(StudentWorkflowEntity studentWorkflowEntity);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentWorkflowEntity onUpdate(StudentWorkflowDto studentWorkflowDto, @MappingTarget StudentWorkflowEntity studentWorkflowEntity);

	@Mapping(target = "requestId", source = "savedEntity.id")
	@Mapping(target = "studentId", source = "savedEntity.studentId")
	@Mapping(target = "authorityType", source = "masterEntity.authorityType")
	@Mapping(target = "approvalLevel", source = "masterEntity.approvalLevel")
	@Mapping(target = "validatorEmail", source = "savedEntity.validatingAuthorityEmail")
	@Mapping(target = "validatorName", source = "savedEntity.validatingAuthority")
	@Mapping(target = "authenticationType", source = "masterEntity.authenticationType")
	@Mapping(target = "category", source = "masterEntity.category")
	@BeanMapping(ignoreByDefault = true)
	void workflowValidatorEntity(@MappingTarget StudentWorkflowEntity workflowEntity, WorkflowMasterEntity masterEntity,
			StudentAppointmentRequestEntity savedEntity);

	@Mapping(target = "requestId", source = "savedEntity.id")
	@Mapping(target = "studentId", source = "savedEntity.studentId")
	@Mapping(target = "authorityType", source = "masterEntity.authorityType")
	@Mapping(target = "approvalLevel", source = "masterEntity.approvalLevel")
	@Mapping(target = "validatorEmail", source = "masterEntity.email")
	@Mapping(target = "validatorName", source = "masterEntity.validatorName")
	@Mapping(target = "authenticationType", source = "masterEntity.authenticationType")
	@Mapping(target = "category", source = "masterEntity.category")
	@BeanMapping(ignoreByDefault = true)
	void workflowEntity(@MappingTarget StudentWorkflowEntity workflowEntity, WorkflowMasterEntity masterEntity,
			StudentAppointmentRequestEntity savedEntity);
	
}