package com.iitm.hosteldine.model.hostel;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"CATERER_LEDGER_MAPPING\"", schema = ModelConstants.SCHEMA)
public class CatererLedgerMappingEntity extends CommonEntity {

	@EmbeddedId
	private CatererLedgerMappingId id;

	@Column(name = "fin_year", nullable = false, length = 16)
	private String finYear;
}
