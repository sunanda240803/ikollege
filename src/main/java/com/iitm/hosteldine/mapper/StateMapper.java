package com.iitm.hosteldine.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.StateDto;
import com.iitm.hosteldine.model.StateEntity;

@Mapper
public interface StateMapper {
    StateMapper INSTANCE = Mappers.getMapper(StateMapper.class);

    @Mapping(target = ".", source = ".")
    StateDto fromStateEntity(StateEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StateEntity toStateEntity(StateDto modelDto);
}