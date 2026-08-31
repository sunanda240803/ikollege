package com.iitm.hosteldine.dto.feedback;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.validator.fieldValidators.ValidStringField;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.lang.Integer;

@Data
public class FeedbackQuestionDto {
    private Long feedbackQuesId;
    @ValidStringField(message = "message.validation.description.required",fieldName = "message.label.description",min = 3,max =80)
    private String feedbackQuesDesc;
    private Integer feedbackWeightage;
    private Integer selectedQualifier;
}