package com.iitm.hosteldine.mapper.hostel;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.hostel.HostelRoomInventoryDto;
import com.iitm.hosteldine.form.hostel.RoomInventoryForm;
import com.iitm.hosteldine.model.hostel.HostelRoomInfoEntity;
import com.iitm.hosteldine.model.hostel.HostelRoomInventoryEntity;

@Mapper
public interface HostelRoomInventoryMapper {
	HostelRoomInventoryMapper INSTANCE = Mappers.getMapper(HostelRoomInventoryMapper.class);

	@Mapping(target = ".", source = ".")
	HostelRoomInventoryDto fromHostelRoomInventoryEntity(HostelRoomInventoryEntity model);

	@Mapping(target = ".", source = ".")
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedBy", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	@Mapping(target = "activeFlag", ignore = true)
	HostelRoomInventoryEntity toHostelRoomInventoryEntity(HostelRoomInventoryDto modelDto);

	@Mapping(target = "room.id", source = "roomId")
	@Mapping(target = "itemId", source = "assetId")
	@Mapping(target = "category.assetCategoryId", source = "assetCategoryId")
	@Mapping(target = "quantity", constant = "1")
	@Mapping(target = "assetCondition", source = "assetConditionStatus")
	@Mapping(target = "assetConditionDate", source = "assetConditionDate")
	@Mapping(target = "assetCode", source = "assetCode")
	@BeanMapping(ignoreByDefault = true)
	HostelRoomInventoryEntity toHostelRoomInventoryEntity(RoomInventoryForm form);

    @Mapping(target = "assetCondition", source = "assetCondition")
    @Mapping(target = "assetConditionDate", source = "assetConditionDate")
    @BeanMapping(ignoreByDefault = true)
	void toHostelRoomInventoryEntity(@MappingTarget HostelRoomInventoryEntity hostel, RoomInventoryForm form);
}
