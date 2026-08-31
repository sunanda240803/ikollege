package com.iitm.hosteldine.mapper.mess;

import com.iitm.hosteldine.dto.mess.MessCouponUserMappingDto;
import com.iitm.hosteldine.model.mess.MessCouponUserMappingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessCouponUserMappingMapper {

    MessCouponUserMappingMapper INSTANCE = Mappers.getMapper(MessCouponUserMappingMapper.class);

    @Mapping(target = ".",source = ".")
    MessCouponUserMappingEntity toEntity(MessCouponUserMappingDto messCouponUserMappingDto);

    @Mapping(target = ".",source = ".")
    @Mapping(target = "messMaster",source = "messMaster")
    MessCouponUserMappingDto toDto(MessCouponUserMappingEntity messCouponUserMappingEntity);

}