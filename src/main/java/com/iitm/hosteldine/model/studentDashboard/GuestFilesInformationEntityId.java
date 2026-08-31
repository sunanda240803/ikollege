package com.iitm.hosteldine.model.studentDashboard;

import java.util.Objects;

import org.hibernate.Hibernate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class GuestFilesInformationEntityId {

	@Column(name = "file_id", nullable = false)
    private Long fileId;
	
	@Column(name = "request_id", nullable = false)
    private Long requestId;
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        GuestFilesInformationEntityId entity = (GuestFilesInformationEntityId) o;
        return  Objects.equals(this.requestId, entity.requestId) &&
                Objects.equals(this.fileId, entity.fileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, fileId);
    }
	
	
}
