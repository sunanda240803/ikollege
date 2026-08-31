package com.iitm.hosteldine.mapper.dashboard.student;

import com.iitm.hosteldine.dto.dashboard.student.HostelNightPaymentTransactionDto;
import com.iitm.hosteldine.model.studentDashboard.HostelNightPaymentTransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HostelNightPaymentTransactionMapper {
    HostelNightPaymentTransactionMapper INSTANCE = Mappers.getMapper(HostelNightPaymentTransactionMapper.class);

    @Mapping(target = ".", source = ".")
    HostelNightPaymentTransactionDto toDto(HostelNightPaymentTransactionEntity entity);

    @Mapping(target = ".", source = ".")
    HostelNightPaymentTransactionEntity toEntity(HostelNightPaymentTransactionDto dto);
}