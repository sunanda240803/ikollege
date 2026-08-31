package com.iitm.hosteldine.service.warden;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.dto.warden.WardenAwayDetailsDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.repository.warden.WardenInchargeDetailsRepository;
import com.iitm.hosteldine.util.Utility;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WardenAwayDetailsService {
	
	private final WardenInchargeDetailsRepository wardenInchargeDetailsRepository;
	private final Utility utility;
	
	public Page<WardenAwayDetailsDto> getWardenAwayRequestList(PaginationForm form) {
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize());
		Page<Object[]> result = Page.empty();
		result = wardenInchargeDetailsRepository.getWardenAwayRequestList(pageable);
		return result.map(objects -> {
			return setWardenAwayDetailsDto(objects);
		});
	}

	private WardenAwayDetailsDto setWardenAwayDetailsDto(Object[] objects) {
		WardenAwayDetailsDto dto = new WardenAwayDetailsDto();
		dto.setId(utility.parseLong(objects[0]));
		dto.setWardenId(utility.parseLong(objects[1]));
		dto.setHostelId(utility.parseLong(objects[2]));
		dto.setHostelName(utility.parseString(objects[3]));
		dto.setAwayFrom(utility.convertToLocalDate(objects[4]));
		dto.setAwayTo(utility.convertToLocalDate(objects[5]));
		dto.setAwayDescription(utility.parseString(objects[6]));
		dto.setInchargerId(utility.parseLong(objects[7]));
		dto.setOfficeNo(utility.parseString(objects[8]));
		dto.setWardenName(utility.parseString(objects[9]));
		dto.setPhoneNumber(utility.parseString(objects[10]));
		dto.setWardenEmail(utility.parseString(objects[11]));
		dto.setAlternateWardenName(utility.parseString(objects[12]));
		dto.setAlternatePhoneNumber(utility.parseString(objects[13]));
		dto.setAlternateWardenEmail(utility.parseString(objects[14]));
		return dto;
	}
	
	public Page<WardenAwayDetailsDto> getHostelWardenDetails(PaginationForm form) {
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize());
		Page<Object[]> result = Page.empty();
		result = wardenInchargeDetailsRepository.getHostelWardenDetails(pageable);
		return result.map(objects -> {
			return setHostelWardenDetailsDto(objects);
		});
	}

	private WardenAwayDetailsDto setHostelWardenDetailsDto(Object[] objects) {
		WardenAwayDetailsDto dto = new WardenAwayDetailsDto();
		dto.setHostelName(utility.parseString(objects[0]));
		dto.setWardenName(utility.parseString(objects[2]));
		dto.setWardenEmail(utility.parseString(objects[3]));
		dto.setOfficeNo(utility.parseString(objects[5]));
		dto.setPhoneNumber(utility.parseString(objects[6]));
		dto.setAlternateWardenName(utility.parseString(objects[8]));
		dto.setAlternateWardenEmail(utility.parseString(objects[9]));
		dto.setAlternatePhoneNumber(utility.parseString(objects[10]));		
		return dto;
	}
	
	
}
