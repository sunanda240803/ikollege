package com.iitm.hosteldine.entity.mess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class AccomodationLedgerBEntityId implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Size(max = 12)
	@NotNull
	@Column(name = "voucher_no", nullable = false, length = 12)
	private String voucherNo;

	@Size(max = 16)
	@NotNull
	@Column(name = "fin_year", nullable = false, length = 16)
	private String finYear;

	@NotNull
	@Column(name = "slno")
	private int slNo;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
			return false;
		AccomodationLedgerBEntityId entity = (AccomodationLedgerBEntityId) o;
		return Objects.equals(this.voucherNo, entity.voucherNo) && 
				Objects.equals(this.finYear, entity.finYear) && 
				Objects.equals(this.finYear, entity.slNo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(voucherNo, finYear, slNo);
	}

}