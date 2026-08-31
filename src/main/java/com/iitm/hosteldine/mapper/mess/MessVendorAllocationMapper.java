package com.iitm.hosteldine.mapper.mess;

import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.mess.MessVendorAllocationDto;
import com.iitm.hosteldine.entity.mess.MessVendorAllocationEntity;
import com.iitm.hosteldine.model.hostel.HostelMasterEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessVendorAllocationMapper {
    MessVendorAllocationMapper INSTANCE = Mappers.getMapper(MessVendorAllocationMapper.class);

    MessVendorAllocationDto toDto(MessVendorAllocationEntity entity);

    @Mapping(target = "id.messId", source = "messId")
    @Mapping(target = "id.vendorCode", source = "vendorCode")
    @Mapping(target = "rate", source = "rate")
    @Mapping(target = "fromDate", source = "fromDate")
    @Mapping(target = "toDate", source = "toDate")
    @Mapping(target = "effectiveDate", source = "fromDate")
    @Mapping(target = "gst", source = "gst")
    MessVendorAllocationEntity toEntity(MessVendorAllocationDto dto);
}
