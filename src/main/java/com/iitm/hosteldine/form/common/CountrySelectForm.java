package com.iitm.hosteldine.form.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iitm.hosteldine.dto.staff.StaffDetailsDto;

import lombok.Data;

@Data
public class CountrySelectForm {
	private InputSettings countrySelect = new InputSettings();
	private InputSettings stateSelect = new InputSettings();
	private InputSettings citySelect = new InputSettings();
	@JsonIgnore
	private Object source = null;

	public CountrySelectForm() {
	}

	public CountrySelectForm(Object source) {
		this.source = source;
	}

	public CountrySelectForm setCountrySelect(InputSettings countrySelect) {
		this.countrySelect = countrySelect;
		return this;
	}

	public CountrySelectForm setStateSelect(InputSettings stateSelect) {
		this.stateSelect = stateSelect;
		return this;
	}

	public CountrySelectForm setCitySelect(InputSettings citySelect) {
		this.citySelect = citySelect;
		return this;
	}

	public void updateFromForm() {
		if (source != null) {
			if (source instanceof StaffDetailsDto dto) {
//				this.getCountrySelect().setValue(String.valueOf(dto.getCountry()));
//				this.getStateSelect().setValue(String.valueOf(dto.getState()));
//				this.getCitySelect().setValue(String.valueOf(dto.getCity()));
			}
		}
	}

	public void updateToForm() {
		if (source != null) {
			if (source instanceof StaffDetailsDto dto) {
//				dto.setCountry(this.getCountrySelect().getValue("0"));
//				dto.setState(this.getStateSelect().getValue("0"));
//				dto.setCity(this.getCitySelect().getValue("0"));
			}
		}
	}

	@Override
	public String toString() {
		return "Country: " + countrySelect + ", State: " + stateSelect + ", City: " + citySelect;
	}

	public void clear() {
		countrySelect.setValue(null);
		stateSelect.setValue(null);
		citySelect.setValue(null);
	}
}
