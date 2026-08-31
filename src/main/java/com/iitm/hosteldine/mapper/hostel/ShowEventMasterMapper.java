package com.iitm.hosteldine.mapper.hostel;

import com.iitm.hosteldine.dto.hostel.ShowEventMasterDto;
import com.iitm.hosteldine.model.hostel.ShowEventMasterEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShowEventMasterMapper {
    ShowEventMasterMapper INSTANCE = Mappers.getMapper(ShowEventMasterMapper.class);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    @Mapping(target = "schoolId", ignore = true)
    ShowEventMasterEntity toEntity(ShowEventMasterDto showEventMasterDto);

    @Mapping(target = ".", source = ".")
    ShowEventMasterDto toDto(ShowEventMasterEntity showEventMasterEntity);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    @Mapping(target = "schoolId", ignore = true)
    @Mapping(target = "id", ignore = true)
    void onUpdateEntity(@MappingTarget ShowEventMasterEntity existingEntity, ShowEventMasterDto dto);
}