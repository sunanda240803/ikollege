package com.iitm.hosteldine.dto;

import com.iitm.hosteldine.dto.student.StudentBulkInfoDto;
import com.iitm.hosteldine.dto.student.StudentDetailsInfoDto;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentDetailsInfoMapper {
    StudentDetailsInfoMapper INSTANCE = Mappers.getMapper(StudentDetailsInfoMapper.class);

    @Mapping(target = ".", source = ".")
    StudentDetailsInfoDto fromStudentDetailsInfoEntity(StudentDetailsInfoEntity model);

    @Mapping(target = "workFlowList", ignore = true)
    @Mapping(target = ".", source = ".")
    StudentDetailsInfoDto fromStudentDetailsEntity(StudentDetailsInfoEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentDetailsInfoEntity toStudentDetailsEntity(StudentBulkInfoDto modelDto);

    @Mapping(target = ".", source = ".")
    StudentDetailsInfoEntity toEntity(StudentDetailsInfoDto studentDetailsInfoDto);

    @Mapping(target = "studentId", source = "studentId")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "gender", source = "gender")
   // @Mapping(target = "previousId", source = "previousId")
    @BeanMapping(ignoreByDefault = true)
    void onUpdateEntity(@MappingTarget StudentDetailsInfoEntity studentDetailsInfoEntity, StudentBulkInfoDto studentBulkInfoDto);
}