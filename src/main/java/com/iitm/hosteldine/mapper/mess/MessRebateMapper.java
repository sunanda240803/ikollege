package com.iitm.hosteldine.mapper.mess;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.mess.MessRebateDto;
import com.iitm.hosteldine.model.mess.MessRebateEntity;

@Mapper
public interface MessRebateMapper {
    MessRebateMapper INSTANCE = Mappers.getMapper(MessRebateMapper.class);

    @Mapping(target = "approvedCount", ignore = true)
	@Mapping(target = "pendingCount", ignore = true)
	@Mapping(target = "rejectedCount", ignore = true)
	@Mapping(target = ".", source = ".")
    MessRebateDto fromMessRebateEntity(MessRebateEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    MessRebateEntity messRebateEntity(MessRebateDto modelDto);
}