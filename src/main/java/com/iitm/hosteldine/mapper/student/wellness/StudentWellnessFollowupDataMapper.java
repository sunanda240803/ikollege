package com.iitm.hosteldine.mapper.student.wellness;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.student.wellness.StudentWellnessFollowupDataDto;
import com.iitm.hosteldine.model.student.wellness.StudentWellnessFollowupDataEntity;
import com.iitm.hosteldine.util.MCrypt;

@Mapper
public interface StudentWellnessFollowupDataMapper {
    StudentWellnessFollowupDataMapper INSTANCE = Mappers.getMapper(StudentWellnessFollowupDataMapper.class);

    @Mappings({
		@Mapping(target = "interactionMode", qualifiedByName = "decrypt"),
		@Mapping(target = "concernsDiscussed", qualifiedByName = "decrypt"),
		@Mapping(target = "futureActionPlan", qualifiedByName = "decrypt"),
		@Mapping(target = "visitStatus", qualifiedByName = "decrypt")
	})
    StudentWellnessFollowupDataDto fromStudentWellnessFollowupDataEntity(StudentWellnessFollowupDataEntity model);

    @Mappings({
	    @Mapping(target = "createdBy", ignore = true),
	    @Mapping(target = "createdAt", ignore = true),
	    @Mapping(target = "modifiedBy", ignore = true),
	    @Mapping(target = "modifiedAt", ignore = true),
	    @Mapping(target = "activeFlag", ignore = true),
		@Mapping(target = "interactionMode", qualifiedByName = "encrypt"),
		@Mapping(target = "concernsDiscussed", qualifiedByName = "encrypt"),
		@Mapping(target = "futureActionPlan", qualifiedByName = "encrypt"),
		@Mapping(target = "visitStatus", qualifiedByName = "encrypt")
	})
    StudentWellnessFollowupDataEntity toStudentWellnessFollowupDataEntity(StudentWellnessFollowupDataDto modelDto);

    @Mappings({ 
	    @Mapping(target = "activeFlag", ignore = true), 
	    @Mapping(target = "createdAt", ignore = true), 
	    @Mapping(target = "createdBy", ignore = true), 
	    @Mapping(target = "modifiedBy", ignore = true),
	    @Mapping(target = "modifiedAt", ignore = true),
	    @Mapping(target = "id", ignore = true),
	    @Mapping(target = "wellness", ignore = true),
	    @Mapping(target = "noOfVisit", ignore = true),
		@Mapping(target = "interactionMode", qualifiedByName = "encrypt"),
		@Mapping(target = "concernsDiscussed", qualifiedByName = "encrypt"),
		@Mapping(target = "futureActionPlan", qualifiedByName = "encrypt"),
		@Mapping(target = "visitStatus", qualifiedByName = "encrypt")
	})
	void toStudentWellnessFollowupDataEntity(@MappingTarget StudentWellnessFollowupDataEntity existingEntity,
			StudentWellnessFollowupDataDto dto);
	
    @Named("decrypt")
    default String decrypt(String value) throws Exception {
        return (value != null) ? MCrypt.getInstance().decryptToString(value) : null;
    }
    
    @Named("encrypt")
    default String encrypt(String value) throws Exception {
        return (value != null) ? MCrypt.getInstance().encryptToText(value) : null;
    }
}