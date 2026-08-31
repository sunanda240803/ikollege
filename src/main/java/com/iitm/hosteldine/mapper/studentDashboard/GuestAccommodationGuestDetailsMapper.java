package com.iitm.hosteldine.mapper.studentDashboard;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.studentDashboard.GuestAccommodationGuestDetailsDto;
import com.iitm.hosteldine.model.studentDashboard.GuestAccommodationGuestDetailsEntity;

@Mapper
public interface GuestAccommodationGuestDetailsMapper {
    GuestAccommodationGuestDetailsMapper INSTANCE = Mappers.getMapper(GuestAccommodationGuestDetailsMapper.class);

    @Mapping(target = ".", source = ".")
    GuestAccommodationGuestDetailsDto fromGuestAccommodationGuestDetailsEntity(GuestAccommodationGuestDetailsEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    GuestAccommodationGuestDetailsEntity toGuestAccommodationGuestDetailsEntity(GuestAccommodationGuestDetailsDto modelDto);
}