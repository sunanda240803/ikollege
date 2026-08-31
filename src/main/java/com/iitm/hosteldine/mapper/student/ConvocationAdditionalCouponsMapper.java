package com.iitm.hosteldine.mapper.student;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.student.ConvocationAdditionalCouponsDto;
import com.iitm.hosteldine.model.student.ConvocationAdditionalCouponsEntity;

@Mapper
public interface ConvocationAdditionalCouponsMapper {
    ConvocationAdditionalCouponsMapper INSTANCE = Mappers.getMapper(ConvocationAdditionalCouponsMapper.class);

    @Mapping(target = ".", source = ".")
    ConvocationAdditionalCouponsDto fromConvocationAdditionalCouponsEntity(ConvocationAdditionalCouponsEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    ConvocationAdditionalCouponsEntity toConvocationAdditionalCouponsEntity(ConvocationAdditionalCouponsDto modelDto);
}