package com.iitm.hosteldine.mapper.student;

import com.iitm.hosteldine.dto.student.SecondaryRolesDto;
import com.iitm.hosteldine.entity.student.SecondaryRolesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SecondaryRolesMapper {

    SecondaryRolesMapper INSTANCE = Mappers.getMapper(SecondaryRolesMapper.class);

    SecondaryRolesDto toDto(SecondaryRolesEntity entity);

    SecondaryRolesEntity toEntity(SecondaryRolesDto dto);
}
