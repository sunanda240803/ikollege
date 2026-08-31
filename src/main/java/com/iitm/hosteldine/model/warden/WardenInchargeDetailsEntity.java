package com.iitm.hosteldine.model.warden;

import java.time.LocalDate;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"WARDEN_INCHARGE_DETAILS\"", schema = ModelConstants.SCHEMA)
public class WardenInchargeDetailsEntity extends CommonEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;

	@Column(name = "warden_id", nullable = false)
	private Integer wardenId;

	@Column(name = "away_from", length = 128)
	private LocalDate awayFrom;

	@Column(name = "away_to", length = 32)
	private LocalDate awayTo;

	@Column(name = "away_description", nullable = false,  length = 64)
	private String awayDescription;

	@Column(name = "incharger_id")
	private Long inchargeId;

}
