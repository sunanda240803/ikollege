package com.iitm.hosteldine.mapper.hostel;

import com.iitm.hosteldine.dto.hostel.HostelBiometricTerminalDto;
import com.iitm.hosteldine.model.hostel.HostelBiometricTerminalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HostelBiometricTerminalMapper {
    HostelBiometricTerminalMapper INSTANCE = Mappers.getMapper(HostelBiometricTerminalMapper.class);

    @Mapping(target = ".", source = ".")
    HostelBiometricTerminalDto toDto(HostelBiometricTerminalEntity entity);

    @Mapping(target = ".", source = ".")
    HostelBiometricTerminalEntity toEntity(HostelBiometricTerminalDto dto);
}