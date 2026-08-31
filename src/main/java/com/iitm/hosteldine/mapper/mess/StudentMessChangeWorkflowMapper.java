package com.iitm.hosteldine.mapper.mess;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.mess.MessAllottedListDTO;
import com.iitm.hosteldine.dto.mess.StudentMessChangeWorkflowDto;
import com.iitm.hosteldine.model.mess.StudentMessChangeWorkflowEntity;


@Mapper (imports = WorkflowStatus.class)
public interface StudentMessChangeWorkflowMapper {
	StudentMessChangeWorkflowMapper INSTANCE = Mappers.getMapper(StudentMessChangeWorkflowMapper.class);

	@Mapping(target = ".", source = ".")
	StudentMessChangeWorkflowDto fromStudentMessChangeWorkflowEntity(StudentMessChangeWorkflowEntity model);

	@Mapping(target = ".", source = ".")
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedBy", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	@Mapping(target = "activeFlag", ignore = true)
	StudentMessChangeWorkflowEntity toStudentMessChangeWorkflowEntity(StudentMessChangeWorkflowDto modelDto);

	@Mapping(target = "studentId", source = "existingDto.studentId")
	@Mapping(target = "originallyMessFromDate", source = "existingDto.fromDate")
	@Mapping(target = "originallyMessToDate", source = "existingDto.toDate")
	@Mapping(target = "originallyMessId", source = "existingDto.messId")
	@Mapping(target = "requestedMessFromDate", source = "dto.effectiveFromDate")
	@Mapping(target = "requestedMessToDate", expression = "java(dto.getToDate() != null ? dto.getToDate() : existingDto.getToDate())")
	@Mapping(target = "requestedMessId", source = "dto.changeMessId")
	@Mapping(target = "approvalStatus", expression = "java(WorkflowStatus.APPROVED.getStatus())")
	@Mapping(target = "description", source = "dto.description")
	@Mapping(target = "description1", expression = "java(WorkflowStatus.MESS_CHANGE.getStatus())")
	@BeanMapping(ignoreByDefault = true)
	void toEntity(@MappingTarget StudentMessChangeWorkflowEntity entity, MessAllottedListDTO dto, MessAllottedListDTO existingDto);
}
