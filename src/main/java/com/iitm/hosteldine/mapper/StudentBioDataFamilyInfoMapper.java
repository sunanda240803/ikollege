package com.iitm.hosteldine.mapper;

import com.iitm.hosteldine.dto.StudentBioDataFamilyInfoDto;
import com.iitm.hosteldine.model.StudentBioDataFamilyInfoEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@Mapper()
public interface StudentBioDataFamilyInfoMapper {
    StudentBioDataFamilyInfoMapper INSTANCE = Mappers.getMapper(StudentBioDataFamilyInfoMapper.class);

    @Mapping(target = ".", source = ".")
    StudentBioDataFamilyInfoDto fromStudentBioDataFamilyInfoEntity(StudentBioDataFamilyInfoEntity model);

    @Mapping(target = ".", source = ".")
    StudentBioDataFamilyInfoEntity toStudentBioDataFamilyInfoEntity(StudentBioDataFamilyInfoDto modelDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDtoToEntity(StudentBioDataFamilyInfoDto dto, @MappingTarget StudentBioDataFamilyInfoEntity entity);
}