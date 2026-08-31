package com.iitm.hosteldine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.model.DashboardWidgetMasterEntity;
import com.iitm.hosteldine.model.MenuListEntity;

import lombok.Data;
import java.lang.Long;

@Data
public class DashboardWidgetPrivilegeDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("dashboardMenu")
    private MenuListEntity dashboardMenu;
    @JsonProperty("widget")
    private DashboardWidgetMasterEntity widget;
}