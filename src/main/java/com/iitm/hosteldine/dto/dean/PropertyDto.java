package com.iitm.hosteldine.dto.dean;

import lombok.Data;

@Data
public class PropertyDto implements Cloneable{
	
	private int id;
	private String displayName;
	private String propertyValue;
	private String property;
	private Boolean action;
	private String actionIcon;
	private String actionStyle;
	private String url;
	private Long order;
	private Boolean isNoteRequired = false;

    @Override
    public PropertyDto clone() throws CloneNotSupportedException{
            return (PropertyDto) super.clone();
    }
}
