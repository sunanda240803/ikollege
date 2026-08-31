package com.iitm.hosteldine.mapper.student;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.student.MessCardAmountTransferControllerDto;
import com.iitm.hosteldine.model.student.MessCardAmountTransferControllerEntity;


@Mapper
public interface MessCardAmountTransferControllerMapper {
    MessCardAmountTransferControllerMapper INSTANCE = Mappers.getMapper(MessCardAmountTransferControllerMapper.class);

    @Mapping(target = ".", source = ".")
    MessCardAmountTransferControllerDto fromMessCardAmountTransferControllerEntity(MessCardAmountTransferControllerEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    MessCardAmountTransferControllerEntity toMessCardAmountTransferControllerEntity(MessCardAmountTransferControllerDto modelDto);
}