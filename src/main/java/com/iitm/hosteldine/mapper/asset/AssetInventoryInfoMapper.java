package com.iitm.hosteldine.mapper.asset;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.asset.AssetInventoryInfoDto;
import com.iitm.hosteldine.form.hostel.RoomInventoryForm;
import com.iitm.hosteldine.model.asset.AssetInventoryInfoEntity;
import com.iitm.hosteldine.validator.common.ValidationCommon;

@Mapper(imports = {ModelConstants.class, ValidationCommon.class})
public interface AssetInventoryInfoMapper {
	AssetInventoryInfoMapper INSTANCE = Mappers.getMapper(AssetInventoryInfoMapper.class);

	@Mapping(target = ".", source = ".")
	AssetInventoryInfoDto fromAssetInventoryInfoEntity(AssetInventoryInfoEntity model);

	@Mapping(target = ".", source = ".")
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedBy", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	@Mapping(target = "activeFlag", ignore = true)
	AssetInventoryInfoEntity toAssetInventoryInfoEntity(AssetInventoryInfoDto modelDto);

	@Mapping(target = "assetCategoryId", source = "assetCategoryId")
	@Mapping(target = "assetName", expression = "java(ValidationCommon.trimString(form.getAssetName()))")
	@Mapping(target = "assetQuantity", constant = "1")
	@Mapping(target = "assetPrice", constant = "0")
	@Mapping(target = "assetLocation", source = "assetLocation")
	@Mapping(target = "assetInUseStatusFlag", source = "assetInUseStatusFlag")
	@Mapping(target = "assetConditionStatus", source = "assetConditionStatus")
	@Mapping(target = "assetCondition", source = "assetConditionStatus")
	@Mapping(target = "assetConditionDate", source = "assetConditionDate")
	@Mapping(target = "assetCode", source = "assetCode")
	@BeanMapping(ignoreByDefault = true)
	AssetInventoryInfoEntity toAssetInventoryInfoEntity(RoomInventoryForm form);
	
	@Mapping(target = "assetName", expression = "java(ValidationCommon.trimString(form.getAssetName()))")
    @Mapping(target = "assetConditionStatus", source = "assetCondition")
    @Mapping(target = "assetConditionDate", source = "assetConditionDate")
    @BeanMapping(ignoreByDefault = true)
    void toAssetInventoryInfoEntity(@MappingTarget AssetInventoryInfoEntity asset, RoomInventoryForm form);
	
}
