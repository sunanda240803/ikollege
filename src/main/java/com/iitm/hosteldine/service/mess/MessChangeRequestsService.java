package com.iitm.hosteldine.service.mess;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.dean.FilterCriteriaDto;
import com.iitm.hosteldine.dto.mess.MessAllottedListDTO;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.repository.mess.StudentMessChangeWorkflowRepository;
import com.iitm.hosteldine.validator.common.ValidationCommon;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessChangeRequestsService {

	private final StudentMessChangeWorkflowRepository repository;
	private final MessMasterCommonService messMasterCommonService;

	public Page<MessAllottedListDTO> getMessChangeRequestsList(PaginationForm form) {
		Page<Object[]> entityList = fetchMessChangeRequestsList(form);
		return entityList.map(record -> {
			try {
				MessAllottedListDTO dto = new MessAllottedListDTO();

				dto.setId(record[0] != null ? Long.parseLong(record[0].toString()) : null);
				dto.setStudentId(ValidationCommon.getStringValueOrHyphen(record[1]));
				dto.setStudentName(ValidationCommon.getStringValueOrHyphen(record[2]));
				dto.setRequestedMessHead(ValidationCommon.getStringValueOrHyphen(record[6]));
				dto.setMessHead(ValidationCommon.getStringValueOrHyphen(record[7]));
				dto.setDescription(ValidationCommon.getStringValueOrHyphen(record[8]));
				dto.setApprovalStatus(ValidationCommon.getStringValueOrHyphen(record[9]));
				dto.setEffectiveFromDate(record[5] != null ? LocalDate.parse(record[5].toString()) : null);
				return dto;

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	private Page<Object[]> fetchMessChangeRequestsList(PaginationForm form) {
		FilterCriteriaDto filter = getFilterData(form);
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize());
		Long messPeriod = filter.getMessPeriod();
		if (messPeriod != null && messPeriod != 0) {
		    if (form.getSearch() == null || form.getSearch().isEmpty()) {
		        return repository.getMessChangeRequestsList(ModelConstants.STATUS_ACTIVE, messPeriod, pageable);
		    } else {
		        return repository.getMessChangeRequestsListByFormSearch(ModelConstants.STATUS_ACTIVE, messPeriod, pageable, form.getSearch());
		    }
		}

		MessMasterControllerDto mmcDto = messMasterCommonService.getCurrentMessPeriod();
		Long mmcId = mmcDto != null ? mmcDto.getId() : null;

		if (form.getSearch() != null && !form.getSearch().isEmpty()) {
		    return repository.getMessChangeRequestsListByFormSearch(ModelConstants.STATUS_ACTIVE, mmcId, pageable, form.getSearch());
		}
		return repository.getMessChangeRequestsList(ModelConstants.STATUS_ACTIVE, mmcId, pageable);
	}

	public FilterCriteriaDto getFilterData(PaginationForm form) {
		FilterCriteriaDto filter = new FilterCriteriaDto();
		filter.setMessPeriod(ValidationCommon.toLongOrZero(form.getAdditionalParam().get("messPeriod")));
		return filter;
	}

}
