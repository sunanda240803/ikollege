package com.iitm.hosteldine.mapper.mess;

import com.iitm.hosteldine.dto.mess.MessLedgerADto;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessLedgerAMapper {
    MessLedgerAMapper INSTANCE = Mappers.getMapper(MessLedgerAMapper.class);

    @Mapping(target = ".",source = ".")
    MessLedgerADto toDto(MessLedgerAEntity messLedgerAEntity);

}