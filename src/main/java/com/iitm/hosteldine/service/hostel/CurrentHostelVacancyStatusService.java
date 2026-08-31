package com.iitm.hosteldine.service.hostel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.dto.hostel.HostelRoomInfoDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.repository.hostel.HostelRoomInfoRepository;
import com.iitm.hosteldine.validator.common.ValidationCommon;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrentHostelVacancyStatusService {

	private final HostelRoomInfoRepository repository;

	public Page<HostelRoomInfoDto> getCurrentHostelVacancyStatusList(PaginationForm form) {
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize());
		Page<Object[]> entityList = repository.getCurrentHostelVacancyStatusList(pageable);
		return entityList.map(record -> {
			try {
				HostelRoomInfoDto dto = new HostelRoomInfoDto();
				dto.setHostelId(ValidationCommon.toLongOrZero(record[0]));
				dto.setHostelName(ValidationCommon.getStringValueOrHyphen(record[1]));
				dto.setTotalCapacity(ValidationCommon.toLongOrZero(record[2]));
				dto.setOccupied(ValidationCommon.toLongOrZero(record[3]).intValue());
				dto.setTotalVacantSeats(ValidationCommon.toLongOrZero(record[4]).intValue());
				dto.setVacCapacity(ValidationCommon.toLongOrZero(record[5]).intValue());
				return dto;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}

}
