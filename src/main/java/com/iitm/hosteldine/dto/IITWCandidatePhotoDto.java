package com.iitm.hosteldine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Integer;
import java.time.LocalDateTime;

@Data
public class IITWCandidatePhotoDto {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("image")
    private byte[] image;
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
    @JsonProperty("schoolId")
    private Integer schoolId;
}