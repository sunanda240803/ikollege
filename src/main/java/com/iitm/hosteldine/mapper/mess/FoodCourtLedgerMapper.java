package com.iitm.hosteldine.mapper.mess;

import com.iitm.hosteldine.dto.mess.FoodCourtLedgerDto;
import com.iitm.hosteldine.model.mess.FoodCourtLedgerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FoodCourtLedgerMapper {
    FoodCourtLedgerMapper INSTANCE = Mappers.getMapper(FoodCourtLedgerMapper.class);

    @Mapping(target = ".",source = ".")
    FoodCourtLedgerEntity toEntity(FoodCourtLedgerDto foodCourtLedgerDto);

    @Mapping(target = ".",source = ".")
    FoodCourtLedgerDto toDto(FoodCourtLedgerEntity foodCourtLedgerEntity);
}
