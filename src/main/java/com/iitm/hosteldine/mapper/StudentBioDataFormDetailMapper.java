package com.iitm.hosteldine.mapper;

import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.dto.student.StudentBulkInfoDto;
import com.iitm.hosteldine.dto.student.StudentDetailsDto;
import com.iitm.hosteldine.model.StudentBioDataFormDetailEntity;

import jakarta.validation.Valid;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentBioDataFormDetailMapper {
    StudentBioDataFormDetailMapper INSTANCE = Mappers.getMapper(StudentBioDataFormDetailMapper.class);

    @Mapping(target = ".", source = ".")
    StudentBioDataFormDetailDto fromStudentBioDataFormDetailEntity(StudentBioDataFormDetailEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentBioDataFormDetailEntity toStudentBioDataFormDetailEntity(StudentBioDataFormDetailDto modelDto);
    
    @Mapping(target = "studentId", source = "studentId")
    @Mapping(target = "studentName", expression = "java(student.getFirstName() + (student.getLastName() != null ? \" \" + student.getLastName() : \"\"))")
    @Mapping(target = "gender", source = "gender")
    @BeanMapping(ignoreByDefault = true)
    StudentBioDataFormDetailEntity onCreateEntity(StudentBulkInfoDto student);

    @Mapping(target = "studentName", expression = "java(student.getFirstName() + (student.getLastName() != null ? \" \" + student.getLastName() : \"\"))")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "studentId", source = "studentId")
    @BeanMapping(ignoreByDefault = true)
	void onUpdateEntity(@MappingTarget StudentBioDataFormDetailEntity studentBioDataForm, StudentBulkInfoDto student);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    @Mapping(target = "studentName", source = "allStudentsDetailsViewDto.studentName")
    @Mapping(target = "dob", source = "allStudentsDetailsViewDto.dob")
    @Mapping(target = "gender", source = "allStudentsDetailsViewDto.gender")
    @Mapping(target = "category", source = "allStudentsDetailsViewDto.category")
    @Mapping(target = "aadhaarNumber", source = "allStudentsDetailsViewDto.aadhaarNumber")
    @Mapping(target = "panNum", source = "allStudentsDetailsViewDto.panNumber")
    @Mapping(target = "bloodGroup", source = "allStudentsDetailsViewDto.bloodGroup")
    @Mapping(target = "studentMobile", source = "allStudentsDetailsViewDto.studentMobile")
    @Mapping(target = "studentPersonalEmail", source = "allStudentsDetailsViewDto.studentPersonalEmail")
    @Mapping(target = "studentAddress", source = "allStudentsDetailsViewDto.studentAddress")
    @Mapping(target = "pwd", source = "allStudentsDetailsViewDto.pwd")
    @Mapping(target = "pwdPercentage", source = "allStudentsDetailsViewDto.pwdPercentage")
    @Mapping(target = "pwdDescription", source = "allStudentsDetailsViewDto.pwdDescription")
    @Mapping(target = "otherInfo", source = "allStudentsDetailsViewDto.otherInfo")
    @Mapping(target = "facultyName", source = "allStudentsDetailsViewDto.facultyName")
    @Mapping(target = "facultyEmail", source = "allStudentsDetailsViewDto.facultyEmail")
    @Mapping(target = "facultyContactNo", source = "allStudentsDetailsViewDto.facultyContactNo")
    @Mapping(target = "messName", source = "allStudentsDetailsViewDto.messPreference")
	void updateStudentBioData(@MappingTarget StudentBioDataFormDetailEntity studentBioDataForm, StudentDetailsDto studentDetailsDto);
}
