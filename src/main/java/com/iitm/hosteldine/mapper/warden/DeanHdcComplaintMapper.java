package com.iitm.hosteldine.mapper.warden;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.dean.DeanHdcComplaintDto;
import com.iitm.hosteldine.model.dean.DeanHdcComplaintEntity;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface DeanHdcComplaintMapper {

    DeanHdcComplaintMapper INSTANCE = Mappers.getMapper(DeanHdcComplaintMapper.class);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    DeanHdcComplaintEntity toEntity(DeanHdcComplaintDto deanHdcComplaintDto);

    @Mapping(target = ".", source = ".")
    DeanHdcComplaintDto toDto(DeanHdcComplaintEntity deanHdcComplaintEntity);
    
    @Mapping(target = "paidAmount", source = "paidAmount")
    @Mapping(target = "penaltyStatus", source = "status")
    @Mapping(target = "paymentReferenceNumber", source = "paymentRefNo")
    @Mapping(target = "paymentDescription", source = "paymentDesc")
    @BeanMapping(ignoreByDefault = true)
	void onUpdateEntity(@MappingTarget DeanHdcComplaintEntity deanHdcComplaintEntity, DeanHdcComplaintDto dto);
    
    @Mapping(target = ".", source = ".")
    DeanHdcComplaintEntity onSaveEntity(DeanHdcComplaintDto deanHdcComplaintDto);
}