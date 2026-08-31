package com.iitm.hosteldine.mapper.hostel;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.hostel.HostelUserMappingDto;
import com.iitm.hosteldine.model.hostel.HostelUserMappingEntity;

@Mapper
public interface HostelUserMappingMapper {
	HostelUserMappingMapper INSTANCE = Mappers.getMapper(HostelUserMappingMapper.class);

	@Mapping(target = ".", source = ".")
	HostelUserMappingDto fromHostelUserMappingEntity(HostelUserMappingEntity model);

	@Mapping(target = ".", source = ".")
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedBy", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	@Mapping(target = "activeFlag", ignore = true)
	HostelUserMappingEntity toHostelUserMappingEntity(HostelUserMappingDto modelDto);

	/*
	 * @Mapping(target = "id.hostel.id", expression =
	 * "java(HostelMasterMapper.INSTANCE.haveOnlyId(dto.getId()))") void
	 * setIdValues(@MappingTarget HostelUserMappingEntity entity, HostelMasterDto
	 * dto);
	 */
}