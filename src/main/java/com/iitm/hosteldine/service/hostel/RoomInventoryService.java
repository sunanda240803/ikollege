package com.iitm.hosteldine.service.hostel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.hostel.HostelRoomInfoDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.asset.AssetCategoryForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.form.hostel.RoomInventoryForm;
import com.iitm.hosteldine.mapper.asset.AssetInventoryInfoMapper;
import com.iitm.hosteldine.mapper.hostel.HostelRoomInventoryMapper;
import com.iitm.hosteldine.model.asset.AssetInventoryInfoEntity;
import com.iitm.hosteldine.model.hostel.HostelRoomInfoEntity;
import com.iitm.hosteldine.model.hostel.HostelRoomInventoryEntity;
import com.iitm.hosteldine.repository.asset.AssetInventoryInfoRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomAllotmentRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomInventoryRepository;
import com.iitm.hosteldine.service.asset.AssetConfigurationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomInventoryService {
	private final MessageSource messageSource;
	private final AssetInventoryInfoRepository assetInventoryInfoRepo;
	private final HostelRoomInventoryRepository hostelRoomInventoryRepo;
	private final AssetConfigurationService assetConfigurationService;
	private final HostelMasterService hostelMasterService;
	private final HostelRoomInfoService hostelRoomInfoService;
	private final BulkRoomInventoryService bulkRoomInventoryService;
	

	public List<RoomInventoryForm> getRoomInventoryList(RoomInventoryForm roomInventoryForm) {
		List<RoomInventoryForm> resultList = new ArrayList<>();
		List<Object[]> list = null;

		if (roomInventoryForm.getAssetCondition().toLowerCase().equals("all")) {
			list = assetInventoryInfoRepo.getAllRoomInventoryList(ModelConstants.STATUS_ACTIVE,
					roomInventoryForm.getHostelId());
		} else {
			list = assetInventoryInfoRepo.getRoomInventoryList(ModelConstants.STATUS_ACTIVE,
					roomInventoryForm.getHostelId(), roomInventoryForm.getAssetCondition().toLowerCase());
		}

		if (list != null && !list.isEmpty()) {
			for (Object[] obj : list) {
				RoomInventoryForm form = new RoomInventoryForm();
				form.setAssetCategoryId(obj[0] != null ? (Long) obj[0] : null);
				form.setAssetCode(obj[1] != null ? obj[1].toString() : null);
				form.setAssetName(obj[2] != null ? obj[2].toString() : null);
				form.setHostelName(obj[3] != null ? obj[3].toString() : null);
				form.setRoomNo(obj[4] != null ? obj[4].toString() : null);
				form.setAssetCondition(obj[5] != null ? obj[5].toString() : null);
				form.setHostelId(obj[6] != null ? (Long) obj[6] : null);
				form.setRoomId(obj[7] != null ? (Long) obj[7] : null);
				resultList.add(form);
			}
		}
		return resultList;
	}

	public RoomInventoryForm getRoomInventoryDetailsByAssetId(Long id) {
		RoomInventoryForm resultList = new RoomInventoryForm();
		List<Object[]> list = assetInventoryInfoRepo.getRoomInventoryDetailsByAssetId(ModelConstants.STATUS_ACTIVE, id);

		if (!list.isEmpty()) {
			Object[] obj = list.get(0);
			resultList.setAssetCategoryId(obj[0] != null ? (Long) obj[0] : null);
			resultList.setAssetCode(obj[1] != null ? obj[1].toString() : null);
			resultList.setAssetName(obj[2] != null ? obj[2].toString() : null);
			resultList.setHostelName(obj[3] != null ? obj[3].toString() : null);
			resultList.setRoomNo(obj[4] != null ? obj[4].toString() : null);
			resultList.setAssetCondition(obj[5] != null ? obj[5].toString() : null);
			resultList.setHostelId(obj[6] != null ? (Long) obj[6] : null);
			resultList.setRoomId(obj[7] != null ?
					(obj[7] instanceof Long ? (Long) obj[7] :
							(obj[7] instanceof Integer ? ((Integer) obj[7]).longValue() : null)) : null);
		}
		return resultList;
	}

	@Transactional
	public String saveAndUpdateRoomInventory(RoomInventoryForm roomInventoryForm) throws RecordNotExistsException {
		if (SecurityCtxUtil.userName().toLowerCase().contains(".hostel")) {
			IntStream.range(0, Math.toIntExact(roomInventoryForm.getQuantity()))
					.mapToObj(i -> {
						RoomInventoryForm copy = copyRoomInventoryForm(roomInventoryForm);
						copy.setAssetCategoryId(0L);
						return copy;
					})
					.forEach(form -> {
						try {
							saveAndUpdate(form);
						} catch (RecordNotExistsException e) {
							throw new RuntimeException("Failed to save Room Inventory at iteration: " + form, e);
						}
					});
			return Constants.SAVED;
		} else {
			return saveAndUpdate(roomInventoryForm);
		}
	}

	private RoomInventoryForm copyRoomInventoryForm(RoomInventoryForm original) {
		RoomInventoryForm copy = new RoomInventoryForm();
		copy.setCategoryId(original.getCategoryId());
		copy.setHostelId(original.getHostelId());
		copy.setRoomId(original.getRoomId());
		copy.setAssetName(original.getAssetName());
		copy.setQuantity(1L);
		return copy;
	}

	public String saveAndUpdate(RoomInventoryForm form) throws RecordNotExistsException {
		String isSaved = null;
		if (form.getAssetCategoryId() != 0) {
			AssetInventoryInfoEntity assetInventoryDetails = assetInventoryInfoRepo
					.findByAssetIdAndActiveFlag(form.getAssetCategoryId(), ModelConstants.STATUS_ACTIVE);
			HostelRoomInventoryEntity hostelRoomInventoryDetails = hostelRoomInventoryRepo
					.findByItemIdAndActiveFlag(String.valueOf(form.getAssetCategoryId()), ModelConstants.STATUS_ACTIVE);
			if (assetInventoryDetails != null && hostelRoomInventoryDetails != null) {
				isSaved = saveOrUpdateInventory(assetInventoryDetails, hostelRoomInventoryDetails, form);
			} else {
				throw new RecordNotExistsException("Asset or Hostel Room Inventory details not found.");
			}
		} else {
			AssetCategoryForm assetCategoryInfo = getAssetCategoryInfo(form.getCategoryId());
			HostelMasterDto hostelInfo = getHostelInfo(form.getHostelId());
			HostelRoomInfoDto roomInfo = getRoomNoInfo(form.getHostelId(), form.getRoomId());

			// If valid data is found, set it in the form
			form.setAssetCategoryId(assetCategoryInfo.getId());
			form.setAssetCategory(assetCategoryInfo.getCategoryName());
			form.setAssetCategoryShortcode(assetCategoryInfo.getAssetCategoryShortcode());
			form.setHostelName(hostelInfo.getHostelName());
			form.setHostelId(hostelInfo.getId());
			form.setHostelShortCode(hostelInfo.getHostelShortCode());
	        form.setRoomNo(roomInfo.getRoomNo());
	        form.setRoomId(roomInfo.getId());
			
			String baseAssetCode = form.getHostelShortCode() + form.getRoomNo() + form.getAssetCategoryShortcode();
			String assetCode = bulkRoomInventoryService.generateUniqueAssetCode(baseAssetCode);

			// Set the generated asset code in the form
			form.setAssetCode(assetCode);

			// Create AssetInventoryInfoEntity and save it to get the ID
			AssetInventoryInfoEntity assetInventoryInfoEntity = bulkRoomInventoryService.createAssetInventoryInfoEntity(form);
			assetInventoryInfoEntity = assetInventoryInfoRepo.saveAndFlush(assetInventoryInfoEntity);

			 // Create and save HostelRoomInventoryEntity with the asset ID
			form.setAssetId(String.valueOf(assetInventoryInfoEntity.getAssetId()));
			HostelRoomInventoryEntity hostelRoomInventory = bulkRoomInventoryService.createHostelRoomInventoryEntity(form);
			HostelRoomInventoryEntity hostelRoomInventoryEntity = hostelRoomInventoryRepo.saveAndFlush(hostelRoomInventory);
			
			if (Objects.nonNull(assetInventoryInfoEntity) && Objects.nonNull(hostelRoomInventoryEntity)) {
				isSaved = "saved";
			}
		}
		return isSaved;
	}

	private String saveOrUpdateInventory(AssetInventoryInfoEntity asset, HostelRoomInventoryEntity hostel,
			RoomInventoryForm form) throws RecordNotExistsException {
		String result = null;

		AssetInventoryInfoMapper.INSTANCE.toAssetInventoryInfoEntity(asset, form);
		asset.onUpdate();

		HostelRoomInfoEntity room = new HostelRoomInfoEntity();
		room.setId(form.getRoomId());
		HostelRoomInventoryMapper.INSTANCE.toHostelRoomInventoryEntity(hostel, form);
		hostel.setRoom(room);
		hostel.onUpdate();

		AssetInventoryInfoEntity entity = assetInventoryInfoRepo.saveAndFlush(asset);
		HostelRoomInventoryEntity entity1 = hostelRoomInventoryRepo.saveAndFlush(hostel);
		if (Objects.nonNull(entity) && Objects.nonNull(entity1)) {
			result = "saved";
		}
		return result;
	}

	public boolean deleteRoomInventoryDetailsById(Long id) throws NoSuchMessageException, RecordNotExistsException {
		// Find and update AssetInventoryInfoEntity
		AssetInventoryInfoEntity assetInventoryDetails = assetInventoryInfoRepo.findByAssetIdAndActiveFlag(id,
				ModelConstants.STATUS_ACTIVE);
		if (assetInventoryDetails == null) {
			throw new RecordNotExistsException(
					messageSource.getMessage("message.validation.room.inventory.is.not.found", null, Locale.getDefault()));
		}

		assetInventoryDetails.setActiveFlag(ModelConstants.STATUS_INACTIVE);
		assetInventoryDetails.onUpdate();
		assetInventoryInfoRepo.saveAndFlush(assetInventoryDetails);

		// Find and update HostelRoomInventoryEntity
		HostelRoomInventoryEntity hostelRoomInventoryDetails = hostelRoomInventoryRepo
				.findByItemIdAndActiveFlag(String.valueOf(id), ModelConstants.STATUS_ACTIVE);
		if (hostelRoomInventoryDetails == null) {
			throw new RecordNotExistsException(
					messageSource.getMessage("message.validation.room.inventory.is.not.found", null, Locale.getDefault()));
		}

		hostelRoomInventoryDetails.setActiveFlag(ModelConstants.STATUS_INACTIVE);
		hostelRoomInventoryDetails.onUpdate();
		hostelRoomInventoryRepo.saveAndFlush(hostelRoomInventoryDetails);

		// Check if both entities are inactive
		boolean isAssetInactive = ModelConstants.STATUS_INACTIVE.equals(assetInventoryDetails.getActiveFlag());
		boolean isHostelRoomInactive = ModelConstants.STATUS_INACTIVE
				.equals(hostelRoomInventoryDetails.getActiveFlag());

		// Return true if both are inactive, false otherwise
		return isAssetInactive && isHostelRoomInactive;
	}

	/**
	 * Retrieves the values of id, category name and asset shortcode from the ASSET_CATEGORY_INFO table 
	 */
	public AssetCategoryForm getAssetCategoryInfo(Long value) {
		return assetConfigurationService.getCategoryList().stream()
				.filter(assetCategory -> assetCategory.getId() == value).findFirst()
				.map(assetCategory -> new AssetCategoryForm(assetCategory.getId(), assetCategory.getCategoryName(),
						assetCategory.getAssetCategoryShortcode()))
				.orElseThrow(() -> new IllegalArgumentException("Asset Category is not found"));
	}
	
	/**
	 * Retrieves the values of id, hostel name and hostel shortcode from the HOSTEL_MASTER table 
	 */
	public HostelMasterDto getHostelInfo(Long value) {
		return hostelMasterService.getHostelList().stream().filter(hostel -> hostel.getId() == value).findFirst()
				.map(hostel -> new HostelMasterDto(hostel.getId(), hostel.getHostelName(), hostel.getHostelShortCode()))
				.orElseThrow(() -> new IllegalArgumentException("Hostel is not found"));
	}

	/**
	 * Retrieves the values of roomId, room name
	 */
	public HostelRoomInfoDto getRoomNoInfo(Long hostelId, Long roomId) {
		// Find the room by hostel ID and room number
		HostelRoomInfoEntity roomEntity = hostelRoomInfoService.findRoomByHostelIdAndRoomId(hostelId, roomId);

		if (roomEntity != null) {
			return new HostelRoomInfoDto(roomEntity.getId(), roomEntity.getRoomNo());
		} else {
			throw new IllegalArgumentException("Room number is not found");
		}
	}

	public Page<RoomInventoryForm> getRoomInventoryList(PaginationForm form) {
		Page<Object[]> result = null;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize());
		if (form.getAdditionalParam() != null
				&& form.getAdditionalParam().get("assetCondition").toString().equalsIgnoreCase("all")) {
			result = assetInventoryInfoRepo.getAllRoomInventoryListSearch(ModelConstants.STATUS_ACTIVE,
					Long.parseLong(form.getAdditionalParam().get("hostelId").toString()), form.getSearch(), pageable);
		} else {
			result = assetInventoryInfoRepo.getRoomInventoryList(ModelConstants.STATUS_ACTIVE,
					Long.parseLong(Objects.requireNonNull(form.getAdditionalParam()).get("hostelId").toString()),
					form.getAdditionalParam().get("assetCondition").toString().toLowerCase(), form.getSearch(),
					pageable);
		}
		return result.map(record -> {
			RoomInventoryForm dto = new RoomInventoryForm();
			dto.setAssetCategoryId(record[0] != null ? (Long) record[0] : null);
			dto.setAssetCode(record[1] != null ? record[1].toString() : null);
			dto.setAssetName(record[2] != null ? record[2].toString() : null);
			dto.setHostelName(record[3] != null ? record[3].toString() : null);
			dto.setRoomNo(record[4] != null ? record[4].toString() : null);
			dto.setAssetCondition(record[5] != null ? record[5].toString() : null);
			dto.setHostelId(record[6] != null ? (Long) record[6] : null);
			dto.setRoomId(record[7] != null ? ((Number) record[7]).longValue() : null);
			return dto;
		});
	}

	public Page<RoomInventoryForm> getRoomInventoryReplacedList(PaginationForm form) {
		long hostelId = Optional.ofNullable(form.getAdditionalParam().get(HostelConstants.HOSTEL_ID.getConstants()))
				.map(Object::toString)
				.map(Long::parseLong)
				.orElseThrow(() -> new IllegalArgumentException(HostelConstants.INVALID_HOSTEL_ID.getConstants()));
		return assetInventoryInfoRepo.getRoomInventoryReplacedList(
				ModelConstants.STATUS_ACTIVE,
						hostelId,
						form.getSearch(),
				PageRequest.of(form.getPage() - 1, form.getSize())
				).map(record -> {
			RoomInventoryForm dto = new RoomInventoryForm();
			dto.setAssetCategoryId(getValue(record[0], Long.class));
			dto.setAssetCode(getValue(record[1], String.class));
			dto.setAssetName(getValue(record[2], String.class));
			dto.setHostelName(getValue(record[3], String.class));
			dto.setRoomNo(getValue(record[4], String.class));
			dto.setAssetCondition(getValue(record[5], String.class));
			dto.setHostelId(getValue(record[6], Long.class));
			dto.setRoomId(getValue(record[7], Number.class) != null ? getValue(record[7], Number.class).longValue() : null);
			dto.setAssetConditionStatus(getValue(record[9], String.class));
			System.out.println(dto);
			return dto;
		});
	}

	public boolean updateReplacedOrRepairedStatus(Long id) throws RecordNotExistsException {
		return assetInventoryInfoRepo.findById(id)
				.map(entity->{
					entity.setAssetConditionStatus(HostelConstants.GOOD.getConstants());
					assetInventoryInfoRepo.save(entity);
					return true;
				})
				.orElseThrow(() -> new RecordNotExistsException(
						messageSource.getMessage("validation.error.id.not.found", null, Locale.getDefault()
						)));
	}

	private static <T> T getValue(Object obj, Class<T> type) {
		return Optional.ofNullable(obj).map(type::cast).orElse(null);
	}
}
