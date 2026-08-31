package com.iitm.hosteldine.model;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "city", schema = ModelConstants.SCHEMA)
public class CityEntity extends CommonEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "city_id", nullable = false)
	private int cityId;
	
	@Column(name = "city_name", nullable = false, length = 64)
	private String cityName;
	
	@Column(name = "state_id")
	int stateId;

}
