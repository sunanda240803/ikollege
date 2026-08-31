package com.iitm.hosteldine.mapper.hostel;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.hostel.ShowSeatDetailsDto;
import com.iitm.hosteldine.model.hostel.ShowSeatDetailsEntity;

@Mapper
public interface ShowSeatDetailsMapper {
    ShowSeatDetailsMapper INSTANCE = Mappers.getMapper(ShowSeatDetailsMapper.class);

    @Mapping(target = ".", source = ".")
    ShowSeatDetailsDto fromShowSeatDetailsEntity(ShowSeatDetailsEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    ShowSeatDetailsEntity toShowSeatDetailsEntity(ShowSeatDetailsDto modelDto);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "show",ignore = true)
	void onUpdateEntity(@MappingTarget ShowSeatDetailsEntity existingEntity, ShowSeatDetailsDto dto);

	ShowSeatDetailsEntity onSaveEntity(ShowSeatDetailsDto dto);
}