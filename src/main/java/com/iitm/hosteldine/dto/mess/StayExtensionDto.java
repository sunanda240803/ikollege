package com.iitm.hosteldine.dto.mess;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StayExtensionDto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String stayFrom;
	private String stayTo;
	private String approvalStatus;

}
