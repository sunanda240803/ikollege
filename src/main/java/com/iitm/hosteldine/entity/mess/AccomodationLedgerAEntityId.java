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
public class AccomodationLedgerAEntityId implements Serializable {

	private static final long serialVersionUID = 4821495913259936919L;
	
	@Size(max = 12)
	@NotNull
	@Column(name = "voucher_no", nullable = false, length = 12)
	private String voucherNo;

	@Size(max = 16)
	@NotNull
	@Column(name = "fin_year", nullable = false, length = 16)
	private String finYear;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
			return false;
		AccomodationLedgerAEntityId entity = (AccomodationLedgerAEntityId) o;
		return Objects.equals(this.voucherNo, entity.voucherNo) && 
				Objects.equals(this.finYear, entity.finYear);
	}

	@Override
	public int hashCode() {
		return Objects.hash(voucherNo, finYear);
	}

}