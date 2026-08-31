package com.iitm.hosteldine.mapper.studentDashboard;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.studentDashboard.GuestAccommodationRequestDto;
import com.iitm.hosteldine.model.studentDashboard.GuestAccommodationRequestEntity;

@Mapper
public interface GuestAccommodationRequestMapper {
    GuestAccommodationRequestMapper INSTANCE = Mappers.getMapper(GuestAccommodationRequestMapper.class);

    @Mapping(target = ".", source = ".")
    GuestAccommodationRequestDto fromGuestAccommodationRequestEntity(GuestAccommodationRequestEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    GuestAccommodationRequestEntity toGuestAccommodationRequestEntity(GuestAccommodationRequestDto modelDto);
    
    @Mapping(target = ".", source = ".")
    @Mapping(target = "studentDetailsInfo.id.studentId", expression = "java(SecurityCtxUtil.userId().toUpperCase())")
	GuestAccommodationRequestEntity onSaveEntity(GuestAccommodationRequestDto dto);
}