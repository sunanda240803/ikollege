package com.iitm.hosteldine.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.CityDto;
import com.iitm.hosteldine.dto.CountryDto;
import com.iitm.hosteldine.dto.StateDto;
import com.iitm.hosteldine.mapper.CityMapper;
import com.iitm.hosteldine.mapper.CountryMapper;
import com.iitm.hosteldine.mapper.StateMapper;
import com.iitm.hosteldine.model.CityEntity;
import com.iitm.hosteldine.model.CountryEntity;
import com.iitm.hosteldine.model.StateEntity;
import com.iitm.hosteldine.repository.CityRepository;
import com.iitm.hosteldine.repository.CountryRepository;
import com.iitm.hosteldine.repository.StateRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class AddressFormService {
	private final CountryRepository countryRepository;
	private final StateRepository stateRepository;
	private final CityRepository cityRepository;

	public List<CountryDto> getCountryList() {
		List<CountryEntity> countryList = countryRepository
				.findAllByActiveFlagOrderByCountryName(ModelConstants.STATUS_ACTIVE);
		List<CountryDto> result = null;
		if (!CollectionUtils.isEmpty(countryList)) {
			List<CountryDto> countryListDto = new ArrayList<>();
			for (CountryEntity country : countryList) {
				countryListDto.add(CountryMapper.INSTANCE.fromCountryEntity(country));
			}
			result = countryListDto;
		}
		return result;
	}

	public List<StateDto> getStateList(Integer countryId) {
		List<StateEntity> stateList = stateRepository
				.findAllByActiveFlagAndCountryIdOrderByStateName(ModelConstants.STATUS_ACTIVE, countryId);
		List<StateDto> result = null;
		if (!CollectionUtils.isEmpty(stateList)) {
			List<StateDto> stateListDto = new ArrayList<>();
			for (StateEntity state : stateList) {
				stateListDto.add(StateMapper.INSTANCE.fromStateEntity(state));
			}
			result = stateListDto;
		}
		return result;
	}

	public List<CityDto> getCityList(Integer stateId) {
		List<CityEntity> cityList = cityRepository.findAllByActiveFlagAndStateIdOrderByCityName(ModelConstants.STATUS_ACTIVE, stateId);
		List<CityDto> result = null;
		if (!CollectionUtils.isEmpty(cityList)) {
			List<CityDto> stateListDto = new ArrayList<>();
			for (CityEntity city : cityList) {
				stateListDto.add(CityMapper.INSTANCE.fromCityEntity(city));
			}
			result = stateListDto;
		}
		return result;
	}
}
