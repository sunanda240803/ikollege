package com.iitm.hosteldine.mapper.mailQueue;

import com.iitm.hosteldine.dto.mailQueue.MailQueueDetailsDto;
import com.iitm.hosteldine.entity.mailQueue.MailQueueDetailsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MailQueueDetailsMapper {
    MailQueueDetailsMapper INSTANCE = Mappers.getMapper(MailQueueDetailsMapper.class);

    MailQueueDetailsDto toDto(MailQueueDetailsEntity entity);

    MailQueueDetailsEntity toEntity(MailQueueDetailsDto dto);
}
