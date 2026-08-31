package com.iitm.hosteldine.dto;

import lombok.Data;

@Data
public class AddressDto {
	
	private int countryId;
	private String countryName;
	private int stateId;
	private String stateName;
	private int cityId;
	private String cityName;

	public AddressDto() {}

	public AddressDto(int stateId, String stateName, int countryId) {
		super();
		this.stateId = stateId;
		this.stateName = stateName;
		this.countryId = countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public void setStateId(int stateId) {
		this.stateId = stateId;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

}
