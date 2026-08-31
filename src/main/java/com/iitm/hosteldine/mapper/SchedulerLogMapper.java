package com.iitm.hosteldine.mapper;

import com.iitm.hosteldine.dto.SchedulerLogDto;
import com.iitm.hosteldine.model.SchedulerLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SchedulerLogMapper {
    SchedulerLogMapper INSTANCE = Mappers.getMapper(SchedulerLogMapper.class);

    @Mapping(target = ".", source = ".")
    SchedulerLogDto fromEntity(SchedulerLog model);

    @Mapping(target = ".", source = ".")
    SchedulerLog toEntity(SchedulerLogDto modelDto);
}