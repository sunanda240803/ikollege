package com.iitm.hosteldine.mapper.asset;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.asset.AssetMaintenanceTypeDto;
import com.iitm.hosteldine.form.asset.AssetCategoryForm;
import com.iitm.hosteldine.model.asset.AssetMaintenanceTypeEntity;

@Mapper (imports = ModelConstants.class)
public interface AssetMaintenanceTypeMapper {
    AssetMaintenanceTypeMapper INSTANCE = Mappers.getMapper(AssetMaintenanceTypeMapper.class);

    @Mapping(target = ".", source = ".")
    AssetMaintenanceTypeDto fromAssetMaintenanceTypeEntity(AssetMaintenanceTypeEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    AssetMaintenanceTypeEntity toAssetMaintenanceTypeEntity(AssetMaintenanceTypeDto modelDto);

	@Mapping(target = "id", source = "maintenanceTypeId")
    @Mapping(target = "categoryType", expression = "java(ModelConstants.ASSET_MAINTENANCE_TYPE)")
    @Mapping(target = "categoryName", source = "maintenanceType")
    @Mapping(target = "modifiedAt", source = "modifiedAt")
    @BeanMapping(ignoreByDefault = true)
    AssetCategoryForm fromAssetMaintenanceTypeEntityToForm(AssetMaintenanceTypeEntity model);
	
    @Mapping(target = "maintenanceTypeId", source = "id")
    @Mapping(target = "maintenanceType", source = "categoryName")
    @BeanMapping(ignoreByDefault = true)
    void toAssetMaintenanceTypeEntity(@MappingTarget AssetMaintenanceTypeEntity entity, AssetCategoryForm form);
}