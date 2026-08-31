package com.iitm.hosteldine.mapper.student;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.student.SaveTransactionFADto;
import com.iitm.hosteldine.model.student.SaveTransactionFAEntity;

@Mapper
public interface SaveTransactionFAMapper {
    SaveTransactionFAMapper INSTANCE = Mappers.getMapper(SaveTransactionFAMapper.class);

    @Mapping(target = ".", source = ".")
    SaveTransactionFADto fromSaveTransactionFAEntity(SaveTransactionFAEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    SaveTransactionFAEntity toSaveTransactionFAEntity(SaveTransactionFADto modelDto);
}