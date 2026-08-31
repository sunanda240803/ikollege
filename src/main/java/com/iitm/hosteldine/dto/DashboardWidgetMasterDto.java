package com.iitm.hosteldine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Long;

@Data
public class DashboardWidgetMasterDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("widgetName")
    private String widgetName;
    @JsonProperty("widgetUrl")
    private String widgetUrl;
    @JsonProperty("widgetDescription")
    private String widgetDescription;
    @JsonProperty("widgetImagePath")
    private String widgetImagePath;
    @JsonProperty("widgetSize")
    private String widgetSize;
    @JsonProperty("orderBy")
    private Integer orderBy;
}