package com.iitm.hosteldine.mapper.staff;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.staff.StaffDesignationMasterDto;
import com.iitm.hosteldine.model.staff.StaffDesignationMasterEntity;

@Mapper
public interface StaffDesignationMasterMapper {
    StaffDesignationMasterMapper INSTANCE = Mappers.getMapper(StaffDesignationMasterMapper.class);

    @Mapping(target = ".", source = ".")
    StaffDesignationMasterDto fromStaffDesignationMasterEntity(StaffDesignationMasterEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StaffDesignationMasterEntity toStaffDesignationMasterEntity(StaffDesignationMasterDto modelDto);

    @Mapping(target = ".", source = ".")
	void onUpdateDesignationEntity(@MappingTarget StaffDesignationMasterEntity entity, StaffDesignationMasterDto designationMasterDto);

    @Mapping(target = ".", source = ".")
	StaffDesignationMasterEntity onSaveEntity(StaffDesignationMasterDto designationMasterDto);
}