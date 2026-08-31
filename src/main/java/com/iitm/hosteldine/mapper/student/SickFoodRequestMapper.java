package com.iitm.hosteldine.mapper.student;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.staff.StaffDesignationMasterDto;
import com.iitm.hosteldine.dto.student.SickFoodRequestDto;
import com.iitm.hosteldine.model.staff.StaffDesignationMasterEntity;
import com.iitm.hosteldine.model.student.SickFoodRequestEntity;

@Mapper
public interface SickFoodRequestMapper {
    SickFoodRequestMapper INSTANCE = Mappers.getMapper(SickFoodRequestMapper.class);

    @Mapping(target = ".", source = ".")
    SickFoodRequestDto fromSickFoodRequestEntity(SickFoodRequestEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    SickFoodRequestEntity toSickFoodRequestEntity(SickFoodRequestDto modelDto);
    
    @Mapping(target = ".", source = ".")
	void onUpdateEntity( @MappingTarget SickFoodRequestEntity existingEntity, SickFoodRequestDto sickFoodRequestDto);

    @Mapping(target = ".", source = ".")
	SickFoodRequestEntity onSaveEntity(SickFoodRequestDto sickFoodRequestDto);
    
    @Mapping(target = ".", source = ".")
	void onCreateEntity(@MappingTarget SickFoodRequestEntity newEntity, SickFoodRequestDto sickFoodRequestDto);
	
	 
}