package com.iitm.hosteldine.mapper.hostel;

import com.iitm.hosteldine.dto.hostel.HostelRoomInfoDto;
import com.iitm.hosteldine.model.hostel.HostelRoomInfoEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HostelRoomInfoMapper {
	HostelRoomInfoMapper INSTANCE = Mappers.getMapper(HostelRoomInfoMapper.class);

	@Mapping(target = ".", source = ".")
	HostelRoomInfoDto fromHostelRoomInfoEntity(HostelRoomInfoEntity model);

	@Mapping(target = ".", source = ".")
	HostelRoomInfoEntity toHostelRoomInfoEntity(HostelRoomInfoDto modelDto);

	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "activeFlag", ignore = true)
	@Mapping(target = "building", expression = "java(HostelFloorMasterMapper.INSTANCE.haveOnlyId(dto.getBuilding().getId()))")
	void onUpdateEntity(@MappingTarget HostelRoomInfoEntity entity, HostelRoomInfoDto dto);
}