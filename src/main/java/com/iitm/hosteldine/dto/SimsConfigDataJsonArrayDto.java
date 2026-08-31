package com.iitm.hosteldine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SimsConfigDataJsonArrayDto {

	@JsonProperty("id")
	private String id;
	@JsonProperty("value")
	private String value;

	public SimsConfigDataJsonArrayDto(String id, String value) {
		this.id = id;
		this.value = value;
	}

	public SimsConfigDataJsonArrayDto() {
		// TODO Auto-generated constructor stub
	}
}
