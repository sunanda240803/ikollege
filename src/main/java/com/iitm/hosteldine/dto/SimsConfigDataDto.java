package com.iitm.hosteldine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.validator.fieldValidators.ValidStringField;

import lombok.Data;

@Data
public class SimsConfigDataDto {
    @JsonProperty("id")
    private Long id;
    @ValidStringField(message = "message.validation.config.key.required",fieldName = "message.label.config.key")
    private String configKey;
    @ValidStringField(message = "message.validation.config.value.required",fieldName = "message.label.config.value")
    private String configValue;
    private String description;
   
}