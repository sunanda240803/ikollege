package com.iitm.hosteldine.form.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iitm.hosteldine.dto.staff.StaffDetailsDto;

import lombok.Data;

@Data
public class AddressForm {
	@JsonIgnore
	private Object source = null;
	@JsonIgnore
	private CountrySelectForm countrySelectForm = null;
	private String address;
	private String taluk;
	private String district;
	private String zipcode;

	public AddressForm() {
	}

	public AddressForm(StaffDetailsDto staffDetailsDto) {
		source = staffDetailsDto;
		countrySelectForm = new CountrySelectForm(staffDetailsDto);
	}

	public AddressForm(Object source) {
		this.source = source;
		countrySelectForm = new CountrySelectForm(source);
	}

	public void updateFromForm() {
		if (source != null) {
			if (source instanceof StaffDetailsDto staffDetailsDto) {
				this.getCountrySelectForm().updateFromForm();
				this.setAddress(staffDetailsDto.getAddressOne());
//				this.setTaluk(staffDetailsDto.getTaluk());
//				this.setDistrict(staffDetailsDto.getDistrict());
//				this.setZipcode(staffDetailsDto.getPinCode() != null
//						? String.valueOf(staffDetailsDto.getPinCode())
//						: "");
			}
		}
	}

	public void updateToForm() {
		if (source != null) {
			if (source instanceof StaffDetailsDto staffDetailsDto) {
				this.getCountrySelectForm().updateToForm();
				staffDetailsDto.setAddressOne(this.getAddress());
//				staffDetailsDto.setTaluk(this.getTaluk());
//				staffDetailsDto.setDistrict(this.getDistrict());
//				staffDetailsDto.setPinCode(Integer.valueOf(this.getZipcode("0")));
			}
		}
	}

	private String getZipcode(String defaultValue) {
		if ((zipcode == null || zipcode.isEmpty()) && (defaultValue != null)) {
			return defaultValue;
		}
		return zipcode;
	}

	@Override
	public String toString() {
		return "Address:" + countrySelectForm + ", " + address + ", " + taluk + ", " + district + ", " + zipcode + '\n';
	}

	public void clear() {
		countrySelectForm.clear();
		address = null;
		taluk = null;
		district = null;
		zipcode = null;
	}
}
