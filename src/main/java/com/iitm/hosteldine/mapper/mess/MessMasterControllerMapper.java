package com.iitm.hosteldine.mapper.mess;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.model.mess.MessMasterControllerEntity;

@Mapper(imports = {ModelConstants.class})
public interface MessMasterControllerMapper {
    MessMasterControllerMapper INSTANCE = Mappers.getMapper(MessMasterControllerMapper.class);

    @Mapping(target = ".", source = ".")
    MessMasterControllerDto fromMessMasterControllerEntity(MessMasterControllerEntity model);
    
    List<MessMasterControllerDto> toDto(List<MessMasterControllerEntity> entityList);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    @Mapping(target = "currentActiveFlag", expression = "java(ModelConstants.STATUS_ACTIVE)")
    MessMasterControllerEntity toMessMasterControllerEntity(MessMasterControllerDto modelDto);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentActiveFlag", ignore = true)
    void updateMessPeriodConfig(@MappingTarget MessMasterControllerEntity entity, MessMasterControllerDto messMasterControllerDto);

    @Mapping(target = "bulkMailSubject", source = "bulkMailSubject")
    @Mapping(target = "bulkMailContent", source = "bulkMailContent")
    @BeanMapping(ignoreByDefault = true)
    void updateBulkMailContent(@MappingTarget MessMasterControllerEntity entity,
                      MessMasterControllerDto messMasterControllerDto);
}