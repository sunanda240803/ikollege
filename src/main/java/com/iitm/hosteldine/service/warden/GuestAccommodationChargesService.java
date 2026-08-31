package com.iitm.hosteldine.service.warden;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.warden.GuestAccommodationChargesDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.warden.GuestAccommodationChargesMapper;
import com.iitm.hosteldine.model.warden.GuestAccommodationChargesEntity;
import com.iitm.hosteldine.repository.warden.GuestAccommodationChargesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuestAccommodationChargesService {
	private final GuestAccommodationChargesRepository guestAccommodationChargesRepository;

	public Page<GuestAccommodationChargesDto> getGuestAccommodationList(PaginationForm form) {
		Page<GuestAccommodationChargesEntity> result = null;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("createdAt").descending());
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			result = guestAccommodationChargesRepository.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE, pageable);
		}
		return result.map(GuestAccommodationChargesMapper.INSTANCE::fromGuestAccommodationChargesEntity);
	}

	public String saveUpdateGuestAccommodationCharge(GuestAccommodationChargesDto dto) {
		String result = Optional.ofNullable(dto.getId()).filter(id -> (id != null && id > 0)).flatMap(
				id -> guestAccommodationChargesRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					GuestAccommodationChargesMapper.INSTANCE.onUpdateEntity(existingEntity, dto);
					guestAccommodationChargesRepository.save(existingEntity);
					return Constants.UPDATED;
				}).orElseGet(() -> {
					GuestAccommodationChargesEntity newEntity = GuestAccommodationChargesMapper.INSTANCE.onSaveEntity(dto);
					guestAccommodationChargesRepository.save(newEntity);
					return Constants.SAVED;
				});
		return result;
	}

	public GuestAccommodationChargesDto getGuestAccommodationDetailsById(long id) {
		return guestAccommodationChargesRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
				.map(GuestAccommodationChargesMapper.INSTANCE::fromGuestAccommodationChargesEntity)
				.orElse(new GuestAccommodationChargesDto());
	}

	public GuestAccommodationChargesDto getGuestAccommodationChargeDetails() {
		return guestAccommodationChargesRepository.getAccommodationCharges(ModelConstants.STATUS_ACTIVE)
				.map(GuestAccommodationChargesMapper.INSTANCE::fromGuestAccommodationChargesEntity)
				.orElse(new GuestAccommodationChargesDto());
	}

	public GuestAccommodationChargesEntity getGuestAccommodationChargesDate(LocalDate fromDate, LocalDate toDate,
			String statusActive) {
		return guestAccommodationChargesRepository.findGuestAccommodationChargesDate(fromDate, toDate, statusActive);
	}

	
}
