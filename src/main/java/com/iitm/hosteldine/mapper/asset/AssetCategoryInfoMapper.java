package com.iitm.hosteldine.mapper.asset;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.asset.AssetCategoryInfoDto;
import com.iitm.hosteldine.form.asset.AssetCategoryForm;
import com.iitm.hosteldine.model.asset.AssetCategoryInfoEntity;

@Mapper (imports = ModelConstants.class)
public interface AssetCategoryInfoMapper {
    AssetCategoryInfoMapper INSTANCE = Mappers.getMapper(AssetCategoryInfoMapper.class);

    @Mapping(target = ".", source = ".")
    AssetCategoryInfoDto fromAssetCategoryInfoEntity(AssetCategoryInfoEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    AssetCategoryInfoEntity toAssetCategoryInfoEntity(AssetCategoryInfoDto modelDto);

    @Mapping(target = "id", source = "assetCategoryId")
    @Mapping(target = "categoryType", expression = "java(ModelConstants.ASSET_CATEGORY)")
    @Mapping(target = "categoryName", source = "assetCategory")
    @Mapping(target = "modifiedAt", source = "modifiedAt")
	@Mapping(target = "assetCategoryShortcode", source = "assetCategoryShortcode")
	@Mapping(target = "minorRepairCost", source = "minorRepairCost")
	@Mapping(target = "majorRepairCost", source = "majorRepairCost")
	@Mapping(target = "replacementCost", source = "replacementCost")
	@Mapping(target = "assetCategoryDescription", source = "assetCategoryDescription")
    @BeanMapping(ignoreByDefault = true)
    AssetCategoryForm fromAssetCategoryInfoEntityToForm(AssetCategoryInfoEntity category);

    @Mapping(target = "assetCategoryId", source = "id")
    @Mapping(target = "assetCategory", source = "categoryName")
	@Mapping(target = "assetCategoryShortcode", source = "assetCategoryShortcode")
	@Mapping(target = "minorRepairCost", source = "minorRepairCost")
	@Mapping(target = "majorRepairCost", source = "majorRepairCost")
	@Mapping(target = "replacementCost", source = "replacementCost")
	@Mapping(target = "assetCategoryDescription", source = "assetCategoryDescription")
    @BeanMapping(ignoreByDefault = true)
    void toAssetCategoryInfoEntity(@MappingTarget AssetCategoryInfoEntity categoryInfoEntity, AssetCategoryForm form);

}
