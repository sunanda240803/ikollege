package com.iitm.hosteldine.mapper.warden;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.warden.GuestAccommodationChargesDto;
import com.iitm.hosteldine.model.warden.GuestAccommodationChargesEntity;


@Mapper
public interface GuestAccommodationChargesMapper {
    GuestAccommodationChargesMapper INSTANCE = Mappers.getMapper(GuestAccommodationChargesMapper.class);

    @Mapping(target = ".", source = ".")
    GuestAccommodationChargesDto fromGuestAccommodationChargesEntity(GuestAccommodationChargesEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    GuestAccommodationChargesEntity toGuestAccommodationChargesEntity(GuestAccommodationChargesDto modelDto);
    
    @Mapping(target = ".", source = ".")
	void onUpdateEntity(@MappingTarget GuestAccommodationChargesEntity existingEntity, GuestAccommodationChargesDto dto);

	GuestAccommodationChargesEntity onSaveEntity(GuestAccommodationChargesDto dto);
}