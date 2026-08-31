package com.iitm.hosteldine.mapper.mess;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.mess.MessVendorMasterDto;
import com.iitm.hosteldine.model.mess.MessVendorMasterEntity;

@Mapper
public interface MessVendorMasterMapper {
	MessVendorMasterMapper INSTANCE = Mappers.getMapper(MessVendorMasterMapper.class);

	@Mapping(target = ".", source = ".")
	MessVendorMasterDto fromMessVendorMasterEntity(MessVendorMasterEntity model);

	@Mapping(target = ".", source = ".")
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedBy", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	@Mapping(target = "activeFlag", ignore = true)
	MessVendorMasterEntity toMessVendorMasterEntity(MessVendorMasterDto modelDto);

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "activeFlag", ignore = true)
	@Mapping(target = "vendorCode", ignore = true)
	void onUpdateEntity(@MappingTarget MessVendorMasterEntity existingEntity, MessVendorMasterDto dto);
}