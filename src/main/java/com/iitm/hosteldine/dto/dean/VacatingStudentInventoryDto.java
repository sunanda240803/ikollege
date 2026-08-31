package com.iitm.hosteldine.dto.dean;

import java.io.Serializable;

import lombok.Data;

@Data
public class VacatingStudentInventoryDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long assetId; 
	private String assetName;
	private String assetCategory;
	private String assetCode;
	private String assetCondition;
	private Long penaltyAmount;
	private String penaltyReason;
	
}
