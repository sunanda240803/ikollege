package com.iitm.hosteldine.dto.dean;

import java.io.Serializable;

import lombok.Data;

@Data
public class DeanMessRebateWorkflowDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String guideName;
	private String guideMail;
	private String modifiedDate;
	private String approvalLevel;
	private String approvalStatus;
	private String approvalNotes;
	private String rejectionNotes;
}
