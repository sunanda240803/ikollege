package com.iitm.hosteldine.mapper.OtherCandidate;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.OtherCandidate.TempAccomPaymentAdviceDto;
import com.iitm.hosteldine.model.OtherCandidate.TempAccomPaymentAdviceEntity;

@Mapper
public interface TempAccomPaymentAdviceMapper {
    TempAccomPaymentAdviceMapper INSTANCE = Mappers.getMapper(TempAccomPaymentAdviceMapper.class);

    @Mapping(target = ".", source = ".")
    TempAccomPaymentAdviceDto fromTempAccomPaymentAdviceEntity(TempAccomPaymentAdviceEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    TempAccomPaymentAdviceEntity toTempAccomPaymentAdviceEntity(TempAccomPaymentAdviceDto modelDto);
    
}