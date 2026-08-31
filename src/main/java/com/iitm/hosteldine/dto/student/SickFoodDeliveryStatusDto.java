package com.iitm.hosteldine.dto.student;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SickFoodDeliveryStatusDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("sickFoodRequest")
    private SickFoodRequestDto sickFoodRequest;
    @JsonProperty("messSession")
    private String messSession;
    @JsonProperty("catererStatus")
    private String catererStatus;
    @JsonProperty("studentDeliveryStatus")
    private String studentDeliveryStatus;
    @JsonProperty("studentFeedback")
    private String studentFeedback;
    @JsonProperty("feedbackRating")
    private String feedbackRating;
    private boolean bfThreshold;
  	private boolean lcThreshold;
  	private boolean dnThreshold;
  	private Boolean foodDeliveryStatus;
  	private String startTime;
  	private String endTime;
  	private String sessionName;
}