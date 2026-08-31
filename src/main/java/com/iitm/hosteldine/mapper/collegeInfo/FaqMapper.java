package com.iitm.hosteldine.mapper.collegeInfo;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.collegeInfo.FaqDto;
import com.iitm.hosteldine.dto.hostel.ShowEventMasterDto;
import com.iitm.hosteldine.model.collegeInfo.FaqEntity;
import com.iitm.hosteldine.model.hostel.ShowEventMasterEntity;

@Mapper
public interface FaqMapper {
    FaqMapper INSTANCE = Mappers.getMapper(FaqMapper.class);

    @Mapping(target = ".", source = ".")
    FaqDto fromFaqEntity(FaqEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    FaqEntity toFaqEntity(FaqDto modelDto);
    
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    @Mapping(target = "id", ignore = true)
    void onUpdateEntity(@MappingTarget FaqEntity existingEntity, FaqDto dto);
}