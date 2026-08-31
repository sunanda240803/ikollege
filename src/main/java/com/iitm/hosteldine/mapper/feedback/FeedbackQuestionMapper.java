package com.iitm.hosteldine.mapper.feedback;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.dto.SimsConfigDataDto;
import com.iitm.hosteldine.dto.feedback.FeedbackQuestionDto;
import com.iitm.hosteldine.model.SimsConfigDataEntity;
import com.iitm.hosteldine.model.feedback.FeedbackQuestionEntity;

@Mapper
public interface FeedbackQuestionMapper {
    FeedbackQuestionMapper INSTANCE = Mappers.getMapper(FeedbackQuestionMapper.class);

    @Mapping(target = ".", source = ".")
    FeedbackQuestionDto fromFeedbackQuestionEntity(FeedbackQuestionEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    FeedbackQuestionEntity toFeedbackQuestionEntity(FeedbackQuestionDto modelDto);
    
    @Mapping(target = ".", source = ".")
  	void onUpdateEntity(@MappingTarget FeedbackQuestionEntity existingEntity, FeedbackQuestionDto dto);

    FeedbackQuestionEntity onSaveEntity(FeedbackQuestionDto dto);
}