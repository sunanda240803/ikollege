package com.iitm.hosteldine.model.hostel;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class CatererLedgerMappingId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "acchead", nullable = false, length = 60)
	private String acchead;

	@Column(name = "caterer_name", nullable = false, length = 60)
	private String catererName;

}
