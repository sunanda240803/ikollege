package com.iitm.hosteldine.mapper.student;

import com.iitm.hosteldine.dto.student.RfidMappingDto;
import com.iitm.hosteldine.entity.student.RfidMappingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RfidMappingMapper {

    RfidMappingMapper INSTANCE = Mappers.getMapper(RfidMappingMapper.class);

    RfidMappingDto toDto(RfidMappingEntity entity);

    RfidMappingEntity toEntity(RfidMappingDto dto);
}
