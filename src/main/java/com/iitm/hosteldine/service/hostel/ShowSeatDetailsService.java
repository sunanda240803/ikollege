package com.iitm.hosteldine.service.hostel;

import java.util.List;
import java.util.Optional;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.repository.student.ShowStudentDetailRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.ShowSeatDetailsDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.hostel.ShowMasterMapper;
import com.iitm.hosteldine.mapper.hostel.ShowSeatDetailsMapper;
import com.iitm.hosteldine.model.hostel.ShowMasterEntity;
import com.iitm.hosteldine.model.hostel.ShowSeatDetailsEntity;
import com.iitm.hosteldine.repository.hostel.ShowSeatDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;

@Service
@RequiredArgsConstructor
public class ShowSeatDetailsService {
	private final ShowSeatDetailsRepository showSeatDetailsRepository;
	private final ShowStudentDetailRepository showStudentDetailRepository;

	public Page<ShowSeatDetailsDto> getSeatDetailsById(Long id,PaginationForm form) {
		Page<ShowSeatDetailsEntity> result = null ;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("modifiedAt").ascending());
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			result = showSeatDetailsRepository.getSeatListByShowId(ModelConstants.STATUS_ACTIVE, pageable,id);
		}
		return result.map(ShowSeatDetailsMapper.INSTANCE::fromShowSeatDetailsEntity);
	}

	public ShowSeatDetailsDto getShowSeatDetailsById(long id) {
		return showSeatDetailsRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
				.map(ShowSeatDetailsMapper.INSTANCE::fromShowSeatDetailsEntity)
				.orElse(new ShowSeatDetailsDto());
	}

	public ShowSeatDetailsDto saveUpdateShowSeat(ShowSeatDetailsDto dto) {
		ShowSeatDetailsEntity[] afterSave = new ShowSeatDetailsEntity[1];
		if (dto.getCurrentlyActive()!=null && dto.getCurrentlyActive().equalsIgnoreCase("on")) {
			dto.setCurrentlyActive(ModelConstants.STATUS_ACTIVE);
		} else {
			dto.setCurrentlyActive(ModelConstants.STATUS_INACTIVE);
		}
		Optional.ofNullable(dto.getId()).filter(id -> (id != null && id > 0))
				.flatMap(id -> showSeatDetailsRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					ShowSeatDetailsMapper.INSTANCE.onUpdateEntity(existingEntity, dto);
					if(dto.getIsAvailable() == null) {
						existingEntity.setIsAvailable(false);
					}
					afterSave[0] = showSeatDetailsRepository.save(existingEntity);
					return Constants.UPDATED;
				}).orElseGet(() -> {
					ShowSeatDetailsEntity newEntity = ShowSeatDetailsMapper.INSTANCE.onSaveEntity(dto);
					if(dto.getIsAvailable() == null) {
						newEntity.setIsAvailable(false);
					}
					afterSave[0] = showSeatDetailsRepository.save(newEntity);
					return Constants.SAVED;
				});
		//return result;
		return ShowSeatDetailsMapper.INSTANCE.fromShowSeatDetailsEntity(afterSave[0]);
	}

	public Page<ShowSeatDetailsDto> getSeatDetailsById(PaginationForm form) {
		Pageable pageable = PageRequest.of(form.getPage() - 1, form.getSize(), Sort.by("createdAt").descending());
		if (!form.getAdditionalParam().isEmpty() && form.getAdditionalParam().containsKey("showId")) {
			Page<ShowSeatDetailsEntity> result = Optional.ofNullable(form.getSearch())
					.filter(search -> !search.isEmpty())
					.map(search -> showSeatDetailsRepository.getSeatListSearchByShowId(ModelConstants.STATUS_ACTIVE,
							pageable, search, Long.parseLong(form.getAdditionalParam().get("showId").toString())))
					.orElseGet(() -> showSeatDetailsRepository.getSeatListByShowId(ModelConstants.STATUS_ACTIVE,
							pageable, Long.parseLong(form.getAdditionalParam().get("showId").toString())));
			return result.map(ShowSeatDetailsMapper.INSTANCE::fromShowSeatDetailsEntity);

		}
		return Page.empty();
	}

	public List<ShowSeatDetailsDto> getSeatDetailsByShowId(Long showId) {
		return showSeatDetailsRepository.findAllByActiveFlagAndShowIdOrderBySize(ModelConstants.STATUS_ACTIVE, showId)
				.stream()
				.map(entity -> {
					var dto = ShowSeatDetailsMapper.INSTANCE.fromShowSeatDetailsEntity(entity);
					dto.setIsSeatBooked(showStudentDetailRepository.existsByActiveFlagAndSeatIdAndStudentIdIgnoreCase(ModelConstants.STATUS_ACTIVE, dto.getId(),
							SecurityCtxUtil.userId().toUpperCase()));
					return dto;
				})
				.toList();
	}

	public ShowSeatDetailsDto getSeatDetailsByIdAndShowIdAndIsAvailable(Long seatId, Long showId) {
		return showSeatDetailsRepository.findByActiveFlagAndIdAndShow_IdAndIsAvailable(ModelConstants.STATUS_ACTIVE,seatId,showId, true)
				.map(ShowSeatDetailsMapper.INSTANCE::fromShowSeatDetailsEntity)
				.orElse(null);
	}

	public boolean getSeatIsAvailable(Long seatId) {
		return showSeatDetailsRepository.existsByActiveFlagAndIdAndIsAvailable(
				ModelConstants.STATUS_ACTIVE, seatId, true);
	}

}
