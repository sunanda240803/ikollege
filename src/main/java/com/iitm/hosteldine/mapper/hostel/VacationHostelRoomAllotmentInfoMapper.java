package com.iitm.hosteldine.mapper.hostel;

import com.iitm.hosteldine.dto.hostel.VacationHostelRoomAllotmentInfoDto;
import com.iitm.hosteldine.model.hostel.VacationHostelRoomAllotmentInfoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VacationHostelRoomAllotmentInfoMapper {

    VacationHostelRoomAllotmentInfoMapper INSTANCE = Mappers.getMapper(VacationHostelRoomAllotmentInfoMapper.class);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    VacationHostelRoomAllotmentInfoEntity toEntity(VacationHostelRoomAllotmentInfoDto vacationHostelRoomAllotmentInfoDto);

    @Mapping(target = ".", source = ".")
    VacationHostelRoomAllotmentInfoDto toDto(VacationHostelRoomAllotmentInfoEntity vacationHostelRoomAllotmentInfoEntity);
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    @Mapping(target = "id", ignore = true)
    void onUpdateEntity(@MappingTarget VacationHostelRoomAllotmentInfoEntity existingEntity, VacationHostelRoomAllotmentInfoDto dto);
}