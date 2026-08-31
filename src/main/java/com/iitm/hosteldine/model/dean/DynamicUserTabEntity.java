package com.iitm.hosteldine.model.dean;

import org.hibernate.annotations.Immutable;

import com.iitm.hosteldine.constant.ModelConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Immutable
@Table(name = "\"DYNAMIC_USER_TABS_DISPLAYABLE_VIEW\"", schema = ModelConstants.SCHEMA)
public class DynamicUserTabEntity{

	@Id
	@Column(name = "id")
	private long id;
	
	@Column(name = "l1_id")
	private long tabId;

	 @Column(name = "role")
	 private String role;
	 
	 @Column(name = "show_hide")
	 private Boolean showHide;
	 
	 
	 @Column(name = "l1_type")
	 private String tabtype;
	 
	 @Column(name = "l1_name")
	 private String tabName;
	 
	 @Column(name = "l1_property")
	 private String tabProperty;
	 
	 @Column(name = "l1_order")
	 private long tabOrder;
	 
	 @Column(name = "l1_url")
	 private String tabUrl;
	 
	 @Column(name = "l2_id")
	 private long columnId;
	 
	 @Column(name = "l2_type")
	 private String columnType;
	 
	 @Column(name = "l2_name")
	 private String columnName;
	 
	 @Column(name = "l2_property")
	 private String columnProperty;

	 @Column(name = "l2_order")
	 private long columnOrder;
	 
	 @Column(name = "icon")
	 private String icon;
	 
	 @Column(name = "url")
	 private String url;
	 
	 @Column(name = "sort")
	 private Boolean sort;
	 
	 @Column(name = "mandatory")
	 private Boolean mandatory;
	 
	 @Column(name = "action")
	 private String action;
	 
	 @Column(name = "user_id")
	 private String userId;
	 

}
