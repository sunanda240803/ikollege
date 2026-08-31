package com.iitm.hosteldine.mapper.staff;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.staff.StaffDetailsDto;
import com.iitm.hosteldine.model.staff.StaffDetailsEntity;

@Mapper
public interface StaffDetailsMapper {
	StaffDetailsMapper INSTANCE = Mappers.getMapper(StaffDetailsMapper.class);

	@Mapping(target = ".", source = ".")
	@Mapping(target = "newFacultyId", source = "facultyId")
	StaffDetailsDto fromStaffDetailsEntity(StaffDetailsEntity model);

	@Mapping(target = ".", source = ".")
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedBy", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	@Mapping(target = "activeFlag", ignore = true)
	StaffDetailsEntity toStaffDetailsEntity(StaffDetailsDto modelDto);
	
	@Mapping(target = ".", source = ".")
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedBy", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	@Mapping(target = "activeFlag", ignore = true)
	@Mapping(target = "employeeId", ignore = true)
	void onUpdateStaffDetailsEntity(@MappingTarget StaffDetailsEntity entity,
			StaffDetailsDto staffDetailsDtoDto);

	@Mapping(target = ".", source = ".")
	StaffDetailsEntity onSaveEntity(StaffDetailsDto staffDetailsDto);
}
