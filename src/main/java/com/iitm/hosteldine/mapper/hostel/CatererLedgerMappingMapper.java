package com.iitm.hosteldine.mapper.hostel;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.hostel.CatererLedgerMappingDto;
import com.iitm.hosteldine.model.hostel.CatererLedgerMappingEntity;

@Mapper
public interface CatererLedgerMappingMapper {
    CatererLedgerMappingMapper INSTANCE = Mappers.getMapper(CatererLedgerMappingMapper.class);

    @Mapping(target = ".", source = ".")
    CatererLedgerMappingDto fromCatererLedgerMappingEntity(CatererLedgerMappingEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    CatererLedgerMappingEntity toCatererLedgerMappingEntity(CatererLedgerMappingDto modelDto);
}