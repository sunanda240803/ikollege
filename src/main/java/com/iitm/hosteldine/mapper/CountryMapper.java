package com.iitm.hosteldine.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.CountryDto;
import com.iitm.hosteldine.model.CountryEntity;

@Mapper
public interface CountryMapper {
    CountryMapper INSTANCE = Mappers.getMapper(CountryMapper.class);

    @Mapping(target = ".", source = ".")
    CountryDto fromCountryEntity(CountryEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    CountryEntity toCountryEntity(CountryDto modelDto);
}