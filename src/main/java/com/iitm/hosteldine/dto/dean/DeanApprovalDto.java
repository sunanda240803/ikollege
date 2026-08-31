package com.iitm.hosteldine.dto.dean;

import java.util.List;

import lombok.Data;

@Data
public class DeanApprovalDto {

	private List<PropertyDto> propertyList;
	private PropertyDto property;
	private List<PropertyDto> actionList;
	private List<PropertyDto> actionUrlList;
	private String data;
}
