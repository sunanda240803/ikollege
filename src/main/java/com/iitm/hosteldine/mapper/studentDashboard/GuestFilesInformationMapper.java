package com.iitm.hosteldine.mapper.studentDashboard;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.studentDashboard.GuestFilesInformationDto;
import com.iitm.hosteldine.model.studentDashboard.GuestFilesInformationEntity;

@Mapper
public interface GuestFilesInformationMapper {
    GuestFilesInformationMapper INSTANCE = Mappers.getMapper(GuestFilesInformationMapper.class);

    @Mapping(target = ".", source = ".")
    GuestFilesInformationDto fromGuestFilesInformationEntity(GuestFilesInformationEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "activeFlag", ignore = true)
    GuestFilesInformationEntity toGuestFilesInformationEntity(GuestFilesInformationDto modelDto);
}