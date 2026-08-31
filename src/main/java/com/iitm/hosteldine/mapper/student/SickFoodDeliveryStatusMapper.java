package com.iitm.hosteldine.mapper.student;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.student.SickFoodDeliveryStatusDto;
import com.iitm.hosteldine.model.student.SickFoodDeliveryStatusEntity;

@Mapper
public interface SickFoodDeliveryStatusMapper {
    SickFoodDeliveryStatusMapper INSTANCE = Mappers.getMapper(SickFoodDeliveryStatusMapper.class);

    @Mapping(target = ".", source = ".")
    SickFoodDeliveryStatusDto fromSickFoodDeliveryStatusEntity(SickFoodDeliveryStatusEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    SickFoodDeliveryStatusEntity toSickFoodDeliveryStatusEntity(SickFoodDeliveryStatusDto modelDto);
}