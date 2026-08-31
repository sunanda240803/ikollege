package com.iitm.hosteldine.mapper.feedback;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.feedback.FeedbackQualifierDto;
import com.iitm.hosteldine.model.feedback.FeedbackQualifierEntity;

@Mapper
public interface FeedbackQualifierMapper {
    FeedbackQualifierMapper INSTANCE = Mappers.getMapper(FeedbackQualifierMapper.class);

    @Mapping(target = ".", source = ".")
    FeedbackQualifierDto fromFeedbackQualifierEntity(FeedbackQualifierEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    FeedbackQualifierEntity toFeedbackQualifierEntity(FeedbackQualifierDto modelDto);
}