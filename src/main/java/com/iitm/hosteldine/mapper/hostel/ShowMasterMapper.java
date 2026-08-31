package com.iitm.hosteldine.mapper.hostel;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.hostel.ShowMasterDto;
import com.iitm.hosteldine.model.hostel.ShowMasterEntity;

@Mapper
public interface ShowMasterMapper {
    ShowMasterMapper INSTANCE = Mappers.getMapper(ShowMasterMapper.class);

    @Mapping(target = ".", source = ".")
    ShowMasterDto fromShowMasterEntity(ShowMasterEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    ShowMasterEntity toShowMasterEntity(ShowMasterDto modelDto);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "showEventMaster", ignore = true)
	void onUpdateEntity(@MappingTarget ShowMasterEntity existingEntity, ShowMasterDto dto);

	ShowMasterEntity onSaveEntity(ShowMasterDto dto);
}