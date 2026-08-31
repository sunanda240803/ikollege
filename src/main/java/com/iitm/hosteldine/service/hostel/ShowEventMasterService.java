package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.AccountHeadDto;
import com.iitm.hosteldine.dto.hostel.ShowEventMasterDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.hostel.ShowEventMasterMapper;
import com.iitm.hosteldine.model.hostel.ShowEventMasterEntity;
import com.iitm.hosteldine.repository.hostel.ShowEventMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowEventMasterService {
    private final ShowEventMasterRepository showEventMasterRepository;
    private final MessageSource messageSource;
    private final AccountHeadService accountHeadService;

    public Page<ShowEventMasterDto> getEventMasterList(PaginationForm form) {
        var pageRequest = PageRequest.of(form.getPage() - 1, form.getSize(), Sort.by("modifiedAt").descending());

        Page<ShowEventMasterEntity> result = Optional.ofNullable(form.getSearch())
                .filter(search -> !search.isEmpty())
                .map(search -> showEventMasterRepository.findByEventNameAndEventDescAndActive(ModelConstants.STATUS_ACTIVE, pageRequest, search))
                .orElseGet(() -> showEventMasterRepository.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE, pageRequest));

        return result.map(ShowEventMasterMapper.INSTANCE::toDto);
    }

    public ShowEventMasterDto getEventMasterById(Long id) {
        return showEventMasterRepository.findById(id).map(ShowEventMasterMapper.INSTANCE::toDto).orElse(new ShowEventMasterDto());
    }

    public String saveOrUpdateEventMaster(ShowEventMasterDto showEventMasterDto) {
        return Optional.ofNullable(showEventMasterDto.getId())
                .filter(id -> id > 0L)
                .flatMap(showEventMasterRepository::findById)
                .map(existingEntity -> {
                    ShowEventMasterMapper.INSTANCE.onUpdateEntity(existingEntity, showEventMasterDto);
                    existingEntity.setCurrentlyActive(Objects.nonNull(existingEntity.getCurrentlyActive())
                            && !existingEntity.getCurrentlyActive().isEmpty()?existingEntity.getCurrentlyActive():"N");
                    showEventMasterRepository.save(existingEntity);
                    return Constants.UPDATED;
                })
                .orElseGet(() -> {
                    ShowEventMasterEntity entity = ShowEventMasterMapper.INSTANCE.toEntity(showEventMasterDto);
                    entity.setCurrentlyActive(Objects.nonNull(entity.getCurrentlyActive())
                            && !entity.getCurrentlyActive().isEmpty()?entity.getCurrentlyActive():"N");
                    entity.setSchoolId(1L);
                    entity.onCreate();
                    showEventMasterRepository.save(entity);
                    return Constants.SAVED;
                });
    }


    public boolean deleteEventMaster(Long id) throws RecordNotExistsException {
        return showEventMasterRepository.findById(id)
                .map(entity->{
                    entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
                    showEventMasterRepository.save(entity);
                    return true;
                })
                .orElseThrow(() -> new RecordNotExistsException(
                        messageSource.getMessage("validation.error.id.not.found", null, Locale.getDefault()
                        )));
    }

    public List<AccountHeadDto> getAccountHeadList() {
        return accountHeadService.getAccoutHeadList();
    }

	public List<ShowEventMasterDto> getActiveEventMasterList() {
		return Optional.ofNullable(showEventMasterRepository.findAllByActiveFlagAndCurrentlyActiveOrderByCreatedAtDesc(ModelConstants.STATUS_ACTIVE,ModelConstants.STATUS_ACTIVE)).orElse(Collections.emptyList()).stream().map(ShowEventMasterMapper.INSTANCE::toDto).collect(Collectors.toList());
	}

    public List<ShowEventMasterDto> getActiveEventListByDate() {
        return showEventMasterRepository.findAllByActiveFlagAndCurrentlyActiveAndEventStartingDateIsLessThanEqualAndEventEndingDateIsGreaterThanEqual(
                ModelConstants.STATUS_ACTIVE, ModelConstants.STATUS_ACTIVE, LocalDate.now(),LocalDate.now())
                .stream()
                .map(ShowEventMasterMapper.INSTANCE::toDto)
                .sorted(Comparator.comparing(ShowEventMasterDto::getEventName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

}
