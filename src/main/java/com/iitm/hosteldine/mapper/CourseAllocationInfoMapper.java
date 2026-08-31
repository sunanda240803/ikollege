package com.iitm.hosteldine.mapper;

import com.iitm.hosteldine.dto.CourseAllocationInfoDto;
import com.iitm.hosteldine.entity.CourseAllocationInfoEntity;
import org.mapstruct.Mapper;

@Mapper
public interface CourseAllocationInfoMapper {
    CourseAllocationInfoDto toDTO(CourseAllocationInfoEntity courseAllocationInfo);
    CourseAllocationInfoEntity toEntity(CourseAllocationInfoDto courseAllocationInfoDTO);
}
