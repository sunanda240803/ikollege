package com.iitm.hosteldine.model.mess;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Embeddable
@EqualsAndHashCode
public class StudentMessPriorityRegistrationId implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Column(name = "mmc_n_id", nullable = false)
	private int mmcNId;

	@Column(name = "studentid", nullable = false, length = 32)
	private String studentId;

	@Column(name = "priority_messid", nullable = false)
	private int priorityMessId;
	
	@Column(name = "priority_order", nullable = false)
    private int priorityOrder;
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentMessPriorityRegistrationId that = (StudentMessPriorityRegistrationId) o;
        return mmcNId == that.mmcNId &&
               priorityMessId == that.priorityMessId &&
               priorityOrder == that.priorityOrder &&
               studentId.equals(that.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mmcNId, studentId, priorityMessId, priorityOrder);
    }

}
