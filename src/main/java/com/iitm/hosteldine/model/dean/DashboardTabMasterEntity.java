package com.iitm.hosteldine.model.dean;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"IIT_WD_DASHBOARD_TAB_MASTER\"", schema = ModelConstants.SCHEMA)
public class DashboardTabMasterEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "tab_type", length = 16)
    private String tabType;

    @Column(name = "tab_name", length = 32)
    private String tabName;

    @Column(name = "property", length = 32)
    private String property;

    @Column(name = "icon", length = 256)
    private String icon;

    @Column(name = "url")
    private String url;

    @Column(name = "sort")
    private Boolean sort;

    @Column(name = "mandatory")
    private Boolean mandatory;

    @Column(name = "action", length = 64)
    private String action;

    @Column(name = "order_by")
    private Long orderBy;

    @Column(name = "tab_url")
    private String tabUrl;

    @Column(name = "style")
    private String style;

}
