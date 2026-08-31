package com.iitm.hosteldine.mapper.hostel;

import com.iitm.hosteldine.dto.hostel.TemporaryAccommodationConfigDto;
import com.iitm.hosteldine.model.hostel.TemporaryAccommodationConfigEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TemporaryAccommodationConfigMapper {
    TemporaryAccommodationConfigMapper INSTANCE = Mappers.getMapper(TemporaryAccommodationConfigMapper.class);

    @Mapping(target = ".", source = ".")
    TemporaryAccommodationConfigDto toDto(TemporaryAccommodationConfigEntity entity);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "id", ignore = true)
    TemporaryAccommodationConfigEntity toEntity(TemporaryAccommodationConfigDto dto);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget TemporaryAccommodationConfigEntity entity, TemporaryAccommodationConfigDto dto);
}