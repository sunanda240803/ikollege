package com.iitm.hosteldine.mapper.dashboard.student;

import com.iitm.hosteldine.dto.dashboard.student.ScholarsHodDetailDto;
import com.iitm.hosteldine.model.dashboard.student.ScholarsHodDetailEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ScholarsHodDetailMapper {

    ScholarsHodDetailMapper INSTANCE = Mappers.getMapper(ScholarsHodDetailMapper.class);


    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    ScholarsHodDetailEntity toEntity(ScholarsHodDetailDto scholarsHodDetailDto);

    @Mapping(target = ".", source = ".")  // This line is not required
    ScholarsHodDetailDto toDto(ScholarsHodDetailEntity scholarsHodDetailEntity);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    ScholarsHodDetailEntity onUpdate(ScholarsHodDetailDto scholarsHodDetailDto, @MappingTarget ScholarsHodDetailEntity scholarsHodDetailEntity);
}