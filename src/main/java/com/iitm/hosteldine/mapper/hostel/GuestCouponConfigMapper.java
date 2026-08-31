package com.iitm.hosteldine.mapper.hostel;


import com.iitm.hosteldine.dto.hostel.GuestCouponConfigDto;
import com.iitm.hosteldine.model.hostel.GuestCouponConfigEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GuestCouponConfigMapper {

    // Convert Entity -> DTO
    @Mapping(target = ".", source = ".")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "activeFlag", ignore = true)
    GuestCouponConfigDto toDto(GuestCouponConfigEntity guestCouponConfig);

    // Convert DTO -> Entity
    @Mapping(target = ".", source = ".")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "activeFlag", defaultValue = "Y")
    GuestCouponConfigEntity toEntity(GuestCouponConfigDto guestCouponConfigDto);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "activeFlag", defaultValue = "Y")
    GuestCouponConfigEntity toUpdateEntity(GuestCouponConfigDto dto);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    @Mapping(target = "id", ignore = true)
    void onUpdateEntity(@MappingTarget GuestCouponConfigEntity existingEntity, GuestCouponConfigDto dto);
}
