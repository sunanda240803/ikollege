package com.iitm.hosteldine.mapper.mess;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.mess.MessRebateWorkflowDto;
import com.iitm.hosteldine.model.dashboard.student.WorkflowMasterEntity;
import com.iitm.hosteldine.model.mess.MessRebateEntity;
import com.iitm.hosteldine.model.mess.MessRebateWorkflowEntity;

@Mapper
public interface MessRebateWorkflowMapper {
    MessRebateWorkflowMapper INSTANCE = Mappers.getMapper(MessRebateWorkflowMapper.class);

    @Mapping(target = ".", source = ".")
    MessRebateWorkflowDto fromMessRebateWorkflowEntity(MessRebateWorkflowEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    MessRebateWorkflowEntity toMessRebateWorkflowEntity(MessRebateWorkflowDto modelDto);

	@Mapping(target = "requestId", source = "rebateEntity.id")
	@Mapping(target = "studentId", source = "rebateEntity.studentId")
	@Mapping(target = "authorityType", source = "masterEntity.authorityType")
	@Mapping(target = "approvalLevel", source = "masterEntity.approvalLevel")
	@Mapping(target = "guideName", source = "rebateEntity.guideName")
	@Mapping(target = "guideEmail", source = "rebateEntity.guideEmail")
	@Mapping(target = "authenticationType", source = "masterEntity.authenticationType")
	@Mapping(target = "cancelStatus", source = "rebateEntity.cancelStatus")
	@Mapping(target = "rejectionDescription", source = "rebateEntity.rejectionDescription")
	@BeanMapping(ignoreByDefault = true)
	void guideWorkflowEntity(@MappingTarget MessRebateWorkflowEntity workflowEntity, WorkflowMasterEntity masterEntity,
			MessRebateEntity rebateEntity);


	@Mapping(target = "requestId", source = "rebateEntity.id")
	@Mapping(target = "studentId", source = "rebateEntity.studentId")
	@Mapping(target = "authorityType", source = "masterEntity.authorityType")
	@Mapping(target = "approvalLevel", source = "masterEntity.approvalLevel")
	@Mapping(target = "guideName", source = "masterEntity.validatorName")
	@Mapping(target = "guideEmail", source = "masterEntity.email")
	@Mapping(target = "authenticationType", source = "masterEntity.authenticationType")
	@Mapping(target = "cancelStatus", source = "rebateEntity.cancelStatus")
	@Mapping(target = "rejectionDescription", source = "rebateEntity.rejectionDescription")
	@BeanMapping(ignoreByDefault = true)
	void workflowEntity(@MappingTarget MessRebateWorkflowEntity workflowEntity, WorkflowMasterEntity masterEntity,
			MessRebateEntity rebateEntity);

	
}

