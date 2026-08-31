package com.iitm.hosteldine.mapper.api;

import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.dto.api.MessTokenAPIDto;
import com.iitm.hosteldine.generated.model.GuestCouponAccessTokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {DateUtility.class})
public interface GuestCouponAccessTokenMapper {
    GuestCouponAccessTokenMapper INSTANCE = Mappers.getMapper(GuestCouponAccessTokenMapper.class);

    @Mapping(target = "username", source = "userName")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "macId", source = "macId")
    @Mapping(target = "generatedAt", expression = "java(DateUtility.getNowTimeInstant())")
    @Mapping(target = "activeFlag", constant = "true")
    @Mapping(target = "timeLimit", source = "timeLimit")
    @Mapping(target = "source", source = "source")
    @Mapping(target = "lastUsed", expression = "java(DateUtility.getNowTimeInstant())")
    @Mapping(target = "userType", source = "userType")
    GuestCouponAccessTokenEntity toEntity(MessTokenAPIDto dto);


}