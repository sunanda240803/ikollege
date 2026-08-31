package com.iitm.hosteldine.dto.mess;

import com.iitm.hosteldine.entity.CommonEntity;

import lombok.Data;

@Data
public class MessSessionDto extends CommonEntity {

	MessSessionIdDto id;

	// private MessSessionsMasterDto session;

	// private MessMasterDto messMaster;

	private String startTime;

	private String endTime;
	
	private String messName;
	
	private String sessionCode;

}