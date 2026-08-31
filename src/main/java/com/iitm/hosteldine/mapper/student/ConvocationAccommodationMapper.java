package com.iitm.hosteldine.mapper.student;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.student.ConvocationAccommodationDto;
import com.iitm.hosteldine.model.student.ConvocationAccommodationEntity;

@Mapper
public interface ConvocationAccommodationMapper {
    ConvocationAccommodationMapper INSTANCE = Mappers.getMapper(ConvocationAccommodationMapper.class);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "hostelName", source = "hostelName")
    @Mapping(target = "accommodationPreference", ignore = true)
    ConvocationAccommodationDto fromConvocationAccommodationEntity(ConvocationAccommodationEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "hostelName", source = "hostelName")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    ConvocationAccommodationEntity toConvocationAccommodationEntity(ConvocationAccommodationDto modelDto);
}
