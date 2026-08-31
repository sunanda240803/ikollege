package com.iitm.hosteldine.model;

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
@Table(name = "dashboard_widget_master", schema = ModelConstants.SCHEMA)
public class DashboardWidgetMasterEntity extends CommonEntity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // @NotNull
    @Column(name = "widget_name", nullable = false)
    private String widgetName;

    // @NotNull
    @Column(name = "widget_url", nullable = false)
    private String widgetUrl;

    @Column(name = "widget_description")
    private String widgetDescription;

    @Column(name = "widget_image_path")
    private String widgetImagePath;
    
    //@NotNull
    @Column(name = "widget_size", nullable = false)
    private String widgetSize;
    
    //@NotNull
    @Column(name = "order_by", nullable = false)
    private int orderBy;

}
