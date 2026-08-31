package com.iitm.hosteldine.service.hostel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.ShowMasterDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.hostel.ShowMasterMapper;
import com.iitm.hosteldine.model.hostel.ShowMasterEntity;
import com.iitm.hosteldine.repository.hostel.ShowMasterRepository;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.util.MCrypt;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShowMasterService {
	
	private final ShowMasterRepository showMasterRepository;
	private final FileService fileService;

	public Page<ShowMasterDto> getShowWiseDetailsList(PaginationForm form) {
		Pageable pageable = PageRequest.of(form.getPage() - 1, form.getSize(), Sort.by("createdAt").descending());
		if (!form.getAdditionalParam().isEmpty() &&  form.getAdditionalParam().containsKey("eventId")) {
		Page<ShowMasterEntity> result = Optional.ofNullable(form.getSearch())
                .filter(search -> !search.isEmpty())
                .map(search -> showMasterRepository.getShowWiseListSearch(ModelConstants.STATUS_ACTIVE, pageable, search , 
                		Long.parseLong(form.getAdditionalParam().get("eventId").toString())))
                .orElseGet(() -> showMasterRepository.getShowWiseList(ModelConstants.STATUS_ACTIVE, pageable,
                		Long.parseLong(form.getAdditionalParam().get("eventId").toString())));
		return result.map(ShowMasterMapper.INSTANCE::fromShowMasterEntity);
		
	}
		return  Page.empty();
	}

	public ShowMasterDto getShowWiseDetailsById(long id) {
		return showMasterRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
				.map(ShowMasterMapper.INSTANCE::fromShowMasterEntity)
				.orElse(new ShowMasterDto());
	}

	public ShowMasterDto saveUpdateShowWise(ShowMasterDto dto) throws Exception {
		String layoutName = null , seatLayoutName = null;
		long currentMillis = System.currentTimeMillis();
		String originalFilenameLayout = dto.getLayout().getOriginalFilename();
		String originalFilenameSeatLayout = dto.getSeatLayout().getOriginalFilename();
		String extension = "" , extensionSeat = "";
		ShowMasterEntity[] afterSave = new ShowMasterEntity[1];
		if (originalFilenameLayout != null && originalFilenameLayout.contains(".")) {
		    extension = originalFilenameLayout.substring(originalFilenameLayout.lastIndexOf(".") + 1);
		}
		if (originalFilenameSeatLayout != null && originalFilenameSeatLayout.contains(".")) {
		    extensionSeat = originalFilenameSeatLayout.substring(originalFilenameSeatLayout.lastIndexOf(".") + 1);
		}
		if (dto.getCurrentlyActive()!=null && dto.getCurrentlyActive().equalsIgnoreCase("on")) {
			dto.setCurrentlyActive(ModelConstants.STATUS_ACTIVE);
		} else {
			dto.setCurrentlyActive(ModelConstants.STATUS_INACTIVE);
		}
		
		if (dto.getLayout() != null &&  !dto.getLayout().getOriginalFilename().isEmpty()) {
			layoutName =  MCrypt.getInstance().encryptToText(dto.getShowEventMaster().getId()+ ModelConstants.UNDERSCORE + currentMillis ) +"."+ extension;
			dto.setImageLocation(layoutName + ModelConstants.UNDERSCORE + ModelConstants.IMAGE_SHOW_WISE_LAYOUT_IMAGE);
		}
		if (dto.getSeatLayout() != null && !dto.getSeatLayout().getOriginalFilename().isEmpty()) {
			seatLayoutName =  MCrypt.getInstance().encryptToText(dto.getShowEventMaster().getId()+ ModelConstants.UNDERSCORE + currentMillis) +"."+ extensionSeat;
			dto.setSeatImageLocation(seatLayoutName + ModelConstants.UNDERSCORE  + ModelConstants.IMAGE_SHOW_WISE_SEAT_IMAGE);
		}
			
		String result = Optional.ofNullable(dto.getId()).filter(id -> (id != null && id > 0))
				.flatMap(id -> showMasterRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					if (dto.getSeatLayout() == null || dto.getSeatLayout().getOriginalFilename().isEmpty()) {
		                dto.setSeatImageLocation(existingEntity.getSeatImageLocation());
		            }
					if (dto.getLayout() == null || dto.getLayout().getOriginalFilename().isEmpty()) {
		                dto.setImageLocation(existingEntity.getImageLocation());
		            }
					ShowMasterMapper.INSTANCE.onUpdateEntity(existingEntity, dto);
					
					afterSave[0] = showMasterRepository.save(existingEntity);
					return Constants.UPDATED;
				}).orElseGet(() -> {
					ShowMasterEntity newEntity = ShowMasterMapper.INSTANCE.onSaveEntity(dto);
					newEntity.setId(null);
					 afterSave[0] = showMasterRepository.save(newEntity);
					return Constants.SAVED;
				});
		if (result.equalsIgnoreCase("Saved") || result.equalsIgnoreCase("updated")) {
			if (dto.getSeatLayout() != null && !dto.getSeatLayout().getOriginalFilename().isEmpty()) {
				fileService.encodeFile(ModelConstants.FILE_SEAT_LAYOUT, dto.getSeatLayout().getBytes(),
						seatLayoutName +  ModelConstants.UNDERSCORE + ModelConstants.IMAGE_SHOW_WISE_SEAT_IMAGE );
			}
			if (dto.getLayout() != null && !dto.getLayout().getOriginalFilename().isEmpty()) {
				fileService.encodeFile(ModelConstants.FILE_SEAT_LAYOUT, dto.getLayout().getBytes(),
						layoutName  + ModelConstants.UNDERSCORE + ModelConstants.IMAGE_SHOW_WISE_LAYOUT_IMAGE);
			}
		}
		//return result;
		return ShowMasterMapper.INSTANCE.fromShowMasterEntity(afterSave[0]);
	}

	public boolean checkShowNameExist(long eventId, String showName, long showId) {
		return showMasterRepository
				.findByActiveFlagAndShowEventMasterIdAndShowNameIgnoreCaseAndIdNot(ModelConstants.STATUS_ACTIVE,eventId, showName,showId)
				.size() > 0;
	}

	public List<ShowMasterDto> getShowNameList(long eventId) {
		return showMasterRepository.
				findAllByActiveFlagAndShowEventMasterIdOrderByShowDescrAscShowNameAsc(ModelConstants.STATUS_ACTIVE,eventId)
    			.stream().map(ShowMasterMapper.INSTANCE::fromShowMasterEntity)
				.collect(Collectors.toList());
	}

	public ShowMasterDto getShowBySeatId(long seatId) {
		return showMasterRepository.getBySeatId(seatId,ModelConstants.STATUS_ACTIVE)
				.map(ShowMasterMapper.INSTANCE::fromShowMasterEntity)
				.orElse(null);
	}

}

