package com.iitm.hosteldine.model.mess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class StudentMessLoginIssuePriorityId {

	@Column(name = "mmc_id", nullable = false)
	private int mmcNId;

	@Column(name = "student_id", nullable = false, length = 32)
	private String studentId;

	@Column(name = "priority_mess_id", nullable = false)
	private int priorityMessId;
	
	@Column(name = "priority_order", nullable = false)
    private int priorityOrder;
}
