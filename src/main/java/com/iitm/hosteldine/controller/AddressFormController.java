package com.iitm.hosteldine.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.iitm.hosteldine.dto.CityDto;
import com.iitm.hosteldine.dto.CountryDto;
import com.iitm.hosteldine.dto.StateDto;
import com.iitm.hosteldine.service.AddressFormService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "${url.address.form}")
public class AddressFormController {

	private  final AddressFormService addressFormService;

	@GetMapping("${url.country.list}")
	
	public  List<CountryDto> getCountryList() throws Exception {
		return addressFormService.getCountryList();
	}

	@GetMapping("${url.state.list}" + "${id}")
	public List<StateDto> getStateList(@PathVariable Integer id, ModelMap map, HttpServletRequest request)
			throws Exception {
		return addressFormService.getStateList(id);
	}

	@GetMapping("${url.city.list}" + "${id}")
	public List<CityDto> getCityList(@PathVariable Integer id, ModelMap map, HttpServletRequest request)
			throws Exception {
		return addressFormService.getCityList(id);
	}

}
