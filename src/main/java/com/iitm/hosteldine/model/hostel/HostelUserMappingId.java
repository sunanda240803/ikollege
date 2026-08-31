package com.iitm.hosteldine.model.hostel;

import com.iitm.hosteldine.entity.UserManagementEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class HostelUserMappingId {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "hostel_id", nullable = false)
	private HostelMasterEntity hostel;

	//@ManyToOne
	/*
	 * @JoinColumns({ @JoinColumn(name = "user_name", referencedColumnName =
	 * "user_name"),
	 * 
	 * @JoinColumn(name = "id", referencedColumnName = "user_id") })
	 */
	@Column(name = "user_name")
	private String user;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		HostelUserMappingId that = (HostelUserMappingId) o;
		return Objects.equals(hostel, that.hostel) && Objects.equals(user, that.user);
	}

	@Override
	public int hashCode() {
		return Objects.hash(hostel, user);
	}
}
