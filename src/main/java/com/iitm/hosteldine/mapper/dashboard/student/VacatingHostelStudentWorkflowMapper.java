package com.iitm.hosteldine.mapper.dashboard.student;

import com.iitm.hosteldine.dto.dashboard.student.VacatingHostelStudentWorkflowDto;
import com.iitm.hosteldine.model.dashboard.student.VacatingHostelStudentWorkflowEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VacatingHostelStudentWorkflowMapper {

    VacatingHostelStudentWorkflowMapper INSTANCE = Mappers.getMapper(VacatingHostelStudentWorkflowMapper.class);


    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    VacatingHostelStudentWorkflowEntity toEntity(VacatingHostelStudentWorkflowDto vacatingHostelStudentWorkflowDto);

    @Mapping(target = ".", source = ".")  // This line is not required
    VacatingHostelStudentWorkflowDto toDto(VacatingHostelStudentWorkflowEntity vacatingHostelStudentWorkflowEntity);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    VacatingHostelStudentWorkflowEntity onUpdate(VacatingHostelStudentWorkflowDto vacatingHostelStudentWorkflowDto, @MappingTarget VacatingHostelStudentWorkflowEntity vacatingHostelStudentWorkflowEntity);

}
