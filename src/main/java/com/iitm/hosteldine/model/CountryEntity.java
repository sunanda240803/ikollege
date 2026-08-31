package com.iitm.hosteldine.model;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "country", schema = ModelConstants.SCHEMA)
public class CountryEntity extends CommonEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "country_id", nullable = false)
	private int countryId;
	
	@Column(name = "country_name", nullable = false, length = 64)
	private String countryName;
}
