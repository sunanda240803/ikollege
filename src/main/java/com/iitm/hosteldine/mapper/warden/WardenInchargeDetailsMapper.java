package com.iitm.hosteldine.mapper.warden;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.warden.WardenInfoDto;
import com.iitm.hosteldine.model.warden.WardenInchargeDetailsEntity;

import jakarta.validation.Valid;

@Mapper
public interface WardenInchargeDetailsMapper {
    WardenInchargeDetailsMapper INSTANCE = Mappers.getMapper(WardenInchargeDetailsMapper.class);

    @Mapping(target = "awayFrom", source = "awayFrom")
    @Mapping(target = "awayTo", source = "awayTo")
    @Mapping(target = "awayDescription", source = "awayDescription")
    @Mapping(target = "inchargeId", source = "wardenId")
    @BeanMapping(ignoreByDefault = true)
    void updateInchargeEntity(@MappingTarget WardenInchargeDetailsEntity wardenInchargeDetailsEntity,  WardenInfoDto modelDto);

    @Mapping(target = "awayFrom", source = "awayFrom")
    @Mapping(target = "awayTo", source = "awayTo")
    @Mapping(target = "awayDescription", source = "awayDescription")
    @Mapping(target = "inchargeId", source = "wardenId")
    @Mapping(target = "wardenId", source = "id")
    @BeanMapping(ignoreByDefault = true)
	void onSaveEntity(@MappingTarget WardenInchargeDetailsEntity inchargeDetailsEntity, @Valid WardenInfoDto wardenInfoDto);
}