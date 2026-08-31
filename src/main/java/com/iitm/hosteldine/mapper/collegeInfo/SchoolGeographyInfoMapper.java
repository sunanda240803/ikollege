package com.iitm.hosteldine.mapper.collegeInfo;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.collegeInfo.SchoolGeographyInfoDto;
import com.iitm.hosteldine.model.collegeInfo.SchoolGeographyInfoEntity;

@Mapper
public interface SchoolGeographyInfoMapper {
    SchoolGeographyInfoMapper INSTANCE = Mappers.getMapper(SchoolGeographyInfoMapper.class);

    @Mapping(target = ".", source = ".")
    SchoolGeographyInfoDto fromSchoolGeographyInfoEntity(SchoolGeographyInfoEntity model);

    @Mapping(target = ".", source = ".")
    SchoolGeographyInfoEntity toSchoolGeographyInfoEntity(SchoolGeographyInfoDto modelDto);
    
    @Mapping(target = ".", source = ".")
    SchoolGeographyInfoDto fromSGI(SchoolGeographyInfoEntity schoolGeographyInfo);

    @Mapping(target = "schoolId", ignore = true)
    void onUpdateEntity(@MappingTarget SchoolGeographyInfoEntity existingEntity, SchoolGeographyInfoDto dto);
}