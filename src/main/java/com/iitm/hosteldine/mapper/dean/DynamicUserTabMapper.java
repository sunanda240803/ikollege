package com.iitm.hosteldine.mapper.dean;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.dean.DynamicUserTabDto;
import com.iitm.hosteldine.model.dean.DynamicUserTabEntity;

@Mapper
public interface DynamicUserTabMapper {
    DynamicUserTabMapper INSTANCE = Mappers.getMapper(DynamicUserTabMapper.class);

    @Mapping(target = ".", source = ".")
    DynamicUserTabDto fromDynamicUserTabEntity(DynamicUserTabEntity model);

    @Mapping(target = ".", source = ".")
    DynamicUserTabEntity toDynamicUserTabEntity(DynamicUserTabDto modelDto);
}