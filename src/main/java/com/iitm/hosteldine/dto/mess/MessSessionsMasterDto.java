package com.iitm.hosteldine.dto.mess;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessSessionsMasterDto {
	@JsonProperty("sessionCode")
    private String sessionCode;
    @JsonProperty("sessionName")
    private String sessionName;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    @JsonProperty("modifiedBy")
    private String modifiedBy;
    @JsonProperty("modifiedAt")
    private LocalDateTime modifiedAt;
    @JsonProperty("activeFlag")
    private String activeFlag;
}