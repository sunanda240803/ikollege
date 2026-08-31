package com.iitm.hosteldine.mapper.mess;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.mess.MessSessionDto;
import com.iitm.hosteldine.model.mess.MessSessionEntity;

@Mapper
public interface MessSessionMapper {
    MessSessionMapper INSTANCE = Mappers.getMapper(MessSessionMapper.class);

    @Mapping(target = ".", source = ".")
    MessSessionDto fromMessSessionEntity(MessSessionEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    MessSessionEntity toMessSessionEntity(MessSessionDto modelDto);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "activeFlag", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
	void onUpdateMessSessionEntity(@MappingTarget MessSessionEntity entity, MessSessionDto messSessionDto);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "id.messId", source = "messSessionDto.id.messId")
    @Mapping(target = "id.sessionName", source = "messSessionDto.id.sessionName")
	MessSessionEntity onSaveEntity(MessSessionDto messSessionDto);
    
}