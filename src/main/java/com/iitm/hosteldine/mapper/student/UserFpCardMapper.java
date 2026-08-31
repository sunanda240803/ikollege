package com.iitm.hosteldine.mapper.student;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.student.UserFpCardDto;
import com.iitm.hosteldine.entity.student.UserFpCardEntity;

@Mapper
public interface UserFpCardMapper {

	UserFpCardMapper INSTANCE = Mappers.getMapper(UserFpCardMapper.class);

	UserFpCardDto toDto(UserFpCardEntity entity);

	@Mapping(target = "pinNo", source = "pinNo")
	@Mapping(target = "cardActiveStatus", source = "cardActiveStatus")
	@BeanMapping(ignoreByDefault = true)
	void toEntity(@MappingTarget UserFpCardEntity entity, UserFpCardDto dto);
}
