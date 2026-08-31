package com.iitm.hosteldine.mapper.student.wellness;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.student.wellness.StudentWellnessCategoricalDataDto;
import com.iitm.hosteldine.model.student.wellness.StudentWellnessCategoricalDataEntity;
import com.iitm.hosteldine.util.MCrypt;

@Mapper
public interface StudentWellnessCategoricalDataMapper {
    StudentWellnessCategoricalDataMapper INSTANCE = Mappers.getMapper(StudentWellnessCategoricalDataMapper.class);

	@Mappings({
		@Mapping(target = "studentDetails", ignore = true), 
		@Mapping(target = "dayScholar", ignore = true), 
		@Mapping(target = "submittedDate", ignore = true), 
		@Mapping(target = "hostelName", ignore = true), 
		@Mapping(target = "noOfVisit", ignore = true),
		@Mapping(target = "studentName", ignore = true), 
		@Mapping(target = "wellnessId", ignore = true),
		@Mapping(target = "referralType", qualifiedByName = "decrypt"),
		@Mapping(target = "referralBy", qualifiedByName = "decrypt"),
		@Mapping(target = "referralEmail", qualifiedByName = "decrypt"),
		@Mapping(target = "referralPhone", qualifiedByName = "decrypt"),
		@Mapping(target = "referralOthersDescription", qualifiedByName = "decrypt"),
		@Mapping(target = "referralLandlineNum", qualifiedByName = "decrypt"),
		@Mapping(target = "coordinatedName", qualifiedByName = "decrypt"),
		@Mapping(target = "coordinatedEmail", qualifiedByName = "decrypt"),
		@Mapping(target = "concernType", qualifiedByName = "decrypt"),
		@Mapping(target = "concernOthersDescription", qualifiedByName = "decrypt"),
		@Mapping(target = "selfHarmType", qualifiedByName = "decrypt"), 
		@Mapping(target = "psychiatricName", qualifiedByName = "decrypt"), 
		@Mapping(target = "additionalDetails", qualifiedByName = "decrypt")
	})
	StudentWellnessCategoricalDataDto fromStudentWellnessCategoricalDataEntity(StudentWellnessCategoricalDataEntity model);

	@Mappings({
	    @Mapping(target = "createdBy", ignore = true),
	    @Mapping(target = "createdAt", ignore = true),
	    @Mapping(target = "modifiedBy", ignore = true),
	    @Mapping(target = "modifiedAt", ignore = true),
	    @Mapping(target = "activeFlag", ignore = true),
		@Mapping(target = "referralType", qualifiedByName = "encrypt"),
		@Mapping(target = "referralBy", qualifiedByName = "encrypt"),
		@Mapping(target = "referralEmail", qualifiedByName = "encrypt"),
		@Mapping(target = "referralPhone", qualifiedByName = "encrypt"),
		@Mapping(target = "referralOthersDescription", qualifiedByName = "encrypt"),
		@Mapping(target = "referralLandlineNum", qualifiedByName = "encrypt"),
		@Mapping(target = "coordinatedName", qualifiedByName = "encrypt"),
		@Mapping(target = "coordinatedEmail", qualifiedByName = "encrypt"),
		@Mapping(target = "concernType", qualifiedByName = "encrypt"),
		@Mapping(target = "concernOthersDescription", qualifiedByName = "encrypt"),
		@Mapping(target = "selfHarmType", qualifiedByName = "encrypt"), 
		@Mapping(target = "psychiatricName", qualifiedByName = "encrypt"), 
		@Mapping(target = "additionalDetails", qualifiedByName = "encrypt")
	})
    StudentWellnessCategoricalDataEntity toStudentWellnessCategoricalDataEntity(StudentWellnessCategoricalDataDto modelDto);

	@Mappings({ 
	    @Mapping(target = "activeFlag", ignore = true), 
	    @Mapping(target = "createdAt", ignore = true), 
	    @Mapping(target = "createdBy", ignore = true), 
	    @Mapping(target = "modifiedBy", ignore = true),
	    @Mapping(target = "modifiedAt", ignore = true),
	    @Mapping(target = "studentId", ignore = true),
	    @Mapping(target = "id", ignore = true),
	    @Mapping(target = "otherStudName", ignore = true),
	    @Mapping(target = "otherStudEmail", ignore = true),
	    @Mapping(target = "otherStudPhone", ignore = true),
	    @Mapping(target = "category", ignore = true),
		@Mapping(target = "referralType", qualifiedByName = "encrypt"),
		@Mapping(target = "referralBy", qualifiedByName = "encrypt"),
		@Mapping(target = "referralEmail", qualifiedByName = "encrypt"),
		@Mapping(target = "referralPhone", qualifiedByName = "encrypt"),
		@Mapping(target = "referralOthersDescription", qualifiedByName = "encrypt"),
		@Mapping(target = "referralLandlineNum", qualifiedByName = "encrypt"),
		@Mapping(target = "coordinatedName", qualifiedByName = "encrypt"),
		@Mapping(target = "coordinatedEmail", qualifiedByName = "encrypt"),
		@Mapping(target = "concernType", qualifiedByName = "encrypt"),
		@Mapping(target = "concernOthersDescription", qualifiedByName = "encrypt"),
		@Mapping(target = "selfHarmType", qualifiedByName = "encrypt"), 
		@Mapping(target = "psychiatricName", qualifiedByName = "encrypt"), 
		@Mapping(target = "additionalDetails", qualifiedByName = "encrypt")
	})
	void toStudentWellnessCategoricalDataEntity(@MappingTarget StudentWellnessCategoricalDataEntity existingEntity,
			StudentWellnessCategoricalDataDto dto);
    
    @Named("decrypt")
    default String decrypt(String value) throws Exception {
        return (value != null) ? MCrypt.getInstance().decryptToString(value) : null;
    }
    
    @Named("encrypt")
    default String encrypt(String value) throws Exception {
        return (value != null) ? MCrypt.getInstance().encryptToText(value) : null;
    }
}