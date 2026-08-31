package com.iitm.hosteldine.model;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "state", schema = ModelConstants.SCHEMA)
public class StateEntity extends CommonEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "state_id", nullable = false)
	int stateId;

	@Column(name = "state_name", nullable = false, length = 64)
	String stateName;

	@Column(name = "country_id")
	private int countryId;

}
