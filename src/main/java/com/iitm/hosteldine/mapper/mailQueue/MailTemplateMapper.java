package com.iitm.hosteldine.mapper.mailQueue;

import com.iitm.hosteldine.dto.mailQueue.MailTemplateDto;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MailTemplateMapper {

    MailTemplateMapper INSTANCE = Mappers.getMapper(MailTemplateMapper.class);

    @Mapping(target =".",source = ".")
    @Mapping(target ="activeFlag",source = "activeFlag")
    MailTemplateDto toDto(MailTemplateEntity entity);

    @Mapping(target = ".",source = ".")
    @Mapping(target ="activeFlag",ignore = true)
    MailTemplateEntity toEntity(MailTemplateDto dto);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    @Mapping(target = "mailType", ignore = true)
    @Mapping(target = "approvalLevel", ignore = true)
    void updateEntity(@MappingTarget MailTemplateEntity entity, MailTemplateDto dto);
}
