package com.iitm.hosteldine.service.hostel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iitm.hosteldine.FinancialYearMapper;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.financialYear.FinancialYearDto;
import com.iitm.hosteldine.dto.hostel.AccountHeadDto;
import com.iitm.hosteldine.dto.hostel.CatererLedgerMappingDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.hostel.CatererLedgerMappingEntity;
import com.iitm.hosteldine.model.hostel.CatererLedgerMappingId;
import com.iitm.hosteldine.repository.financialYear.FinancialYearRepository;
import com.iitm.hosteldine.repository.hostel.CatererLedgerMappingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CatererLedgerMappingService {

	private final CatererLedgerMappingRepository catererLedgerMappingRepository;
    private final FinancialYearRepository financialYearRepository;

	public Page<CatererLedgerMappingDto> getCatererLedgerMappingList(PaginationForm form) {
		Page<Object[]> result = null;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("c.modifiedAt").descending());
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			result = catererLedgerMappingRepository.getCatererList(ModelConstants.STATUS_ACTIVE, pageable);
		} else {
			result = catererLedgerMappingRepository.getCatererListBySearch(ModelConstants.STATUS_ACTIVE, pageable,
					form.getSearch());
		}
		return result.map(record -> {
			CatererLedgerMappingDto dto = new CatererLedgerMappingDto();
			dto.setCatererName(String.valueOf(record[0]));
			dto.setAccHead(String.valueOf(record[1]));
			dto.setAccname(String.valueOf(record[2]));
			return dto;

		});
	}
	
	@Transactional
	public String saveAndUpdate(CatererLedgerMappingDto dto) {
		if (dto.getUserName() == null || dto.getCatererAccountHeadList() == null) {
			return null;
		}

		FinancialYearDto finYearDto = Optional
				.ofNullable(financialYearRepository.getFinYearDetails(Constants.CREDIT, ModelConstants.STATUS_ACTIVE))
				.map(FinancialYearMapper.INSTANCE::toDto).orElse(null);

		if (finYearDto == null) {
			return null;
		}

		List<CatererLedgerMappingEntity> entityList = dto.getCatererAccountHeadList().stream()
				.filter(AccountHeadDto::getStatus)
				.map(accountHeadDto -> mapToEntity(accountHeadDto, dto.getUserName(), finYearDto)).toList();

		if (!entityList.isEmpty()) {
			catererLedgerMappingRepository.saveAll(entityList);
			return Constants.SAVED;
		}
		return null;
	}

	private CatererLedgerMappingEntity mapToEntity(AccountHeadDto accountHeadDto,
			String username, FinancialYearDto finYearDto) {
		return catererLedgerMappingRepository.findByIdAccheadAndIdCatererName(accountHeadDto.getId().getAcchead(),
				username).map(existingEntity -> {
					existingEntity.setActiveFlag(ModelConstants.STATUS_ACTIVE);
					existingEntity.onUpdate();
					return existingEntity;
				}).orElseGet(() -> {
					CatererLedgerMappingEntity newEntity = new CatererLedgerMappingEntity();
					CatererLedgerMappingId catererMappingId = new CatererLedgerMappingId();
					catererMappingId.setAcchead(accountHeadDto.getId().getAcchead());
					catererMappingId.setCatererName(username);
					newEntity.setFinYear(finYearDto.getFinYear());
					newEntity.setId(catererMappingId);
					newEntity.onCreate();
					return newEntity;
				});
	}

	public boolean deleteMappingById(CatererLedgerMappingDto dto) {
		return catererLedgerMappingRepository.findByIdAccheadAndIdCatererName(dto.getAccHead(), dto.getCatererName())
				.map(entity -> {
					entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
					entity.onUpdate();
					catererLedgerMappingRepository.save(entity);
					return true;
				}).orElse(false);
	}

}
