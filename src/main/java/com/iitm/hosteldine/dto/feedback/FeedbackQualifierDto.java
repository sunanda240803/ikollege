package com.iitm.hosteldine.dto.feedback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Integer;

@Data
public class FeedbackQualifierDto {
    @JsonProperty("feedbackId")
    private Integer feedbackId;
    @JsonProperty("feedbackDesc")
    private String feedbackDesc;
    @JsonProperty("feedbackPoints")
    private Integer feedbackPoints;
}