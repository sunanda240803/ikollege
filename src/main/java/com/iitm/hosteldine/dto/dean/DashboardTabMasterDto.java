package com.iitm.hosteldine.dto.dean;

import lombok.Data;
import lombok.Builder;
import java.lang.Long;
import java.lang.Boolean;

@Data
@Builder
public class DashboardTabMasterDto {
    private Long id;
    private Long parentId;
    private String tabType;
    private String tabName;
    private String property;
    private String icon;
    private String url;
    private Boolean sort;
    private Boolean mandatory;
    private String action;
    private Long orderBy;
    private String tabUrl;
    private String style;
}