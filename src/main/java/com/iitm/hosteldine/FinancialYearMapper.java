package com.iitm.hosteldine;

import com.iitm.hosteldine.dto.financialYear.FinancialYearDto;
import com.iitm.hosteldine.model.financialYear.FinancialYearEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FinancialYearMapper {
    FinancialYearMapper INSTANCE = Mappers.getMapper(FinancialYearMapper.class);

    @Mapping(target =".",source =".")
    FinancialYearEntity toEntity(FinancialYearDto financialYearDto);

    @Mapping(target = ".",source = ".")
    FinancialYearDto toDto(FinancialYearEntity financialYearEntity);
}