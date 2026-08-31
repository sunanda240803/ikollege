package com.iitm.hosteldine.mapper.student;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.student.MessCardAmountTransferDto;
import com.iitm.hosteldine.model.student.MessCardAmountTransferEntity;

@Mapper
public interface MessCardAmountTransferMapper {
    MessCardAmountTransferMapper INSTANCE = Mappers.getMapper(MessCardAmountTransferMapper.class);

    @Mapping(target = ".", source = ".")
    MessCardAmountTransferDto fromMessCardAmountTransferEntity(MessCardAmountTransferEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    MessCardAmountTransferEntity toMessCardAmountTransferEntity(MessCardAmountTransferDto modelDto);

	MessCardAmountTransferEntity onSaveEntity(MessCardAmountTransferDto dto);
}