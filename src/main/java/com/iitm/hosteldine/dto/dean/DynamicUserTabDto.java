package com.iitm.hosteldine.dto.dean;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class DynamicUserTabDto {
    private String role;
    private boolean showHide;
    private long tabId;
    private String tabtype;
    private String tabName;
    private String tabProperty;
    private long tabOrder;
    private String tabUrl;
    private long columnId;
    private String columnType;
    private String columnName;
    private String columnProperty;
    private long columnOrder;
    private String icon;
    private String url;
    private boolean sort;
    private boolean mandatory;
    private String action;
    private String userId;
}