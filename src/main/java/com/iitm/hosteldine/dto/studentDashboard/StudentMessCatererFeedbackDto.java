package com.iitm.hosteldine.dto.studentDashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.dto.feedback.FeedbackQualifierDto;
import com.iitm.hosteldine.dto.feedback.FeedbackQuestionDto;

import lombok.Data;
import java.lang.Long;
import java.time.LocalDate;
import java.util.List;
import java.lang.Integer;

@Data
public class StudentMessCatererFeedbackDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("studentId")
    private String studentId;
    @JsonProperty("messMasterId")
    private Long messMasterId;
    @JsonProperty("feedbackQuesId")
    private Integer feedbackQuesId;
    @JsonProperty("feedbackScore")
    private Integer feedbackScore;
    @JsonProperty("messControllerId")
    private Long messControllerId;
    private List<FeedbackQualifierDto> qualifiers;
    private List<FeedbackQuestionDto> questions;
    private String messName;
    private LocalDate fromDate;
    private LocalDate toDate;
    
}