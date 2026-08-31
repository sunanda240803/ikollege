package com.iitm.hosteldine.mapper.studentDashboard;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.StudentDetailsInfoMapper;
import com.iitm.hosteldine.dto.mess.MessAllottedListDTO;
import com.iitm.hosteldine.dto.studentDashboard.StudentMessDetailsDto;
import com.iitm.hosteldine.mapper.mess.MessMasterMapper;
import com.iitm.hosteldine.mapper.student.AllStudentsDetailsViewMapper;
import com.iitm.hosteldine.model.mess.StudentMessDetailsEntity;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;

@Mapper(imports = { StudentDetailsInfoMapper.class, MessMasterMapper.class, AllStudentsDetailsViewMapper.class,
		ModelConstants.class, WorkflowStatus.class })
public interface StudentMessDetailsMapper {
    StudentMessDetailsMapper INSTANCE = Mappers.getMapper(StudentMessDetailsMapper.class);

    @Mapping(target = ".", source = ".")
    StudentMessDetailsDto fromStudentMessDetailsEntity(StudentMessDetailsEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentMessDetailsEntity toStudentMessDetailsEntity(StudentMessDetailsDto modelDto);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "fromDate", source = "entity.fromDate")
    @Mapping(target = "toDate", source = "entity.toDate")
    @Mapping(target = "messMaster", source = "entity.messMaster")
    @Mapping(target = "messMasterDto", source = "entity.messMaster")
    @Mapping(target = "allStudentsDetailsViewDto", source = "studentDetailsView")
    StudentMessDetailsDto fromEntityAndView(StudentMessDetailsEntity entity,
                                            AllStudentsDetailsViewEntity studentDetailsView);

	@Mapping(target = "studentDetailsInfo.studentId", source = "studentId")
	@Mapping(target = "messMaster.id", source = "messId")
	@Mapping(target = "fromDate", source = "diningFromDate")
	@Mapping(target = "toDate", source = "diningToDate")
	@Mapping(target = "changeFromDate", source = "fromDate")
	@Mapping(target = "changeToDate", expression = "java(dto.getToDate() != null ? dto.getToDate() : dto.getDiningToDate())")
	@Mapping(target = "toPushDate", source = "fromDate")
	@Mapping(target = "currentActiveFlag", expression = "java(ModelConstants.STATUS_ACTIVE)")
	@Mapping(target = "pushRemoveStatus", expression = "java(WorkflowStatus.TO_BE_PUSHED.getStatus())")
	@Mapping(target = "remarks", expression = "java(WorkflowStatus.ALLOTTED.getStatus())")
	@BeanMapping(ignoreByDefault = true)
	void toEntity(@MappingTarget StudentMessDetailsEntity entity, MessAllottedListDTO dto);

	@Mapping(target = "studentDetailsInfo.studentId", source = "studentId")
	@Mapping(target = "messMaster.id", source = "messId")
	@Mapping(target = "hostelSigned", expression = "java(ModelConstants.STATUS_ACTIVE)")
	@Mapping(target = "messSigned", expression = "java(ModelConstants.STATUS_ACTIVE)")
	@Mapping(target = "allottedSlno", expression = "java(1)")
	@Mapping(target = "currentActiveFlag", expression = "java(ModelConstants.STATUS_ACTIVE)")
	@Mapping(target = "pushRemoveStatus", expression = "java(WorkflowStatus.TO_BE_PUSHED.getStatus())")
	@BeanMapping(ignoreByDefault = true)
	void toEntity(@MappingTarget StudentMessDetailsEntity entity, StudentMessDetailsDto dto);
}
