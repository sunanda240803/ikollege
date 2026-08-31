package com.iitm.hosteldine.mapper.hostel;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.HostelRoomAllotmentInfoDto;
import com.iitm.hosteldine.model.hostel.HostelRoomAllotmentInfoEntity;

@Mapper(imports = ModelConstants.class)
public interface HostelRoomAllotmentInfoMapper {
	HostelRoomAllotmentInfoMapper INSTANCE = Mappers.getMapper(HostelRoomAllotmentInfoMapper.class);

    @Mapping(target = ".", source = ".")
    HostelRoomAllotmentInfoDto fromHostelRoomAllotmentInfoEntity(HostelRoomAllotmentInfoEntity model);

    @Mapping(target = "activeFlag", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "isMissing", expression = "java(Boolean.parseBoolean(ModelConstants.FALSE))")
    @Mapping(target = ".", source = ".")
    HostelRoomAllotmentInfoEntity toHostelRoomAllotmentInfoEntity(HostelRoomAllotmentInfoDto modelDto);

}
